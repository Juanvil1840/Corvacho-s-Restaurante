package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Adicional;
import com.example.demo.entities.AdicionalSeleccionado;
import com.example.demo.entities.Categoria;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Domiciliario;
import com.example.demo.entities.Pedido;
import com.example.demo.entities.Producto;
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:pedido_integracion;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class PedidoServiceIntegrationTest {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DomiciliarioRepository domiciliarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Autowired
    private EntityManager entityManager;

    private int secuencia = 0;

    // 1. Crear un pedido lo deja en "Recibido" y guarda sus detalles con los datos reales de la BD
    @Test
    void crearPedido_guardaPedidoRecibidoConDetallesYAdicionalesDeLaBD() {
        Cliente cliente = crearCliente(true);
        Categoria categoria = categoriaRepository.save(new Categoria("Pastas prueba"));
        Producto producto = crearProducto("Lasagna prueba", 32000, true, categoria);
        Adicional queso = adicionalRepository.save(new Adicional("Queso extra", 4000, true, categoria));

        // El frontend manda un nombre y precio falsos del adicional: el servicio debe ignorarlos
        AdicionalSeleccionado enviado = new AdicionalSeleccionado(queso.getId_adicional(), "Nombre falso", 1, 2);
        Pedido nuevo = pedidoCon(cliente, null, detalle(producto, 2, enviado));

        Pedido creado = pedidoService.crearPedido(nuevo);
        limpiarContexto();

        Pedido enBD = pedidoRepository.findById(creado.getId_Pedido()).orElseThrow();
        assertThat(enBD.getEstado()).isEqualTo("Recibido");
        assertThat(enBD.getFechaCreacion()).isNotNull();
        assertThat(enBD.getFechaEntrega()).isNull();
        assertThat(enBD.getCliente().getClienteId()).isEqualTo(cliente.getClienteId());

        List<DetallePedido> detalles = pedidoService.findDetallesByPedidoId(creado.getId_Pedido());
        assertThat(detalles).hasSize(1);
        DetallePedido detalleGuardado = detalles.get(0);
        assertThat(detalleGuardado.getCantidad()).isEqualTo(2);
        assertThat(detalleGuardado.getProducto().getId()).isEqualTo(producto.getId());
        assertThat(detalleGuardado.getAdicionales()).hasSize(1);
        AdicionalSeleccionado adicionalGuardado = detalleGuardado.getAdicionales().get(0);
        assertThat(adicionalGuardado.getNombre()).isEqualTo("Queso extra");
        assertThat(adicionalGuardado.getPrecio()).isEqualTo(4000);
        assertThat(adicionalGuardado.getCantidad()).isEqualTo(2);

        assertThat(pedidoService.findByClienteId(cliente.getClienteId()))
                .extracting(Pedido::getId_Pedido)
                .containsExactly(creado.getId_Pedido());
    }

    // 2. Un cliente desactivado no puede hacer pedidos y no se guarda nada en la BD
    @Test
    void crearPedido_conClienteDesactivado_lanzaErrorYNoGuardaNada() {
        Cliente clienteInactivo = crearCliente(false);
        Producto producto = crearProducto("Pizza prueba", 28000, true,
                categoriaRepository.save(new Categoria("Pizzas prueba")));
        long pedidosAntes = pedidoRepository.count();
        long detallesAntes = detallePedidoRepository.count();

        assertThatThrownBy(() -> pedidoService.crearPedido(pedidoCon(clienteInactivo, null, detalle(producto, 1))))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST))
                .hasMessageContaining("desactivado");

        assertThat(pedidoRepository.count()).isEqualTo(pedidosAntes);
        assertThat(detallePedidoRepository.count()).isEqualTo(detallesAntes);
    }

    // 3. No se puede pedir un producto agotado
    @Test
    void crearPedido_conProductoAgotado_lanzaError() {
        Cliente cliente = crearCliente(true);
        Producto agotado = crearProducto("Pulpo agotado", 45000, false,
                categoriaRepository.save(new Categoria("Mariscos prueba")));

        assertThatThrownBy(() -> pedidoService.crearPedido(pedidoCon(cliente, null, detalle(agotado, 1))))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Pulpo agotado")
                .hasMessageContaining("agotado");
    }

    // 4. Flujo completo: Recibido -> Cocinando -> Enviado -> Entregado, ocupando y liberando al domiciliario
    @Test
    void actualizarPedido_flujoCompletoDeEstados_ocupaYLiberaAlDomiciliario() {
        Cliente cliente = crearCliente(true);
        Domiciliario domiciliario = crearDomiciliario();
        Producto producto = crearProducto("Carpaccio prueba", 30000, true,
                categoriaRepository.save(new Categoria("Entradas prueba")));

        Long id = pedidoService.crearPedido(pedidoCon(cliente, domiciliario, detalle(producto, 1))).getId_Pedido();
        limpiarContexto();

        pedidoService.actualizarPedido(id, cambioDeEstado("Cocinando", cliente, domiciliario));
        limpiarContexto();
        assertThat(pedidoRepository.findById(id).orElseThrow().getEstado()).isEqualTo("Cocinando");
        assertThat(domiciliarioDisponible(domiciliario)).isTrue();

        // Al enviarse, el domiciliario queda ocupado (el estado en minusculas se normaliza)
        pedidoService.actualizarPedido(id, cambioDeEstado("enviado", cliente, domiciliario));
        limpiarContexto();
        assertThat(pedidoRepository.findById(id).orElseThrow().getEstado()).isEqualTo("Enviado");
        assertThat(domiciliarioDisponible(domiciliario)).isFalse();

        // Al entregarse, vuelve a estar disponible y se registra la fecha de entrega
        pedidoService.actualizarPedido(id, cambioDeEstado("Entregado", cliente, domiciliario));
        limpiarContexto();
        Pedido entregado = pedidoRepository.findById(id).orElseThrow();
        assertThat(entregado.getEstado()).isEqualTo("Entregado");
        assertThat(entregado.getFechaEntrega()).isNotNull();
        assertThat(domiciliarioDisponible(domiciliario)).isTrue();

        // Un pedido entregado ya no aparece entre los activos
        assertThat(pedidoService.findActivos()).extracting(Pedido::getId_Pedido).doesNotContain(id);
    }

    // 5. No se puede saltar estados ni modificar un pedido ya entregado
    @Test
    void actualizarPedido_rechazaSaltosDeEstadoYCambiosEnPedidoEntregado() {
        Cliente cliente = crearCliente(true);
        Domiciliario domiciliario = crearDomiciliario();
        Producto producto = crearProducto("Burrata prueba", 27000, true,
                categoriaRepository.save(new Categoria("Quesos prueba")));

        Long id = pedidoService.crearPedido(pedidoCon(cliente, domiciliario, detalle(producto, 1))).getId_Pedido();
        limpiarContexto();

        // Recibido -> Entregado no esta permitido
        assertThatThrownBy(() -> pedidoService.actualizarPedido(id, cambioDeEstado("Entregado", cliente, domiciliario)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Transición no permitida");
        limpiarContexto();
        assertThat(pedidoRepository.findById(id).orElseThrow().getEstado()).isEqualTo("Recibido");

        // Se marca como entregado directamente en la BD y se intenta volver a modificar
        Pedido pedido = pedidoRepository.findById(id).orElseThrow();
        pedido.setEstado("Entregado");
        pedidoRepository.save(pedido);
        limpiarContexto();

        assertThatThrownBy(() -> pedidoService.actualizarPedido(id, cambioDeEstado("Entregado", cliente, domiciliario)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ya fue entregado");

        // Actualizar un pedido que no existe devuelve null
        assertThat(pedidoService.actualizarPedido(999_999L, cambioDeEstado("Cocinando", cliente, domiciliario)))
                .isNull();
    }

    // 6. Eliminar un pedido en camino lo borra con sus detalles y libera al domiciliario
    @Test
    void deleteById_pedidoEnviado_eliminaPedidoYDetallesYLiberaDomiciliario() {
        Cliente cliente = crearCliente(true);
        Domiciliario domiciliario = crearDomiciliario();
        Producto producto = crearProducto("Berenjenas prueba", 26000, true,
                categoriaRepository.save(new Categoria("Vegetarianos prueba")));

        Long id = pedidoService.crearPedido(pedidoCon(cliente, domiciliario, detalle(producto, 3))).getId_Pedido();
        limpiarContexto();
        pedidoService.actualizarPedido(id, cambioDeEstado("Cocinando", cliente, domiciliario));
        limpiarContexto();
        pedidoService.actualizarPedido(id, cambioDeEstado("Enviado", cliente, domiciliario));
        limpiarContexto();
        assertThat(domiciliarioDisponible(domiciliario)).isFalse();
        assertThat(pedidoService.findDetallesByPedidoId(id)).hasSize(1);

        pedidoService.deleteById(id);
        limpiarContexto();

        assertThat(pedidoService.findById(id)).isNull();
        assertThat(pedidoService.findDetallesByPedidoId(id)).isEmpty();
        assertThat(domiciliarioDisponible(domiciliario)).isTrue();
    }

    // ---------- Metodos auxiliares ----------

    // Escribe los cambios en H2 y vacia la cache de JPA, como si fueran peticiones separadas
    private void limpiarContexto() {
        entityManager.flush();
        entityManager.clear();
    }

    private Cliente crearCliente(boolean activo) {
        int n = ++secuencia;
        return clienteRepository.save(new Cliente("Cliente" + n, "Prueba", "cliente.it" + n + "@prueba.com",
                "clave123", "300000000" + n, "Calle prueba " + n, activo));
    }

    private Domiciliario crearDomiciliario() {
        int n = ++secuencia;
        return domiciliarioRepository.save(new Domiciliario("Domiciliario" + n, "31099900" + n, "IT-" + n, true, null));
    }

    private Producto crearProducto(String nombre, double precio, boolean disponible, Categoria categoria) {
        return productoRepository.save(new Producto(nombre, precio, "Descripcion de prueba", "img.png", disponible, categoria));
    }

    private boolean domiciliarioDisponible(Domiciliario domiciliario) {
        return domiciliarioRepository.findById(domiciliario.getId_domiciliario()).orElseThrow().isInformaciondisp();
    }

    private DetallePedido detalle(Producto producto, int cantidad, AdicionalSeleccionado... adicionales) {
        DetallePedido detalle = new DetallePedido();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setAdicionales(new java.util.ArrayList<>(List.of(adicionales)));
        return detalle;
    }

    private Pedido pedidoCon(Cliente cliente, Domiciliario domiciliario, DetallePedido... detalles) {
        Pedido pedido = new Pedido();
        pedido.setCliente(referenciaCliente(cliente));
        pedido.setDomiciliario(referenciaDomiciliario(domiciliario));
        pedido.setDetallePedidos(new java.util.ArrayList<>(List.of(detalles)));
        return pedido;
    }

    // Simula el cuerpo que manda el frontend al cambiar de estado: solo ids de cliente y domiciliario
    private Pedido cambioDeEstado(String estado, Cliente cliente, Domiciliario domiciliario) {
        Pedido cambio = new Pedido();
        cambio.setEstado(estado);
        cambio.setCliente(referenciaCliente(cliente));
        cambio.setDomiciliario(referenciaDomiciliario(domiciliario));
        return cambio;
    }

    private Cliente referenciaCliente(Cliente cliente) {
        Cliente ref = new Cliente();
        ref.setClienteId(cliente.getClienteId());
        return ref;
    }

    private Domiciliario referenciaDomiciliario(Domiciliario domiciliario) {
        if (domiciliario == null) {
            return null;
        }
        Domiciliario ref = new Domiciliario();
        ref.setId_domiciliario(domiciliario.getId_domiciliario());
        return ref;
    }
}

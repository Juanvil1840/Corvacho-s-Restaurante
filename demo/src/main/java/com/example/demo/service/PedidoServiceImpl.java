package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Adicional;
import com.example.demo.entities.AdicionalSeleccionado;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Domiciliario;
import com.example.demo.entities.Operador;
import com.example.demo.entities.Pedido;
import com.example.demo.entities.Producto;
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.OperadorRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository;

// Implementacion del servicio para la gestion de pedidos, estados y asignacion de domiciliarios
@Service
public class PedidoServiceImpl implements PedidoService {

    // Estados posibles de un pedido, en orden
    private static final String RECIBIDO = "Recibido";
    private static final String COCINANDO = "Cocinando";
    private static final String ENVIADO = "Enviado";
    private static final String ENTREGADO = "Entregado";

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final OperadorRepository operadorRepository;
    private final DomiciliarioRepository domiciliarioRepository;
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final AdicionalRepository adicionalRepository;

    // Constructor con inyeccion de todos los repositorios requeridos
    public PedidoServiceImpl(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            OperadorRepository operadorRepository,
            DomiciliarioRepository domiciliarioRepository,
            ProductoRepository productoRepository,
            DetallePedidoRepository detallePedidoRepository,
            AdicionalRepository adicionalRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.operadorRepository = operadorRepository;
        this.domiciliarioRepository = domiciliarioRepository;
        this.productoRepository = productoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.adicionalRepository = adicionalRepository;
    }

    // Obtiene todos los pedidos registrados
    @Override
    public Collection<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    // Obtiene todos los pedidos que aun no han sido entregados
    @Override
    public Collection<Pedido> findActivos() {
        return pedidoRepository.findByEstadoNotIgnoreCase(ENTREGADO);
    }

    // Busca un pedido por su id o devuelve null si no existe
    @Override
    public Pedido findById(Long id) {
        return pedidoRepository.findById(id)
                .orElse(null);
    }

    // Guarda un pedido directamente en la base de datos
    @Override
    @Transactional
    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    // Crea un pedido nuevo verificando cliente activo, estado inicial "Recibido" y guardando sus detalles
    @Override
    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        pedido.setId_Pedido(null);
        pedido.setCliente(buscarCliente(pedido));
        validarClienteActivo(pedido.getCliente());
        pedido.setOperador(buscarOperador(pedido));
        pedido.setDomiciliario(buscarDomiciliario(pedido));
        validarDomiciliarioActivo(pedido.getDomiciliario());
        actualizarDisponibilidadDomiciliario(pedido, null, null);
        if (pedido.getFechaCreacion() == null) {
            pedido.setFechaCreacion(LocalDateTime.now());
        }
        if (pedido.getEstado() == null || pedido.getEstado().trim().isEmpty()) {
            pedido.setEstado(RECIBIDO);
        } else {
            String normalizado = normalizarEstado(pedido.getEstado());
            if (!normalizado.equals(RECIBIDO)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Un pedido nuevo debe iniciarse obligatoriamente en estado 'Recibido'.");
            }
            pedido.setEstado(normalizado);
        }

        List<DetallePedido> detallesRecibidos = pedido.getDetallePedidos();
        pedido.setDetallePedidos(null);

        Pedido guardado = pedidoRepository.save(pedido);
        guardarDetallesDelPedido(guardado, detallesRecibidos, new ArrayList<>());

        return guardado;
    }

    // Actualiza un pedido existente validando cambios de estado, domiciliario y detalles
    @Override
    @Transactional
    public Pedido actualizarPedido(Long id, Pedido pedido) {
        Pedido existente = findById(id);
        if (existente == null) {
            return null;
        }
        String estadoAnterior = existente.getEstado();
        Domiciliario domiciliarioAnterior = existente.getDomiciliario();

        // Un pedido entregado ya no se puede modificar
        if (ENTREGADO.equals(estadoAnterior)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El pedido ya fue entregado y no se puede modificar");
        }

        if (pedido.getEstado() != null && !pedido.getEstado().trim().isEmpty()) {
            validarTransicionEstado(estadoAnterior, pedido.getEstado());
            existente.setEstado(normalizarEstado(pedido.getEstado()));
        }

        // El cliente, la fecha de creacion y las comidas solo se cambian mientras el pedido esta "Recibido"
        boolean puedeCambiarDatos = RECIBIDO.equals(estadoAnterior);
        if (puedeCambiarDatos) {
            Cliente cliente = buscarCliente(pedido);
            if (existente.getCliente() == null
                    || !existente.getCliente().getClienteId().equals(cliente.getClienteId())) {
                validarClienteActivo(cliente);
            }
            existente.setCliente(cliente);
            if (pedido.getFechaCreacion() != null) {
                existente.setFechaCreacion(pedido.getFechaCreacion());
            }
        }

        existente.setOperador(buscarOperador(pedido));

        // Cuando el pedido ya fue enviado no se cambia el domiciliario
        if (!ENVIADO.equals(estadoAnterior)) {
            Domiciliario domiciliarioSolicitado = buscarDomiciliario(pedido);
            boolean conservaDomiciliarioActual = existente.getDomiciliario() != null
                    && domiciliarioSolicitado != null
                    && existente.getDomiciliario().getId_domiciliario()
                            .equals(domiciliarioSolicitado.getId_domiciliario());
            if (!conservaDomiciliarioActual) {
                validarDomiciliarioActivo(domiciliarioSolicitado);
            }
            existente.setDomiciliario(domiciliarioSolicitado);
        }
        existente.setFechaEntrega(pedido.getFechaEntrega());
        if (ENTREGADO.equals(existente.getEstado()) && existente.getFechaEntrega() == null) {
            existente.setFechaEntrega(LocalDateTime.now());
        }

        actualizarDisponibilidadDomiciliario(existente, estadoAnterior, domiciliarioAnterior);

        Pedido actualizado = pedidoRepository.save(existente);

        if (puedeCambiarDatos && pedido.getDetallePedidos() != null) {
            List<DetallePedido> previos = detallePedidoRepository.findByPedido_Id_Pedido(id);
            List<Long> productosPrevios = new ArrayList<>();
            for (DetallePedido previo : previos) {
                productosPrevios.add(previo.getProducto().getId());
            }
            if (!previos.isEmpty()) {
                detallePedidoRepository.deleteAll(previos);
            }
            guardarDetallesDelPedido(actualizado, pedido.getDetallePedidos(), productosPrevios);
        }

        return actualizado;
    }

    // Valida que el pedido solo pueda cambiar al estado inmediatamente siguiente:
    // Recibido -> Cocinando -> Enviado -> Entregado
    private void validarTransicionEstado(String estadoActual, String nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            return;
        }

        String actual = normalizarEstado(estadoActual);
        String nuevo = normalizarEstado(nuevoEstado);

        // Si se mantiene en el mismo estado, es válido
        if (actual.equalsIgnoreCase(nuevo)) {
            return;
        }

        // "Entregado" es el estado final y ya no puede cambiar
        boolean transicionValida = false;
        if (actual.equals(RECIBIDO)) {
            transicionValida = nuevo.equals(COCINANDO);
        } else if (actual.equals(COCINANDO)) {
            transicionValida = nuevo.equals(ENVIADO);
        } else if (actual.equals(ENVIADO)) {
            transicionValida = nuevo.equals(ENTREGADO);
        }

        if (!transicionValida) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transición no permitida: el pedido en estado '" + actual
                            + "' solo puede pasar al siguiente estado, no a '" + nuevo + "'.");
        }
    }

    // Deja el estado con la primera letra en mayuscula, por ejemplo "enviado" -> "Enviado"
    private String normalizarEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return RECIBIDO;
        }
        String texto = estado.trim().toLowerCase();
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    // Cuando el pedido pasa a "Enviado" el domiciliario queda ocupado.
    // Cuando deja de estar "Enviado" (por ejemplo al entregarse) vuelve a estar disponible.
    private void actualizarDisponibilidadDomiciliario(Pedido pedido, String estadoAnterior, Domiciliario domiciliarioAnterior) {
        Domiciliario domiciliario = pedido.getDomiciliario();
        boolean estabaEnviado = ENVIADO.equals(estadoAnterior);
        boolean quedaEnviado = ENVIADO.equals(pedido.getEstado());
        boolean mismoDomiciliario = domiciliarioAnterior != null && domiciliario != null
                && domiciliarioAnterior.getId_domiciliario().equals(domiciliario.getId_domiciliario());

        if (estabaEnviado && quedaEnviado && mismoDomiciliario) {
            return;
        }

        if (estabaEnviado && domiciliarioAnterior != null) {
            domiciliarioAnterior.setInformaciondisp(domiciliarioAnterior.isActivo());
            domiciliarioRepository.save(domiciliarioAnterior);
        }

        if (quedaEnviado) {
            if (domiciliario == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Para enviar el pedido debe seleccionar un domiciliario");
            }
            if (!domiciliario.isInformaciondisp()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El domiciliario seleccionado no está disponible");
            }
            domiciliario.setInformaciondisp(false);
            domiciliarioRepository.save(domiciliario);
        }
    }

    // productosPrevios son los productos que el pedido ya tenia: esos se dejan aunque ahora esten agotados
    private void guardarDetallesDelPedido(Pedido pedidoGuardado, List<DetallePedido> detallesRecibidos,
            List<Long> productosPrevios) {
        if (detallesRecibidos == null || detallesRecibidos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El pedido debe tener al menos una comida");
        }
        for (DetallePedido d : detallesRecibidos) {
            if (d.getProducto() == null || d.getProducto().getId() == null) {
                continue;
            }
            Producto producto = productoRepository.findById(d.getProducto().getId()).orElse(null);
            if (producto == null) {
                continue;
            }
            boolean yaEstabaEnElPedido = productosPrevios.contains(producto.getId());
            if (!producto.isDisponible() && !yaEstabaEnElPedido) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El producto " + producto.getNombre() + " está agotado");
            }
            DetallePedido nuevo = new DetallePedido();
            nuevo.setPedido(pedidoGuardado);
            nuevo.setProducto(producto);
            nuevo.setCantidad(d.getCantidad() > 0 ? d.getCantidad() : 1);

            // Se toman nombre y precio de la base de datos, no de lo que manda el frontend
            List<AdicionalSeleccionado> adicionales = new ArrayList<>();
            if (d.getAdicionales() != null) {
                for (AdicionalSeleccionado seleccionado : d.getAdicionales()) {
                    if (seleccionado == null || seleccionado.getId_adicional() == null) {
                        continue;
                    }
                    Adicional adicional = adicionalRepository.findById(seleccionado.getId_adicional()).orElse(null);
                    if (adicional == null) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Uno de los adicionales del pedido ya no existe");
                    }
                    if (!adicional.isDisponible() && !yaEstabaEnElPedido) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "El adicional " + adicional.getNombre() + " está agotado");
                    }
                    int cantidad = seleccionado.getCantidad() > 0 ? seleccionado.getCantidad() : 1;
                    adicionales.add(new AdicionalSeleccionado(
                            adicional.getId_adicional(),
                            adicional.getNombre(),
                            adicional.getPrecio(),
                            cantidad));
                }
            }
            nuevo.setAdicionales(adicionales);
            detallePedidoRepository.save(nuevo);
        }
    }

    // Elimina un pedido por su id y libera al domiciliario si el pedido estaba en camino
    @Override
    @Transactional
    public void deleteById(Long id) {
        Pedido pedido = findById(id);
        // Si el pedido iba en camino, el domiciliario vuelve a quedar disponible
        if (pedido != null && ENVIADO.equals(pedido.getEstado()) && pedido.getDomiciliario() != null) {
            Domiciliario domiciliario = pedido.getDomiciliario();
            domiciliario.setInformaciondisp(domiciliario.isActivo());
            domiciliarioRepository.save(domiciliario);
        }
        pedidoRepository.deleteById(id);
    }

    // Obtiene todos los pedidos realizados por un cliente especifico
    @Override
    public List<Pedido> findByClienteId(Long clienteId) {
        return pedidoRepository.findByClienteClienteId(clienteId);
    }

    // Obtiene la lista de detalles de un pedido por su id
    @Override
    public List<DetallePedido> findDetallesByPedidoId(Long pedidoId) {
        if (pedidoId == null) {
            return Collections.emptyList();
        }
        return detallePedidoRepository.findByPedido_Id_Pedido(pedidoId);
    }

    // Valida y obtiene el cliente asociado al pedido
    private Cliente buscarCliente(Pedido pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().getClienteId() == null
                || pedido.getCliente().getClienteId() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar un cliente");
        }
        return clienteRepository.findById(pedido.getCliente().getClienteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El cliente seleccionado no existe"));
    }

    // Busca el operador asignado al pedido si se envio uno
    private Operador buscarOperador(Pedido pedido) {
        if (pedido.getOperador() == null || pedido.getOperador().getId_operador() == null
                || pedido.getOperador().getId_operador() <= 0) {
            return null;
        }
        return operadorRepository.findById(pedido.getOperador().getId_operador())
                .orElse(null);
    }

    // Busca el domiciliario asignado al pedido si se envio uno
    private Domiciliario buscarDomiciliario(Pedido pedido) {
        if (pedido.getDomiciliario() == null || pedido.getDomiciliario().getId_domiciliario() == null
                || pedido.getDomiciliario().getId_domiciliario() <= 0) {
            return null;
        }
        return domiciliarioRepository.findById(pedido.getDomiciliario().getId_domiciliario())
                .orElse(null);
    }

    // Verifica que el cliente tenga su cuenta activa para poder hacer pedidos
    private void validarClienteActivo(Cliente cliente) {
        if (!cliente.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El cliente está desactivado y no puede hacer pedidos");
        }
    }

    // Verifica que el domiciliario seleccionado se encuentre activo
    private void validarDomiciliarioActivo(Domiciliario domiciliario) {
        if (domiciliario != null && !domiciliario.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El domiciliario seleccionado está inactivo");
        }
    }
}

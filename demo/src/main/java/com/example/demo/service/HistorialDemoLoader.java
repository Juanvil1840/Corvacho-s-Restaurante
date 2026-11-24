package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

// DATOS DE DEMOSTRACION: genera un historial de pedidos ya entregados de los ultimos 180 dias (asi el filtro de 90 dias tambien tiene periodo anterior para comparar)
// para que el dashboard de reportes tenga informacion realista en la presentacion.
// Solo se activa con la propiedad app.demo.historial=true y solo si la base tiene pocos pedidos.
// Usa una semilla fija, asi los datos salen iguales cada vez que se crea la base.
@Component
@Order(3)
@ConditionalOnProperty(name = "app.demo.historial", havingValue = "true")
public class HistorialDemoLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(HistorialDemoLoader.class);

    private static final int DIAS_DE_HISTORIAL = 180;
    // Si ya hay mas pedidos que esto, se asume que el historial ya existe y no se genera otra vez
    private static final long MAXIMO_PEDIDOS_EXISTENTES = 50;

    // Peso de cada hora del dia (0-23): picos al almuerzo y a la cena
    private static final int[] PESO_HORA = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 9, 12, 8, 3, 2, 2, 5, 10, 13, 9, 4, 1 };

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final AdicionalRepository adicionalRepository;
    private final OperadorRepository operadorRepository;
    private final DomiciliarioRepository domiciliarioRepository;

    private final Random random = new Random(2026);

    public HistorialDemoLoader(PedidoRepository pedidoRepository, DetallePedidoRepository detallePedidoRepository,
            ClienteRepository clienteRepository, ProductoRepository productoRepository,
            AdicionalRepository adicionalRepository, OperadorRepository operadorRepository,
            DomiciliarioRepository domiciliarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.adicionalRepository = adicionalRepository;
        this.operadorRepository = operadorRepository;
        this.domiciliarioRepository = domiciliarioRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (pedidoRepository.count() > MAXIMO_PEDIDOS_EXISTENTES) {
            return;
        }
        List<Cliente> clientes = clienteRepository.findAll().stream().filter(Cliente::isActivo).toList();
        List<Producto> productos = productoRepository.findAll().stream().filter(Producto::isDisponible).toList();
        List<Operador> operadores = operadorRepository.findAll();
        List<Domiciliario> domiciliarios = domiciliarioRepository.findAll();
        if (clientes.isEmpty() || productos.isEmpty()) {
            return;
        }
        Map<Long, List<Adicional>> adicionalesPorCategoria = adicionalRepository.findAll().stream()
                .filter(Adicional::isDisponible)
                .filter(a -> a.getCategoria() != null)
                .collect(Collectors.groupingBy(a -> a.getCategoria().getId()));

        // Cada plato y cada cliente tiene una "popularidad" distinta para que los rankings sean variados
        int[] pesoProducto = productos.stream().mapToInt(p -> 1 + random.nextInt(12)).toArray();
        int[] pesoCliente = clientes.stream().mapToInt(c -> 1 + random.nextInt(8)).toArray();

        List<Pedido> pedidos = new ArrayList<>();
        List<DetallePedido> detalles = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (int diasAtras = DIAS_DE_HISTORIAL; diasAtras >= 1; diasAtras--) {
            LocalDate dia = hoy.minusDays(diasAtras);
            int cantidadPedidos = pedidosDelDia(dia, diasAtras);
            for (int i = 0; i < cantidadPedidos; i++) {
                LocalDateTime creado = dia.atTime(elegir(PESO_HORA), random.nextInt(60));
                Pedido pedido = new Pedido("Entregado", creado, creado.plusMinutes(25 + random.nextInt(35)),
                        clientes.get(elegir(pesoCliente)),
                        operadores.isEmpty() ? null : operadores.get(random.nextInt(operadores.size())),
                        domiciliarios.isEmpty() ? null : domiciliarios.get(random.nextInt(domiciliarios.size())));
                pedidos.add(pedido);
                detalles.addAll(lineasDelPedido(pedido, productos, pesoProducto, adicionalesPorCategoria));
            }
        }
        pedidoRepository.saveAll(pedidos);
        detallePedidoRepository.saveAll(detalles);
        log.info("Historial de demostracion: {} pedidos y {} detalles de los ultimos {} dias",
                pedidos.size(), detalles.size(), DIAS_DE_HISTORIAL);
    }

    // Mas pedidos los fines de semana y una tendencia de crecimiento a lo largo del historial
    private int pedidosDelDia(LocalDate dia, int diasAtras) {
        double base = 4 + 4.0 * (DIAS_DE_HISTORIAL - diasAtras) / DIAS_DE_HISTORIAL;
        double factorDia = switch (dia.getDayOfWeek()) {
            case FRIDAY -> 1.5;
            case SATURDAY -> 1.8;
            case SUNDAY -> 1.4;
            case MONDAY -> 0.7;
            default -> 1.0;
        };
        long cantidad = Math.round(base * factorDia + random.nextGaussian() * 1.5);
        return (int) Math.max(1, cantidad);
    }

    // 1 a 3 lineas por pedido; a veces con un adicional de la misma categoria del plato
    private List<DetallePedido> lineasDelPedido(Pedido pedido, List<Producto> productos, int[] pesoProducto,
            Map<Long, List<Adicional>> adicionalesPorCategoria) {
        List<DetallePedido> lineas = new ArrayList<>();
        int numeroLineas = 1 + random.nextInt(3);
        for (int i = 0; i < numeroLineas; i++) {
            Producto producto = productos.get(elegir(pesoProducto));
            DetallePedido detalle = new DetallePedido(random.nextInt(10) < 8 ? 1 : 2, producto, pedido, null);
            List<Adicional> opciones = producto.getCategoria() != null
                    ? adicionalesPorCategoria.getOrDefault(producto.getCategoria().getId(), List.of())
                    : List.of();
            if (!opciones.isEmpty() && random.nextInt(100) < 40) {
                Adicional adicional = opciones.get(random.nextInt(opciones.size()));
                detalle.getAdicionales().add(new AdicionalSeleccionado(
                        adicional.getId_adicional(), adicional.getNombre(), adicional.getPrecio(), 1));
            }
            lineas.add(detalle);
        }
        return lineas;
    }

    // Elige una posicion al azar respetando los pesos (mas peso = mas probable)
    private int elegir(int[] pesos) {
        int total = 0;
        for (int peso : pesos) {
            total += peso;
        }
        int valor = random.nextInt(total);
        for (int i = 0; i < pesos.length; i++) {
            valor -= pesos[i];
            if (valor < 0) {
                return i;
            }
        }
        return pesos.length - 1;
    }
}

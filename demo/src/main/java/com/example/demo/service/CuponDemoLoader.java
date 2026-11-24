package com.example.demo.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Cupon;
import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Pedido;
import com.example.demo.repository.CuponRepository;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.repository.PedidoRepository;

// DATOS DE DEMOSTRACION: crea unos cupones de ejemplo la primera vez (cuando no hay ninguno).
// Si app.demo.historial=true, ademas marca algunos pedidos ya entregados del historial como pagados con cupon,
// para que el dashboard de reportes muestre cuanto se ha descontado. Usa una semilla fija.
@Component
@Order(4)
public class CuponDemoLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CuponDemoLoader.class);

    // Porcentaje aproximado de pedidos del historial que usaron cupon
    private static final int PROBABILIDAD_CUPON = 12;

    private final CuponRepository cuponRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final CuponService cuponService;
    private final boolean conHistorial;
    private final Random random = new Random(2026);

    public CuponDemoLoader(CuponRepository cuponRepository, PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository, CuponService cuponService,
            @Value("${app.demo.historial:false}") boolean conHistorial) {
        this.cuponRepository = cuponRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.cuponService = cuponService;
        this.conHistorial = conHistorial;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (cuponRepository.count() > 0) {
            asegurarCuponesRuleta();
            return;
        }
        LocalDate hoy = LocalDate.now();
        Cupon bienvenida = new Cupon("BIENVENIDA10", "10% de descuento de bienvenida", Cupon.PORCENTAJE, 10, 0,
                500, hoy.plusDays(60), true);
        Cupon corvacho = new Cupon("CORVACHO5000", "$5.000 menos en compras desde $40.000", Cupon.VALOR_FIJO, 5000,
                40000, null, null, true);
        Cupon finde = new Cupon("FINDE15", "15% los fines de semana en compras desde $60.000", Cupon.PORCENTAJE, 15,
                60000, null, hoy.plusDays(30), true);
        Cupon halloween = new Cupon("HALLOWEEN20", "Promoción de Halloween", Cupon.PORCENTAJE, 20, 30000,
                null, hoy.minusDays(21), true);
        Cupon primeros = new Cupon("PRIMEROS50", "$10.000 para los primeros 50 pedidos", Cupon.VALOR_FIJO, 10000,
                35000, 50, null, true);
        List<Cupon> cupones = cuponRepository.saveAll(List.of(bienvenida, corvacho, finde, halloween, primeros));
        log.info("Cupones de demostracion creados: {}", cupones.size());

        if (conHistorial) {
            asignarAlHistorial(bienvenida, corvacho, finde, halloween, primeros, hoy);
        }
        asegurarCuponesRuleta();
    }

    // Premios de la ruleta de bienvenida: el cliente nuevo que gana se lleva uno de estos cupones.
    // El backend solo los acepta en el primer pedido del cliente (ver PedidoServiceImpl)
    private void asegurarCuponesRuleta() {
        crearSiNoExiste(new Cupon(Cupon.CODIGO_RULETA_10, "Premio de la ruleta: 10% en tu primer pedido",
                Cupon.PORCENTAJE, 10, 0, null, null, true));
        crearSiNoExiste(new Cupon(Cupon.CODIGO_RULETA_5, "Premio de la ruleta: 5% en tu primer pedido",
                Cupon.PORCENTAJE, 5, 0, null, null, true));
    }

    private void crearSiNoExiste(Cupon cupon) {
        if (!cuponRepository.existsByCodigoIgnoreCase(cupon.getCodigo())) {
            cuponRepository.save(cupon);
        }
    }

    // Algunos pedidos entregados de antes de hoy quedan como pagados con un cupon que era valido ese dia
    private void asignarAlHistorial(Cupon bienvenida, Cupon corvacho, Cupon finde, Cupon halloween, Cupon primeros,
            LocalDate hoy) {
        LocalDateTime inicio = hoy.minusDays(366).atStartOfDay();
        LocalDateTime fin = hoy.atStartOfDay();
        Map<Long, List<DetallePedido>> detallesPorPedido = detallePedidoRepository.findDePedidosCreadosEntre(inicio, fin)
                .stream()
                .collect(Collectors.groupingBy(d -> d.getPedido().getId_Pedido()));
        List<Pedido> pedidos = pedidoRepository.findCreadosEntre(inicio, fin).stream()
                .filter(p -> "Entregado".equalsIgnoreCase(p.getEstado()) && p.getCupon() == null)
                .sorted(Comparator.comparing(Pedido::getFechaCreacion))
                .toList();

        List<Pedido> modificados = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            if (random.nextInt(100) >= PROBABILIDAD_CUPON) {
                continue;
            }
            LocalDate dia = pedido.getFechaCreacion().toLocalDate();
            double subtotal = PreciosPedido.subtotal(detallesPorPedido.get(pedido.getId_Pedido()));

            List<Cupon> posibles = new ArrayList<>(List.of(bienvenida, bienvenida, corvacho));
            boolean finDeSemana = dia.getDayOfWeek().getValue() >= DayOfWeek.FRIDAY.getValue();
            if (finDeSemana && !dia.isBefore(hoy.minusDays(60))) {
                posibles.add(finde);
                posibles.add(finde);
            }
            if (!dia.isAfter(halloween.getFechaVencimiento()) && !dia.isBefore(halloween.getFechaVencimiento().minusDays(30))) {
                posibles.add(halloween);
                posibles.add(halloween);
                posibles.add(halloween);
            }
            if (primeros.getUsos() < primeros.getUsosMaximos()) {
                posibles.add(primeros);
            }
            Cupon cupon = posibles.get(random.nextInt(posibles.size()));
            if (subtotal < cupon.getCompraMinima()) {
                continue;
            }
            pedido.setCupon(cupon);
            pedido.setDescuento(cuponService.calcularDescuento(cupon, subtotal));
            cupon.setUsos(cupon.getUsos() + 1);
            modificados.add(pedido);
        }
        pedidoRepository.saveAll(modificados);
        cuponRepository.saveAll(List.of(bienvenida, corvacho, finde, halloween, primeros));
        log.info("Historial de demostracion: {} pedidos marcados con cupon", modificados.size());
    }
}

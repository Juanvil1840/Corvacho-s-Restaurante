package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.CalificacionProducto;
import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Pedido;
import com.example.demo.entities.Resena;
import com.example.demo.repository.CalificacionProductoRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ResenaRepository;

// DATOS DE DEMOSTRACION: si app.demo.historial=true y todavia no hay reseñas, califica una parte de los
// pedidos entregados del historial (reseña del pedido y estrellas de sus productos) para que el panel de
// reseñas y el promedio de estrellas del menu no salgan vacios. Usa una semilla fija.
@Component
@Order(5)
public class ResenaDemoLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ResenaDemoLoader.class);

    // De los pedidos entregados mas recientes, cuantos se revisan y que porcentaje deja reseña
    private static final int PEDIDOS_RECIENTES = 160;
    private static final int PROBABILIDAD_RESENA = 30;
    private static final int PROBABILIDAD_ESTRELLAS_PRODUCTO = 45;

    private static final String[][] COMENTARIOS = {
            {}, // 0 (sin uso)
            {"La comida llegó fría y tarde.", "No era lo que esperaba."},
            {"El pedido tardó bastante.", "Faltó un adicional que pedí."},
            {"Bien, aunque la entrega se demoró un poco.", "Buen sabor, porción algo pequeña."},
            {"Muy rico, llegó a tiempo.", "Buena presentación y buen sabor.", "Todo bien, repetiría."},
            {"¡Excelente! La pasta estaba deliciosa.", "Llegó rapidísimo y caliente, 10/10.",
                    "La mejor burrata que he probado.", "Servicio impecable, muy recomendado.",
                    "Perfecto para una cena en casa."}
    };

    private final ResenaRepository resenaRepository;
    private final CalificacionProductoRepository calificacionProductoRepository;
    private final PedidoRepository pedidoRepository;
    private final boolean conHistorial;
    private final Random random = new Random(2026);

    public ResenaDemoLoader(ResenaRepository resenaRepository,
            CalificacionProductoRepository calificacionProductoRepository, PedidoRepository pedidoRepository,
            @Value("${app.demo.historial:false}") boolean historial,
            @Value("${app.demo.resenas:true}") boolean resenas) {
        this.resenaRepository = resenaRepository;
        this.calificacionProductoRepository = calificacionProductoRepository;
        this.pedidoRepository = pedidoRepository;
        // Las pruebas de integracion de reseñas lo apagan con app.demo.resenas=false para partir sin reseñas
        this.conHistorial = historial && resenas;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!conHistorial || resenaRepository.count() > 0 || calificacionProductoRepository.count() > 0) {
            return;
        }
        List<Pedido> entregados = pedidoRepository.findAll().stream()
                .filter(p -> "Entregado".equalsIgnoreCase(p.getEstado()) && p.getFechaCreacion() != null)
                .sorted(Comparator.comparing(Pedido::getFechaCreacion).reversed())
                .limit(PEDIDOS_RECIENTES)
                .toList();

        int resenas = 0;
        int estrellas = 0;
        for (Pedido pedido : entregados) {
            LocalDateTime fecha = (pedido.getFechaEntrega() != null ? pedido.getFechaEntrega()
                    : pedido.getFechaCreacion()).plusMinutes(20 + random.nextInt(90));

            if (random.nextInt(100) < PROBABILIDAD_RESENA) {
                int puntuacion = puntuacionAleatoria();
                Resena resena = new Resena();
                resena.setPedido(pedido);
                resena.setPuntuacion(puntuacion);
                // Algunas reseñas son solo estrellas, sin comentario
                if (random.nextInt(100) < 75) {
                    String[] opciones = COMENTARIOS[puntuacion];
                    resena.setComentario(opciones[random.nextInt(opciones.length)]);
                }
                resena.setFechaCreacion(fecha);
                resenaRepository.save(resena);
                resenas++;
            }

            if (random.nextInt(100) < PROBABILIDAD_ESTRELLAS_PRODUCTO && pedido.getDetallePedidos() != null) {
                Set<Long> yaCalificados = new HashSet<>();
                for (DetallePedido detalle : pedido.getDetallePedidos()) {
                    if (detalle.getProducto() == null || !yaCalificados.add(detalle.getProducto().getId())) {
                        continue;
                    }
                    CalificacionProducto calificacion = new CalificacionProducto();
                    calificacion.setPedido(pedido);
                    calificacion.setProducto(detalle.getProducto());
                    calificacion.setPuntuacion(puntuacionAleatoria());
                    calificacion.setFechaCreacion(fecha);
                    calificacionProductoRepository.save(calificacion);
                    estrellas++;
                }
            }
        }
        log.info("Reseñas de demostracion: {} de pedidos y {} de productos", resenas, estrellas);
    }

    // La mayoria de clientes quedan contentos: 5★ 45%, 4★ 35%, 3★ 12%, 2★ 6%, 1★ 2%
    private int puntuacionAleatoria() {
        int r = random.nextInt(100);
        if (r < 45) return 5;
        if (r < 80) return 4;
        if (r < 92) return 3;
        if (r < 98) return 2;
        return 1;
    }
}

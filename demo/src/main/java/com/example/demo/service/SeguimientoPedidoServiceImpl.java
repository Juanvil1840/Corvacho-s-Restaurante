package com.example.demo.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.dto.SeguimientoPedidoDTO;
import com.example.demo.entities.Pedido;
import com.example.demo.repository.PedidoRepository;

// Mantiene los canales abiertos por pedido y les avisa cuando el pedido cambia.
// Cada navegador que sigue un pedido tiene su propio SseEmitter (una respuesta HTTP que queda abierta).
@Service
public class SeguimientoPedidoServiceImpl implements SeguimientoPedidoService {

    private static final Logger log = LoggerFactory.getLogger(SeguimientoPedidoServiceImpl.class);

    // El canal se cierra a los 30 minutos; el navegador se vuelve a conectar solo
    private static final long DURACION_CANAL_MS = 30 * 60 * 1000L;
    // Si todavia no hay entregas para calcular, se usa este tiempo estimado
    private static final long MINUTOS_POR_DEFECTO = 40;
    // Dias hacia atras que se usan para calcular el tiempo promedio de entrega
    private static final int DIAS_PARA_PROMEDIO = 30;
    private static final String EVENTO_ESTADO = "estado";

    private final PedidoRepository pedidoRepository;

    // pedidoId -> navegadores conectados (estructuras seguras porque varios hilos las usan a la vez)
    private final Map<Long, Set<SseEmitter>> canales = new ConcurrentHashMap<>();

    public SeguimientoPedidoServiceImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SeguimientoPedidoDTO consultar(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El pedido no existe"));
        return SeguimientoPedidoDTO.fromEntity(pedido, minutosPromedioEntrega());
    }

    @Override
    public SseEmitter suscribir(Long pedidoId) {
        SeguimientoPedidoDTO actual = consultar(pedidoId);

        SseEmitter emisor = new SseEmitter(DURACION_CANAL_MS);
        canales.computeIfAbsent(pedidoId, id -> ConcurrentHashMap.newKeySet()).add(emisor);
        emisor.onCompletion(() -> quitar(pedidoId, emisor));
        emisor.onTimeout(() -> quitar(pedidoId, emisor));
        emisor.onError(error -> quitar(pedidoId, emisor));

        // Lo primero que recibe el navegador es el estado actual
        enviar(pedidoId, emisor, actual);
        return emisor;
    }

    // Se ejecuta DESPUES de que el cambio del pedido quedo guardado (commit) en la base de datos,
    // asi nunca se le muestra al cliente un estado que al final no se guardo
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void alActualizarPedido(PedidoActualizadoEvent evento) {
        Set<SseEmitter> conectados = canales.get(evento.pedidoId());
        if (conectados == null || conectados.isEmpty()) {
            return;
        }
        SeguimientoPedidoDTO seguimiento = consultar(evento.pedidoId());
        conectados.forEach(emisor -> enviar(evento.pedidoId(), emisor, seguimiento));
    }

    // Cada 20 segundos se manda un comentario vacio para que proxies y navegadores no cierren el canal
    @Scheduled(fixedRate = 20_000)
    public void mantenerVivos() {
        canales.forEach((pedidoId, emisores) -> emisores.forEach(emisor -> {
            try {
                emisor.send(SseEmitter.event().comment("ping"));
            } catch (IOException | IllegalStateException e) {
                quitar(pedidoId, emisor);
            }
        }));
    }

    private void enviar(Long pedidoId, SseEmitter emisor, SeguimientoPedidoDTO seguimiento) {
        try {
            emisor.send(SseEmitter.event().name(EVENTO_ESTADO).data(seguimiento));
        } catch (IOException | IllegalStateException e) {
            // El navegador ya cerro la pestaña: se descarta ese canal
            log.debug("Canal del pedido {} cerrado: {}", pedidoId, e.getMessage());
            quitar(pedidoId, emisor);
        }
    }

    private void quitar(Long pedidoId, SseEmitter emisor) {
        canales.computeIfPresent(pedidoId, (id, emisores) -> {
            emisores.remove(emisor);
            return emisores.isEmpty() ? null : emisores;
        });
    }

    // Tiempo promedio real de entrega de los ultimos 30 dias, redondeado a minutos
    private long minutosPromedioEntrega() {
        LocalDateTime desde = LocalDateTime.now().minusDays(DIAS_PARA_PROMEDIO);
        return Math.round(TiemposEntrega.promedioMinutos(pedidoRepository.findEntregadosDesde(desde))
                .orElse(MINUTOS_POR_DEFECTO));
    }
}

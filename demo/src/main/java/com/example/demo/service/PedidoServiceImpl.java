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

import com.example.demo.entities.AdicionalSeleccionado;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Domiciliario;
import com.example.demo.entities.Operador;
import com.example.demo.entities.Pedido;
import com.example.demo.errors.PedidoNotFoundException;
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.OperadorRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final OperadorRepository operadorRepository;
    private final DomiciliarioRepository domiciliarioRepository;
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final AdicionalRepository adicionalRepository;

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

    @Override
    public Collection<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public Collection<Pedido> findActivos() {
        return pedidoRepository.findByEstadoNotIgnoreCase("Entregado");
    }

    @Override
    public Pedido findById(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
    }

    @Override
    @Transactional
    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        pedido.setId_Pedido(null);
        pedido.setCliente(buscarCliente(pedido));
        pedido.setOperador(buscarOperador(pedido));
        pedido.setDomiciliario(buscarDomiciliario(pedido));
        validarDomiciliarioActivo(pedido.getDomiciliario());
        actualizarDisponibilidadDomiciliario(pedido, null, null);
        if (pedido.getFechaCreacion() == null) {
            pedido.setFechaCreacion(LocalDateTime.now());
        }
        if (pedido.getEstado() == null || pedido.getEstado().trim().isEmpty()) {
            pedido.setEstado("Recibido");
        } else {
            String normalizado = normalizarEstado(pedido.getEstado());
            if (!normalizado.equals("Recibido")) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Un pedido nuevo debe iniciarse obligatoriamente en estado 'Recibido'.");
            }
            pedido.setEstado(normalizado);
        }

        List<DetallePedido> detallesRecibidos = pedido.getDetallePedidos();
        pedido.setDetallePedidos(null);

        Pedido guardado = pedidoRepository.save(pedido);
        guardarDetallesDelPedido(guardado, detallesRecibidos);

        return guardado;
    }

    @Override
    @Transactional
    public Pedido actualizarPedido(Long id, Pedido pedido) {
        Pedido existente = findById(id);
        String estadoAnterior = existente.getEstado();
        Domiciliario domiciliarioAnterior = existente.getDomiciliario();

        if (pedido.getEstado() != null && !pedido.getEstado().trim().isEmpty()) {
            validarTransicionEstado(estadoAnterior, pedido.getEstado());
            existente.setEstado(normalizarEstado(pedido.getEstado()));
        }

        existente.setCliente(buscarCliente(pedido));
        existente.setOperador(buscarOperador(pedido));
        Domiciliario domiciliarioSolicitado = buscarDomiciliario(pedido);
        boolean conservaDomiciliarioActual = existente.getDomiciliario() != null
                && domiciliarioSolicitado != null
                && existente.getDomiciliario().getId_domiciliario()
                        .equals(domiciliarioSolicitado.getId_domiciliario());
        if (!conservaDomiciliarioActual) {
            validarDomiciliarioActivo(domiciliarioSolicitado);
        }
        existente.setDomiciliario(domiciliarioSolicitado);
        if (pedido.getFechaCreacion() != null) {
            existente.setFechaCreacion(pedido.getFechaCreacion());
        }
        existente.setFechaEntrega(pedido.getFechaEntrega());
        if ("Entregado".equalsIgnoreCase(existente.getEstado()) && existente.getFechaEntrega() == null) {
            existente.setFechaEntrega(LocalDateTime.now());
        }

        actualizarDisponibilidadDomiciliario(existente, estadoAnterior, domiciliarioAnterior);

        Pedido actualizado = pedidoRepository.save(existente);

        if (pedido.getDetallePedidos() != null) {
            List<DetallePedido> previos = detallePedidoRepository.findByPedido_Id_Pedido(id);
            if (!previos.isEmpty()) {
                detallePedidoRepository.deleteAll(previos);
            }
            guardarDetallesDelPedido(actualizado, pedido.getDetallePedidos());
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

        boolean transicionValida = switch (actual) {
            case "Recibido" -> nuevo.equals("Cocinando");
            case "Cocinando" -> nuevo.equals("Enviado");
            case "Enviado" -> nuevo.equals("Entregado");
            default -> false; // "Entregado" o cualquier otro estado final no puede cambiar
        };

        if (!transicionValida) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Transición no permitida: el pedido en estado '%s' solo puede avanzar al siguiente estado secuencial, no a '%s'.", actual, nuevo));
        }
    }

    private String normalizarEstado(String estado) {
        if (estado == null) return "Recibido";
        String s = estado.trim().toLowerCase();
        return switch (s) {
            case "recibido", "pendiente" -> "Recibido";
            case "cocinando" -> "Cocinando";
            case "enviado" -> "Enviado";
            case "entregado" -> "Entregado";
            case "cancelado" -> "Cancelado";
            default -> estado.trim();
        };
    }

    // Cuando el pedido pasa a "Enviado" el domiciliario queda ocupado.
    // Cuando deja de estar "Enviado" (entregado, cancelado...) vuelve a estar disponible.
    private void actualizarDisponibilidadDomiciliario(Pedido pedido, String estadoAnterior, Domiciliario domiciliarioAnterior) {
        Domiciliario domiciliario = pedido.getDomiciliario();
        boolean estabaEnviado = "Enviado".equalsIgnoreCase(estadoAnterior);
        boolean quedaEnviado = "Enviado".equalsIgnoreCase(pedido.getEstado());
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

    private void guardarDetallesDelPedido(Pedido pedidoGuardado, List<DetallePedido> detallesRecibidos) {
        if (detallesRecibidos == null || detallesRecibidos.isEmpty()) {
            return;
        }
        for (DetallePedido d : detallesRecibidos) {
            if (d.getProducto() != null && d.getProducto().getId() != null) {
                productoRepository.findById(d.getProducto().getId()).ifPresent(prod -> {
                    DetallePedido nuevo = new DetallePedido();
                    nuevo.setPedido(pedidoGuardado);
                    nuevo.setProducto(prod);
                    nuevo.setCantidad(d.getCantidad() > 0 ? d.getCantidad() : 1);

                    List<AdicionalSeleccionado> listaAdicionales = new ArrayList<>();

                    if (d.getAdicionales() != null && !d.getAdicionales().isEmpty()) {
                        for (AdicionalSeleccionado as : d.getAdicionales()) {
                            if (as != null && as.getId_adicional() != null && as.getId_adicional() > 0) {
                                adicionalRepository.findById(as.getId_adicional()).ifPresent(adic -> {
                                    int cantAdic = as.getCantidad() > 0 ? as.getCantidad() : 1;
                                    listaAdicionales.add(new AdicionalSeleccionado(
                                            adic.getId_adicional(),
                                            adic.getNombre(),
                                            adic.getPrecio(),
                                            cantAdic
                                    ));
                                    if (nuevo.getAdicional() == null) {
                                        nuevo.setAdicional(adic);
                                    }
                                });
                            }
                        }
                    } else if (d.getAdicional() != null && d.getAdicional().getId_adicional() != null && d.getAdicional().getId_adicional() > 0) {
                        adicionalRepository.findById(d.getAdicional().getId_adicional()).ifPresent(adic -> {
                            nuevo.setAdicional(adic);
                            listaAdicionales.add(new AdicionalSeleccionado(
                                    adic.getId_adicional(),
                                    adic.getNombre(),
                                    adic.getPrecio(),
                                    1
                            ));
                        });
                    }

                    nuevo.setAdicionales(listaAdicionales);
                    detallePedidoRepository.save(nuevo);
                });
            }
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        pedidoRepository.deleteById(id);
    }

    @Override
    public List<Pedido> findByClienteId(Long clienteId) {
        return pedidoRepository.findByClienteClienteId(clienteId);
    }

    @Override
    public List<DetallePedido> findDetallesByPedidoId(Long pedidoId) {
        if (pedidoId == null) {
            return Collections.emptyList();
        }
        return detallePedidoRepository.findByPedido_Id_Pedido(pedidoId);
    }

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

    private Operador buscarOperador(Pedido pedido) {
        if (pedido.getOperador() == null || pedido.getOperador().getId_operador() == null
                || pedido.getOperador().getId_operador() <= 0) {
            return null;
        }
        return operadorRepository.findById(pedido.getOperador().getId_operador())
                .orElse(null);
    }

    private Domiciliario buscarDomiciliario(Pedido pedido) {
        if (pedido.getDomiciliario() == null || pedido.getDomiciliario().getId_domiciliario() == null
                || pedido.getDomiciliario().getId_domiciliario() <= 0) {
            return null;
        }
        return domiciliarioRepository.findById(pedido.getDomiciliario().getId_domiciliario())
                .orElse(null);
    }

    private void validarDomiciliarioActivo(Domiciliario domiciliario) {
        if (domiciliario != null && !domiciliario.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El domiciliario seleccionado está inactivo");
        }
    }
}

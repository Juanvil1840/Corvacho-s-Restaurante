package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.AdicionalPedidoDTO;
import com.example.demo.dto.CarritoDTO;
import com.example.demo.dto.ProductoCarritoDTO;
import com.example.demo.entities.Adicional;
import com.example.demo.entities.AdicionalSeleccionado;
import com.example.demo.entities.Carrito;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Producto;
import com.example.demo.entities.ProductoCarrito;
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.ProductoRepository;

@Service
public class CarritoServiceImpl implements CarritoService{

    private final CarritoRepository carritoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final AdicionalRepository adicionalRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository, ClienteRepository clienteRepository,
            ProductoRepository productoRepository, AdicionalRepository adicionalRepository) {
        this.carritoRepository = carritoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.adicionalRepository = adicionalRepository;
    }

    @Override
    public Collection<Carrito> findAll() {
        return carritoRepository.findAll();
    }

    @Override
    public Carrito findById(Long id) {
       return carritoRepository.findById(id).orElse(null);
    }

    @Override
    public Carrito guardar(Carrito carrito) {
        return carritoRepository.save(carrito);
    }

    @Override
    public void deleteById(Long id) {
        carritoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CarritoDTO obtenerPorCliente(Long clienteId) {
        return carritoRepository.findByClienteClienteId(clienteId)
                .map(carrito -> convertir(carrito.getProductoCarritos()))
                .orElseGet(() -> new CarritoDTO());
    }

    @Override
    @Transactional
    public CarritoDTO actualizarPorCliente(Long clienteId, CarritoDTO datos) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        Carrito carrito = carritoRepository.findByClienteClienteId(clienteId)
                .orElseGet(() -> new Carrito(cliente));
        carrito.getProductoCarritos().clear();

        for (ProductoCarritoDTO item : datos.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no encontrado"));
            if (!producto.isDisponible()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El producto " + producto.getNombre() + " no está disponible");
            }

            ProductoCarrito productoCarrito = new ProductoCarrito(item.getCantidad(), producto, carrito);
            List<AdicionalSeleccionado> adicionales = new ArrayList<>();
            for (AdicionalPedidoDTO adicionalSeleccionado : item.getAdicionales()) {
                Adicional adicional = adicionalRepository.findById(adicionalSeleccionado.getId_adicional())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Adicional no encontrado"));
                if (!adicional.isDisponible()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "El adicional " + adicional.getNombre() + " no está disponible");
                }
                if (!adicional.getCategoria().getId().equals(producto.getCategoria().getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "El adicional no corresponde a este producto");
                }
                adicionales.add(new AdicionalSeleccionado(adicional.getId_adicional(), adicional.getNombre(),
                        adicional.getPrecio(), adicionalSeleccionado.getCantidad()));
            }
            productoCarrito.setAdicionales(adicionales);
            carrito.getProductoCarritos().add(productoCarrito);
        }

        carritoRepository.save(carrito);
        return convertir(carrito.getProductoCarritos());
    }

    @Override
    @Transactional
    public void vaciarPorCliente(Long clienteId) {
        carritoRepository.findByClienteClienteId(clienteId).ifPresent(carrito -> {
            carrito.getProductoCarritos().clear();
            carritoRepository.save(carrito);
        });
    }

    private CarritoDTO convertir(List<ProductoCarrito> productos) {
        List<ProductoCarritoDTO> items = new ArrayList<>();
        for (ProductoCarrito item : productos) {
            List<AdicionalPedidoDTO> adicionales = new ArrayList<>();
            for (AdicionalSeleccionado adicional : item.getAdicionales()) {
                adicionales.add(AdicionalPedidoDTO.fromEntity(adicional));
            }
            items.add(new ProductoCarritoDTO(
                    item.getProducto().getId(),
                    item.getProducto().getNombre(),
                    item.getProducto().getPrecio(),
                    item.getProducto().getImagen(),
                    item.getCantidadP(),
                    adicionales));
        }
        return new CarritoDTO(items);
    }
}

package com.example.demo.service;

import java.util.List;

import com.example.demo.entities.Cupon;

// Servicio para los cupones de descuento: administracion y validacion al hacer un pedido
public interface CuponService {

    List<Cupon> findAll();

    Cupon findById(Long id);

    Cupon crear(Cupon cupon);

    Cupon actualizar(Long id, Cupon cupon);

    // Elimina el cupon; si ya se uso en algun pedido lanza un error (en ese caso se desactiva)
    void eliminar(Long id);

    // Busca el cupon por codigo y verifica que se pueda usar en una compra de ese valor.
    // Si no se puede, lanza un error 400 con el motivo para mostrarselo al cliente
    Cupon validar(String codigo, double subtotal);

    // Valor que descuenta el cupon sobre ese subtotal (nunca mas que el subtotal)
    double calcularDescuento(Cupon cupon, double subtotal);

    // Suma un uso al cupon
    void registrarUso(Cupon cupon);
}

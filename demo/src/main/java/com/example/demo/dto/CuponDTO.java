package com.example.demo.dto;

import java.time.LocalDate;

import com.example.demo.entities.Cupon;
import com.example.demo.service.CuponServiceImpl;

// Cupon como lo ve el administrador, con su estado calculado (Activo, Inactivo, Vencido o Agotado)
public record CuponDTO(
        Long id_cupon,
        String codigo,
        String descripcion,
        String tipo,
        double valor,
        double compraMinima,
        Integer usosMaximos,
        int usos,
        LocalDate fechaVencimiento,
        boolean activo,
        String estado) {

    public static CuponDTO fromEntity(Cupon cupon, LocalDate hoy) {
        if (cupon == null) {
            return null;
        }
        return new CuponDTO(
                cupon.getId_cupon(),
                cupon.getCodigo(),
                cupon.getDescripcion(),
                cupon.getTipo(),
                cupon.getValor(),
                cupon.getCompraMinima(),
                cupon.getUsosMaximos(),
                cupon.getUsos(),
                cupon.getFechaVencimiento(),
                cupon.isActivo(),
                CuponServiceImpl.estado(cupon, hoy));
    }
}

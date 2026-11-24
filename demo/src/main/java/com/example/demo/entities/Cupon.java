package com.example.demo.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Cupon de descuento que el administrador crea y el cliente escribe en el carrito
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cupon {

    // Tipos de descuento
    public static final String PORCENTAJE = "PORCENTAJE";

    // Cupones que entrega la ruleta de bienvenida (solo sirven en el primer pedido del cliente)
    public static final String CODIGO_RULETA_10 = "RULETA10";
    public static final String CODIGO_RULETA_5 = "RULETA5";

    public boolean esPremioRuleta() {
        return CODIGO_RULETA_10.equalsIgnoreCase(codigo) || CODIGO_RULETA_5.equalsIgnoreCase(codigo);
    }
    public static final String VALOR_FIJO = "VALOR_FIJO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_cupon;

    // Lo que escribe el cliente, por ejemplo "BIENVENIDA10". Se guarda en mayusculas
    @Column(length = 20, nullable = false, unique = true)
    private String codigo;

    @Column(length = 120)
    private String descripcion;

    // PORCENTAJE (valor = 10 es 10%) o VALOR_FIJO (valor = 5000 son $5.000)
    @Column(length = 20, nullable = false)
    private String tipo;

    @Column(nullable = false)
    private double valor;

    // Valor minimo de la compra (antes del descuento) para poder usarlo; 0 = sin minimo
    @Column(nullable = false)
    private double compraMinima;

    // Cuantas veces se puede usar en total; null = sin limite
    private Integer usosMaximos;

    // Cuantas veces se ha usado
    @Column(nullable = false)
    private int usos;

    // Ultimo dia en que se puede usar (incluido); null = no vence
    private LocalDate fechaVencimiento;

    @Column(nullable = false)
    private boolean activo = true;

    // Constructor para crear cupones sin id ni usos
    public Cupon(String codigo, String descripcion, String tipo, double valor, double compraMinima,
            Integer usosMaximos, LocalDate fechaVencimiento, boolean activo) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.valor = valor;
        this.compraMinima = compraMinima;
        this.usosMaximos = usosMaximos;
        this.fechaVencimiento = fechaVencimiento;
        this.activo = activo;
    }
}

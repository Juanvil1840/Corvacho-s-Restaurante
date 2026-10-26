package com.example.demo.dto;

import com.example.demo.entities.Administrador;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id_administrador", "nombre", "usuario"})
public class AdministradorDTO {

    @JsonProperty("id_administrador")
    private Long id_administrador;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("usuario")
    private String usuario;

    public AdministradorDTO(Long id_administrador, String nombre, String usuario) {
        this.id_administrador = id_administrador;
        this.nombre = nombre;
        this.usuario = usuario;
    }

    public static AdministradorDTO fromEntity(Administrador admin) {
        if (admin == null) {
            return null;
        }
        return new AdministradorDTO(
                admin.getId_administrador(),
                admin.getNombre(),
                admin.getUsuario()
        );
    }
}
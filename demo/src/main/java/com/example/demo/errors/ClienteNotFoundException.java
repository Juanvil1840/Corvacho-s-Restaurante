package com.example.demo.errors;

//Excepcion cuando no se encuentra un cliente, con su id, este en formato de Long
public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException(Long id) {
        super("Cliente no encontrado con el ID: " + id);
    }
}
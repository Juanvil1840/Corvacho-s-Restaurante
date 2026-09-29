import { Injectable } from '@angular/core';
import { Cliente } from '../models/cliente.model';

@Injectable({
  providedIn: 'root'
})
export class ClienteServiceService {

  private clientes: Cliente[] = [
    {
      clienteId: 1,
      nombre: "Daniel",
      apellido: "Cedeño",
      correo: "cedeno.danielc@javeriana.edu.co",
      contrasena: "1234567",
      telefono: "3106699555",
      direccion: "Calle 45 #7-95",
      activo: true
    },
    {
      clienteId: 2,
      nombre: "Jose",
      apellido: "Pulido",
      correo: "jose.p@javeriana.edu.co",
      contrasena: "5678923",
      telefono: "3113479585",
      direccion: "Calle 98 #8-45",
      activo: true
    },
    {
      clienteId: 3,
      nombre: "Sebastián",
      apellido: "Rincón",
      correo: "rincon.sebas@javeriana.edu.co",
      contrasena: "123",
      telefono: "3112549855",
      direccion: "Calle 100 #4-35",
      activo: true
    },
    {
      clienteId: 4,
      nombre: "Eileen",
      apellido: "Rodriguez",
      correo: "eileen.rodriguez@javeriana.edu.co",
      contrasena: "1234900",
      telefono: "3100076895",
      direccion: "Calle 129 #9-42",
      activo: true
    },
    {
      clienteId: 5,
      nombre: "Giovanny",
      apellido: "Durán",
      correo: "gio.duran_@javeriana.edu.co",
      contrasena: "1236547",
      telefono: "3057789635",
      direccion: "Calle 40 #7-90",
      activo: true
    },
    {
      clienteId: 6,
      nombre: "Diego",
      apellido: "Melgarejo",
      correo: "diegui200@gmail.com",
      contrasena: "1233377",
      telefono: "3177799950",
      direccion: "Calle 80 #13-43",
      activo: true
    },
    {
      clienteId: 7,
      nombre: "Karen",
      apellido: "Colmenares",
      correo: "karencol07_@hotmail.com",
      contrasena: "7843567",
      telefono: "3195054045",
      direccion: "Calle 127 #5-35",
      activo: true
    },
    {
      clienteId: 8,
      nombre: "Laura",
      apellido: "Corvacho",
      correo: "corvacho.laura_xx@gmail.com",
      contrasena: "9876543",
      telefono: "3096789500",
      direccion: "Calle 200 #15-90",
      activo: true
    },
    {
      clienteId: 9,
      nombre: "Juan",
      apellido: "vil",
      correo: "vil_juan_2005@gmail.com",
      contrasena: "7799881",
      telefono: "3145533789",
      direccion: "Calle 150 #9-50",
      activo: true
    },
    {
      clienteId: 10,
      nombre: "Sebastián",
      apellido: "Angarita",
      correo: "angarita.sebastian@yahoo.com",
      contrasena: "5554445",
      telefono: "3143224455",
      direccion: "Calle 24b #5-75",
      activo: true
    }
  ];

  constructor() { }

  getClientes(): Cliente[] {
    return this.clientes;
  }
  // READ - Obtener uno por ID
  getClienteById(id: number): Cliente | undefined {
    return this.clientes.find(c => c.clienteId === id);
  }

  // CREATE - Agregar
  agregarCliente(cliente: Cliente): void {
    const nuevoId = Math.max(...this.clientes.map(c => c.clienteId), 0) + 1;
    cliente.clienteId = nuevoId;
    this.clientes.push(cliente);
  }

  // UPDATE - Actualizar
  actualizarCliente(cliente: Cliente): void {
    const index = this.clientes.findIndex(c => c.clienteId === cliente.clienteId);
    if (index !== -1) {
      this.clientes[index] = cliente;
    }
  }

  // DELETE - Eliminar
  eliminarCliente(id: number): void {
    this.clientes = this.clientes.filter(c => c.clienteId !== id);
  }
}
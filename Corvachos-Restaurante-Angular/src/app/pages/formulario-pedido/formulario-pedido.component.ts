import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PedidoService } from '../../service/pedido.service';
import { ClienteService } from '../../service/cliente.service';
import { OperadorService } from '../../service/operador.service';
import { DomiciliarioService } from '../../service/domiciliario.service';
import { ProductoServiceService } from '../../service/producto-service.service';
import { AdicionalService } from '../../service/adicional.service';
import { DetallePedidoServiceService } from '../../service/detalle-pedido-service.service';
import { Producto } from '../../models/producto.model';
import { Adicional } from '../../models/adicional.model';
import { Pedido } from '../../models/pedido.model';
import { Cliente } from '../../models/cliente.model';
import { Operador } from '../../models/operador.model';
import { Domiciliario } from '../../models/domiciliario.model';
import { PedidoCamposComponent } from './components/pedido-campos/pedido-campos.component';

@Component({
  selector: 'app-formulario-pedido',
  standalone: true,
  imports: [CommonModule, FormsModule, PedidoCamposComponent],
  templateUrl: './formulario-pedido.component.html',
  styleUrl: './formulario-pedido.component.scss'
})
export class FormularioPedidoComponent implements OnInit {

  pedido: Pedido = {
    id_Pedido: 0,
    estado: 'Recibido',
    fechaCreacion: FormularioPedidoComponent.ahoraLocal(),
    fechaEntrega: null,
    cliente: { clienteId: 0, nombre: '', apellido: '', correo: '', contrasena: '', telefono: '', direccion: '', activo: true },
    operador: { id_operador: 0, nombre: '', usuario: '', contrasena: '' },
    domiciliario: { id_domiciliario: 0, nombre: '', celular: '', cedula: '', informaciondisp: true, operador: { id_operador: 0, nombre: '', usuario: '', contrasena: '' } }
  };

  clientes: Cliente[] = [];
  operadores: Operador[] = [];
  domiciliarios: Domiciliario[] = [];
  productos: Producto[] = [];
  adicionales: Adicional[] = [];
  comidasSeleccionadas: { producto: Producto; cantidad: number; adicional?: Adicional | null }[] = [];

  editando = false;
  domiciliarioAsignadoId = 0;
  guardando = false;
  error = '';

  constructor(
    private pedidoService: PedidoService,
    private clienteService: ClienteService,
    private operadorService: OperadorService,
    private domiciliarioService: DomiciliarioService,
    private productoService: ProductoServiceService,
    private adicionalService: AdicionalService,
    private detallePedidoService: DetallePedidoServiceService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.clienteService.getClientes().subscribe({
      next: (clis) => this.clientes = clis,
      error: (e) => console.error('Error al cargar clientes:', e)
    });
    this.operadorService.getOperadores().subscribe({
      next: (ops) => this.operadores = ops,
      error: (e) => console.error('Error al cargar operadores:', e)
    });
    this.domiciliarioService.getDomiciliarios().subscribe({
      next: (doms) => this.domiciliarios = doms,
      error: (e) => console.error('Error al cargar domiciliarios:', e)
    });
    this.productoService.getProductos().subscribe({
      next: (prods) => this.productos = prods,
      error: (e) => console.error('Error al cargar productos:', e)
    });
    this.adicionalService.getAdicionales().subscribe({
      next: (adics) => this.adicionales = adics,
      error: (e) => console.error('Error al cargar adicionales:', e)
    });

    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.editando = true;

      this.pedidoService.getPedidoById(Number(id)).subscribe({
        next: (pedido) => {
          this.domiciliarioAsignadoId = pedido.domiciliario?.id_domiciliario ?? 0;
          this.pedido = {
            ...pedido,
            cliente: pedido.cliente ?? { clienteId: 0, nombre: '', apellido: '', correo: '', contrasena: '', telefono: '', direccion: '', activo: true },
            operador: pedido.operador ?? { id_operador: 0, nombre: '', usuario: '', contrasena: '' },
            domiciliario: pedido.domiciliario ?? { id_domiciliario: 0, nombre: '', celular: '', cedula: '', informaciondisp: true, operador: { id_operador: 0, nombre: '', usuario: '', contrasena: '' } },
            fechaCreacion: pedido.fechaCreacion ? pedido.fechaCreacion.toString().substring(0, 16) : '',
            fechaEntrega: pedido.fechaEntrega ? pedido.fechaEntrega.toString().substring(0, 16) : null
          };
        },
        error: (error) => {
          console.error('Error al cargar el pedido:', error);
          this.error = 'No fue posible cargar el pedido.';
        }
      });

      this.detallePedidoService.obtenerPorPedido(Number(id)).subscribe({
        next: (detalles) => {
          if (detalles && detalles.length > 0) {
            this.comidasSeleccionadas = detalles
              .filter(d => d.producto)
              .map(d => ({
                producto: d.producto,
                cantidad: d.cantidad || 1,
                adicional: d.adicional ?? null
              }));
          }
        },
        error: (e) => console.error('Error al cargar comidas del pedido:', e)
      });
    }
  }

  actualizarRelaciones(): void {
    if (this.pedido.cliente && this.pedido.cliente.clienteId) {
      const cli = this.clientes.find(c => c.clienteId === Number(this.pedido.cliente.clienteId));
      if (cli) this.pedido.cliente = cli;
    }

    if (this.pedido.operador && this.pedido.operador.id_operador && Number(this.pedido.operador.id_operador) > 0) {
      const op = this.operadores.find(o => o.id_operador === Number(this.pedido.operador!.id_operador));
      this.pedido.operador = op ?? null;
    } else {
      this.pedido.operador = null;
    }

    if (this.pedido.domiciliario && this.pedido.domiciliario.id_domiciliario && Number(this.pedido.domiciliario.id_domiciliario) > 0) {
      const dom = this.domiciliarios.find(d => d.id_domiciliario === Number(this.pedido.domiciliario!.id_domiciliario));
      this.pedido.domiciliario = dom ?? null;
    } else {
      this.pedido.domiciliario = null;
    }

    if (this.pedido.fechaEntrega === '') {
      this.pedido.fechaEntrega = null;
    }
  }

  guardar(): void {
    if (this.pedido.estado === 'Enviado' && !Number(this.pedido.domiciliario?.id_domiciliario)) {
      this.error = 'Para enviar el pedido debe seleccionar un domiciliario disponible.';
      return;
    }

    this.actualizarRelaciones();

    if (!this.pedido.estado || this.pedido.estado.trim() === '') {
      this.pedido.estado = 'Recibido';
    }

    if (!this.pedido.cliente || !this.pedido.cliente.clienteId || Number(this.pedido.cliente.clienteId) <= 0) {
      this.error = 'Debe seleccionar un cliente.';
      return;
    }

    if (this.comidasSeleccionadas.length === 0) {
      this.error = 'Debe añadir al menos una comida al pedido.';
      return;
    }

    this.pedido.detallePedidos = this.comidasSeleccionadas.map(c => ({
      cantidad: c.cantidad,
      producto: { id: c.producto.id },
      adicional: c.adicional && c.adicional.id_adicional ? { id_adicional: c.adicional.id_adicional } : null
    }));

    this.guardando = true;
    this.error = '';

    const peticion = this.editando
      ? this.pedidoService.actualizarPedido(this.pedido)
      : this.pedidoService.agregarPedido(this.pedido);

    peticion.subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/pedidos']);
      },
      error: (error) => {
        console.error('Error al guardar el pedido:', error);
        this.guardando = false;
        this.error = error.error?.mensaje || 'No fue posible guardar el pedido. Intenta nuevamente.';
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/pedidos']);
  }

  private static ahoraLocal(): string {
    const d = new Date();
    d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
    return d.toISOString().substring(0, 16);
  }
}

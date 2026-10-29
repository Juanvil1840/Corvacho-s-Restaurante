import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PedidoService } from '../../service/pedido.service';
import { ClienteService } from '../../service/cliente.service';
import { OperadorService } from '../../service/operador.service';
import { DomiciliarioService } from '../../service/domiciliario.service';
import { ProductoService } from '../../service/producto.service';
import { AdicionalService } from '../../service/adicional.service';
import { Producto } from '../../models/producto.model';
import { Adicional } from '../../models/adicional.model';
import { PedidoGuardar } from '../../models/pedido.model';
import { Cliente } from '../../models/cliente.model';
import { Operador } from '../../models/operador.model';
import { Domiciliario } from '../../models/domiciliario.model';
import { ComidaPedido, FormularioPedido, PedidoCamposComponent } from './components/pedido-campos/pedido-campos.component';

@Component({
  selector: 'app-formulario-pedido',
  standalone: true,
  imports: [CommonModule, FormsModule, PedidoCamposComponent],
  templateUrl: './formulario-pedido.component.html',
  styleUrl: './formulario-pedido.component.scss'
})
export class FormularioPedidoComponent implements OnInit {

  formulario: FormularioPedido = {
    estado: 'Recibido',
    fechaCreacion: FormularioPedidoComponent.ahoraLocal(),
    fechaEntrega: null,
    clienteId: 0,
    operadorId: 0,
    domiciliarioId: 0
  };

  clientes: Cliente[] = [];
  operadores: Operador[] = [];
  domiciliarios: Domiciliario[] = [];
  productos: Producto[] = [];
  adicionales: Adicional[] = [];
  comidasSeleccionadas: ComidaPedido[] = [];

  idPedido = 0;
  editando = false;
  estadoOriginal = 'Recibido';
  domiciliarioAsignadoId = 0;
  guardando = false;
  error = '';

  constructor(
    private pedidoService: PedidoService,
    private clienteService: ClienteService,
    private operadorService: OperadorService,
    private domiciliarioService: DomiciliarioService,
    private productoService: ProductoService,
    private adicionalService: AdicionalService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.clienteService.getClientes().subscribe({
      next: (clientes) => this.clientes = clientes,
      error: (e) => console.error('Error al cargar clientes:', e)
    });
    this.operadorService.getOperadores().subscribe({
      next: (operadores) => this.operadores = operadores,
      error: (e) => console.error('Error al cargar operadores:', e)
    });
    this.domiciliarioService.getDomiciliarios().subscribe({
      next: (domiciliarios) => this.domiciliarios = domiciliarios,
      error: (e) => console.error('Error al cargar domiciliarios:', e)
    });
    this.productoService.getProductos().subscribe({
      next: (productos) => this.productos = productos,
      error: (e) => console.error('Error al cargar productos:', e)
    });
    this.adicionalService.getAdicionales().subscribe({
      next: (adicionales) => this.adicionales = adicionales,
      error: (e) => console.error('Error al cargar adicionales:', e)
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editando = true;
      this.idPedido = Number(id);
      this.pedidoService.getPedidoById(this.idPedido).subscribe({
        next: (pedido) => {
          this.estadoOriginal = pedido.estado;
          this.domiciliarioAsignadoId = pedido.domiciliario?.id_domiciliario ?? 0;
          this.formulario = {
            estado: pedido.estado,
            // El input datetime-local necesita el formato yyyy-MM-ddTHH:mm
            fechaCreacion: pedido.fechaCreacion ? pedido.fechaCreacion.substring(0, 16) : '',
            fechaEntrega: pedido.fechaEntrega ? pedido.fechaEntrega.substring(0, 16) : null,
            clienteId: pedido.cliente?.clienteId ?? 0,
            operadorId: pedido.operador?.id_operador ?? 0,
            domiciliarioId: pedido.domiciliario?.id_domiciliario ?? 0
          };
          // Se copian todos los adicionales para no perderlos al guardar
          this.comidasSeleccionadas = pedido.detallePedidos.map(detalle => ({
            producto: detalle.producto,
            cantidad: detalle.cantidad,
            adicionales: detalle.adicionales
          }));
        },
        error: (error) => {
          console.error('Error al cargar el pedido:', error);
          this.error = 'No fue posible cargar el pedido.';
        }
      });
    }
  }

  guardar(): void {
    if (!this.formulario.clienteId) {
      this.error = 'Debe seleccionar un cliente.';
      return;
    }
    if (this.formulario.estado === 'Enviado' && !this.formulario.domiciliarioId) {
      this.error = 'Para enviar el pedido debe seleccionar un domiciliario disponible.';
      return;
    }
    if (this.comidasSeleccionadas.length === 0) {
      this.error = 'Debe añadir al menos una comida al pedido.';
      return;
    }

    // Al backend solo se le envian los ids
    const pedido: PedidoGuardar = {
      estado: this.formulario.estado,
      fechaCreacion: this.formulario.fechaCreacion,
      fechaEntrega: this.formulario.fechaEntrega || null,
      cliente: { clienteId: this.formulario.clienteId },
      operador: this.formulario.operadorId ? { id_operador: this.formulario.operadorId } : null,
      domiciliario: this.formulario.domiciliarioId ? { id_domiciliario: this.formulario.domiciliarioId } : null,
      detallePedidos: this.comidasSeleccionadas.map(comida => ({
        cantidad: comida.cantidad,
        producto: { id: comida.producto.id },
        adicionales: comida.adicionales.map(a => ({ id_adicional: a.id_adicional, cantidad: a.cantidad }))
      }))
    };

    this.guardando = true;
    this.error = '';

    const peticion = this.editando
      ? this.pedidoService.actualizarPedido(this.idPedido, pedido)
      : this.pedidoService.agregarPedido(pedido);

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

  // Fecha y hora actual en el formato del input datetime-local
  private static ahoraLocal(): string {
    const fecha = new Date();
    fecha.setMinutes(fecha.getMinutes() - fecha.getTimezoneOffset());
    return fecha.toISOString().substring(0, 16);
  }
}

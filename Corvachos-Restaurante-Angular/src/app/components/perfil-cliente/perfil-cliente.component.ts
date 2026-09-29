import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ClienteServiceService } from '../../service/cliente-service.service';
import { Cliente } from '../../models/cliente.model';
import { FooterComponent } from '../footer/footer.component';


@Component({
  selector: 'app-perfil-cliente',
  standalone: true,
  imports: [CommonModule, RouterLink, FooterComponent],
  templateUrl: './perfil-cliente.component.html',
  styleUrl: './perfil-cliente.component.scss'
})
export class PerfilClienteComponent implements OnInit {

  cliente: Cliente | undefined;

  constructor(
    private clienteService: ClienteServiceService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.cliente = this.clienteService.getClienteById(Number(id));
    }
  }

  eliminarCuenta(): void {
  if (confirm('¿Estas seguro de que deseas eliminar tu cuenta? Esta accion no se puede deshacer.')) {
    if (this.cliente) {
      this.clienteService.eliminarCliente(this.cliente.clienteId);
      this.router.navigate(['/']);  
    }
  }
}
}
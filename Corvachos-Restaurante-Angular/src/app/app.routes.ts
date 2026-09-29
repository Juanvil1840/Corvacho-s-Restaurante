import { Routes } from '@angular/router';
import { ListaProductosComponent } from './components/lista-productos/lista-productos.component';
import { FormularioProductoComponent } from './components/formulario-producto/formulario-producto.component';
import { ListaClientesComponent } from './components/lista-clientes/lista-clientes.component';
import { FormularioClienteComponent } from './components/formulario-cliente/formulario-cliente.component';
import { LoginClienteComponent } from './components/login-cliente/login-cliente.component';
import { RegistroClienteComponent } from './components/registro-cliente/registro-cliente.component';
import { PerfilClienteComponent } from './components/perfil-cliente/perfil-cliente.component';
import { MenuComponent } from './components/menu/menu.component';
import { DetalleProductoComponent } from './components/detalle-producto/detalle-producto.component';

export const routes: Routes = [
    { path: 'productos', component: ListaProductosComponent },
    { path: 'productos/nuevo', component: FormularioProductoComponent },
    { path: 'productos/editar/:id', component: FormularioProductoComponent },
    { path: 'clientes', component: ListaClientesComponent },
    { path: 'clientes/nuevo', component: FormularioClienteComponent },
    { path: 'clientes/editar/:id', component: FormularioClienteComponent },
    { path: 'clientes/perfil/:id', component: PerfilClienteComponent },
    { path: 'login', component: LoginClienteComponent },
    { path: 'registro', component: RegistroClienteComponent },
    { path: 'menu', component: MenuComponent },
    { path: 'menu/detalle/:id', component: DetalleProductoComponent },
    { path: '', redirectTo: '/login', pathMatch: 'full' },
];
import { Routes } from '@angular/router';

import { LandingComponent } from './pages/landing/landing.component';
import { ListaProductosComponent } from './pages/lista-productos/lista-productos.component';
import { FormularioProductoComponent } from './pages/formulario-producto/formulario-producto.component';
import { ListaClientesComponent } from './pages/lista-clientes/lista-clientes.component';
import { FormularioClienteComponent } from './pages/formulario-cliente/formulario-cliente.component';
import { LoginClienteComponent } from './pages/login-cliente/login-cliente.component';
import { RegistroClienteComponent } from './pages/registro-cliente/registro-cliente.component';
import { PerfilClienteComponent } from './pages/perfil-cliente/perfil-cliente.component';
import { MenuComponent } from './pages/menu/menu.component';
import { DetalleProductoComponent } from './pages/detalle-producto/detalle-producto.component';

// NUEVOS
import { ListaCategoriasComponent } from './pages/lista-categorias/lista-categorias.component';
import { FormularioCategoriaComponent } from './pages/formulario-categoria/formulario-categoria.component';
import { ListaAdicionalesComponent } from './pages/lista-adicionales/lista-adicionales.component';
import { FormularioAdicionalComponent } from './pages/formulario-adicional/formulario-adicional.component';

export const routes: Routes = [

    { path: '', component: LandingComponent },
    { path: 'productos', component: ListaProductosComponent },
    { path: 'productos/nuevo', component: FormularioProductoComponent },
    { path: 'productos/editar/:id', component: FormularioProductoComponent },

    // NUEVAS RUTAS
    { path: 'categorias', component: ListaCategoriasComponent },
    { path: 'categorias/nuevo', component: FormularioCategoriaComponent },
    { path: 'categorias/editar/:id', component: FormularioCategoriaComponent },
    { path: 'adicionales', component: ListaAdicionalesComponent },
    { path: 'adicionales/nuevo', component: FormularioAdicionalComponent },
    { path: 'adicionales/editar/:id', component: FormularioAdicionalComponent },

    { path: 'clientes', component: ListaClientesComponent },
    { path: 'clientes/nuevo', component: FormularioClienteComponent },
    { path: 'clientes/editar/:id', component: FormularioClienteComponent },
    { path: 'clientes/perfil/:id', component: PerfilClienteComponent },
    { path: 'login', component: LoginClienteComponent },
    { path: 'registro', component: RegistroClienteComponent },
    { path: 'menu', component: MenuComponent },
    { path: 'menu/detalle/:id', component: DetalleProductoComponent },
];
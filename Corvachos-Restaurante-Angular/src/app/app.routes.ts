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
import { LoginAdminComponent } from './pages/login-admin/login-admin.component';
import { PortalAdminComponent } from './pages/portal-admin/portal-admin.component';
import { PortalOperarioComponent } from './pages/portal-operario/portal-operario.component';

// Categorias y adicionales
import { ListaCategoriasComponent } from './pages/lista-categorias/lista-categorias.component';
import { FormularioCategoriaComponent } from './pages/formulario-categoria/formulario-categoria.component';
import { ListaAdicionalesComponent } from './pages/lista-adicionales/lista-adicionales.component';
import { FormularioAdicionalComponent } from './pages/formulario-adicional/formulario-adicional.component';

// CRUD Pedido y Domiciliario
import { ListaPedidosComponent } from './pages/lista-pedidos/lista-pedidos.component';
import { FormularioPedidoComponent } from './pages/formulario-pedido/formulario-pedido.component';
import { ListaDomiciliariosComponent } from './pages/lista-domiciliarios/lista-domiciliarios.component';
import { FormularioDomiciliarioComponent } from './pages/formulario-domiciliario/formulario-domiciliario.component';

// CRUD Operador, Administrador y páginas pendientes
import { ListaOperadoresComponent } from './pages/lista-operadores/lista-operadores.component';
import { FormularioOperadorComponent } from './pages/formulario-operador/formulario-operador.component';
import { ListaAdministradoresComponent } from './pages/lista-administradores/lista-administradores.component';
import { FormularioAdministradorComponent } from './pages/formulario-administrador/formulario-administrador.component';
import { DetallePedidoComponent } from './pages/detalle-pedido/detalle-pedido.component';
import { PaginaErrorComponent } from './pages/pagina-error/pagina-error.component';
import { LoginOperarioComponent } from './pages/login-operario/login-operario.component';

// Seguridad: guards por rol (JWT)
import { authGuard, sinSesionGuard } from './guards/auth.guard';

// Roles que pueden entrar a cada grupo de páginas
const SOLO_ADMIN = { roles: ['ADMIN'] };
const SOLO_OPERADOR = { roles: ['OPERADOR'] };
const ADMIN_U_OPERADOR = { roles: ['ADMIN', 'OPERADOR'] };
const CLIENTE_O_ADMIN = { roles: ['CLIENTE', 'ADMIN'] };
const CUALQUIER_ROL = { roles: ['CLIENTE', 'OPERADOR', 'ADMIN'] };

// Definición de todas las rutas de navegación de la aplicación
export const routes: Routes = [

    // Página de inicio y gestión de productos
    { path: '', component: LandingComponent, canActivate: [sinSesionGuard] },
    { path: 'productos', component: ListaProductosComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'productos/nuevo', component: FormularioProductoComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'productos/editar/:id', component: FormularioProductoComponent, canActivate: [authGuard], data: SOLO_ADMIN },

    // Categorias y adicionales
    { path: 'categorias', component: ListaCategoriasComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'categorias/nuevo', component: FormularioCategoriaComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'categorias/editar/:id', component: FormularioCategoriaComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'adicionales', component: ListaAdicionalesComponent, canActivate: [authGuard], data: ADMIN_U_OPERADOR },
    { path: 'adicionales/nuevo', component: FormularioAdicionalComponent, canActivate: [authGuard], data: ADMIN_U_OPERADOR },
    { path: 'adicionales/editar/:id', component: FormularioAdicionalComponent, canActivate: [authGuard], data: ADMIN_U_OPERADOR },
    { path: 'portal-operario', component: PortalOperarioComponent, canActivate: [authGuard], data: SOLO_OPERADOR },

    // CRUD Pedido y Domiciliario
    { path: 'pedidos', component: ListaPedidosComponent, canActivate: [authGuard], data: ADMIN_U_OPERADOR },
    { path: 'pedidos/nuevo', component: FormularioPedidoComponent, canActivate: [authGuard], data: ADMIN_U_OPERADOR },
    { path: 'pedidos/editar/:id', component: FormularioPedidoComponent, canActivate: [authGuard], data: ADMIN_U_OPERADOR },
    { path: 'pedidos/detalle/:id', component: DetallePedidoComponent, canActivate: [authGuard], data: CUALQUIER_ROL },
    { path: 'domiciliarios', component: ListaDomiciliariosComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'domiciliarios/nuevo', component: FormularioDomiciliarioComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'domiciliarios/editar/:id', component: FormularioDomiciliarioComponent, canActivate: [authGuard], data: SOLO_ADMIN },

    // CRUD Operador y Administrador
    { path: 'operadores', component: ListaOperadoresComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'operadores/nuevo', component: FormularioOperadorComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'operadores/editar/:id', component: FormularioOperadorComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'administradores', component: ListaAdministradoresComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'administradores/nuevo', component: FormularioAdministradorComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'administradores/editar/:id', component: FormularioAdministradorComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'login-operario', component: LoginOperarioComponent, canActivate: [sinSesionGuard] },
    { path: 'login-admin', component: LoginAdminComponent, canActivate: [sinSesionGuard] },
    { path: 'portal-admin', component: PortalAdminComponent, canActivate: [authGuard], data: SOLO_ADMIN },

    // Dashboard de reportes: se carga bajo demanda (lazy) porque trae las librerias de graficas
    {
        path: 'reportes',
        loadComponent: () => import('./pages/reportes/reportes.component').then(m => m.ReportesComponent),
        canActivate: [authGuard],
        data: SOLO_ADMIN
    },

    // Rutas de clientes, autenticación, menú público y manejo de errores
    { path: 'clientes', component: ListaClientesComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'clientes/nuevo', component: FormularioClienteComponent, canActivate: [authGuard], data: SOLO_ADMIN },
    { path: 'clientes/editar/:id', component: FormularioClienteComponent, canActivate: [authGuard], data: CLIENTE_O_ADMIN },
    { path: 'clientes/perfil/:id', component: PerfilClienteComponent, canActivate: [authGuard], data: CLIENTE_O_ADMIN },
    { path: 'login', component: LoginClienteComponent, canActivate: [sinSesionGuard] },
    { path: 'registro', component: RegistroClienteComponent, canActivate: [sinSesionGuard] },
    { path: 'menu', component: MenuComponent },
    { path: 'menu/detalle/:id', component: DetalleProductoComponent },
    { path: 'error', component: PaginaErrorComponent },
    { path: '**', component: PaginaErrorComponent },
];

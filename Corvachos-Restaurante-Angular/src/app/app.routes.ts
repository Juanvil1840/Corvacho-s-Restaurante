import { Routes } from '@angular/router';
import { ListaProductosComponent } from './components/lista-productos/lista-productos.component';
import { FormularioProductoComponent } from './components/formulario-producto/formulario-producto.component';

export const routes: Routes = [
    { path: 'productos', component: ListaProductosComponent },
    { path: 'productos/nuevo', component: FormularioProductoComponent },
    { path: 'productos/editar/:id', component: FormularioProductoComponent },
    { path: '', redirectTo: '/productos', pathMatch: 'full' }
];
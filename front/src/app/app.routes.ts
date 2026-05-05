import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import {Home} from './features/dashboard/home/home';
import { ApartamentoDetails } from './features/dashboard/apartamento-details/apartamento-details';
import { ApartamentoEdit } from './features/dashboard/apartamento-edit/apartamento-edit';
import {ApartamentoCreate} from './features/dashboard/apartamento-create/apartamento-create';
import {VincularCodigoComponent} from './features/dashboard/vincular-codigo/vincular-codigo';
import {ReservaManualComponent} from './features/dashboard/reserva-manual/reserva-manual';
import {Balance} from './features/dashboard/finanzas/balance/balance';
import { NuevoMovimiento } from './features/dashboard/finanzas/nuevo-movimiento/nuevo-movimiento';
import {ListaContratos} from './features/dashboard/contratos/lista-contratos/lista-contratos'
import {DetallesContrato} from './features/dashboard/contratos/detalles-contrato/detalles-contrato';
import {ListaElementos} from './features/dashboard/inventario/lista-elementos/lista-elementos';
import {CrearElemento} from './features/dashboard/inventario/crear-elemento/crear-elemento';
import {ElementoDetalle} from './features/dashboard/inventario/elemento-detalle/elemento-detalle';
import {EditarElemento} from './features/dashboard/inventario/editar-elemento/editar-elemento';

export const routes: Routes = [

  { path: 'login', component: LoginComponent },
  { path: 'vincular-vivienda', component: VincularCodigoComponent },
  {path: 'home', component: Home},
  {
    path: 'finanzas',
    children: [
      { path: '', redirectTo: 'balance', pathMatch: 'full' }, // Si van a /finanzas, saltan a balance
      { path: 'balance', component: Balance },
      { path: 'nuevo-movimiento', component: NuevoMovimiento }
    ]
  },
  {path: 'contratos', component: ListaContratos},
  { path: 'apartamento/nuevo', component: ApartamentoCreate },
  { path: 'contratos/:id', component: DetallesContrato },
  { path: 'apartamento/:id', component: ApartamentoDetails },
  { path: 'apartamento/editar/:id', component: ApartamentoEdit },
  { path: 'apartamento/:id/inventario', component: ListaElementos },
  { path: 'apartamento/:id/inventario/nuevo', component: CrearElemento },
  { path: 'apartamento/:id/inventario/editar/:itemId', component: EditarElemento },
  { path: 'apartamento/:id/inventario/:itemId', component: ElementoDetalle },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {path: 'register', component:RegisterComponent},
  {path: 'apartamento/:id/reserva-manual', component: ReservaManualComponent},
  { path: '**', redirectTo: '/login' }
];

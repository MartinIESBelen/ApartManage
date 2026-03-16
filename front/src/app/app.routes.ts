import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login'; // Importamos tu componente
import {Home} from './features/dashboard/home/home';
import { ApartamentoDetails } from './features/dashboard/apartamento-details/apartamento-details';
import { ApartamentoEdit } from './features/dashboard/apartamento-edit/apartamento-edit';
import {ApartamentoCreate} from './features/dashboard/apartamento-create/apartamento-create';

export const routes: Routes = [

  { path: 'login', component: LoginComponent },
  {path: 'home', component: Home},
  { path: 'apartamento/nuevo', component: ApartamentoCreate },
  { path: 'apartamento/:id', component: ApartamentoDetails },
  { path: 'apartamento/editar/:id', component: ApartamentoEdit },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];

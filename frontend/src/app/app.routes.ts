import { Routes } from '@angular/router';
import { loginGuard } from './auth/login.guard';
import { authGuard } from './auth/auth.guard';

export const routes: Routes = [
{ path: '', pathMatch: 'full', redirectTo: 'login' },

{
path: 'login',
canActivate: [loginGuard],
loadComponent: () =>
      import('./auth/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    
    loadComponent: () =>
      import('./components/dashboard.component').then(m => m.DashboardComponent),
  },
  {
    path: 'painel',
    canActivate: [authGuard],

    loadComponent: () =>
      import('./components/painel.component').then(m => m.PainelComponent),
  },
  {
    path: 'unauthorized',
    loadComponent: () =>
      import('./auth/unauthorized.component').then(m => m.UnauthorizedComponent),
  },
  { path: '**', redirectTo: 'login' },
];

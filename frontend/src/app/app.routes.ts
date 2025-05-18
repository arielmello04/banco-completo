import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login.component';
import { loginGuard } from './auth/login.guard';
import { authGuard } from './auth/auth.guard';

export const routes: Routes = [
  {
  path: '',
  loadComponent: () => import('./home-redirect.component').then(m => m.HomeRedirectComponent),
},
  {
    path: 'dashboard',
    canActivate: [authGuard],
    data: { roles: ['GERENTE', 'ADMIN'] },
    loadComponent: () => import('./components/dashboard.component').then(m => m.DashboardComponent),
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [loginGuard],
  },
  {
    path: 'painel',
    canActivate: [authGuard],
    data: { roles: ['GERENTE', 'ADMIN'] },
    loadComponent: () => import('./components/painel.component').then(m => m.PainelComponent),
  },
  {
    path: 'unauthorized',
    loadComponent: () => import('./auth/unauthorized.component').then(m => m.UnauthorizedComponent),
  },
  {
    path: '**',
    redirectTo: 'login',
  }
];

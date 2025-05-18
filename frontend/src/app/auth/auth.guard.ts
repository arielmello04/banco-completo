import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const isLogged = auth.isAuthenticated();
  const allowedRoles = route.data?.['roles'] as string[] | undefined;
  const userRole = auth.getUserRole();

  if (!isLogged || !userRole) {
    localStorage.removeItem('auth_token'); // limpa token inválido
    router.navigateByUrl('/login');
    return false;
  }

  if (allowedRoles && !allowedRoles.includes(userRole)) {
    router.navigateByUrl('/unauthorized');
    return false;
  }

  return true;
};

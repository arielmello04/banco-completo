// src/app/auth/login.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const loginGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) {
    const role = auth.getUserRole();
    if (role === 'ADMIN' || role === 'GERENTE') {
      router.navigateByUrl('/painel');
    } else {
      router.navigateByUrl('/dashboard');
    }
    return false;
  }

  return true;
};


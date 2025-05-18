// src/app/home-redirect.component.ts
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth/auth.service';

@Component({
  standalone: true,
  selector: 'app-home-redirect',
  template: '',
})
export class HomeRedirectComponent implements OnInit {
  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    if (!this.auth.isLoggedIn()) {
      this.router.navigateByUrl('/login');
    } else {
      const role = this.auth.getUserRole();
      if (role === 'GERENTE' || role === 'ADMIN') {
        this.router.navigateByUrl('/painel');
      } else {
        this.router.navigateByUrl('/dashboard');
      }
    }
  }
}

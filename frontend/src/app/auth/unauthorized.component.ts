// src/app/auth/unauthorized.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-unauthorized',
  template: `<h2>Acesso negado</h2><p>Você não tem permissão para acessar esta página.</p>`,
  imports: [CommonModule],
})
export class UnauthorizedComponent {}

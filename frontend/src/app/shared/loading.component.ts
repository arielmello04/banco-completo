// src/app/shared/loading.component.ts
import { Component } from '@angular/core';

@Component({
  selector: 'app-loading',
  standalone: true,
  template: `<div class="loading">Carregando...</div>`,
  styles: [`
    .loading {
      text-align: center;
      margin-top: 2rem;
      font-size: 1.2rem;
      color: #555;
    }
  `]
})
export class LoadingComponent {}

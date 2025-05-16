import { Component } from '@angular/core';
import { DashboardComponent } from './components/dashboard.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [DashboardComponent], // Apenas componentes standalone aqui!
  templateUrl: './app.component.html',
})
export class AppComponent {}
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardChartComponent } from './dashboard-chart.component'; // ajuste o caminho se necessário
import { DashboardService } from '../services/dashboard.service';
import { DashboardGraficosDTO } from '../models/dashboard-graficos.dto';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, DashboardChartComponent],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  dados: DashboardGraficosDTO | null = null;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dashboardService.getGraficos().subscribe({
      next: (res) => (this.dados = res),
      error: (err) => console.error('Erro ao carregar gráficos', err),
    });
  }
}

// src/app/components/dashboard.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardChartComponent } from './dashboard-chart.component';
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

  labelsSaldo: string[] = [];
dataSaldo: number[] = [];

labelsTransacoes: string[] = [];
dataTransacoes: number[] = [];

labelsTipos: string[] = [];
dataTipos: number[] = [];

ngOnInit(): void {
  this.dashboardService.getGraficos().subscribe({
    next: (res) => {
      console.log('Dados recebidos:', res);
      this.dados = res;
      this.labelsSaldo = res.saldoTotalPorDia.map(p => p.data);
      this.dataSaldo = res.saldoTotalPorDia.map(p => p.valor);

      this.labelsTransacoes = res.transacoesPorDia.map(p => p.data);
      this.dataTransacoes = res.transacoesPorDia.map(p => p.valor);

      this.labelsTipos = res.transacoesPorTipo.map(p => p.tipo);
      this.dataTipos = res.transacoesPorTipo.map(p => p.quantidade);
    },
    error: (err) => console.error('Erro ao carregar gráficos', err),
  });
}

}

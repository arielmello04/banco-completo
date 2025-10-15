// src/app/components/dashboard.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { DashboardChartComponent } from './dashboard-chart.component';
import { DashboardService } from '../services/dashboard.service';
import { DashboardGraficosDTO } from '../models/dashboard-graficos.dto';
import { AuthService } from '../auth/auth.service';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, DashboardChartComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  dados: DashboardGraficosDTO | null = null;

  constructor(
    private dashboardService: DashboardService,
    private auth: AuthService,
    private router: Router
  ) {}
  sair() {
    this.auth.logout?.();
    localStorage.removeItem('token');
    this.router.navigateByUrl('/login');
  }

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
      this.labelsSaldo = res.saldoTotalPorDia.map(p => {
        const [ano, mes, dia] = p.data.split('-');
        return `${dia}/${mes}/${ano}`;
      });
      this.dataSaldo = res.saldoTotalPorDia.map(p => p.valor);

      this.labelsTransacoes = res.transacoesPorDia.map(p => {
        const [ano, mes, dia] = p.data.split('-');
        return `${dia}/${mes}/${ano}`;
      });
      this.dataTransacoes = res.transacoesPorDia.map(p => p.valor);

      this.labelsTipos = res.transacoesPorTipo.map(p => p.tipo);
      this.dataTipos = res.transacoesPorTipo.map(p => p.quantidade);
    },
    error: (err) => console.error('Erro ao carregar gráficos', err),
  });
}

}

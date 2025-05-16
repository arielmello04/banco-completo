import { DashboardService } from '../services/dashboard.service';


export interface SerieTemporalDTO {
  data: string; // ISO string (ex: "2024-05-01")
  valor: number;
}

export interface ContagemPorTipoDTO {
  tipo: string;
  quantidade: number;
}

export interface DashboardGraficosDTO {
  saldoPorDia: { data: string; valor: number }[];
  transacoesPorDia: { data: string; valor: number }[];
  contagemPorTipo: { tipo: string; quantidade: number }[];
}

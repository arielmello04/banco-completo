// src/app/services/dashboard.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DashboardGraficosDTO } from '../models/dashboard-graficos.dto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly API = 'http://localhost:8081/api/gerente/dashboard/graficos';

  constructor(private http: HttpClient) {}

  getGraficos(): Observable<DashboardGraficosDTO> {
    return this.http.get<DashboardGraficosDTO>(this.API);
  }
}

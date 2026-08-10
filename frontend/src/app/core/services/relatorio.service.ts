import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { TotalPorMoeda, RankingCliente } from '../models/relatorio.model';

@Injectable({ providedIn: 'root' })
export class RelatorioService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/relatorios`;

  totalPorMoeda(): Observable<TotalPorMoeda[]> {
    return this.http.get<TotalPorMoeda[]>(`${this.baseUrl}/total-por-moeda`);
  }

  rankingClientes(): Observable<RankingCliente[]> {
    return this.http.get<RankingCliente[]>(`${this.baseUrl}/ranking-clientes`);
  }
}
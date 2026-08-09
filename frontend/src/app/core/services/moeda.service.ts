import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { MoedaResponse } from '../models/moeda.model';

@Injectable({ providedIn: 'root' })
export class MoedaService {
    private http = inject(HttpClient);
    private baseUrl = `${environment.apiUrl}/moedas`;

    listarTodas(): Observable<MoedaResponse[]> {
        return this.http.get<MoedaResponse[]>(this.baseUrl);
    }

    sincronizar(): Observable<string> {
        return this.http.post(`${this.baseUrl}/sincronizar`, {}, { responseType: 'text'})
    }
}
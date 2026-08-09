import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { ClienteRequest, ClienteResponse } from '../models/cliente.model';

@Injectable({ providedIn: 'root' })
export class ClienteService {
    private http = inject(HttpClient);
    private baseUrl = `${environment.apiUrl}/clientes`;

    listarTodos(): Observable<ClienteResponse[]> {
        return this.http.get<ClienteResponse[]>(this.baseUrl);
    }

    buscarPorId(id: number): Observable<ClienteResponse> {
        return this.http.get<ClienteResponse>(`${this.baseUrl}/${id}`);
    }

    cadastrar(dto: ClienteRequest): Observable<ClienteResponse> {
        return this.http.post<ClienteResponse>(this.baseUrl, dto);
    }

    deletar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }
}
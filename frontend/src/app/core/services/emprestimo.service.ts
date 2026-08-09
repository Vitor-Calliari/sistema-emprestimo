import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { EmprestimoRequest, EmprestimoResponse } from '../models/emprestimo.model';

@Injectable({ providedIn: 'root'})
export class EmprestimoService {
    private http = inject(HttpClient);
    private baseUrl = `${environment.apiUrl}/emprestimos`;

    listarTodos(): Observable<EmprestimoResponse[]> {
        return this.http.get<EmprestimoResponse[]>(this.baseUrl);
    }

    buscarPorId(id: number): Observable<EmprestimoResponse> {
        return this.http.get<EmprestimoResponse>(`${this.baseUrl}/${id}`);
    }

    cadastrar(dto: EmprestimoRequest): Observable<EmprestimoResponse> {
        return this.http.post<EmprestimoResponse>(this.baseUrl, dto);
    }

    deletar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }
}

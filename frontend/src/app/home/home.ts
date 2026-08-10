import { Component, inject, signal, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { RelatorioService } from '../core/services/relatorio.service';
import { TotalPorMoeda, RankingCliente } from '../core/models/relatorio.model';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [MatCardModule, MatTableModule, CurrencyPipe],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {
  private relatorioService = inject(RelatorioService);

  totalPorMoeda = signal<TotalPorMoeda[]>([]);
  rankingClientes = signal<RankingCliente[]>([]);

  colunasMoeda = ['moedaCodigo', 'moedaNome', 'quantidadeEmprestimos', 'totalEmprestado'];
  colunasRanking = ['clienteNome', 'quantidadeEmprestimos', 'totalEmprestado'];

  ngOnInit(): void {
    this.relatorioService.totalPorMoeda().subscribe((dados) => this.totalPorMoeda.set(dados));
    this.relatorioService.rankingClientes().subscribe((dados) => this.rankingClientes.set(dados));
  }
}
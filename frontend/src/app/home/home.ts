import { Component, inject, signal, OnInit } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RelatorioService } from '../core/services/relatorio.service';
import { MoedaService } from '../core/services/moeda.service';
import { TotalPorMoeda, RankingCliente } from '../core/models/relatorio.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [MatCardModule, MatTableModule, MatButtonModule, MatIconModule, CurrencyPipe],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {
  private relatorioService = inject(RelatorioService);
  private moedaService = inject(MoedaService);
  private snackBar = inject(MatSnackBar);

  totalPorMoeda = signal<TotalPorMoeda[]>([]);
  rankingClientes = signal<RankingCliente[]>([]);
  sincronizando = false;

  colunasMoeda = ['moedaCodigo', 'moedaNome', 'quantidadeEmprestimos', 'totalEmprestado'];
  colunasRanking = ['clienteNome', 'quantidadeEmprestimos', 'totalEmprestado'];

  ngOnInit(): void {
    this.carregarRelatorios();
  }

  carregarRelatorios(): void {
    this.relatorioService.totalPorMoeda().subscribe((dados) => this.totalPorMoeda.set(dados));
    this.relatorioService.rankingClientes().subscribe((dados) => this.rankingClientes.set(dados));
  }

  sincronizarMoedas(): void {
    this.sincronizando = true;
    this.moedaService.sincronizar().subscribe({
      next: (mensagem) => {
        this.sincronizando = false;
        this.snackBar.open(mensagem, 'Fechar', { duration: 4000 });
      },
      error: () => {
        this.sincronizando = false;
        this.snackBar.open('Erro ao sincronizar moedas com o Banco Central', 'Fechar', { duration: 4000 });
      }
    });
  }
}
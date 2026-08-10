import { Component, inject, OnInit, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EmprestimoService } from '../../core/services/emprestimo.service';
import { EmprestimoResponse } from '../../core/models/emprestimo.model';
import { EmprestimoForm } from '../emprestimo-form/emprestimo-form';
import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-emprestimo-list',
  standalone: true,
  imports: [
    MatTableModule, 
    MatButtonModule, 
    MatIconModule, 
    DecimalPipe, 
    DatePipe, 
    CurrencyPipe
  ],
  templateUrl: './emprestimo-list.html',
  styleUrl: './emprestimo-list.css',
})
export class EmprestimoList implements OnInit {
  private emprestimoService = inject(EmprestimoService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);

  emprestimos = signal<EmprestimoResponse[]>([]);
  colunasExibidas = [
    'clienteNome', 'moedaCodigo', 'valorObtido', 'taxaConversao',
    'valorReais', 'dataVencimento', 'numeroMeses', 'valorPagarVencimento', 'acoes'
  ];

  ngOnInit(): void {
    this.carregarEmprestimos();
  }

  carregarEmprestimos(): void {
    this.emprestimoService.listarTodos().subscribe({
      next: (dados) => this.emprestimos.set(dados),
      error: () => this.snackBar.open('Erro ao carregar empréstimos', 'Fechar', { duration: 3000 })
    });
  }

  excluir(id: number): void {
    if (!confirm('Deseja realmente excluir este empréstimo?')) {
      return;
    }
    this.emprestimoService.deletar(id).subscribe({
      next: () => {
        this.snackBar.open('Empréstimo excluído com sucesso!', 'Fechar', { duration: 3000});
        this.carregarEmprestimos();
      },
      error: () => this.snackBar.open('Erro ao excluir empréstimo', 'Fechar', { duration: 3000})
    });
  }

  novoEmprestimo(): void {
    this.abrirFormulario();
  }

  editarEmprestimo(emprestimo: EmprestimoResponse): void {
    this.abrirFormulario(emprestimo);
  }

  private abrirFormulario(emprestimo?: EmprestimoResponse): void {
    const dialogRef = this.dialog.open(EmprestimoForm, {
      width: '450px',
      data: emprestimo ?? null
    });

    dialogRef.afterClosed().subscribe((salvou) => {
      if (salvou) {
        this.carregarEmprestimos();
      }
    });
  }

}

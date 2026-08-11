import { Component, inject, signal, OnInit } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ClienteService } from '../../core/services/cliente.service';
import { ClienteResponse } from '../../core/models/cliente.model';
import { ClienteForm } from '../cliente-form/cliente-form';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-cliente-list',
  standalone: true,
  imports: [MatTableModule, MatButtonModule, MatIconModule],
  templateUrl: './cliente-list.html',
  styleUrl: './cliente-list.css',
})
export class ClienteList implements OnInit {
  private clienteService = inject(ClienteService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);

  clientes = signal<ClienteResponse[]>([]);
  colunasExibidas = ['nome', 'documento', 'email', 'telefone', 'acoes'];

  ngOnInit(): void {
    this.carregarClientes();
  }

  carregarClientes(): void{
    this.clienteService.listarTodos().subscribe({
      next: (dados) => this.clientes.set(dados),
      error: () => this.snackBar.open('Erro ao carregar clientes', 'Fechar', {duration: 5000})
    });
  }

  excluir(id: number): void {
    if (!confirm('Deseja realmente excluir este cliente?')) {
      return;
    }

    this.clienteService.deletar(id).subscribe({
      next: () => {
        console.log('DELETE bem sucedido, atualizando lista...');
        this.snackBar.open('Cliente excluído com sucesso', 'Fechar', {duration: 5000});
        this.carregarClientes();
      },
      error: (err) => {
        console.error('Erro ao excluir:', err);
        this.snackBar.open('Erro ao excluir cadastro. Verifique se há empréstimos registrados para ele ', 'Fechar', {duration: 5000})
      }
    });
  }

  novoCliente(): void {
    this.abrirFormulario();
  }

  editarCliente(cliente: ClienteResponse): void {
    this.abrirFormulario(cliente);
  }

  private abrirFormulario(cliente?: ClienteResponse): void {
    const dialogRef = this.dialog.open(ClienteForm, {
      width: '90vw', maxWidth: '400px' ,
      data: cliente ?? null
    });

    dialogRef.afterClosed().subscribe((salvou) => {
      if (salvou) {
        this.carregarClientes();
      }
    });
  }
}

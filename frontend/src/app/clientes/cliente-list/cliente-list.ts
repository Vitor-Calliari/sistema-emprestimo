import { Component, inject, signal, OnInit } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ClienteService } from '../../core/services/cliente.service';
import { ClienteResponse } from '../../core/models/cliente.model';

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
        this.snackBar.open('Cliente excluído com sucesso', 'Fechar', {duration: 3000});
        this.carregarClientes();
      },
      error: () => this.snackBar.open('Erro ao excluir cliente', 'Fechar', {duration: 3000})
    });
  }

  novoCliente(): void {

  }
}

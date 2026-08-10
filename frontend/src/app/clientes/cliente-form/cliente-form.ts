import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MatDialogModule, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ClienteService } from '../../core/services/cliente.service';
import { ClienteResponse } from '../../core/models/cliente.model';

@Component({
  selector: 'app-cliente-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './cliente-form.html',
  styleUrl: './cliente-form.css'
})
export class ClienteForm {
  private fb = inject(FormBuilder);
  private clienteService = inject(ClienteService);
  private dialogRef = inject(MatDialogRef<ClienteForm>);
  private snackBar = inject(MatSnackBar);
  private dadosEdicao = inject<ClienteResponse | null>(MAT_DIALOG_DATA);

  salvando = false;
  modoEdicao = !!this.dadosEdicao;

  form = this.fb.nonNullable.group({
    nome: [this.dadosEdicao?.nome ?? '', [Validators.required, Validators.maxLength(150)]],
    documento: [this.dadosEdicao?.documento ?? '', [Validators.required, Validators.maxLength(20)]],
    email: [this.dadosEdicao?.email ?? '', [Validators.email]],
    telefone: [this.dadosEdicao?.telefone ?? '']
  });

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.salvando = true;
    const dto = this.form.getRawValue();

    const request = this.modoEdicao
      ? this.clienteService.atualizar(this.dadosEdicao!.id, dto)
      : this.clienteService.cadastrar(dto);

    request.subscribe({
      next: () => {
        const mensagem = this.modoEdicao
          ? 'Cliente atualizado com sucesso'
          : 'Cliente cadastrado com sucesso';
        this.snackBar.open(mensagem, 'Fechar', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.salvando = false;
        const mensagem = err.status === 409
          ? 'Ja existe um cliente cadastrado com esse documento'
          : 'Erro ao salvar cliente';
        this.snackBar.open(mensagem, 'Fechar', { duration: 3000 });
      }
    });
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }
}
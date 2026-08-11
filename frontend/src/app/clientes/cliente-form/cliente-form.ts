import { Component, inject } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { MatDialogRef, MatDialogModule, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ClienteService } from '../../core/services/cliente.service';
import { ClienteResponse } from '../../core/models/cliente.model';

function documentoValidoValidator(control: AbstractControl): ValidationErrors | null {
  const digitos = (control.value ?? '').replace(/\D/g, '');
  return digitos.length === 11 || digitos.length === 14 ? null : { documentoInvalido: true };
}

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
    documento: [this.dadosEdicao?.documento ?? '', [Validators.required, documentoValidoValidator]],
    email: [this.dadosEdicao?.email ?? '', [Validators.email]],
    telefone: [this.dadosEdicao?.telefone ?? '']
  });

  formatarDocumento(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digitos = input.value.replace(/\D/g, '').slice(0, 14);
    let formatado = digitos;

    if (digitos.length <= 11) {
      formatado = digitos
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    } else {
      formatado = digitos
        .replace(/(\d{2})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1/$2')
        .replace(/(\d{4})(\d{1,2})$/, '$1-$2');
    }

    this.form.controls.documento.setValue(formatado, { emitEvent: false });
  }

  formatarTelefone(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digitos = input.value.replace(/\D/g, '').slice(0, 11);
    let formatado = digitos;

    if (digitos.length <= 10) {
      formatado = digitos
        .replace(/(\d{2})(\d)/, '($1) $2')
        .replace(/(\d{4})(\d{1,4})$/, '$1-$2');
    } else {
      formatado = digitos
        .replace(/(\d{2})(\d)/, '($1) $2')
        .replace(/(\d{5})(\d{1,4})$/, '$1-$2');
    }

    this.form.controls.telefone.setValue(formatado, { emitEvent: false });
  }

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
          ? 'Já existe um cliente cadastrado com esse documento'
          : 'Erro ao salvar cliente';
        this.snackBar.open(mensagem, 'Fechar', { duration: 3000 });
      }
    });
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }
}
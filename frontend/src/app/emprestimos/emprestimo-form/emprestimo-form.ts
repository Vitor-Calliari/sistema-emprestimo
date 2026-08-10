import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MatDialogModule, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EmprestimoService } from '../../core/services/emprestimo.service';
import { ClienteService } from '../../core/services/cliente.service';
import { MoedaService } from '../../core/services/moeda.service';
import { ClienteResponse } from '../../core/models/cliente.model';
import { MoedaResponse } from '../../core/models/moeda.model';
import { EmprestimoResponse } from '../../core/models/emprestimo.model';
import { D } from '@angular/cdk/keycodes';

@Component({
  selector: 'app-emprestimo-form',
  standalone: true,
  imports: [
    ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatDatepickerModule, MatButtonModule
  ],
  templateUrl: './emprestimo-form.html',
  styleUrl: './emprestimo-form.css'
})
export class EmprestimoForm implements OnInit {
  private fb = inject(FormBuilder);
  private emprestimoService = inject(EmprestimoService);
  private clienteService = inject(ClienteService);
  private moedaService = inject(MoedaService);
  private dialogRef = inject(MatDialogRef<EmprestimoForm>);
  private snackBar = inject(MatSnackBar);
  private dadosEdicao = inject<EmprestimoResponse | null>(MAT_DIALOG_DATA);

  salvando = false;
  modoEdicao = !!this.dadosEdicao;
  clientes = signal<ClienteResponse[]>([]);
  moedas = signal<MoedaResponse[]>([]);

  form = this.fb.nonNullable.group({
    clienteId: [this.dadosEdicao?.clienteId ?? null as number | null, [Validators.required]],
    moedaCodigo: [(this.dadosEdicao?.moedaCodigo ?? '') as string, [Validators.required]],
    dataEmprestimo: [this.paraDate(this.dadosEdicao?.dataEmprestimo), [Validators.required]],
    valorObtido: [this.dadosEdicao ? String(this.dadosEdicao.valorObtido) : '',
      [Validators.required, Validators.pattern(/^\d+([.,]\d{1,2})?$/)]],
    dataVencimento: [
      this.dadosEdicao ? this.paraDate(this.dadosEdicao.dataVencimento) : null as Date | null,
      [Validators.required]
    ],
    taxaJurosMensal: [this.dadosEdicao ? String(this.dadosEdicao.taxaJurosMensal) : '',
      [Validators.required, Validators.pattern(/^\d+([.,]\d{1,4})?$/)]]
  });

  ngOnInit(): void {
    this.clienteService.listarTodos().subscribe((dados) => this.clientes.set(dados));
    this.moedaService.listarTodas().subscribe((dados) => this.moedas.set(dados));
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.salvando = true;
    const valores = this.form.getRawValue();

    const dto = {
      clienteId: valores.clienteId!,
      moedaCodigo: valores.moedaCodigo,
      dataEmprestimo: this.formatarData(valores.dataEmprestimo),
      valorObtido: this.paraNumero(valores.valorObtido),
      dataVencimento: this.formatarData(valores.dataVencimento!),
      taxaJurosMensal: this.paraNumero(valores.taxaJurosMensal)
    };

    const request = this.modoEdicao
      ? this.emprestimoService.atualizar(this.dadosEdicao!.id, dto)
      : this.emprestimoService.cadastrar(dto); 

    request.subscribe({
      next: () => {
        const mensagem = this.modoEdicao
          ? 'Emprestimo atualizado com sucesso'
          : 'Emprestimo cadastrado com sucesso';
        this.snackBar.open(mensagem, 'Fechar', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.salvando = false;
        const mensagem = err.status === 503
          ? 'Nao foi possivel obter a cotacao do Banco Central. Tente novamente.'
          : err.status === 400
            ? (err.error?.mensagem ?? 'Dados invalidos')
            : 'Erro ao salvar emprestimo';
        this.snackBar.open(mensagem, 'Fechar', { duration: 3000 });
      }
    });
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }

  private paraDate(dataIso?: string): Date {
    if (!dataIso) return new Date();
    const [ano, mes, dia] = dataIso.split('-').map(Number);
    return new Date(ano, mes - 1, dia);
  }

  private formatarData(data: Date): string {
    const ano = data.getFullYear();
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const dia = String(data.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
  }

  private paraNumero(valor: string): number {
    return Number(valor.replace(',', '.'));
  }
}
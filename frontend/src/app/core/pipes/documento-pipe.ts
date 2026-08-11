import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'documento',
  standalone: true
})
export class DocumentoPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    if (!value) return '-';

    const digitos = value.replace(/\D/g, '');

    if (digitos.length === 11) {
      return digitos.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    }

    if (digitos.length === 14) {
      return digitos.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5');
    }

    return value;
  }
}
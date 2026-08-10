import { Injectable } from '@angular/core';
import { NativeDateAdapter } from '@angular/material/core';

@Injectable()
export class AppDateAdapter extends NativeDateAdapter {

  override parse(value: any, parseFormat?: any): Date | null {
    if (typeof value === 'string' && value.trim().length > 0) {
      const partes = value.trim().split('-');
      if (partes.length === 3) {
        const dia = Number(partes[0]);
        const mes = Number(partes[1]) - 1;
        const ano = Number(partes[2]);
        const data = new Date(ano, mes, dia);
        return isNaN(data.getTime()) ? null : data;
      }
      return null;
    }
    return super.parse(value, parseFormat);
  }

  override format(date: Date, displayFormat: any): string {
    if (displayFormat === 'dd-MM-yyyy') {
      const dia = String(date.getDate()).padStart(2, '0');
      const mes = String(date.getMonth() + 1).padStart(2, '0');
      const ano = date.getFullYear();
      return `${dia}-${mes}-${ano}`;
    }
    return super.format(date, displayFormat);
  }
}

export const APP_DATE_FORMATS = {
  parse: {
    dateInput: 'dd-MM-yyyy'
  },
  display: {
    dateInput: 'dd-MM-yyyy',
    monthYearLabel: { year: 'numeric', month: 'short' },
    dateA11yLabel: { year: 'numeric', month: 'long', day: 'numeric' },
    monthYearA11yLabel: { year: 'numeric', month: 'long' }
  }
};
import { Routes } from '@angular/router';
import { ShellComponent } from './layout/shell/shell';
import { HomeComponent } from './home/home';
import { ClienteList } from './clientes/cliente-list/cliente-list';
import { EmprestimoList } from './emprestimos/emprestimo-list/emprestimo-list';

export const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
      { path: 'clientes', component: ClienteList },
      { path: 'emprestimos', component: EmprestimoList },
      { path: '**', redirectTo: 'home', pathMatch: 'full' }
    ]
  }
];

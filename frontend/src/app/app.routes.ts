import { Routes } from '@angular/router';
import { ShellComponent } from './layout/shell/shell';
import { ClienteList } from './clientes/cliente-list/cliente-list';

export const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: '', redirectTo: 'clientes', pathMatch: 'full' },
      { path: 'clientes', component: ClienteList }
      // as rotas de clientes e emprestimos serao adicionadas aqui
      // quando criarmos esses componentes
    ]
  }
];

export interface TotalPorMoeda {
  moedaCodigo: string;
  moedaNome: string;
  totalEmprestado: number;
  quantidadeEmprestimos: number;
}

export interface RankingCliente {
  clienteId: number;
  clienteNome: string;
  quantidadeEmprestimos: number;
  totalEmprestado: number;
}
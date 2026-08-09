export interface EmprestimoRequest {
    clienteId: number;
    moedaCodigo: string;
    dataEmprestimo: string;
    valorObtido: number;
    dataVencimento: string;
    taxaJurosMensal: number;
}

export interface EmprestimoResponse {
    id: number;
    clienteId: number;
    clienteNome: string;
    moedaCodigo: number;
    dataEmprestimo: string;
    valorObtido: number;
    taxaConversao: number;
    valorReais: number;
    dataVencumento: string;
    numeroMeses: number;
    taxaJurosMensal: number;
    valorPagarVencimento: number;
    criadoEm: string;
}
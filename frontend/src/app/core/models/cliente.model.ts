export interface ClienteRequest {
  nome: string;
  documento: string;
  email?: string;
  telefone?: string;
}

export interface ClienteResponse {
  id: number;
  nome: string;
  documento: string;
  email?: string;
  telefone?: string;
  criadoEm: string;
}
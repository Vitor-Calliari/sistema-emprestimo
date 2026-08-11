# Referência da API

URL base: `http://localhost:8080/api`

## Clientes

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/clientes` | Cadastra um novo cliente |
| `GET` | `/clientes` | Lista todos os clientes |
| `GET` | `/clientes/{id}` | Busca um cliente por id |
| `PUT` | `/clientes/{id}` | Atualiza um cliente |
| `DELETE` | `/clientes/{id}` | Exclui um cliente (falha com `409` se houver empréstimos vinculados) |

## Moedas

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/moedas` | Lista as moedas sincronizadas |
| `GET` | `/moedas/{codigo}` | Busca uma moeda por código |
| `POST` | `/moedas/sincronizar` | Sincroniza a lista de moedas com o Banco Central |

## Empréstimos

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/emprestimos` | Cadastra um novo empréstimo (busca a cotação do dia automaticamente) |
| `GET` | `/emprestimos` | Lista todos os empréstimos |
| `GET` | `/emprestimos/{id}` | Busca um empréstimo por id |
| `PUT` | `/emprestimos/{id}` | Atualiza um empréstimo (recalcula a cotação do dia da edição) |
| `DELETE` | `/emprestimos/{id}` | Exclui um empréstimo |

## Relatórios

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/relatorios/total-por-moeda` | Total emprestado agrupado por moeda |
| `GET` | `/relatorios/ranking-clientes` | Ranking de clientes com mais empréstimos |

## Exemplo — cadastrar empréstimo

```
POST /api/emprestimos
Content-Type: application/json

{
  "clienteId": 1,
  "moedaCodigo": "USD",
  "dataEmprestimo": "2026-08-08",
  "valorObtido": 1000.00,
  "dataVencimento": "2027-02-08",
  "taxaJurosMensal": 1.5
}
```

## Códigos de resposta relevantes

| Código | Situação |
|---|---|
| `400` | Dados inválidos ou malformados na requisição |
| `404` | Recurso não encontrado |
| `409` | Conflito (ex: documento duplicado, exclusão com vínculo existente) |
| `503` | Falha na integração com o Banco Central (indisponibilidade momentânea) |
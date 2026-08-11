# Arquitetura e decisões de projeto

## Modelagem do banco de dados

Três tabelas principais:

- **`cliente`**: dados cadastrais (`nome`, `documento`, `email`, `telefone`). O `documento` é normalizado antes de ser salvo (apenas dígitos, sem pontuação), evitando duplicidade disfarçada por formatação diferente do mesmo CPF/CNPJ.
- **`moeda`**: catálogo de moedas, com o **código ISO (ex: `USD`)** como chave primária — decisão tomada por refletir o próprio identificador usado pela API do Banco Central, evitando uma tradução desnecessária entre um ID interno e o código usado nas chamadas externas.
- **`emprestimo`**: referencia `cliente` e `moeda`, e guarda os valores do empréstimo. Todos os campos monetários usam `NUMERIC` (nunca `FLOAT`/`DOUBLE`), evitando erros de arredondamento de ponto flutuante em cálculos financeiros. Há uma constraint `CHECK (data_vencimento > data_emprestimo)` garantindo a integridade da regra de negócio diretamente no banco.

## Regras de negócio

- **Cotação de câmbio**: obtida da API PTAX do Banco Central na data do cadastro (busca automática do último dia útil anterior, caso a data caia em fim de semana/feriado).
- **Taxa de juros**: informada pelo usuário no cadastro do empréstimo (não vem do Banco Central). Ficou definido como evolução futura possível: usar a taxa Selic (também disponível via API do BCB) como alternativa.
- **Número de meses até o vencimento**: calculado dinamicamente (`Period.between`), nunca persistido — sempre reflete o cálculo em relação às datas cadastradas.
- **Valor a pagar no vencimento**: calculado com juros compostos (`M = C × (1 + i)ⁿ`), usando `BigDecimal` em toda a cadeia de cálculo para preservar precisão.
- **Data de vencimento**: precisa ser posterior à data do empréstimo — mas **não** necessariamente posterior à data atual, permitindo o cadastro retroativo de empréstimos já vencidos (registro histórico).

## Por que queries SQL nativas nos relatórios

O CRUD básico usa Spring Data JPA (produtividade, menos código repetitivo). Os dois relatórios do dashboard (total emprestado por moeda, ranking de clientes) usam **SQL nativo com `JOIN` e `GROUP BY`**, deliberadamente, para demonstrar domínio de SQL além da abstração do ORM — já que o contexto da vaga envolve um sistema legado com forte uso de SQL/PL-SQL.

## Segurança

### Verificado e protegido

- **SQL Injection**: não há risco — todo o CRUD passa por JPA (*prepared statements* automáticos), e as queries nativas dos relatórios são strings fixas, sem nenhuma interpolação de entrada do usuário.
- **XSS**: o Angular escapa automaticamente qualquer conteúdo interpolado no template (`{{ }}`), então dados do usuário nunca são executados como código na tela.
- **Tratamento de erros padronizado**: exceções previstas (recurso não encontrado, dados inválidos, conflito de integridade, falha na integração externa) retornam respostas HTTP consistentes e sem detalhes internos sensíveis, via `@RestControllerAdvice`.

### Limitações conhecidas

- **Sem autenticação/autorização**: não havia esse requisito no escopo do desafio. Qualquer pessoa com acesso à API pode ler/escrever dados livremente. Evolução natural: Spring Security + JWT.
- **Sem *rate limiting*** nas chamadas à API do Banco Central.
- **Sem *optimistic locking*** (`@Version`) nas entidades — edições concorrentes no mesmo registro não geram aviso de conflito.

## Integração com o Banco Central (BCB)

A chamada é feita **pelo backend**, nunca diretamente pelo frontend — evita problemas de CORS do lado do BCB e centraliza a lógica de *fallback*/tratamento de erro. Falhas de rede ou indisponibilidade do serviço são capturadas e convertidas numa resposta `503` amigável, em vez de expor o erro técnico bruto ao usuário.
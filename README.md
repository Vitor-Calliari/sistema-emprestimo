# Sistema de Gestão de Empréstimos

Aplicação full stack para cadastro de clientes e empréstimos em moeda estrangeira, desenvolvida como desafio técnico de processo seletivo para a vaga de Analista de Suporte.

A aplicação permite cadastrar clientes e empréstimos, calcula automaticamente o número de meses até o vencimento e o valor a pagar (juros compostos), e obtém a cotação de câmbio em tempo real diretamente da API de dados abertos do Banco Central do Brasil (PTAX).

## Índice

- [Funcionalidades](#funcionalidades)
- [Tecnologias e justificativas](#tecnologias-e-justificativas)
- [Arquitetura](#arquitetura)
- [Como rodar o projeto](#como-rodar-o-projeto)
- [Estrutura de pastas](#estrutura-de-pastas)
- [Documentação complementar](#documentação-complementar)

## Funcionalidades

- **Clientes**: cadastro, listagem, edição e exclusão (CRUD completo), com validação e normalização de CPF/CNPJ e telefone
- **Empréstimos**: cadastro, listagem, edição e exclusão, com:
  - Cotação de câmbio obtida automaticamente do Banco Central na data do cadastro (com *fallback* para o último dia útil em fins de semana/feriados)
  - Cálculo automático do número de meses entre a data do empréstimo e o vencimento
  - Cálculo automático do valor a pagar no vencimento, com juros compostos
- **Moedas**: sincronização automática da lista de moedas disponíveis a partir do Banco Central
- **Relatórios**: total emprestado por moeda e ranking de clientes com mais empréstimos, calculados via queries SQL nativas (`JOIN`/`GROUP BY`) diretamente no PostgreSQL
- **Interface responsiva**, com tema em tons de azul (Angular Material)

## Tecnologias e justificativas

### Backend
| Tecnologia | Por quê |
|---|---|
| Java 21 + Spring Boot 4 | Ecossistema robusto e familiaridade com o uso das tecnologias por conta da graduação |
| Spring Data JPA / Hibernate | Produtividade no CRUD básico, combinado com queries SQL nativas pontuais nos relatórios para demonstrar domínio de SQL além da abstração do ORM |
| PostgreSQL | Banco relacional robusto; uso de `NUMERIC` para todos os valores monetários, evitando erros de arredondamento de ponto flutuante |
| Bean Validation | Validação declarativa dos DTOs de entrada |
| Lombok | Redução de boilerplate (getters/setters/construtores) |

### Integração externa
API PTAX (dados abertos) do Banco Central do Brasil, para lista de moedas e cotação de câmbio. A integração trata: ausência de boletim em fins de semana/feriados (busca automática do último dia útil anterior), e falhas de rede/indisponibilidade do serviço externo (retorna erro `503` amigável em vez de expor detalhes internos).

### Frontend
| Tecnologia | Por quê |
|---|---|
| Angular 22 (standalone components + signals) | Versão atual do framework, sem dependência de `NgModule` |
| Angular Material | Componentes de UI prontos e acessíveis, com tema customizado em tons de azul |
| Angular CDK (`BreakpointObserver`) | Responsividade (sidebar fixa em telas grandes, *overlay* em telas pequenas) |
| Reactive Forms | Validação de formulários com feedback imediato, espelhando as validações do backend |

### Infraestrutura
Docker (PostgreSQL containerizado via `docker-compose.yml`).

## Arquitetura

```
Angular (localhost:4200)
        |
        |  HTTP/JSON (REST)
        v
Spring Boot API (localhost:8080)
        |
        |-- JPA/Hibernate --> PostgreSQL (localhost:5432, via Docker)
        |
        '-- RestTemplate --> API PTAX do Banco Central (externa)
```

O backend segue arquitetura em camadas: `Controller` (endpoints REST) → `Service` (regras de negócio e cálculos) → `Repository` (acesso a dados via JPA). A integração com o Banco Central fica isolada numa camada `client/`, desacoplada do restante da aplicação.

## Como rodar o projeto

Guia resumido — o passo a passo detalhado está em [`docs/COMO_RODAR.md`](docs/COMO_RODAR.md).

**Pré-requisitos**: Docker, JDK 21+, Node.js 20+ e Angular CLI 22+.

```bash
# 1. Subir o banco de dados
docker compose up -d

# 2. Rodar o backend (na pasta backend/)
# via IDE (IntelliJ) ou:
./mvnw spring-boot:run

# 3. Rodar o frontend (na pasta frontend/)
npm install
ng serve
```

Acesse a aplicação em `http://localhost:4200`.

## Estrutura de pastas

```
sistema-emprestimo/
├── docker-compose.yml       # PostgreSQL containerizado
├── db/
│   └── schema.sql           # script DDL (cliente, moeda, emprestimo)
├── docs/                    # documentação complementar
├── backend/                 # API REST (Spring Boot)
│   └── src/main/java/.../
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── model/
│       ├── dto/
│       ├── client/          # integração com o Banco Central
│       └── exception/
└── frontend/                 # SPA (Angular)
    └── src/app/
        ├── core/             # models e services HTTP
        ├── layout/           # shell (toolbar + sidebar)
        ├── home/             # dashboard com relatórios
        ├── clientes/
        └── emprestimos/
```

## Documentação complementar

- [`docs/COMO_RODAR.md`](docs/COMO_RODAR.md) — passo a passo detalhado de configuração e execução
- [`docs/ARQUITETURA.md`](docs/ARQUITETURA.md) — decisões de arquitetura, regras de negócio e considerações de segurança
- [`docs/API.md`](docs/API.md) — referência dos endpoints da API

## Autor

Vitor Calliari
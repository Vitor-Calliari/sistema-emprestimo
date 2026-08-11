# Como rodar o projeto

## Pré-requisitos

- **Docker** e Docker Compose
- **JDK 21** ou superior (projeto testado com Spring Boot 4 / Spring Framework 7)
- **Node.js 20+** e **Angular CLI 22+** (`npm install -g @angular/cli`)
- Um cliente SQL (recomendado: **DBeaver**), opcional, apenas para inspecionar o banco manualmente

> **Nota de compatibilidade**: o projeto usa Spring Boot 4, que introduziu algumas mudanças de API em relação à versão 3.x (ex: `UriComponentsBuilder.fromHttpUrl(...)` foi removido em favor de `fromUriString(...)`). Se ao clonar o projeto aparecerem erros de compilação relacionados a métodos "cannot find symbol", provavelmente é uma divergência de versão do Spring Boot — confira o `pom.xml` e ajuste a versão do `spring-boot-starter-parent` conforme o Spring Boot instalado no seu ambiente.

## 1. Clonar o repositório

```bash
git clone https://github.com/Vitor-Calliari/sistema-emprestimo.git
cd sistema-emprestimo
```

## 2. Subir o banco de dados (Docker)

Na raiz do repositório, onde está o `docker-compose.yml`:

```bash
docker compose up -d
```

Isso sobe um container PostgreSQL na porta `5432`, com o banco `emprestimos` já criado (usuário e senha definidos no próprio `docker-compose.yml`).

Confirme que o container está de pé:

```bash
docker ps
```

## 3. Criar o schema do banco

Execute o script `db/schema.sql` no banco `emprestimos` (via DBeaver, `psql`, ou qualquer cliente SQL de sua preferência). Esse script cria as tabelas `cliente`, `moeda` e `emprestimo`, com as constraints de integridade (chaves estrangeiras, `CHECK` de datas, etc).

## 4. Rodar o backend

Abra a pasta `backend/` no IntelliJ (ou outra IDE de sua preferência) como projeto Maven.

Confirme que `backend/src/main/resources/application.properties` aponta para as mesmas credenciais definidas no `docker-compose.yml`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/emprestimos
spring.datasource.username=emprestimos_user
spring.datasource.password=emprestimos_pass
```

Rode a classe principal (`EmprestimosApplication`), ou via terminal:

```bash
cd backend
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. Confirme acessando `http://localhost:8080/api/clientes` (deve retornar uma lista vazia `[]` na primeira execução).

### Sincronizar as moedas

Antes de cadastrar o primeiro empréstimo, sincronize a lista de moedas com o Banco Central:

```
POST http://localhost:8080/api/moedas/sincronizar
```

(pode ser feito via Postman, `curl`, ou qualquer cliente HTTP)

## 5. Rodar o frontend

Em outro terminal:

```bash
cd frontend
npm install
ng serve
```

Acesse `http://localhost:4200` no navegador.

## Solução de problemas comuns

| Sintoma | Causa provável |
|---|---|
| Erro de CORS no console do navegador | Backend não está rodando, ou a configuração de CORS não libera `http://localhost:4200` |
| `ECONNREFUSED` na porta 8080 | Backend não está rodando (verifique o console da IDE) |
| Erro de conexão com o banco ao subir o backend | Container do Docker não está rodando, ou a porta 5432 já está em uso por outra instância local do Postgres |
| `503` ao cadastrar empréstimo | Instabilidade momentânea na API do Banco Central (dependência externa, fora do controle da aplicação) — tente novamente em alguns instantes |
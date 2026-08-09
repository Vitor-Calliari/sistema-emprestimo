CREATE TABLE cliente (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    documento       VARCHAR(20) NOT NULL UNIQUE,
    email           VARCHAR(150),
    telefone        VARCHAR(20),
    criado_em       TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE moeda (
    codigo          VARCHAR(3) PRIMARY KEY,
    nome            VARCHAR(50) NOT NULL
);

CREATE TABLE emprestimo (
    id                      BIGSERIAL PRIMARY KEY,
    cliente_id              BIGINT NOT NULL REFERENCES cliente(id),
    moeda_codigo            VARCHAR(3) NOT NULL REFERENCES moeda(codigo),
    data_emprestimo         DATE NOT NULL,
    valor_obtido            NUMERIC(15,2) NOT NULL,
    taxa_conversao          NUMERIC(15,6) NOT NULL,
    valor_reais             NUMERIC(15,2) NOT NULL,
    data_vencimento         DATE NOT NULL,
    taxa_juros_mensal       NUMERIC(6,4) NOT NULL,   -- informada pelo usuário, ex: 1.50 = 1,5% a.m.
    valor_pagar_vencimento  NUMERIC(15,2) NOT NULL,   -- calculado com juros compostos e persistido
    criado_em               TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT chk_datas CHECK (data_vencimento > data_emprestimo)
);
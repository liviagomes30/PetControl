-- Script adaptado para PostgreSQL a partir de Oracle SQL Developer

CREATE TABLE acertoestoque (
    idacerto          INTEGER PRIMARY KEY,
    data              DATE NOT NULL,
    usuario_pessoa_id INTEGER NOT NULL,
    motivo            VARCHAR(500) NOT NULL,
    observacao        VARCHAR(500)
);

COMMENT ON TABLE acertoestoque IS 'Registra operações de acerto de estoque';

CREATE TABLE adocao (
    idadocao        INTEGER PRIMARY KEY,
    idadotante      INTEGER NOT NULL,
    idanimal        INTEGER NOT NULL,
    dataadocao      DATE NOT NULL,
    pessoa_idpessoa INTEGER NOT NULL,
    obs             VARCHAR(500)
);

CREATE TABLE agendavacinacao (
    idagendavacinacao       INTEGER PRIMARY KEY,
    animal_idanimal         INTEGER NOT NULL,
    vacina_idproduto        INTEGER NOT NULL,
    data                    DATE,
    motivo                  VARCHAR(500),
    usuario_pessoa_idpessoa INTEGER NOT NULL
);

CREATE TABLE animal (
    idanimal    INTEGER PRIMARY KEY,
    nome        VARCHAR(255),
    especie     VARCHAR(100),
    idade       INTEGER,
    idhistorico INTEGER NOT NULL,
    raca        VARCHAR(100)
);

CREATE TABLE estoque (
    idestoque  INTEGER PRIMARY KEY,
    idproduto  INTEGER NOT NULL,
    quantidade INTEGER NOT NULL
);

CREATE TABLE evento (
    idevento        INTEGER PRIMARY KEY,
    descricao       VARCHAR(500),
    data            DATE,
    foto            XML,
    animal_idanimal INTEGER NOT NULL,
    local           VARCHAR(255)
);

CREATE TABLE historico (
    idhistorico           INTEGER PRIMARY KEY,
    descricao             VARCHAR(1000) NOT NULL,
    data                  DATE,
    animal_idanimal       INTEGER NOT NULL,
    vacinacao_idvacinacao INTEGER NOT NULL,
    medicacao_idmedicacao INTEGER NOT NULL
);

CREATE TABLE itemacertoestoque (
    iditem            INTEGER PRIMARY KEY,
    acerto_id         INTEGER NOT NULL,
    produto_id        INTEGER NOT NULL,
    quantidade_antes  NUMERIC(10, 2) NOT NULL,
    quantidade_depois NUMERIC(10, 2) NOT NULL,
    tipoajuste        VARCHAR(10) NOT NULL,
    CONSTRAINT chk_tipoajuste CHECK ( tipoajuste IN ( 'ENTRADA', 'SAIDA' ) )
);

COMMENT ON TABLE itemacertoestoque IS 'Detalhes dos produtos envolvidos em cada acerto de estoque';

CREATE TABLE itemmovimentacao (
    iditem                INTEGER PRIMARY KEY,
    movimentacao_id       INTEGER NOT NULL,
    produto_id            INTEGER NOT NULL,
    quantidade            NUMERIC(10, 2) NOT NULL,
    motivomovimentacao_id INTEGER
);

COMMENT ON TABLE itemmovimentacao IS 'Itens de produtos envolvidos em cada movimentação';

CREATE TABLE medicacao (
    idmedicacao                            INTEGER PRIMARY KEY,
    idanimal                               INTEGER NOT NULL,
    idhistorico                            INTEGER NOT NULL,
    posologia_medicamento_idproduto        INTEGER NOT NULL,
    posologia_receitamedicamento_idreceita INTEGER NOT NULL,
    data                                   DATE
);

CREATE TABLE medicamento (
    idproduto  INTEGER PRIMARY KEY,
    composicao VARCHAR(50) NOT NULL
);

CREATE TABLE motivomovimentacao (
    idmotivo  INTEGER PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    tipo      VARCHAR(10) NOT NULL,
    CONSTRAINT chk_tipomotivo CHECK ( tipo IN ( 'AMBOS', 'ENTRADA', 'SAIDA' ) )
);

COMMENT ON TABLE motivomovimentacao IS 'Categoriza os motivos de entrada e saída de produtos';

CREATE TABLE movimentacaoestoque (
    idmovimentacao    INTEGER PRIMARY KEY,
    tipomovimentacao  VARCHAR(10) NOT NULL,
    data              DATE NOT NULL,
    usuario_pessoa_id INTEGER NOT NULL,
    obs               VARCHAR(500),
    fornecedor        VARCHAR(20),
    CONSTRAINT chk_tipomovimento CHECK ( tipomovimentacao IN ( 'ENTRADA', 'SAIDA' ) )
);

COMMENT ON TABLE movimentacaoestoque IS 'Registra todas as movimentações de estoque (entradas e saídas)';

CREATE TABLE pessoa (
    idpessoa INTEGER PRIMARY KEY,
    nome     VARCHAR(255) NOT NULL,
    cpf      VARCHAR(14) NOT NULL UNIQUE,
    endereco VARCHAR(500),
    telefone VARCHAR(20),
    email    VARCHAR(255)
);

CREATE TABLE posologia (
    dose                         VARCHAR(50) NOT NULL,
    quantidadedias               INTEGER NOT NULL,
    intervalohoras               INTEGER NOT NULL,
    medicamento_idproduto        INTEGER NOT NULL,
    receitamedicamento_idreceita INTEGER NOT NULL,
    PRIMARY KEY (medicamento_idproduto, receitamedicamento_idreceita)
);

CREATE TABLE produto (
    idproduto       INTEGER PRIMARY KEY,
    nome            VARCHAR(255) NOT NULL,
    idtipoproduto   INTEGER NOT NULL,
    idunidademedida INTEGER NOT NULL,
    fabricante      VARCHAR(255)
);

CREATE TABLE receitamedicamento (
    idreceita       INTEGER PRIMARY KEY,
    data            DATE,
    medico          VARCHAR(255),
    clinica         VARCHAR(255),
    animal_idanimal INTEGER NOT NULL
);

CREATE TABLE tipoproduto (
    idtipoproduto INTEGER PRIMARY KEY,
    descricao     VARCHAR(255) NOT NULL
);

CREATE TABLE unidadedemedida (
    idunidademedida INTEGER PRIMARY KEY,
    descricao       VARCHAR(255) NOT NULL
);

CREATE TABLE usuario (
    login           VARCHAR(50),
    senha           VARCHAR(255),
    pessoa_idpessoa INTEGER PRIMARY KEY
);

CREATE TABLE vacina (
    idproduto INTEGER PRIMARY KEY,
    lote      VARCHAR(50) NOT NULL,
    validade  DATE NOT NULL
);

CREATE TABLE vacinacao (
    idvacinacao INTEGER PRIMARY KEY,
    idvacina    INTEGER NOT NULL,
    idanimal    INTEGER NOT NULL,
    idhistorico INTEGER NOT NULL,
    data        DATE,
    local       VARCHAR(255)
);

-- Foreign Keys

ALTER TABLE acertoestoque
    ADD FOREIGN KEY (usuario_pessoa_id) REFERENCES usuario (pessoa_idpessoa);

ALTER TABLE adocao
    ADD FOREIGN KEY (idanimal) REFERENCES animal (idanimal),
    ADD FOREIGN KEY (pessoa_idpessoa) REFERENCES pessoa (idpessoa);

ALTER TABLE agendavacinacao
    ADD FOREIGN KEY (animal_idanimal) REFERENCES animal (idanimal),
    ADD FOREIGN KEY (usuario_pessoa_idpessoa) REFERENCES usuario (pessoa_idpessoa),
    ADD FOREIGN KEY (vacina_idproduto) REFERENCES vacina (idproduto);

ALTER TABLE estoque
    ADD FOREIGN KEY (idproduto) REFERENCES produto (idproduto);

ALTER TABLE evento
    ADD FOREIGN KEY (animal_idanimal) REFERENCES animal (idanimal);

ALTER TABLE historico
    ADD FOREIGN KEY (animal_idanimal) REFERENCES animal (idanimal),
    ADD FOREIGN KEY (medicacao_idmedicacao) REFERENCES medicacao (idmedicacao),
    ADD FOREIGN KEY (vacinacao_idvacinacao) REFERENCES vacinacao (idvacinacao);

ALTER TABLE itemacertoestoque
    ADD FOREIGN KEY (acerto_id) REFERENCES acertoestoque (idacerto),
    ADD FOREIGN KEY (produto_id) REFERENCES produto (idproduto);

ALTER TABLE itemmovimentacao
    ADD FOREIGN KEY (motivomovimentacao_id) REFERENCES motivomovimentacao (idmotivo),
    ADD FOREIGN KEY (movimentacao_id) REFERENCES movimentacaoestoque (idmovimentacao),
    ADD FOREIGN KEY (produto_id) REFERENCES produto (idproduto);

ALTER TABLE medicacao
    ADD FOREIGN KEY (idanimal) REFERENCES animal (idanimal),
    ADD FOREIGN KEY (posologia_medicamento_idproduto, posologia_receitamedicamento_idreceita)
        REFERENCES posologia (medicamento_idproduto, receitamedicamento_idreceita);

ALTER TABLE medicamento
    ADD FOREIGN KEY (idproduto) REFERENCES produto (idproduto);

ALTER TABLE movimentacaoestoque
    ADD FOREIGN KEY (usuario_pessoa_id) REFERENCES usuario (pessoa_idpessoa);

ALTER TABLE posologia
    ADD FOREIGN KEY (medicamento_idproduto) REFERENCES medicamento (idproduto),
    ADD FOREIGN KEY (receitamedicamento_idreceita) REFERENCES receitamedicamento (idreceita);

ALTER TABLE produto
    ADD FOREIGN KEY (idtipoproduto) REFERENCES tipoproduto (idtipoproduto),
    ADD FOREIGN KEY (idunidademedida) REFERENCES unidadedemedida (idunidademedida);

ALTER TABLE receitamedicamento
    ADD FOREIGN KEY (animal_idanimal) REFERENCES animal (idanimal);

ALTER TABLE usuario
    ADD FOREIGN KEY (pessoa_idpessoa) REFERENCES pessoa (idpessoa);

ALTER TABLE vacina
    ADD FOREIGN KEY (idproduto) REFERENCES produto (idproduto);

ALTER TABLE vacinacao
    ADD FOREIGN KEY (idanimal) REFERENCES animal (idanimal),
    ADD FOREIGN KEY (idvacina) REFERENCES vacina (idproduto);

-- Criação de sequences para autoincremento de IDs
CREATE SEQUENCE seq_acertoestoque;
CREATE SEQUENCE seq_adocao;
CREATE SEQUENCE seq_agendavacinacao;
CREATE SEQUENCE seq_animal;
CREATE SEQUENCE seq_estoque;
CREATE SEQUENCE seq_evento;
CREATE SEQUENCE seq_historico;
CREATE SEQUENCE seq_itemacertoestoque;
CREATE SEQUENCE seq_itemmovimentacao;
CREATE SEQUENCE seq_medicacao;
CREATE SEQUENCE seq_motivomovimentacao;
CREATE SEQUENCE seq_movimentacaoestoque;
CREATE SEQUENCE seq_pessoa;
CREATE SEQUENCE seq_produto;
CREATE SEQUENCE seq_receitamedicamento;
CREATE SEQUENCE seq_tipoproduto;
CREATE SEQUENCE seq_unidadedemedida;
CREATE SEQUENCE seq_vacinacao;

-- Alterando colunas para usar nextval da sequence correspondente
ALTER TABLE acertoestoque ALTER COLUMN idacerto SET DEFAULT nextval('seq_acertoestoque');
ALTER TABLE adocao ALTER COLUMN idadocao SET DEFAULT nextval('seq_adocao');
ALTER TABLE agendavacinacao ALTER COLUMN idagendavacinacao SET DEFAULT nextval('seq_agendavacinacao');
ALTER TABLE animal ALTER COLUMN idanimal SET DEFAULT nextval('seq_animal');
ALTER TABLE estoque ALTER COLUMN idestoque SET DEFAULT nextval('seq_estoque');
ALTER TABLE evento ALTER COLUMN idevento SET DEFAULT nextval('seq_evento');
ALTER TABLE historico ALTER COLUMN idhistorico SET DEFAULT nextval('seq_historico');
ALTER TABLE itemacertoestoque ALTER COLUMN iditem SET DEFAULT nextval('seq_itemacertoestoque');
ALTER TABLE itemmovimentacao ALTER COLUMN iditem SET DEFAULT nextval('seq_itemmovimentacao');
ALTER TABLE medicacao ALTER COLUMN idmedicacao SET DEFAULT nextval('seq_medicacao');
ALTER TABLE motivomovimentacao ALTER COLUMN idmotivo SET DEFAULT nextval('seq_motivomovimentacao');
ALTER TABLE movimentacaoestoque ALTER COLUMN idmovimentacao SET DEFAULT nextval('seq_movimentacaoestoque');
ALTER TABLE pessoa ALTER COLUMN idpessoa SET DEFAULT nextval('seq_pessoa');
ALTER TABLE produto ALTER COLUMN idproduto SET DEFAULT nextval('seq_produto');
ALTER TABLE receitamedicamento ALTER COLUMN idreceita SET DEFAULT nextval('seq_receitamedicamento');
ALTER TABLE tipoproduto ALTER COLUMN idtipoproduto SET DEFAULT nextval('seq_tipoproduto');
ALTER TABLE unidadedemedida ALTER COLUMN idunidademedida SET DEFAULT nextval('seq_unidadedemedida');
ALTER TABLE vacinacao ALTER COLUMN idvacinacao SET DEFAULT nextval('seq_vacinacao');

-- Inserindo pessoas e usuários
INSERT INTO pessoa (nome, cpf, endereco, telefone, email)
VALUES ('João Silva', '123.456.789-00', 'Rua A, 123', '11999999999', 'joao@email.com');

INSERT INTO usuario (login, senha, pessoa_idpessoa)
VALUES ('joao', 'senha123', currval('seq_pessoa'));

-- Tipos e unidades
INSERT INTO tipoproduto (descricao) VALUES ('Medicamento');
INSERT INTO unidadedemedida (descricao) VALUES ('ml');

-- Produto, vacina e medicamento
INSERT INTO produto (nome, idtipoproduto, idunidademedida, fabricante)
VALUES ('Vacina XYZ', currval('seq_tipoproduto'), currval('seq_unidadedemedida'), 'LabABC');

INSERT INTO vacina (idproduto, lote, validade)
VALUES (currval('seq_produto'), 'L12345', '2025-12-31');

INSERT INTO medicamento (idproduto, composicao)
VALUES (currval('seq_produto'), 'Composto X');

-- Animal
INSERT INTO animal (nome, especie, idade, idhistorico, raca)
VALUES ('Rex', 'Cachorro', 3, 1, 'Vira-lata');

-- Receita médica
INSERT INTO receitamedicamento (data, medico, clinica, animal_idanimal)
VALUES ('2024-05-01', 'Dr. Veterinário', 'Clínica Pet', currval('seq_animal'));

-- Posologia
INSERT INTO posologia (dose, quantidadedias, intervalohoras, medicamento_idproduto, receitamedicamento_idreceita)
VALUES ('10ml', 5, 8, currval('seq_produto'), currval('seq_receitamedicamento'));

-- Vacinação
INSERT INTO vacinacao (idvacina, idanimal, idhistorico, data, local)
VALUES (currval('seq_produto'), currval('seq_animal'), 1, '2024-05-01', 'Clínica Pet');

-- Estoque
INSERT INTO estoque (idproduto, quantidade)
VALUES (currval('seq_produto'), 100);

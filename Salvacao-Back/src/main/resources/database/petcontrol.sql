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
    idanimal        INTEGER PRIMARY KEY,
    nome            VARCHAR(255),
    especie         VARCHAR(100),
    datanascimento  DATE,
    raca            VARCHAR(100),
    porte           VARCHAR(50),
    sexo            VARCHAR(10),
    status          VARCHAR(50),
    dataresgate     DATE,
    foto            VARCHAR(500),
    castrado        BOOLEAN DEFAULT FALSE,
    cor             VARCHAR(50)
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
    foto            VARCHAR(500),
    animal_idanimal INTEGER NOT NULL,
    local           VARCHAR(255),
    responsavel     VARCHAR(255),
    status          VARCHAR(50)
);

CREATE TABLE historico (
    idhistorico           INTEGER PRIMARY KEY,
    descricao             VARCHAR(1000) NOT NULL,
    data                  DATE,
    animal_idanimal       INTEGER NOT NULL,
    vacinacao_idvacinacao INTEGER,
    medicacao_idmedicacao INTEGER
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
    posologia_receitamedicamento_idreceita INTEGER,
    data                                   DATE
);

COMMENT ON COLUMN medicacao.posologia_receitamedicamento_idreceita IS 'ID da receita médica. NULL para medicações sem prescrição.';

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
    frequencia_diaria            INTEGER,
    medicamento_idproduto        INTEGER NOT NULL,
    receitamedicamento_idreceita INTEGER NOT NULL,
    PRIMARY KEY (medicamento_idproduto, receitamedicamento_idreceita)
);

CREATE TABLE produto (
    idproduto       INTEGER PRIMARY KEY,
    nome            VARCHAR(255) NOT NULL,
    idtipoproduto   INTEGER NOT NULL,
    idunidademedida INTEGER NOT NULL,
    fabricante      VARCHAR(255),
    preco           NUMERIC(10, 2),
    estoque_minimo  INTEGER DEFAULT 0,
    data_cadastro   DATE DEFAULT CURRENT_DATE,
    ativo           BOOLEAN DEFAULT TRUE
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
    descricao       VARCHAR(255) NOT NULL,
    sigla           VARCHAR(10)
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
    ADD FOREIGN KEY (idhistorico) REFERENCES historico (idhistorico),
    ADD FOREIGN KEY (posologia_medicamento_idproduto) REFERENCES medicamento (idproduto),
    ADD FOREIGN KEY (posologia_receitamedicamento_idreceita) REFERENCES receitamedicamento (idreceita);

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

-- Altera a tabela de estoque para aceitar quantidades fracionadas
ALTER TABLE estoque
ALTER COLUMN quantidade TYPE NUMERIC(10, 2);

-- Adiciona uma coluna na tabela de medicação para registrar a quantidade exata que foi administrada
ALTER TABLE medicacao
ADD COLUMN quantidade_administrada NUMERIC(10, 2);

COMMENT ON COLUMN medicacao.quantidade_administrada IS 'Quantidade exata do medicamento que foi administrada na aplicação.';

ALTER TABLE receitamedicamento
ADD COLUMN status VARCHAR(20) DEFAULT 'ATIVA' NOT NULL;

COMMENT ON COLUMN receitamedicamento.status IS 'Status da receita (ATIVA, CONCLUIDA)';

-- =====================================
-- DADOS DE TESTE ABRANGENTES
-- =====================================

-- Inserindo pessoas e usuários
INSERT INTO pessoa (nome, cpf, endereco, telefone, email) VALUES 
('João Silva', '123.456.789-00', 'Rua A, 123', '11999999999', 'joao@email.com'),
('Maria Santos', '987.654.321-00', 'Avenida B, 456', '11888888888', 'maria@email.com'),
('Pedro Costa', '456.789.123-00', 'Rua C, 789', '11777777777', 'pedro@email.com'),
('Ana Paula', '321.654.987-00', 'Alameda D, 321', '11666666666', 'ana@email.com'),
('Carlos Souza', '789.123.456-00', 'Travessa E, 654', '11555555555', 'carlos@email.com');

INSERT INTO usuario (login, senha, pessoa_idpessoa) VALUES 
('joao', 'senha123', 1),
('maria', 'senha456', 2),
('pedro', 'senha789', 3),
('ana', 'senha321', 4),
('carlos', 'senha654', 5);

-- Tipos de produtos
INSERT INTO tipoproduto (descricao) VALUES 
('Medicamento'),
('Vacina'),
('Suplemento'),
('Material Cirúrgico'),
('Ração'),
('Brinquedo'),
('Higiene');

-- Unidades de medida
INSERT INTO unidadedemedida (descricao, sigla) VALUES 
('Mililitro', 'ml'),
('Miligrama', 'mg'),
('Comprimido', 'comp'),
('Unidade', 'un'),
('Quilograma', 'kg'),
('Grama', 'g'),
('Litro', 'l');

-- Produtos diversos
INSERT INTO produto (nome, idtipoproduto, idunidademedida, fabricante, preco, estoque_minimo, data_cadastro, ativo) VALUES 
('Vacina Antirrábica', 2, 1, 'LabVet', 35.50, 10, '2024-01-01', TRUE),
('Vermífugo Canino', 1, 3, 'PetMed', 12.80, 20, '2024-01-01', TRUE),
('Ração Premium Cães', 5, 5, 'NutriPet', 89.90, 5, '2024-01-01', TRUE),
('Anti-inflamatório', 1, 2, 'VetFarma', 18.75, 15, '2024-01-01', TRUE),
('Vitamina B12', 3, 1, 'SupVet', 24.30, 12, '2024-01-01', TRUE),
('Seringa 10ml', 4, 4, 'MedEquip', 2.50, 50, '2024-01-01', TRUE),
('Shampoo Antisséptico', 7, 1, 'CleanPet', 15.60, 8, '2024-01-01', TRUE),
('Vacina V10', 2, 1, 'LabVet', 45.00, 8, '2024-01-01', TRUE),
('Antibiótico Amoxilina', 1, 2, 'VetFarma', 22.40, 18, '2024-01-01', TRUE),
('Ração Gatos Filhotes', 5, 5, 'FelineFood', 67.80, 6, '2024-01-01', TRUE);

-- Vacinas
INSERT INTO vacina (idproduto, lote, validade) VALUES 
(1, 'VAR001', '2025-12-31'),
(8, 'V10002', '2025-06-30');

-- Medicamentos
INSERT INTO medicamento (idproduto, composicao) VALUES 
(2, 'Praziquantel + Pirantel'),
(4, 'Meloxicam 0,5mg'),
(9, 'Amoxilina 500mg');

INSERT INTO animal (nome, especie, datanascimento, raca, porte, sexo, status, dataresgate, foto, castrado, cor) VALUES 
('Rex', 'Cachorro', '2021-01-15', 'Vira-lata', 'Médio', 'Macho', 'Disponível', '2023-12-01', NULL, TRUE, 'Marrom'),
('Mimi', 'Gato', '2022-02-20', 'Persa', 'Pequeno', 'Fêmea', 'Disponível', '2024-01-10', NULL, TRUE, 'Branco'),
('Bolt', 'Cachorro', '2019-03-10', 'Pastor Alemão', 'Grande', 'Macho', 'Disponível', '2023-11-15', NULL, FALSE, 'Preto e Marrom'),
('Luna', 'Gato', '2023-04-05', 'Siamês', 'Pequeno', 'Fêmea', 'Disponível', '2024-02-01', NULL, FALSE, 'Creme'),
('Thor', 'Cachorro', '2020-05-12', 'Golden Retriever', 'Grande', 'Macho', 'Disponível', '2023-10-20', NULL, TRUE, 'Dourado'),
('Nala', 'Gato', '2021-06-01', 'Vira-lata', 'Pequeno', 'Fêmea', 'Disponível', '2024-01-25', NULL, TRUE, 'Cinza'),
('Max', 'Cachorro', '2018-07-15', 'Bulldog', 'Médio', 'Macho', 'Disponível', '2023-09-30', NULL, TRUE, 'Branco e Marrom'),
('Bella', 'Cachorro', '2022-08-01', 'Poodle', 'Pequeno', 'Fêmea', 'Disponível', '2024-03-05', NULL, FALSE, 'Branco');

INSERT INTO historico (descricao, data, animal_idanimal, vacinacao_idvacinacao, medicacao_idmedicacao) VALUES 
('Vacinação antirrábica e vermifugação', '2024-01-15', 1, NULL, NULL),
('Tratamento anti-inflamatório para artrite', '2024-02-20', 2, NULL, NULL),
('Vacinação V10 e tratamento antibiótico', '2024-03-10', 3, NULL, NULL),
('Vacinação de rotina', '2024-04-05', 4, NULL, NULL),
('Vacinação antirrábica', '2024-05-12', 5, NULL, NULL),
('Consulta de rotina', '2024-06-01', 6, NULL, NULL),
('Check-up geral', '2024-06-15', 7, NULL, NULL),
('Consulta dermatológica', '2024-07-01', 8, NULL, NULL);



-- Receitas médicas
INSERT INTO receitamedicamento (data, medico, clinica, animal_idanimal) VALUES 
('2024-01-15', 'Dr. João Veterinário', 'Clínica VetCare', 1),
('2024-02-20', 'Dra. Maria Pet', 'Hospital Animal', 2),
('2024-03-10', 'Dr. Pedro Silva', 'Clínica VetCare', 3),
('2024-04-05', 'Dra. Ana Costa', 'Centro Veterinário', 4),
('2024-05-12', 'Dr. Carlos Vet', 'Hospital Animal', 5);

-- Posologias
INSERT INTO posologia (dose, quantidadedias, intervalohoras, frequencia_diaria, medicamento_idproduto, receitamedicamento_idreceita) VALUES 
('1 comprimido', 7, 24, 1, 2, 1),
('0.5ml', 5, 12, 2, 4, 2),
('250mg', 10, 8, 3, 9, 3);

-- Medicações
INSERT INTO medicacao (idanimal, idhistorico, posologia_medicamento_idproduto, posologia_receitamedicamento_idreceita, data) VALUES 
(1, 1, 2, 1, '2024-01-15'),
(2, 2, 4, 2, '2024-02-20'),
(3, 3, 9, 3, '2024-03-10');

-- Vacinações
INSERT INTO vacinacao (idvacina, idanimal, idhistorico, data, local) VALUES 
(1, 1, 1, '2024-01-15', 'Clínica VetCare'),
(8, 2, 2, '2024-02-20', 'Hospital Animal'),
(1, 3, 3, '2024-03-10', 'Clínica VetCare'),
(8, 4, 4, '2024-04-05', 'Centro Veterinário'),
(1, 5, 5, '2024-05-12', 'Hospital Animal');

-- Update historico records to reference vacinacao and medicacao
UPDATE historico SET vacinacao_idvacinacao = 1, medicacao_idmedicacao = 1 WHERE idhistorico = 1;
UPDATE historico SET vacinacao_idvacinacao = 2, medicacao_idmedicacao = 2 WHERE idhistorico = 2;
UPDATE historico SET vacinacao_idvacinacao = 3, medicacao_idmedicacao = 3 WHERE idhistorico = 3;
UPDATE historico SET vacinacao_idvacinacao = 4 WHERE idhistorico = 4;
UPDATE historico SET vacinacao_idvacinacao = 5 WHERE idhistorico = 5;



-- Estoque inicial
INSERT INTO estoque (idproduto, quantidade) VALUES 
(1, 50),   -- Vacina Antirrábica
(2, 100),  -- Vermífugo Canino
(3, 25),   -- Ração Premium Cães
(4, 80),   -- Anti-inflamatório
(5, 60),   -- Vitamina B12
(6, 200),  -- Seringa 10ml
(7, 30),   -- Shampoo Antisséptico
(8, 45),   -- Vacina V10
(9, 75),   -- Antibiótico Amoxilina
(10, 20);  -- Ração Gatos Filhotes

-- Motivos de movimentação
INSERT INTO motivomovimentacao (descricao, tipo) VALUES 
('Compra de fornecedor', 'ENTRADA'),
('Doação recebida', 'ENTRADA'),
('Devolução de cliente', 'ENTRADA'),
('Venda para cliente', 'SAIDA'),
('Uso em tratamento', 'SAIDA'),
('Produto vencido', 'SAIDA'),
('Transferência entre clínicas', 'AMBOS'),
('Perda/Quebra', 'SAIDA');

-- Movimentações de estoque
INSERT INTO movimentacaoestoque (tipomovimentacao, data, usuario_pessoa_id, obs, fornecedor) VALUES 
('ENTRADA', '2024-01-10', 1, 'Compra mensal de medicamentos', 'FornecedorA'),
('ENTRADA', '2024-02-15', 2, 'Doação de vacinas', NULL),
('SAIDA', '2024-03-20', 3, 'Tratamentos realizados no mês', NULL),
('ENTRADA', '2024-04-05', 1, 'Reposição de estoque', 'FornecedorB'),
('SAIDA', '2024-05-10', 4, 'Vendas do período', NULL);

-- Itens de movimentação
INSERT INTO itemmovimentacao (movimentacao_id, produto_id, quantidade, motivomovimentacao_id) VALUES 
(1, 1, 30, 1),    -- Entrada vacina antirrábica
(1, 4, 50, 1),    -- Entrada anti-inflamatório
(2, 8, 20, 2),    -- Doação vacina V10
(3, 2, 15, 5),    -- Saída vermífugo para tratamento
(3, 9, 10, 5),    -- Saída antibiótico para tratamento
(4, 6, 100, 1),   -- Entrada seringas
(5, 3, 5, 4),     -- Venda ração premium
(5, 7, 3, 4);     -- Venda shampoo

-- Acertos de estoque
INSERT INTO acertoestoque (data, usuario_pessoa_id, motivo, observacao) VALUES 
('2024-06-01', 1, 'Inventário mensal', 'Ajuste após contagem física'),
('2024-06-15', 2, 'Produto vencido encontrado', 'Lote VAR001 com validade próxima');

-- Itens de acerto de estoque
INSERT INTO itemacertoestoque (acerto_id, produto_id, quantidade_antes, quantidade_depois, tipoajuste) VALUES 
(1, 5, 60, 58, 'SAIDA'),      -- Vitamina B12 - diferença no inventário
(1, 10, 20, 22, 'ENTRADA'),   -- Ração gatos - encontrada unidade extra
(2, 1, 65, 60, 'SAIDA');      -- Vacina antirrábica - descarte por vencimento

-- Agendamentos de vacinação
INSERT INTO agendavacinacao (animal_idanimal, vacina_idproduto, data, motivo, usuario_pessoa_idpessoa) VALUES 
(6, 1, '2024-08-15', 'Vacinação antirrábica anual', 1),
(7, 8, '2024-08-20', 'Primeira dose V10', 2),
(8, 1, '2024-09-01', 'Revacinação', 3),
(1, 8, '2024-09-15', 'Reforço V10', 1),
(2, 1, '2024-10-01', 'Vacinação anual', 2);

-- Adoções
INSERT INTO adocao (idadotante, idanimal, dataadocao, pessoa_idpessoa, obs) VALUES 
(1, 6, '2024-07-01', 4, 'Adoção bem-sucedida, animal adaptado'),
(2, 7, '2024-07-15', 5, 'Família com experiência em pets');

-- Eventos
INSERT INTO evento (descricao, data, foto, animal_idanimal, local, responsavel, status) VALUES 
('Feira de adoção', '2024-06-10', NULL, 6, 'Praça Central', 'João Silva', 'Realizado'),
('Campanha de vacinação', '2024-05-15', NULL, 1, 'Clínica VetCare', 'Maria Santos', 'Realizado'),
('Evento de conscientização', '2024-07-20', NULL, 8, 'Shopping PetFriendly', 'Pedro Costa', 'Agendado'),
('Dia de brincadeiras', '2024-08-05', NULL, 5, 'Parque Municipal', 'Ana Paula', 'Agendado');

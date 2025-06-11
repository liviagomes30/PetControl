package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.dto.BatchMedicacaoRequestDTO;
import salvacao.petcontrol.dto.ItemMedicacaoRequestDTO;
import salvacao.petcontrol.dto.MedicamentoCompletoDTO;
import salvacao.petcontrol.dto.PosologiaDTO;
import salvacao.petcontrol.model.MedicacaoModel;
import salvacao.petcontrol.model.AnimalModel;
import salvacao.petcontrol.model.MedicamentoModel;
import salvacao.petcontrol.model.ReceitaMedicamentoModel;
import salvacao.petcontrol.model.PosologiaModel;
import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.model.HistoricoModel;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


@Service
public class MedicacaoService {

    @Autowired
    private MedicacaoModel medicacaoModel = new MedicacaoModel();

    @Autowired
    private AnimalModel animalModel = new AnimalModel();

    @Autowired
    private MedicamentoModel medicamentoModel = new MedicamentoModel();

    @Autowired
    private ReceitaMedicamentoModel receitaMedicamentoModel = new ReceitaMedicamentoModel();

    @Autowired
    private PosologiaModel posologiaModel = new PosologiaModel();

    @Autowired
    private EstoqueModel estoqueModel = new EstoqueModel();

    @Autowired
    private HistoricoModel historicoModel = new HistoricoModel();

    public MedicacaoModel getId(Integer id) {
        return medicacaoModel.getMedDAO().getId(id);
    }

    public List<MedicacaoModel> getAll() {
        return medicacaoModel.getMedDAO().getAll();
    }


    public MedicacaoModel gravar(MedicacaoModel medicacao) throws Exception {
        if (medicacao.getIdanimal() == null) {
            throw new Exception("ID do animal é obrigatório.");
        }

        if (medicacao.getPosologia_medicamento_idproduto() == null && medicacao.getPosologia_receitamedicamento_idreceita() != null) {
            throw new Exception("ID do medicamento para posologia é obrigatório se a receita for informada.");
        }
        if (medicacao.getData() == null) {
            medicacao.setData(LocalDate.now());
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            MedicacaoModel novaMedicacao = medicacaoModel.getMedDAO().gravar(medicacao, conn);

            conn.commit();
            return novaMedicacao;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao gravar medicação: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    public boolean alterar(MedicacaoModel medicacao) throws Exception {
        if (medicacao.getIdmedicacao() == null) {
            throw new Exception("ID da medicação é obrigatório para alteração.");
        }
        if (medicacao.getIdanimal() == null) {
            throw new Exception("ID do animal é obrigatório.");
        }
        if (medicacao.getPosologia_medicamento_idproduto() == null && medicacao.getPosologia_receitamedicamento_idreceita() != null) {
            throw new Exception("ID do medicamento para posologia é obrigatório se a receita for informada.");
        }
        if (medicacao.getData() == null) {
            medicacao.setData(LocalDate.now());
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean atualizado = medicacaoModel.getMedDAO().alterar(medicacao, conn);

            if (atualizado) {
                conn.commit();
            } else {
                conn.rollback();
            }
            return atualizado;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao alterar medicação: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public boolean apagar(Integer id) throws Exception {
        if (id == null) {
            throw new Exception("ID da medicação é obrigatório para exclusão.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Inicia a transação

            MedicacaoModel medicacaoParaApagar = medicacaoModel.getMedDAO().getId(id);
            if (medicacaoParaApagar == null) {
                throw new Exception("Medicação não encontrada para exclusão.");
            }
            Integer receitaId = medicacaoParaApagar.getPosologia_receitamedicamento_idreceita();

            boolean deletado = medicacaoModel.getMedDAO().apagar(id, conn);

            if (deletado && receitaId != null) {
                receitaMedicamentoModel.getRmDAO().reativarReceita(receitaId, conn);
            }

            if (deletado) {
                conn.commit();
            } else {
                conn.rollback();
            }
            return deletado;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao excluir medicação: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public ResultadoOperacao efetuarMedicacaoEmLote(BatchMedicacaoRequestDTO request) throws Exception {
        if (request.getIdAnimal() == null || request.getMedicacoes() == null || request.getMedicacoes().isEmpty()) {
            throw new Exception("Requisição inválida: ID do animal e lista de medicações são obrigatórios.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;

        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Inicia a transação para garantir a integridade dos dados

            // ETAPA 1: Processa e grava cada medicação aplicada
            for (ItemMedicacaoRequestDTO item : request.getMedicacoes()) {
                if (item.getIdMedicamentoProduto() == null ||
                        item.getQuantidadeAdministrada() == null ||
                        item.getQuantidadeAdministrada().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new Exception("Dados inválidos para o medicamento ID: " + item.getIdMedicamentoProduto() + ". Quantidade deve ser maior que zero.");
                }

                // Validação de estoque
                EstoqueModel estoqueAtual = estoqueModel.getEstDAO().getByProdutoId(item.getIdMedicamentoProduto());
                if (estoqueAtual == null || estoqueAtual.getQuantidade().compareTo(item.getQuantidadeAdministrada()) < 0) {
                    MedicamentoCompletoDTO medInfo = medicamentoModel.getMedDAO().findMedicamentoCompleto(item.getIdMedicamentoProduto());
                    String nomeMed = medInfo != null ? medInfo.getProduto().getNome() : "ID " + item.getIdMedicamentoProduto();
                    throw new Exception("Estoque insuficiente para o medicamento: " + nomeMed);
                }

                // Decrementa o estoque
                boolean estoqueDecrementado = estoqueModel.getEstDAO().decrementarEstoque(item.getIdMedicamentoProduto(), item.getQuantidadeAdministrada());
                if (!estoqueDecrementado) {
                    throw new SQLException("Falha ao decrementar o estoque para o medicamento ID: " + item.getIdMedicamentoProduto());
                }

                // Cria o histórico primeiro (sem referenciar a medicação ainda)
                HistoricoModel historico = new HistoricoModel();
                historico.setAnimal_idanimal(request.getIdAnimal());
                historico.setData(request.getDataMedicao() != null ? request.getDataMedicao() : LocalDate.now());
                historico.setMedicacao_idmedicacao(null); // Será atualizado depois
                MedicamentoCompletoDTO medCompleto = medicamentoModel.getMedDAO().findMedicamentoCompleto(item.getIdMedicamentoProduto());
                String nomeMedicamento = medCompleto != null ? medCompleto.getProduto().getNome() : "ID " + item.getIdMedicamentoProduto();
                historico.setDescricao(item.getDescricaoHistorico() != null && !item.getDescricaoHistorico().trim().isEmpty() ?
                        item.getDescricaoHistorico() :
                        "Medicação de " + nomeMedicamento + " administrada (Qtd: " + item.getQuantidadeAdministrada() + ").");
                HistoricoModel novoHistorico = historicoModel.getHistDAO().gravar(historico, conn);

                // Agora cria a medicação com o ID do histórico
                MedicacaoModel medicacao = new MedicacaoModel();
                medicacao.setIdanimal(request.getIdAnimal());
                medicacao.setIdhistorico(novoHistorico.getIdhistorico());
                medicacao.setPosologia_medicamento_idproduto(item.getIdMedicamentoProduto());
                medicacao.setPosologia_receitamedicamento_idreceita(request.getIdReceitaMedicamento());
                medicacao.setData(request.getDataMedicao() != null ? request.getDataMedicao() : LocalDate.now());
                MedicacaoModel novaMedicacao = medicacaoModel.getMedDAO().gravar(medicacao, conn);

                // Atualiza o histórico com o ID da medicação criada
                novoHistorico.setMedicacao_idmedicacao(novaMedicacao.getIdmedicacao());
                boolean historicoAtualizado = historicoModel.getHistDAO().alterar(novoHistorico, conn);
                if (!historicoAtualizado) {
                    throw new SQLException("Falha ao vincular a medicação ao histórico.");
                }
            }

            if (request.getIdReceitaMedicamento() != null) {
                List<PosologiaModel> posologiasDaReceita = posologiaModel.getPosDAO().getByReceita(request.getIdReceitaMedicamento());

                if (!posologiasDaReceita.isEmpty()) {
                    boolean todosTratamentosConcluidos = true;

                    for (PosologiaModel posologia : posologiasDaReceita) {
                        int frequencia = (posologia.getFrequencia_diaria() != null && posologia.getFrequencia_diaria() > 0)
                                ? posologia.getFrequencia_diaria()
                                : 1;

                        int totalDosesRequeridas = frequencia * posologia.getQuantidadedias();

                        int dosesAdministradas = medicacaoModel.getMedDAO().countAdministracoes(
                                request.getIdAnimal(),
                                request.getIdReceitaMedicamento(),
                                posologia.getMedicamento_idproduto(),
                                conn
                        );

                        if (dosesAdministradas < totalDosesRequeridas) {
                            todosTratamentosConcluidos = false;
                            break;
                        }
                    }

                    if (todosTratamentosConcluidos) {
                        receitaMedicamentoModel.getRmDAO().inativarReceita(request.getIdReceitaMedicamento(), conn);
                    }
                }
            }

            conn.commit();
            return new ResultadoOperacao("efetuarMedicacaoEmLote", true, "Medicações efetuadas com sucesso!");

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new Exception("Erro ao processar lote de medicações: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(autoCommitOriginal); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public ResultadoOperacao efetuarMedicacao(
            Integer idAnimal,
            Integer idMedicamentoProduto,
            Integer idReceitaMedicamento,
            BigDecimal quantidadeAdministrada,
            LocalDate dataMedicao,
            String descricaoHistorico
    ) throws Exception {
        if (idAnimal == null || idMedicamentoProduto == null || quantidadeAdministrada == null || quantidadeAdministrada.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Dados obrigatórios incompletos para efetuar a medicação: ID Animal, ID Medicamento, Quantidade Administrada.");
        }

        AnimalModel animal = animalModel.getAnimalDAO().getId(idAnimal);
        if (animal == null) {
            throw new Exception("Animal não encontrado.");
        }

        MedicamentoCompletoDTO medicamentoCompleto = medicamentoModel.getMedDAO().findMedicamentoCompleto(idMedicamentoProduto);
        if (medicamentoCompleto == null || medicamentoCompleto.getProduto() == null || medicamentoCompleto.getMedicamento() == null) {
            throw new Error("Medicamento não encontrado ou dados incompletos.");
        }

        ReceitaMedicamentoModel receita = null;
        if (idReceitaMedicamento != null) {
            receita = receitaMedicamentoModel.getRmDAO().getId(idReceitaMedicamento);
            if (receita == null) {
                throw new Exception("Receita médica não encontrada para o ID fornecido.");
            }
        }

        PosologiaModel posologia = null;
        if (idReceitaMedicamento != null) {
            posologia = posologiaModel.getPosDAO().getId(idMedicamentoProduto, idReceitaMedicamento);
            if (posologia == null) {
                throw new Exception("Posologia para o medicamento e receita especificados não encontrada.");
            }
        }

        EstoqueModel estoqueAtual = estoqueModel.getEstDAO().getByProdutoId(idMedicamentoProduto);
        if (estoqueAtual == null || estoqueAtual.getQuantidade().compareTo(quantidadeAdministrada) < 0) {
            throw new Error("Estoque insuficiente para o medicamento: " + medicamentoCompleto.getProduto().getNome()); // Corrected to throw new Error
        }

        if (dataMedicao == null) {
            dataMedicao = LocalDate.now();
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        ResultadoOperacao resultado = new ResultadoOperacao();

        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);


            boolean estoqueDecrementado = estoqueModel.getEstDAO().decrementarEstoque(idMedicamentoProduto, quantidadeAdministrada);
            if (!estoqueDecrementado) {
                throw new SQLException("Falha ao decrementar estoque.");
            }

            // Cria o histórico primeiro (sem referenciar a medicação ainda)
            HistoricoModel historico = new HistoricoModel();
            historico.setAnimal_idanimal(idAnimal);
            historico.setData(dataMedicao);
            historico.setMedicacao_idmedicacao(null); // Será atualizado depois
            historico.setDescricao(descricaoHistorico != null && !descricaoHistorico.isEmpty() ?
                    descricaoHistorico :
                    "Medicação de " + medicamentoCompleto.getProduto().getNome() + " administrada.");

            HistoricoModel novoHistorico = historicoModel.getHistDAO().gravar(historico, conn);
            if (novoHistorico == null || novoHistorico.getIdhistorico() == null) {
                throw new SQLException("Falha ao registrar histórico da medicação.");
            }

            // Agora cria a medicação com o ID do histórico
            MedicacaoModel medicacao = new MedicacaoModel();
            medicacao.setIdanimal(idAnimal);
            medicacao.setIdhistorico(novoHistorico.getIdhistorico());
            medicacao.setPosologia_medicamento_idproduto(idMedicamentoProduto);

            if (idReceitaMedicamento != null) {
                medicacao.setPosologia_receitamedicamento_idreceita(idReceitaMedicamento);
            } else {
                medicacao.setPosologia_receitamedicamento_idreceita(null);
            }
            medicacao.setData(dataMedicao);

            MedicacaoModel novaMedicacao = medicacaoModel.getMedDAO().gravar(medicacao, conn);
            if (novaMedicacao == null || novaMedicacao.getIdmedicacao() == null) {
                throw new SQLException("Falha ao registrar medicação.");
            }

            // Atualiza o histórico com o ID da medicação criada
            novoHistorico.setMedicacao_idmedicacao(novaMedicacao.getIdmedicacao());
            boolean historicoAtualizado = historicoModel.getHistDAO().alterar(novoHistorico, conn);
            if (!historicoAtualizado) {
                throw new SQLException("Falha ao vincular a medicação ao histórico.");
            }


            conn.commit();

            resultado.setSucesso(true);
            resultado.setMensagem("Medicação efetuada com sucesso!");
            resultado.setOperacao("efetuarMedicacao");
            return resultado;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            resultado.setSucesso(false);
            resultado.setMensagem("Erro ao efetuar medicação: " + e.getMessage());
            resultado.setOperacao("efetuarMedicacao");
            return resultado;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public List<MedicacaoModel> getMedicacoesByAnimal(Integer idAnimal) {
        return medicacaoModel.getMedDAO().getMedicacoesByAnimal(idAnimal);
    }

    public List<MedicacaoModel> searchMedicacoesByComposicao(String searchTerm) {
        return medicacaoModel.getMedDAO().searchMedicacoesByComposicao(searchTerm);
    }
}
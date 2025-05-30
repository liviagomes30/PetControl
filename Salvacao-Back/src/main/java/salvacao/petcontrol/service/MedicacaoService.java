// Salvacao-Back/src/main/java/salvacao/petcontrol/service/MedicacaoService.java
package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.dto.MedicamentoCompletoDTO;
import salvacao.petcontrol.model.MedicacaoModel;
import salvacao.petcontrol.model.AnimalModel; // Needed for validation and data retrieval
import salvacao.petcontrol.model.MedicamentoModel; // Needed for validation and data retrieval
import salvacao.petcontrol.model.ReceitaMedicamentoModel; // Needed for validation and data retrieval
import salvacao.petcontrol.model.PosologiaModel; // Needed for validation and data retrieval
import salvacao.petcontrol.model.EstoqueModel; // Needed for stock update
import salvacao.petcontrol.model.HistoricoModel; // Needed for history recording
import salvacao.petcontrol.util.ResultadoOperacao; // For structured response

import java.math.BigDecimal; // For quantity handling
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class MedicacaoService {

    @Autowired
    private MedicacaoModel medicacaoModel = new MedicacaoModel();

    @Autowired
    private AnimalModel animalModel = new AnimalModel(); // Autowire AnimalModel to access AnimalDAO

    @Autowired
    private MedicamentoModel medicamentoModel = new MedicamentoModel(); // Autowire MedicamentoModel to access MedicamentoDAO

    @Autowired
    private ReceitaMedicamentoModel receitaMedicamentoModel = new ReceitaMedicamentoModel(); // Autowire ReceitaMedicamentoModel to access its DAO

    @Autowired
    private PosologiaModel posologiaModel = new PosologiaModel(); // Autowire PosologiaModel to access its DAO

    @Autowired
    private EstoqueModel estoqueModel = new EstoqueModel(); // Autowire EstoqueModel to access EstoqueDAO

    @Autowired
    private HistoricoModel historicoModel = new HistoricoModel(); // Autowire HistoricoModel to access HistoricoDAO

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
        // If idReceitaMedicamento is null, it means no posology is required or provided
        // This validation should be adjusted based on business rules for posology in MedicacaoModel
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
            conn.setAutoCommit(false); // Start transaction

            MedicacaoModel novaMedicacao = medicacaoModel.getMedDAO().gravar(medicacao, conn);

            conn.commit();
            return novaMedicacao;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao gravar medicação: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal); // Restore auto-commit state
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
        // Adjusted validation for posologia. If a recipe is provided, medicamento ID is required.
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
            conn.setAutoCommit(false); // Start transaction

            boolean atualizado = medicacaoModel.getMedDAO().alterar(medicacao, conn);

            if (atualizado) {
                conn.commit(); // Commit transaction if successful
            } else {
                conn.rollback(); // Rollback if update failed
            }
            return atualizado;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao alterar medicação: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal); // Restore auto-commit state
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Exclui uma medicação.
     * @param id O ID da medicação a ser excluída.
     * @return true se a medicação foi excluída com sucesso, false caso contrário.
     * @throws Exception Se ocorrer um erro de validação ou de banco de dados.
     */
    public boolean apagar(Integer id) throws Exception {
        if (id == null) {
            throw new Exception("ID da medicação é obrigatório para exclusão.");
        }
        MedicacaoModel existente = medicacaoModel.getMedDAO().getId(id);
        if (existente == null) {
            throw new Exception("Medicação não encontrada para exclusão.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            // Access DAO via Model instance and pass connection
            boolean deletado = medicacaoModel.getMedDAO().apagar(id, conn);

            if (deletado) {
                conn.commit(); // Commit transaction if successful
            } else {
                conn.rollback(); // Rollback if deletion failed
            }
            return deletado;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao excluir medicação: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal); // Restore auto-commit state
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public ResultadoOperacao efetuarMedicacao(
            Integer idAnimal,
            Integer idMedicamentoProduto,
            Integer idReceitaMedicamento, // Marked as potentially null
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
            throw new Error("Medicamento não encontrado ou dados incompletos."); // Corrected to throw new Error
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

            MedicacaoModel medicacao = new MedicacaoModel();
            medicacao.setIdanimal(idAnimal);
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

            HistoricoModel historico = new HistoricoModel();
            historico.setAnimal_idanimal(idAnimal);
            historico.setData(dataMedicao);
            historico.setMedicacao_idmedicacao(novaMedicacao.getIdmedicacao()); // Link to new medication
            historico.setDescricao(descricaoHistorico != null && !descricaoHistorico.isEmpty() ?
                    descricaoHistorico :
                    "Medicação de " + medicamentoCompleto.getProduto().getNome() + " administrada.");


            HistoricoModel novoHistorico = historicoModel.getHistDAO().gravar(historico, conn);
            if (novoHistorico == null || novoHistorico.getIdhistorico() == null) {
                throw new SQLException("Falha ao registrar histórico da medicação.");
            }

            novaMedicacao.setIdhistorico(novoHistorico.getIdhistorico());
            // Access MedicacaoDAO via MedicacaoModel instance and pass connection
            boolean medicacaoAtualizadaComHistorico = medicacaoModel.getMedDAO().alterar(novaMedicacao, conn);
            if (!medicacaoAtualizadaComHistorico) {
                throw new SQLException("Falha ao atualizar medicação com ID de histórico.");
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
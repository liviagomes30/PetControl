package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.config.SingletonDB; // Import SingletonDB
import salvacao.petcontrol.model.PosologiaModel;
import salvacao.petcontrol.model.MedicamentoModel; // For validation
import salvacao.petcontrol.model.ReceitaMedicamentoModel; // For validation

import java.sql.Connection; // Import Connection
import java.sql.SQLException; // Import SQLException
import java.util.List;

@Service
public class PosologiaService {

    @Autowired
    private PosologiaModel posologiaModel = new PosologiaModel(); // Autowire the PosologiaModel

    @Autowired
    private MedicamentoModel medicamentoModel = new MedicamentoModel(); // Autowire MedicamentoModel for validation

    @Autowired
    private ReceitaMedicamentoModel receitaMedicamentoModel = new ReceitaMedicamentoModel(); // Autowire ReceitaMedicamentoModel for validation

    public PosologiaModel getId(Integer medicamentoId, Integer receitaId) {
        return posologiaModel.getPosDAO().getId(medicamentoId, receitaId);
    }

    public List<PosologiaModel> getAll() {
        return posologiaModel.getPosDAO().getAll();
    }

    public List<PosologiaModel> getByMedicamento(Integer medicamentoId) {
        return posologiaModel.getPosDAO().getByMedicamento(medicamentoId);
    }

    public List<PosologiaModel> getByReceita(Integer receitaId) {
        return posologiaModel.getPosDAO().getByReceita(receitaId);
    }

    public PosologiaModel gravar(PosologiaModel posologia) throws Exception {
        // Basic validations
        if (posologia.getDose() == null || posologia.getDose().trim().isEmpty()) {
            throw new Exception("A dose é obrigatória.");
        }
        if (posologia.getQuantidadedias() == null || posologia.getQuantidadedias() <= 0) {
            throw new Exception("Quantidade de dias deve ser maior que zero.");
        }
        if (posologia.getIntervalohoras() == null || posologia.getIntervalohoras() <= 0) {
            throw new Exception("Intervalo em horas deve ser maior que zero.");
        }
        if (posologia.getMedicamento_idproduto() == null) {
            throw new Exception("ID do medicamento é obrigatório.");
        }
        if (posologia.getReceitamedicamento_idreceita() == null) {
            throw new Exception("ID da receita de medicamento é obrigatório.");
        }

        // Validate existence of related entities
        if (medicamentoModel.getMedDAO().getId(posologia.getMedicamento_idproduto()) == null) {
            throw new Exception("Medicamento não encontrado.");
        }
        if (receitaMedicamentoModel.getRmDAO().getId(posologia.getReceitamedicamento_idreceita()) == null) {
            throw new Exception("Receita de medicamento não encontrada.");
        }

        // Check for duplicate primary key (composite key)
        if (posologiaModel.getPosDAO().getId(posologia.getMedicamento_idproduto(), posologia.getReceitamedicamento_idreceita()) != null) {
            throw new Exception("Esta posologia já existe para o medicamento e receita informados.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            // Access DAO via Model instance and pass connection
            PosologiaModel novaPosologia = posologiaModel.getPosDAO().gravar(posologia, conn);

            conn.commit(); // Commit transaction if successful
            return novaPosologia;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao gravar posologia: " + e.getMessage(), e);
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

    public boolean alterar(PosologiaModel posologia) throws Exception {
        // Primary keys must be set for update
        if (posologia.getMedicamento_idproduto() == null || posologia.getReceitamedicamento_idreceita() == null) {
            throw new Exception("IDs de medicamento e receita são obrigatórios para alteração da posologia.");
        }
        // Basic validations (similar to gravar)
        if (posologia.getDose() == null || posologia.getDose().trim().isEmpty()) {
            throw new Exception("A dose é obrigatória.");
        }
        if (posologia.getQuantidadedias() == null || posologia.getQuantidadedias() <= 0) {
            throw new Exception("Quantidade de dias deve ser maior que zero.");
        }
        if (posologia.getIntervalohoras() == null || posologia.getIntervalohoras() <= 0) {
            throw new Exception("Intervalo em horas deve ser maior que zero.");
        }

        // Check if the posologia exists before attempting to alter
        if (posologiaModel.getPosDAO().getId(posologia.getMedicamento_idproduto(), posologia.getReceitamedicamento_idreceita()) == null) {
            throw new Exception("Posologia não encontrada para alteração.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            // Access DAO via Model instance and pass connection
            boolean atualizado = posologiaModel.getPosDAO().alterar(posologia, conn);

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
            throw new Exception("Erro ao alterar posologia: " + e.getMessage(), e);
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

    public boolean apagar(Integer medicamentoId, Integer receitaId) throws Exception {
        if (medicamentoId == null || receitaId == null) {
            throw new Exception("IDs de medicamento e receita são obrigatórios para exclusão da posologia.");
        }

        // Check if the posologia exists before attempting to delete
        if (posologiaModel.getPosDAO().getId(medicamentoId, receitaId) == null) {
            throw new Exception("Posologia não encontrada para exclusão.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            // Access DAO via Model instance and pass connection
            boolean deletado = posologiaModel.getPosDAO().apagar(medicamentoId, receitaId, conn);

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
            throw new Exception("Erro ao excluir posologia: " + e.getMessage(), e);
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
}
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
    private PosologiaModel posologiaModel = new PosologiaModel();

    @Autowired
    private MedicamentoModel medicamentoModel = new MedicamentoModel();

    @Autowired
    private ReceitaMedicamentoModel receitaMedicamentoModel = new ReceitaMedicamentoModel();

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
            conn.setAutoCommit(false);

            PosologiaModel novaPosologia = posologiaModel.getPosDAO().gravar(posologia, conn);

            conn.commit(); // Commit transaction if successful
            return novaPosologia;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao gravar posologia: " + e.getMessage(), e);
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

    public boolean alterar(PosologiaModel posologia) throws Exception {
        if (posologia.getMedicamento_idproduto() == null || posologia.getReceitamedicamento_idreceita() == null) {
            throw new Exception("IDs de medicamento e receita são obrigatórios para alteração da posologia.");
        }
        if (posologia.getDose() == null || posologia.getDose().trim().isEmpty()) {
            throw new Exception("A dose é obrigatória.");
        }
        if (posologia.getQuantidadedias() == null || posologia.getQuantidadedias() <= 0) {
            throw new Exception("Quantidade de dias deve ser maior que zero.");
        }
        if (posologia.getIntervalohoras() == null || posologia.getIntervalohoras() <= 0) {
            throw new Exception("Intervalo em horas deve ser maior que zero.");
        }

        if (posologiaModel.getPosDAO().getId(posologia.getMedicamento_idproduto(), posologia.getReceitamedicamento_idreceita()) == null) {
            throw new Exception("Posologia não encontrada para alteração.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean atualizado = posologiaModel.getPosDAO().alterar(posologia, conn);

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
            throw new Exception("Erro ao alterar posologia: " + e.getMessage(), e);
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

    public boolean apagar(Integer medicamentoId, Integer receitaId) throws Exception {
        if (medicamentoId == null || receitaId == null) {
            throw new Exception("IDs de medicamento e receita são obrigatórios para exclusão da posologia.");
        }

        if (posologiaModel.getPosDAO().getId(medicamentoId, receitaId) == null) {
            throw new Exception("Posologia não encontrada para exclusão.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean deletado = posologiaModel.getPosDAO().apagar(medicamentoId, receitaId, conn);

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
            throw new Exception("Erro ao excluir posologia: " + e.getMessage(), e);
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
}
package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.config.SingletonDB; // Import SingletonDB
import salvacao.petcontrol.dto.PosologiaDTO;
import salvacao.petcontrol.model.PosologiaModel;
import salvacao.petcontrol.model.ReceitaMedicamentoModel;
import salvacao.petcontrol.model.AnimalModel; // For validation

import java.sql.Connection; // Import Connection
import java.sql.SQLException; // Import SQLException
import java.time.LocalDate;
import java.util.List;

@Service
public class ReceitaMedicamentoService {

    @Autowired
    private ReceitaMedicamentoModel receitaMedicamentoModel = new ReceitaMedicamentoModel();

    @Autowired
    private AnimalModel animalModel = new AnimalModel();

    @Autowired
    private PosologiaModel posologiaModel = new PosologiaModel();

    public ReceitaMedicamentoModel getId(Integer id) {
        return receitaMedicamentoModel.getRmDAO().getId(id);
    }

    public List<ReceitaMedicamentoModel> getAll() {
        return receitaMedicamentoModel.getRmDAO().getAll();
    }

    public List<ReceitaMedicamentoModel> getReceitasByAnimal(Integer animalId) {
        return receitaMedicamentoModel.getRmDAO().getReceitasByAnimal(animalId);
    }

    public List<ReceitaMedicamentoModel> searchReceitas(String searchTerm) {
        return receitaMedicamentoModel.getRmDAO().searchReceitas(searchTerm);
    }

    public ReceitaMedicamentoModel gravar(ReceitaMedicamentoModel receita) throws Exception {
        if (receita.getAnimal_idanimal() == null) {
            throw new Exception("ID do animal é obrigatório para a receita.");
        }
        if (receita.getMedico() == null || receita.getMedico().trim().isEmpty()) {
            throw new Exception("Nome do médico é obrigatório.");
        }
        if (receita.getClinica() == null || receita.getClinica().trim().isEmpty()) {
            throw new Exception("Nome da clínica é obrigatório.");
        }
        if (receita.getData() == null) {
            receita.setData(LocalDate.now());
        }

        // Validate existence of related Animal
        if (animalModel.getAnimalDAO().getId(receita.getAnimal_idanimal()) == null) {
            throw new Exception("Animal não encontrado.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            ReceitaMedicamentoModel novaReceita = receitaMedicamentoModel.getRmDAO().gravar(receita, conn);

            conn.commit();
            return novaReceita;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao gravar receita de medicamento: " + e.getMessage(), e);
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

    public boolean alterar(ReceitaMedicamentoModel receita) throws Exception {
        if (receita.getIdreceita() == null) {
            throw new Exception("ID da receita é obrigatório para alteração.");
        }
        if (receita.getAnimal_idanimal() == null) {
            throw new Exception("ID do animal é obrigatório para a receita.");
        }
        if (receita.getMedico() == null || receita.getMedico().trim().isEmpty()) {
            throw new Exception("Nome do médico é obrigatório.");
        }
        if (receita.getClinica() == null || receita.getClinica().trim().isEmpty()) {
            throw new Exception("Nome da clínica é obrigatório.");
        }
        if (receita.getData() == null) {
            receita.setData(LocalDate.now());
        }

        if (receitaMedicamentoModel.getRmDAO().getId(receita.getIdreceita()) == null) {
            throw new Exception("Receita de medicamento não encontrada para alteração.");
        }

        if (animalModel.getAnimalDAO().getId(receita.getAnimal_idanimal()) == null) {
            throw new Exception("Animal não encontrado.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean atualizado = receitaMedicamentoModel.getRmDAO().alterar(receita, conn);

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
            throw new Exception("Erro ao alterar receita de medicamento: " + e.getMessage(), e);
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
            throw new Exception("ID da receita é obrigatório para exclusão.");
        }
        if (receitaMedicamentoModel.getRmDAO().getId(id) == null) {
            throw new Exception("Receita de medicamento não encontrada para exclusão.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean deletado = receitaMedicamentoModel.getRmDAO().apagar(id, conn);

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
            throw new Exception("Erro ao excluir receita de medicamento: " + e.getMessage(), e);
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
    public List<PosologiaDTO> buscarPosologiasPorReceita(Integer receitaId) {
        return posologiaModel.getPosDAO().listarPorReceita(receitaId);
    }
}
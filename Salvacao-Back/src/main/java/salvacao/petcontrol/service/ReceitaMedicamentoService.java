package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.config.SingletonDB; // Import SingletonDB
import salvacao.petcontrol.model.ReceitaMedicamentoModel;
import salvacao.petcontrol.model.AnimalModel; // For validation

import java.sql.Connection; // Import Connection
import java.sql.SQLException; // Import SQLException
import java.time.LocalDate;
import java.util.List;

@Service
public class ReceitaMedicamentoService {

    @Autowired
    private ReceitaMedicamentoModel receitaMedicamentoModel = new ReceitaMedicamentoModel(); // Autowire the ReceitaMedicamentoModel

    @Autowired
    private AnimalModel animalModel = new AnimalModel(); // Autowire AnimalModel for validation

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
            conn.setAutoCommit(false); // Start transaction

            // Access DAO via Model instance and pass connection
            ReceitaMedicamentoModel novaReceita = receitaMedicamentoModel.getRmDAO().gravar(receita, conn);

            conn.commit(); // Commit transaction if successful
            return novaReceita;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao gravar receita de medicamento: " + e.getMessage(), e);
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

    public boolean alterar(ReceitaMedicamentoModel receita) throws Exception {
        if (receita.getIdreceita() == null) {
            throw new Exception("ID da receita é obrigatório para alteração.");
        }
        // Basic validations (similar to gravar)
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

        // Check if the recipe exists before attempting to alter
        if (receitaMedicamentoModel.getRmDAO().getId(receita.getIdreceita()) == null) {
            throw new Exception("Receita de medicamento não encontrada para alteração.");
        }

        // Validate existence of related Animal (again)
        if (animalModel.getAnimalDAO().getId(receita.getAnimal_idanimal()) == null) {
            throw new Exception("Animal não encontrado.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            // Access DAO via Model instance and pass connection
            boolean atualizado = receitaMedicamentoModel.getRmDAO().alterar(receita, conn);

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
            throw new Exception("Erro ao alterar receita de medicamento: " + e.getMessage(), e);
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

    public boolean apagar(Integer id) throws Exception {
        if (id == null) {
            throw new Exception("ID da receita é obrigatório para exclusão.");
        }
        // Check if the recipe exists before attempting to delete
        if (receitaMedicamentoModel.getRmDAO().getId(id) == null) {
            throw new Exception("Receita de medicamento não encontrada para exclusão.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            // Access DAO via Model instance and pass connection
            boolean deletado = receitaMedicamentoModel.getRmDAO().apagar(id, conn);

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
            throw new Exception("Erro ao excluir receita de medicamento: " + e.getMessage(), e);
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
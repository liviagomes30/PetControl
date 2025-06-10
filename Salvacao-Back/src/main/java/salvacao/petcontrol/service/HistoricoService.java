package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.config.SingletonDB; // Import SingletonDB
import salvacao.petcontrol.model.HistoricoModel;

import java.sql.Connection; // Import Connection
import java.sql.SQLException; // Import SQLException
import java.time.LocalDate;
import java.util.List;

@Service
public class HistoricoService {
    @Autowired
    private HistoricoModel historicoModel = new HistoricoModel();

    public HistoricoModel getId(Integer id){
        return historicoModel.getHistDAO().getId(id);
    }

    public List<HistoricoModel> getAll(){
        return historicoModel.getHistDAO().getAll();
    }
    public List<HistoricoModel> getByAnimal(Integer idAnimal){
        return historicoModel.getHistDAO().getByAnimal(idAnimal);
    }
    public List<HistoricoModel> getByAnimalAndPeriodo(Integer idAnimal, LocalDate dataInicio, LocalDate dataFim){
        return historicoModel.getHistDAO().getByAnimalAndPeriodo(idAnimal, dataInicio, dataFim);
    }
    public List<HistoricoModel> getByVacinacao(Integer idVacinacao){
        return historicoModel.getHistDAO().getByVacinacao(idVacinacao);
    }
    public List<HistoricoModel> getByMedicacao(Integer idMedicacao){
        return historicoModel.getHistDAO().getByMedicacao(idMedicacao);
    }
    public List<HistoricoModel> getByPeriodo(LocalDate dataInicio, LocalDate dataFim){
        return historicoModel.getHistDAO().getByPeriodo(dataInicio, dataFim);
    }
    public boolean existeHistoricoPorAnimal(int idAnimal){
        return historicoModel.getHistDAO().existeHistoricoPorAnimal(idAnimal);
    }
    public List<HistoricoModel> getByDescricao(String filtro){
        return historicoModel.getHistDAO().getByDescricao(filtro);
    }

    public HistoricoModel gravar(HistoricoModel historico) throws Exception{
        if (historico.getDescricao() == null || historico.getDescricao().trim().isEmpty()) {
            throw new Exception("A descrição é obrigatória.");
        }
        if (historico.getAnimal_idanimal() == null) {
            throw new Exception("O animal é obrigatório para o histórico.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            HistoricoModel novoHistorico = historicoModel.getHistDAO().gravar(historico, conn);

            conn.commit();
            return novoHistorico;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao gravar histórico: " + e.getMessage(), e);
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

    public boolean alterar(HistoricoModel historico) throws Exception{
        if (historico.getIdhistorico() == null) {
            throw new Exception("ID do histórico é obrigatório para alteração.");
        }
        if (historico.getDescricao() == null || historico.getDescricao().trim().isEmpty()) {
            throw new Exception("A descrição é obrigatória.");
        }
        if (historico.getAnimal_idanimal() == null) {
            throw new Exception("O animal é obrigatório para o histórico.");
        }

        HistoricoModel existente = historicoModel.getHistDAO().getId(historico.getIdhistorico());
        if (existente == null) {
            throw new Exception("Histórico não encontrado para atualização.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean atualizado = historicoModel.getHistDAO().alterar(historico, conn);

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
            throw new Exception("Erro ao alterar histórico: " + e.getMessage(), e);
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

    public boolean apagar(Integer id) throws Exception{
        HistoricoModel existente = historicoModel.getHistDAO().getId(id);
        if (existente == null) {
            throw new Exception("Histórico não encontrado para exclusão.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean deletado = historicoModel.getHistDAO().apagar(id, conn);

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
            throw new Exception("Erro ao excluir histórico: " + e.getMessage(), e);
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
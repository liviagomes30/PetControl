package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.UnidadeMedidaModel;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class UnidadeMedidaService {

    @Autowired
    private UnidadeMedidaModel unidadeMedidaModel = new UnidadeMedidaModel();

    public UnidadeMedidaModel getId(Integer id) {
        return unidadeMedidaModel.getUnDAO().getId(id);
    }

    public List<UnidadeMedidaModel> getAll() {
        return unidadeMedidaModel.getUnDAO().getAll();
    }

    public UnidadeMedidaModel gravar(UnidadeMedidaModel unidadeMedida) throws Exception {
        if (unidadeMedida.getDescricao() == null || unidadeMedida.getDescricao().trim().isEmpty()) {
            throw new Exception("A descrição é obrigatória.");
        }
        if (unidadeMedida.getDescricao().length() > 100) {
            throw new Exception("A descrição não pode ter mais de 100 caracteres.");
        }
        if (unidadeMedida.getSigla() == null || unidadeMedida.getSigla().trim().isEmpty()) {
            throw new Exception("A sigla é obrigatória.");
        }
        if (unidadeMedida.getSigla().length() > 10) {
            throw new Exception("A sigla não pode ter mais de 10 caracteres.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            UnidadeMedidaModel novaUnidadeMedida = unidadeMedidaModel.getUnDAO().gravar(unidadeMedida, conn);

            conn.commit();
            return novaUnidadeMedida;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao cadastrar unidade de medida: " + e.getMessage(), e);
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

    public boolean alterar(UnidadeMedidaModel unidadeMedida) throws Exception {
        if (unidadeMedida.getDescricao() == null || unidadeMedida.getDescricao().trim().isEmpty()) {
            throw new Exception("A descrição é obrigatória.");
        }
        if (unidadeMedida.getDescricao().length() > 100) {
            throw new Exception("A descrição não pode ter mais de 100 caracteres.");
        }
        if (unidadeMedida.getSigla() == null || unidadeMedida.getSigla().trim().isEmpty()) {
            throw new Exception("A sigla é obrigatória.");
        }
        if (unidadeMedida.getSigla().length() > 10) {
            throw new Exception("A sigla não pode ter mais de 10 caracteres.");
        }
        UnidadeMedidaModel existente = unidadeMedidaModel.getUnDAO().getId(unidadeMedida.getIdUnidadeMedida());
        if (existente == null) {
            throw new Exception("Unidade de medida não encontrada para atualização.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean atualizado = unidadeMedidaModel.getUnDAO().alterar(unidadeMedida, conn);

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
            throw new Exception("Erro ao atualizar unidade de medida: " + e.getMessage(), e);
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
        UnidadeMedidaModel existente = unidadeMedidaModel.getUnDAO().getId(id);
        if (existente == null) {
            throw new Exception("Unidade de medida não encontrada para exclusão.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean deletado = unidadeMedidaModel.getUnDAO().apagar(id, conn);

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
            throw new Exception("Erro ao excluir unidade de medida: " + e.getMessage(), e);
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

    public List<UnidadeMedidaModel> getByDescricaoSigla(String filtro) {
        return unidadeMedidaModel.getUnDAO().getByDescricaoSigla(filtro);
    }
}
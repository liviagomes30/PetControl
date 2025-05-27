package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.config.SingletonDB; // Import SingletonDB
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.sql.Connection; // Import Connection
import java.sql.SQLException; // Import SQLException
import java.util.List;

@Service
public class TipoProdutoService {

    @Autowired
    private TipoProdutoModel tipoProdutoModel = new TipoProdutoModel(); // Autowire the Model

    public TipoProdutoModel getId(Integer id) {
        return tipoProdutoModel.getTpDAO().getId(id);
    }

    public List<TipoProdutoModel> getAll() {
        return tipoProdutoModel.getTpDAO().getAll();
    }

    public TipoProdutoModel gravar(TipoProdutoModel tipoProduto) throws Exception {
        if (tipoProduto.getDescricao() == null || tipoProduto.getDescricao().trim().isEmpty()) {
            throw new Exception("A descrição é obrigatória.");
        }
        if (tipoProduto.getDescricao().length() > 100) {
            throw new Exception("A descrição não pode ter mais de 100 caracteres.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            // Access DAO via Model instance and pass connection
            TipoProdutoModel novoTipoProduto = tipoProdutoModel.getTpDAO().gravar(tipoProduto, conn);

            conn.commit(); // Commit transaction if successful
            return novoTipoProduto;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao cadastrar tipo de produto: " + e.getMessage(), e);
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

    public boolean alterar(TipoProdutoModel tipoProduto) throws Exception {
        if (tipoProduto.getDescricao() == null || tipoProduto.getDescricao().trim().isEmpty()) {
            throw new Exception("A descrição é obrigatória.");
        }
        if (tipoProduto.getDescricao().length() > 100) {
            throw new Exception("A descrição não pode ter mais de 100 caracteres.");
        }

        // Access DAO via Model instance
        TipoProdutoModel existente = tipoProdutoModel.getTpDAO().getId(tipoProduto.getIdtipoproduto());
        if (existente == null) {
            throw new Exception("Tipo de produto não encontrado para atualização.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            // Access DAO via Model instance and pass connection
            boolean atualizado = tipoProdutoModel.getTpDAO().alterar(tipoProduto, conn);

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
            throw new Exception("Erro ao atualizar tipo de produto: " + e.getMessage(), e);
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
        // Access DAO via Model instance
        TipoProdutoModel existente = tipoProdutoModel.getTpDAO().getId(id);
        if (existente == null) {
            throw new Exception("Tipo de produto não encontrado para exclusão.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            // Access DAO via Model instance and pass connection
            boolean deletado = tipoProdutoModel.getTpDAO().apagar(id, conn);

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
            throw new Exception("Erro ao excluir tipo de produto: " + e.getMessage(), e);
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

    public List<TipoProdutoModel> getByDescricao(String filtro) {
        return tipoProdutoModel.getTpDAO().getByDescricao(filtro);
    }
}
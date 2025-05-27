package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.config.SingletonDB; // Import SingletonDB
import salvacao.petcontrol.model.PessoaModel; // Autowire PessoaModel
import salvacao.petcontrol.model.UsuarioModel;

import java.sql.Connection; // Import Connection
import java.sql.SQLException; // Import SQLException
import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioModel usuarioModel; // Autowire the UsuarioModel

    @Autowired
    private PessoaModel pessoaModel; // Autowire the PessoaModel to access its DAO

    public UsuarioModel getId(Integer pessoaId){
        // Access DAO via Model instance, pass PessoaModel to it
        return usuarioModel.getUsuDAO().getId(pessoaId, pessoaModel);
    }

    public List<UsuarioModel> getAll(){
        return usuarioModel.getUsuDAO().getAll();
    }
    public List<UsuarioModel> getByLogin(String filtro){
        return usuarioModel.getUsuDAO().getByLogin(filtro);
    }

    public UsuarioModel gravar(UsuarioModel usuario, PessoaModel pessoa) throws Exception{
        // Basic validation for Pessoa first
        if (pessoa.getNome() == null || pessoa.getNome().trim().isEmpty()) {
            throw new Exception("Nome da pessoa é obrigatório.");
        }
        // Add more Pessoa validations here as needed

        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            throw new Exception("Login do usuário é obrigatório.");
        }
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new Exception("Senha do usuário é obrigatória.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            // 1. Gravar a Pessoa primeiro, para obter o idpessoa
            // Access PessoaDAO via PessoaModel instance and pass connection
            PessoaModel novaPessoa = pessoaModel.getPessoaDAO().gravar(pessoa, conn);
            if (novaPessoa == null || novaPessoa.getIdpessoa() == null) {
                throw new SQLException("Falha ao gravar pessoa.");
            }

            // 2. Definir o idpessoa no UsuarioModel e gravar o Usuario
            usuario.setPessoa_idpessoa(novaPessoa.getIdpessoa());
            // Access UsuarioDAO via UsuarioModel instance and pass connection
            UsuarioModel novoUsuario = usuarioModel.getUsuDAO().gravar(usuario, conn);
            if (novoUsuario == null) {
                throw new SQLException("Falha ao gravar usuário.");
            }

            conn.commit(); // Commit transaction if successful
            return novoUsuario;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao gravar usuário e pessoa: " + e.getMessage(), e);
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

    public boolean alterar(UsuarioModel usuario, PessoaModel pessoa) throws Exception{
        if (usuario.getPessoa_idpessoa() == null) {
            throw new Exception("ID da pessoa do usuário é obrigatório para alteração.");
        }
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            throw new Exception("Login do usuário é obrigatório.");
        }
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new Exception("Senha do usuário é obrigatória.");
        }
        // Basic validation for Pessoa
        if (pessoa.getNome() == null || pessoa.getNome().trim().isEmpty()) {
            throw new Exception("Nome da pessoa é obrigatório para alteração.");
        }

        // Access DAO via Model instance to check if user exists
        UsuarioModel existente = usuarioModel.getUsuDAO().getId(usuario.getPessoa_idpessoa(), pessoaModel);
        if (existente == null) {
            throw new Exception("Usuário não encontrado para atualização.");
        }
        // Set idpessoa for PessoaModel explicitly for update
        pessoa.setIdpessoa(usuario.getPessoa_idpessoa());

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            // 1. Alterar a Pessoa
            // Access PessoaDAO via PessoaModel instance and pass connection
            boolean pessoaAtualizada = pessoaModel.getPessoaDAO().alterar(pessoa, conn);
            if (!pessoaAtualizada) {
                throw new SQLException("Falha ao atualizar pessoa.");
            }

            // 2. Alterar o Usuario
            // Access UsuarioDAO via UsuarioModel instance and pass connection
            boolean usuarioAtualizado = usuarioModel.getUsuDAO().alterar(usuario, conn);
            if (!usuarioAtualizado) {
                throw new SQLException("Falha ao atualizar usuário.");
            }

            conn.commit(); // Commit transaction if successful
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao alterar usuário e pessoa: " + e.getMessage(), e);
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

    public boolean apagar(Integer pessoaId) throws Exception{
        // Access DAO via Model instance to check if user exists
        UsuarioModel existente = usuarioModel.getUsuDAO().getId(pessoaId, pessoaModel);
        if (existente == null) {
            throw new Exception("Usuário não encontrado para exclusão.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            // 1. Apagar o Usuario (checks for dependencies internally)
            // Access UsuarioDAO via UsuarioModel instance and pass connection
            boolean usuarioDeletado = usuarioModel.getUsuDAO().apagar(pessoaId, conn);
            if (!usuarioDeletado) {
                // The DAO might throw an exception if dependencies exist, which is handled by the catch block.
                // If it returns false without throwing, it means the user record wasn't found or an unexpected issue.
                throw new SQLException("Falha ao excluir usuário.");
            }

            // 2. Apagar a Pessoa
            // Access PessoaDAO via PessoaModel instance and pass connection
            boolean pessoaDeletada = pessoaModel.getPessoaDAO().apagar(pessoaId, conn);
            if (!pessoaDeletada) {
                throw new SQLException("Falha ao excluir pessoa associada.");
            }

            conn.commit(); // Commit transaction if successful
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao excluir usuário e pessoa: " + e.getMessage(), e);
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
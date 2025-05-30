package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.PessoaModel;
import salvacao.petcontrol.model.UsuarioModel;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioModel usuarioModel;

    @Autowired
    private PessoaModel pessoaModel;

    public UsuarioModel getId(Integer pessoaId){
        return usuarioModel.getUsuDAO().getId(pessoaId, pessoaModel);
    }

    public List<UsuarioModel> getAll(){
        return usuarioModel.getUsuDAO().getAll();
    }
    public List<UsuarioModel> getByLogin(String filtro){
        return usuarioModel.getUsuDAO().getByLogin(filtro);
    }

    public UsuarioModel gravar(UsuarioModel usuario, PessoaModel pessoa) throws Exception{
        if (pessoa.getNome() == null || pessoa.getNome().trim().isEmpty()) {
            throw new Exception("Nome da pessoa é obrigatório.");
        }

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
            conn.setAutoCommit(false);


            PessoaModel novaPessoa = pessoaModel.getPessoaDAO().gravar(pessoa, conn);
            if (novaPessoa == null || novaPessoa.getIdpessoa() == null) {
                throw new SQLException("Falha ao gravar pessoa.");
            }

            usuario.setPessoa_idpessoa(novaPessoa.getIdpessoa());
            UsuarioModel novoUsuario = usuarioModel.getUsuDAO().gravar(usuario, conn);
            if (novoUsuario == null) {
                throw new SQLException("Falha ao gravar usuário.");
            }

            conn.commit();
            return novoUsuario;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao gravar usuário e pessoa: " + e.getMessage(), e);
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
        if (pessoa.getNome() == null || pessoa.getNome().trim().isEmpty()) {
            throw new Exception("Nome da pessoa é obrigatório para alteração.");
        }

        UsuarioModel existente = usuarioModel.getUsuDAO().getId(usuario.getPessoa_idpessoa(), pessoaModel);
        if (existente == null) {
            throw new Exception("Usuário não encontrado para atualização.");
        }
        pessoa.setIdpessoa(usuario.getPessoa_idpessoa());

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            boolean pessoaAtualizada = pessoaModel.getPessoaDAO().alterar(pessoa, conn);
            if (!pessoaAtualizada) {
                throw new SQLException("Falha ao atualizar pessoa.");
            }

            boolean usuarioAtualizado = usuarioModel.getUsuDAO().alterar(usuario, conn);
            if (!usuarioAtualizado) {
                throw new SQLException("Falha ao atualizar usuário.");
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao alterar usuário e pessoa: " + e.getMessage(), e);
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

    public boolean apagar(Integer pessoaId) throws Exception{
        UsuarioModel existente = usuarioModel.getUsuDAO().getId(pessoaId, pessoaModel);
        if (existente == null) {
            throw new Exception("Usuário não encontrado para exclusão.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);


            boolean usuarioDeletado = usuarioModel.getUsuDAO().apagar(pessoaId, conn);
            if (!usuarioDeletado) {

                throw new SQLException("Falha ao excluir usuário.");
            }


            boolean pessoaDeletada = pessoaModel.getPessoaDAO().apagar(pessoaId, conn);
            if (!pessoaDeletada) {
                throw new SQLException("Falha ao excluir pessoa associada.");
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao excluir usuário e pessoa: " + e.getMessage(), e);
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
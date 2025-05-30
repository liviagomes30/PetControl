package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.config.SingletonDB; // Import SingletonDB
import salvacao.petcontrol.model.PessoaModel;
import salvacao.petcontrol.model.UsuarioModel; // Import UsuarioModel for dependency check

import java.sql.Connection; // Import Connection
import java.sql.SQLException; // Import SQLException
import java.util.List;

@Service
public class PessoaService {
    @Autowired
    private PessoaModel pessoaModel = new PessoaModel();

    @Autowired
    private UsuarioModel usuarioModel = new UsuarioModel();

    public PessoaModel getId(Integer id){
        return pessoaModel.getPessoaDAO().getId(id);
    }

    public List<PessoaModel> getAll(){
        return pessoaModel.getPessoaDAO().getAll();
    }
    public List<PessoaModel> getByNome(String filtro){
        return pessoaModel.getPessoaDAO().getByNome(filtro);
    }
    public List<PessoaModel> getByCpf(String filtro){
        return pessoaModel.getPessoaDAO().getByCpf(filtro);
    }
    public List<PessoaModel> getByEmail(String filtro){
        return pessoaModel.getPessoaDAO().getByEmail(filtro);
    }
    public List<PessoaModel> getByTelefone(String filtro){
        return pessoaModel.getPessoaDAO().getByTelefone(filtro);
    }

    public PessoaModel gravar(PessoaModel pessoa) throws Exception{
        if (pessoa.getNome() == null || pessoa.getNome().trim().isEmpty()) {
            throw new Exception("Nome da pessoa é obrigatório.");
        }
        if (pessoa.getCpf() == null || pessoa.getCpf().trim().isEmpty()) {
            throw new Exception("CPF da pessoa é obrigatório.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            PessoaModel novaPessoa = pessoaModel.getPessoaDAO().gravar(pessoa, conn);

            conn.commit();
            return novaPessoa;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao gravar pessoa: " + e.getMessage(), e);
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

    public boolean alterar(PessoaModel pessoa) throws Exception{
        if (pessoa.getIdpessoa() == null) {
            throw new Exception("ID da pessoa é obrigatório para alteração.");
        }
        if (pessoa.getNome() == null || pessoa.getNome().trim().isEmpty()) {
            throw new Exception("Nome da pessoa é obrigatório.");
        }

        PessoaModel existente = pessoaModel.getPessoaDAO().getId(pessoa.getIdpessoa());
        if (existente == null) {
            throw new Exception("Pessoa não encontrada para atualização.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean atualizado = pessoaModel.getPessoaDAO().alterar(pessoa, conn);

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
            throw new Exception("Erro ao alterar pessoa: " + e.getMessage(), e);
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
        PessoaModel existente = pessoaModel.getPessoaDAO().getId(id);
        if (existente == null) {
            throw new Exception("Pessoa não encontrada para exclusão.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean isAssociatedWithUser = usuarioModel.getUsuDAO().getId(id, pessoaModel) != null;
            if (isAssociatedWithUser) {
                throw new SQLException("Pessoa não pode ser excluída pois está associada a um usuário.");
            }

            boolean deletado = pessoaModel.getPessoaDAO().apagar(id, conn);

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
            throw new Exception("Erro ao excluir pessoa: " + e.getMessage(), e);
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
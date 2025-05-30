// salvacao.petcontrol.dal.UsuarioDAO.java
package salvacao.petcontrol.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.PessoaModel;
import salvacao.petcontrol.model.UsuarioModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioDAO {

    @Autowired
    private PessoaModel pessoaModel = new PessoaModel();

    public UsuarioModel getId(Integer pessoaId) {
        UsuarioModel usuario = null;
        String sql = "SELECT * FROM usuario WHERE pessoa_idpessoa = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, pessoaId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new UsuarioModel(
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getInt("pessoa_idpessoa")
                );

                // Carregar dados da pessoa
                PessoaModel pessoa = pessoaModel.getPessoaDAO().getId(pessoaId);
                if (pessoa != null) {
                    usuario.setPessoa(pessoa);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    public UsuarioModel gravar(UsuarioModel usuario) { // Added gravar method
        String sql = "INSERT INTO usuario (login, senha, pessoa_idpessoa) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getLogin());
            stmt.setString(2, usuario.getSenha());
            stmt.setInt(3, usuario.getPessoa_idpessoa());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar usuário: " + e.getMessage(), e);
        }
        return usuario;
    }

    public boolean alterar(UsuarioModel usuario) {
        String sql = "UPDATE usuario SET login = ?, senha = ? WHERE pessoa_idpessoa = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, usuario.getLogin());
            stmt.setString(2, usuario.getSenha());
            stmt.setInt(3, usuario.getPessoa_idpessoa());
            int linhasMod = stmt.executeUpdate();
            if (linhasMod == 0) {
                throw new RuntimeException("Nenhum usuário foi atualizado.");
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    public boolean apagar(Integer pessoaId) throws SQLException {
        String sqlCheckAcertoEstoque = "SELECT COUNT(*) FROM acertoestoque WHERE usuario_pessoa_id = ?";
        try (PreparedStatement stmtCheck = SingletonDB.getConexao().getPreparedStatement(sqlCheckAcertoEstoque)) {
            stmtCheck.setInt(1, pessoaId);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Usuário não pode ser excluído pois está associado a acertos de estoque.");
            }
        }

        String sql = "DELETE FROM usuario WHERE pessoa_idpessoa = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, pessoaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<UsuarioModel> getAll() {
        List<UsuarioModel> list = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try {
            ResultSet rs = SingletonDB.getConexao().consultar(sql);
            while (rs.next()) {
                UsuarioModel usuario = new UsuarioModel(
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getInt("pessoa_idpessoa")
                );

                list.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<UsuarioModel> getByLogin(String filtro) {
        List<UsuarioModel> list = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE UPPER(login) LIKE UPPER(?)";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UsuarioModel usuario = new UsuarioModel(
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getInt("pessoa_idpessoa")
                );
                list.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
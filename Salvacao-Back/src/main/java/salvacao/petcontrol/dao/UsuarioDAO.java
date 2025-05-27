package salvacao.petcontrol.dao;

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
import java.sql.Connection; // Import Connection

@Repository
public class UsuarioDAO {

    // Removed @Autowired PessoaModel pessoaModel = new PessoaModel();
    // PessoaModel will be passed from the service.

    public UsuarioModel getId(Integer pessoaId, PessoaModel pessoaModel) { // Accept PessoaModel to access its DAO
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

                // Carregar dados da pessoa usando o DAO da instância de PessoaModel
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

    public UsuarioModel gravar(UsuarioModel usuario, Connection conn) throws SQLException { // Accept Connection
        String sql = "INSERT INTO usuario (login, senha, pessoa_idpessoa) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Use provided connection
            stmt.setString(1, usuario.getLogin());
            stmt.setString(2, usuario.getSenha());
            stmt.setInt(3, usuario.getPessoa_idpessoa());
            int linhasMod = stmt.executeUpdate();
            if (linhasMod == 0) {
                throw new SQLException("Nenhum usuário foi adicionado.");
            }
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                // Assuming id is for the Pessoa_idpessoa which is already set
                // If a new ID for the 'usuario' table itself is generated, it would be set here.
                // For now, it matches pessoa_idpessoa as per the current schema.
            }
        } catch (SQLException e) {
            throw e; // Re-throw to be caught by the service for rollback
        }
        return usuario;
    }

    public boolean alterar(UsuarioModel usuario, Connection conn) throws SQLException { // Accept Connection
        String sql = "UPDATE usuario SET login = ?, senha = ? WHERE pessoa_idpessoa = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) { // Use provided connection
            stmt.setString(1, usuario.getLogin());
            stmt.setString(2, usuario.getSenha());
            stmt.setInt(3, usuario.getPessoa_idpessoa());
            int linhasMod = stmt.executeUpdate();
            if (linhasMod == 0) {
                return false; // Indicate no row was updated
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw e; // Re-throw to be caught by the service for rollback
        }
    }

    public boolean apagar(Integer pessoaId, Connection conn) throws SQLException { // Accept Connection
        String sqlCheckAcertoEstoque = "SELECT COUNT(*) FROM acertoestoque WHERE usuario_pessoa_id = ?";
        try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheckAcertoEstoque)) { // Use provided connection
            stmtCheck.setInt(1, pessoaId);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Usuário não pode ser excluído pois está associado a acertos de estoque.");
            }
        }

        String sql = "DELETE FROM usuario WHERE pessoa_idpessoa = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) { // Use provided connection
            stmt.setInt(1, pessoaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw e; // Re-throw to be caught by the service for rollback
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
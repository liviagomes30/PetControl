package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.PessoaModel;
import salvacao.petcontrol.model.UsuarioModel;
import salvacao.petcontrol.dto.UsuarioCompletoDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioDAO {

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
                        rs.getInt("pessoa_idpessoa"));

                // Carregar dados da pessoa usando consulta direta
                PessoaModel pessoa = buscarPessoaPorId(pessoaId);
                if (pessoa != null) {
                    usuario.setPessoa(pessoa);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    // Método auxiliar para buscar pessoa por ID
    private PessoaModel buscarPessoaPorId(Integer id) {
        PessoaModel pessoa = null;
        String sql = "SELECT * FROM pessoa WHERE idpessoa = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                pessoa = new PessoaModel(
                        rs.getInt("idpessoa"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pessoa;
    }

    public UsuarioModel gravar(UsuarioModel usuario) { // Added gravar method
        String sql = "INSERT INTO usuario (login, senha, pessoa_idpessoa) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
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
                        rs.getInt("pessoa_idpessoa"));

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
                        rs.getInt("pessoa_idpessoa"));
                list.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // New method to find complete user information
    public UsuarioCompletoDTO findUsuarioCompleto(Integer pessoaId) {
        String sql = "SELECT u.login, u.senha, u.pessoa_idpessoa, " +
                "p.idpessoa, p.nome, p.cpf, p.endereco, p.telefone, p.email " +
                "FROM usuario u " +
                "INNER JOIN pessoa p ON u.pessoa_idpessoa = p.idpessoa " +
                "WHERE u.pessoa_idpessoa = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, pessoaId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UsuarioModel usuario = new UsuarioModel(
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getInt("pessoa_idpessoa"));

                PessoaModel pessoa = new PessoaModel(
                        rs.getInt("idpessoa"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("email"));

                return new UsuarioCompletoDTO(usuario, pessoa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Check if user can be deleted (not referenced in other tables)
    public boolean usuarioPodeSerExcluido(Integer pessoaId) throws SQLException {
        String[] checkTables = {
                "SELECT COUNT(*) FROM acertoestoque WHERE usuario_pessoa_id = ?",
                "SELECT COUNT(*) FROM agendavacinacao WHERE usuario_pessoa_idpessoa = ?",
                "SELECT COUNT(*) FROM movimentacaoestoque WHERE usuario_pessoa_id = ?"
        };

        for (String sql : checkTables) {
            try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
                stmt.setInt(1, pessoaId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    // Deactivate user by updating a status field (assuming we add an 'ativo' field)
    public boolean desativarUsuario(Integer pessoaId) {
        // Note: The database schema doesn't have an 'ativo' field, so we'll add a
        // comment
        // In a real scenario, you might want to add an 'ativo' BOOLEAN field to the
        // usuario table
        String sql = "UPDATE usuario SET login = CONCAT(login, '_INATIVO_', EXTRACT(EPOCH FROM NOW()::timestamp)) WHERE pessoa_idpessoa = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, pessoaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Reactivate user
    public boolean reativarUsuario(Integer pessoaId) {
        // This assumes login was modified during deactivation
        String sql = "UPDATE usuario SET login = SUBSTRING(login, 1, POSITION('_INATIVO_' IN login) - 1) " +
                "WHERE pessoa_idpessoa = ? AND login LIKE '%_INATIVO_%'";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, pessoaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get users by "fabricante" - adapting to search by pessoa name
    public List<UsuarioCompletoDTO> getByFabricante(String filtro) {
        return getByNome(filtro); // Reusing nome search since fabricante doesn't apply to users
    }

    public List<UsuarioCompletoDTO> listarUsuariosFiltrados(String filtro) throws SQLException {
        List<UsuarioCompletoDTO> list = new ArrayList<>();
        String sql = "SELECT u.login, u.senha, u.pessoa_idpessoa, " +
                "p.idpessoa, p.nome, p.cpf, p.endereco, p.telefone, p.email " +
                "FROM usuario u " +
                "INNER JOIN pessoa p ON u.pessoa_idpessoa = p.idpessoa ";

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql += "WHERE UPPER(u.login) LIKE UPPER(?) " +
                    "OR UPPER(p.nome) LIKE UPPER(?) " +
                    "OR UPPER(p.cpf) LIKE UPPER(?) " +
                    "OR UPPER(p.email) LIKE UPPER(?)";
        }

        sql += " ORDER BY p.nome";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            if (filtro != null && !filtro.trim().isEmpty()) {
                String searchPattern = "%" + filtro + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);
            }
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UsuarioModel usuario = new UsuarioModel(
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getInt("pessoa_idpessoa"));

                PessoaModel pessoa = new PessoaModel(
                        rs.getInt("idpessoa"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("email"));

                list.add(new UsuarioCompletoDTO(usuario, pessoa));
            }
        }

        return list;
    }

    // Get users by "tipo descricao" - adapting to search by login
    public List<UsuarioCompletoDTO> getByTipoDescricao(String filtro) {
        List<UsuarioCompletoDTO> list = new ArrayList<>();
        String sql = "SELECT u.login, u.senha, u.pessoa_idpessoa, " +
                "p.idpessoa, p.nome, p.cpf, p.endereco, p.telefone, p.email " +
                "FROM usuario u " +
                "INNER JOIN pessoa p ON u.pessoa_idpessoa = p.idpessoa " +
                "WHERE UPPER(u.login) LIKE UPPER(?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UsuarioModel usuario = new UsuarioModel(
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getInt("pessoa_idpessoa"));

                PessoaModel pessoa = new PessoaModel(
                        rs.getInt("idpessoa"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("email"));

                list.add(new UsuarioCompletoDTO(usuario, pessoa));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Helper method to search by nome
    public List<UsuarioCompletoDTO> getByNome(String filtro) {
        List<UsuarioCompletoDTO> list = new ArrayList<>();
        String sql = "SELECT u.login, u.senha, u.pessoa_idpessoa, " +
                "p.idpessoa, p.nome, p.cpf, p.endereco, p.telefone, p.email " +
                "FROM usuario u " +
                "INNER JOIN pessoa p ON u.pessoa_idpessoa = p.idpessoa ";

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql += "WHERE UPPER(p.nome) LIKE UPPER(?)";
        }

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            if (filtro != null && !filtro.trim().isEmpty()) {
                stmt.setString(1, "%" + filtro + "%");
            }
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UsuarioModel usuario = new UsuarioModel(
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getInt("pessoa_idpessoa"));

                PessoaModel pessoa = new PessoaModel(
                        rs.getInt("idpessoa"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("email"));

                list.add(new UsuarioCompletoDTO(usuario, pessoa));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Method to get all users with complete information
    public List<UsuarioCompletoDTO> getAllUsuarios() {
        List<UsuarioCompletoDTO> list = new ArrayList<>();
        String sql = "SELECT u.login, u.senha, u.pessoa_idpessoa, " +
                "p.idpessoa, p.nome, p.cpf, p.endereco, p.telefone, p.email " +
                "FROM usuario u " +
                "INNER JOIN pessoa p ON u.pessoa_idpessoa = p.idpessoa " +
                "ORDER BY p.nome";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UsuarioModel usuario = new UsuarioModel(
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getInt("pessoa_idpessoa"));

                PessoaModel pessoa = new PessoaModel(
                        rs.getInt("idpessoa"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("email"));

                list.add(new UsuarioCompletoDTO(usuario, pessoa));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Authentication methods
    public UsuarioCompletoDTO findByLogin(String login) {
        String sql = "SELECT u.login, u.senha, u.pessoa_idpessoa, " +
                "p.idpessoa, p.nome, p.cpf, p.endereco, p.telefone, p.email " +
                "FROM usuario u " +
                "INNER JOIN pessoa p ON u.pessoa_idpessoa = p.idpessoa " +
                "WHERE u.login = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UsuarioModel usuario = new UsuarioModel(
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getInt("pessoa_idpessoa"));

                PessoaModel pessoa = new PessoaModel(
                        rs.getInt("idpessoa"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("email"));

                return new UsuarioCompletoDTO(usuario, pessoa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean validateCredentials(String login, String senha) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE login = ? AND senha = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(Integer pessoaId, String novaSenha) {
        String sql = "UPDATE usuario SET senha = ? WHERE pessoa_idpessoa = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, novaSenha);
            stmt.setInt(2, pessoaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginExists(String login) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE login = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
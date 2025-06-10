package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.PessoaModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;

@Repository
public class PessoaDAO {

    public PessoaModel getId(Integer id) {
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

    public PessoaModel gravar(PessoaModel pessoa, Connection conn) throws SQLException {
        String sql = "INSERT INTO pessoa (nome, cpf, endereco, telefone, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, pessoa.getNome());
            stmt.setString(2, pessoa.getCpf());
            stmt.setString(3, pessoa.getEndereco());
            stmt.setString(4, pessoa.getTelefone());
            stmt.setString(5, pessoa.getEmail());
            int linhasMod = stmt.executeUpdate();
            if (linhasMod > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    pessoa.setIdpessoa(rs.getInt(1));
                }
            } else {
                throw new SQLException("Falha ao adicionar pessoa.");
            }
        } catch (SQLException e) {
            throw e;
        }
        return pessoa;
    }

    public boolean alterar(PessoaModel pessoa, Connection conn) throws SQLException {
        String sql = "UPDATE pessoa SET nome = ?, cpf = ?, endereco = ?, telefone = ?, email = ? WHERE idpessoa = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pessoa.getNome());
            stmt.setString(2, pessoa.getCpf());
            stmt.setString(3, pessoa.getEndereco());
            stmt.setString(4, pessoa.getTelefone());
            stmt.setString(5, pessoa.getEmail());
            stmt.setInt(6, pessoa.getIdpessoa());
            int linhasMod = stmt.executeUpdate();
            if (linhasMod == 0) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    public boolean apagar(Integer id, Connection conn) throws SQLException {

        String sqlCheckUsuario = "SELECT COUNT(*) FROM usuario WHERE pessoa_idpessoa = ?";
        try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheckUsuario)) {
            stmtCheck.setInt(1, id);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Pessoa não pode ser excluída pois está associada a um usuário.");
            }
        }

        String sql = "DELETE FROM pessoa WHERE idpessoa = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<PessoaModel> getAll() {
        List<PessoaModel> list = new ArrayList<>();
        String sql = "SELECT * FROM pessoa";
        try {
            ResultSet rs = SingletonDB.getConexao().consultar(sql);
            while (rs.next()) {
                PessoaModel pessoa = new PessoaModel(
                        rs.getInt("idpessoa"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("email")
                );
                list.add(pessoa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<PessoaModel> getByNome(String filtro) {
        List<PessoaModel> list = new ArrayList<>();
        String sql = "SELECT * FROM pessoa WHERE UPPER(nome) LIKE UPPER(?)";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PessoaModel pessoa = new PessoaModel(
                        rs.getInt("idpessoa"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("email")
                );
                list.add(pessoa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<PessoaModel> getByCpf(String filtro) {
        List<PessoaModel> list = new ArrayList<>();
        String sql = "SELECT * FROM pessoa WHERE UPPER(cpf) LIKE UPPER(?)";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PessoaModel pessoa = new PessoaModel(
                        rs.getInt("idpessoa"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("email")
                );
                list.add(pessoa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<PessoaModel> getByEmail(String filtro) {
        List<PessoaModel> list = new ArrayList<>();
        String sql = "SELECT * FROM pessoa WHERE UPPER(email) LIKE UPPER(?)";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PessoaModel pessoa = new PessoaModel(
                        rs.getInt("idpessoa"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("email")
                );
                list.add(pessoa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<PessoaModel> getByTelefone(String filtro) {
        List<PessoaModel> list = new ArrayList<>();
        String sql = "SELECT * FROM pessoa WHERE UPPER(telefone) LIKE UPPER(?)";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PessoaModel pessoa = new PessoaModel(
                        rs.getInt("idpessoa"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getString("email")
                );
                list.add(pessoa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
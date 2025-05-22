package salvacao.petcontrol.dal;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.PessoaModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PessoaDAL {

    public PessoaModel findById(Integer id) {
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

    public PessoaModel findByPessoa(String cpf) {
        String sql = "SELECT * FROM pessoa WHERE cpf = ?";
        try(PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                PessoaModel pessoa = new PessoaModel();
                pessoa.setIdpessoa(rs.getInt("idpessoa"));
                pessoa.setNome(rs.getString("nome"));
                pessoa.setCpf(rs.getString("cpf"));
                pessoa.setEndereco(rs.getString("endereco"));
                pessoa.setTelefone(rs.getString("telefone"));
                pessoa.setEmail(rs.getString("email"));
                return pessoa;
            }

            return null;
        } catch (SQLException e) {
            System.err.println("Erro ao buscar pessoa por CPF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public PessoaModel addPessoa(PessoaModel pessoa) {
        String sql = "INSERT INTO pessoa (idpessoa, nome, cpf, endereco, telefone, email) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            // Desabilitar autocommit para controle manual da transação
            SingletonDB.getConexao().getConnection().setAutoCommit(false);

            try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
                stmt.setInt(1, pessoa.getIdpessoa());
                stmt.setString(2, pessoa.getNome());
                stmt.setString(3, pessoa.getCpf());
                stmt.setString(4, pessoa.getEndereco());
                stmt.setString(5, pessoa.getTelefone());
                stmt.setString(6, pessoa.getEmail());

                if (stmt.executeUpdate() > 0) {
                    // Commit da transação se a inserção foi bem-sucedida
                    SingletonDB.getConexao().getConnection().commit();
                } else {
                    // Rollback em caso de falha
                    SingletonDB.getConexao().getConnection().rollback();
                    throw new RuntimeException("Falha ao inserir pessoa");
                }
            }

            // Reativar autocommit
            SingletonDB.getConexao().getConnection().setAutoCommit(true);

        } catch (SQLException e) {
            try {
                SingletonDB.getConexao().getConnection().rollback();
                SingletonDB.getConexao().getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Erro ao adicionar pessoa: " + e.getMessage(), e);
        }

        return pessoa;
    }

    public Boolean updatePessoa(String cpf, PessoaModel pessoa) {
        if (!cpf.equals(pessoa.getCpf()))
            throw new IllegalArgumentException("O CPF não pode ser alterado.");

        String sql = "UPDATE pessoa SET nome = ?, endereco = ?, telefone = ?, email = ? WHERE cpf = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, pessoa.getNome());
            stmt.setString(2, pessoa.getEndereco());
            stmt.setString(3, pessoa.getTelefone());
            stmt.setString(4, pessoa.getEmail());
            stmt.setString(5, cpf); // CPF como critério de busca

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar a pessoa: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteByPessoa(String cpf) {
        String sql = "DELETE FROM pessoa WHERE cpf = ?";
        try(PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql))
        {
            stmt.setString(1, cpf);
            return stmt.executeUpdate() > 0;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public List<PessoaModel> getAll() {
        List<PessoaModel> pessoas = new ArrayList<>();

        String sql = "select * from pessoa";
        try(ResultSet rs = SingletonDB.getConexao().consultar(sql))
        {
            while (rs.next()) {
                PessoaModel pessoa = new PessoaModel(
                        rs.getInt("idpessoa"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getString("endereco")
                );
                pessoas.add(pessoa);
            }

        } catch (Exception e) {
            System.err.println("nao foi nao kkk" + e.getMessage());
            e.printStackTrace();
        }
        return pessoas;
    }

    // Etc
}
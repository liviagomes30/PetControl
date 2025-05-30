package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.MovimentacaoEstoqueModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

@Repository
public class MovimentacaoEstoqueDAO {

    public MovimentacaoEstoqueModel gravar(MovimentacaoEstoqueModel movimentacao) {
        String sql = "INSERT INTO movimentacaoestoque(tipomovimentacao, data, usuario_pessoa_id, obs, fornecedor) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, movimentacao.getTipomovimentacao());
            stmt.setDate(2, java.sql.Date.valueOf(movimentacao.getData()));
            stmt.setInt(3, movimentacao.getUsuarioPessoaId());

            if (movimentacao.getObs() != null) {
                stmt.setString(4, movimentacao.getObs());
            } else {
                stmt.setNull(4, java.sql.Types.VARCHAR);
            }

            if (movimentacao.getFornecedor() != null) {
                stmt.setString(5, movimentacao.getFornecedor());
            } else {
                stmt.setNull(5, java.sql.Types.VARCHAR);
            }

            int linhasMod = stmt.executeUpdate();
            if (linhasMod > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    movimentacao.setIdmovimentacao(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar movimentação de estoque: " + e.getMessage(), e);
        }

        return movimentacao;
    }

    public MovimentacaoEstoqueModel getId(Integer id) {
        String sql = "SELECT * FROM movimentacaoestoque WHERE idmovimentacao = ?";
        MovimentacaoEstoqueModel movimentacao = null;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                LocalDate data = resultset.getDate("data").toLocalDate();

                movimentacao = new MovimentacaoEstoqueModel(
                        resultset.getInt("idmovimentacao"),
                        resultset.getString("tipomovimentacao"),
                        data,
                        resultset.getInt("usuario_pessoa_id"),
                        resultset.getString("obs"),
                        resultset.getString("fornecedor")
                );
            } else {
                System.out.println("Nenhuma movimentação encontrada com ID: " + id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movimentacao;
    }



}

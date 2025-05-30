package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.ItemMovimentacaoModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class ItemMovimentacaoDAO {

    public ItemMovimentacaoModel gravar(ItemMovimentacaoModel item) {
        String sql = "INSERT INTO itemmovimentacao(movimentacao_id, produto_id, quantidade, motivomovimentacao_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.getMovimentacaoId());
            stmt.setInt(2, item.getProdutoId());
            stmt.setDouble(3, item.getQuantidade());

            if (item.getMotivomovimentacaoId() != null) {
                stmt.setInt(4, item.getMotivomovimentacaoId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            int linhasMod = stmt.executeUpdate();
            if (linhasMod > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    item.setIditem(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar item de movimentação: " + e.getMessage(), e);
        }

        return item;
    }

    public ItemMovimentacaoModel getId(Integer id) {
        String sql = "SELECT * FROM itemmovimentacao WHERE iditem = ?";
        ItemMovimentacaoModel item = null;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                item = new ItemMovimentacaoModel(
                        resultset.getInt("iditem"),
                        resultset.getInt("movimentacao_id"),
                        resultset.getInt("produto_id"),
                        resultset.getDouble("quantidade"),
                        resultset.getInt("motivomovimentacao_id")
                );
            } else {
                System.out.println("Nenhum item de movimentação encontrado com ID: " + id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }



}

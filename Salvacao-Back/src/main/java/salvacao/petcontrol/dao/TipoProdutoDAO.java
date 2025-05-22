// salvacao.petcontrol.dal.TipoProdutoDAO.java
package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.TipoProdutoModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TipoProdutoDAO {

    public TipoProdutoModel getId(Integer id) {
        TipoProdutoModel tipoProduto = null;
        String sql = "SELECT * FROM tipoproduto WHERE idtipoproduto = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tipoProduto = new TipoProdutoModel(
                        rs.getInt("idtipoproduto"),
                        rs.getString("descricao")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tipoProduto;
    }

    public List<TipoProdutoModel> getAll() {
        List<TipoProdutoModel> list = new ArrayList<>();
        String sql = "SELECT * FROM tipoproduto";
        try {
            ResultSet rs = SingletonDB.getConexao().consultar(sql);
            while (rs.next()) {
                TipoProdutoModel tipoProduto = new TipoProdutoModel(
                        rs.getInt("idtipoproduto"),
                        rs.getString("descricao")
                );
                list.add(tipoProduto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public TipoProdutoModel gravar(TipoProdutoModel tipoProduto) { // Added gravar method
        String sql = "INSERT INTO tipoproduto (descricao) VALUES (?)";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tipoProduto.getDescricao());
            int linhasMod = stmt.executeUpdate();
            if (linhasMod > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    tipoProduto.setIdtipoproduto(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar tipo de produto: " + e.getMessage(), e);
        }
        return tipoProduto;
    }

    public boolean alterar(TipoProdutoModel tipoProduto) { // Added alterar method
        String sql = "UPDATE tipoproduto SET descricao = ? WHERE idtipoproduto = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, tipoProduto.getDescricao());
            stmt.setInt(2, tipoProduto.getIdtipoproduto());
            int linhasMod = stmt.executeUpdate();
            if (linhasMod == 0) {
                throw new RuntimeException("Nenhum tipo de produto foi atualizado.");
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar tipo de produto: " + e.getMessage(), e);
        }
    }

    public boolean apagar(Integer id) throws SQLException { // Added apagar method
        // Check if TipoProduto is referenced by any Produto
        String sqlCheck = "SELECT COUNT(*) FROM produto WHERE idtipoproduto = ?";
        try (PreparedStatement stmtCheck = SingletonDB.getConexao().getPreparedStatement(sqlCheck)) {
            stmtCheck.setInt(1, id);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Tipo de produto não pode ser excluído pois está associado a produtos.");
            }
        }

        String sql = "DELETE FROM tipoproduto WHERE idtipoproduto = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<TipoProdutoModel> getByDescricao(String filtro) { // Implemented the method
        List<TipoProdutoModel> list = new ArrayList<>();
        String sql = "SELECT * FROM tipoproduto WHERE UPPER(descricao) LIKE UPPER(?)";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TipoProdutoModel tipoProduto = new TipoProdutoModel(
                        rs.getInt("idtipoproduto"),
                        rs.getString("descricao")
                );
                list.add(tipoProduto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
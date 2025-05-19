package salvacao.petcontrol.dal;


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
public class TipoProdutoDAL {

    public TipoProdutoModel findById(Integer id) {
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
}

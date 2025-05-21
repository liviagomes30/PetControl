package salvacao.petcontrol.dalNÃOUSARMAIS;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.UnidadeMedidaModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UnidadeMedidaDAL {

    public UnidadeMedidaModel findById(Integer id) {
        UnidadeMedidaModel unidadeMedida = null;
        String sql = "SELECT * FROM unidadedemedida WHERE idunidademedida = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                unidadeMedida = new UnidadeMedidaModel(
                        rs.getInt("idunidademedida"),
                        rs.getString("descricao"),
                        rs.getString("sigla")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unidadeMedida;
    }

    public List<UnidadeMedidaModel> getAll() {
        List<UnidadeMedidaModel> list = new ArrayList<>();
        String sql = "SELECT * FROM unidadedemedida";
        try {
            ResultSet rs = SingletonDB.getConexao().consultar(sql);
            while (rs.next()) {
                UnidadeMedidaModel unidadeMedida = new UnidadeMedidaModel(
                        rs.getInt("idunidademedida"),
                        rs.getString("descricao"),
                        rs.getString("sigla")
                );
                list.add(unidadeMedida);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
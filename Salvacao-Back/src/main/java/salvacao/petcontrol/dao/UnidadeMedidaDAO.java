package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.UnidadeMedidaModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection; // Import Connection

@Repository
public class UnidadeMedidaDAO {

    public UnidadeMedidaModel getId(Integer id) {
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

    public UnidadeMedidaModel gravar(UnidadeMedidaModel unidadeMedida, Connection conn) throws SQLException {
        String sql = "INSERT INTO unidadedemedida (descricao, sigla) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, unidadeMedida.getDescricao());
            stmt.setString(2, unidadeMedida.getSigla());
            int linhasMod = stmt.executeUpdate();
            if (linhasMod > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    unidadeMedida.setIdUnidadeMedida(rs.getInt(1));
                }
            } else {
                throw new SQLException("Falha ao adicionar unidade de medida.");
            }
        } catch (SQLException e) {
            throw e;
        }
        return unidadeMedida;
    }

    public boolean alterar(UnidadeMedidaModel unidadeMedida, Connection conn) throws SQLException {
        String sql = "UPDATE unidadedemedida SET descricao = ?, sigla = ? WHERE idunidademedida = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) { // Use provided connection
            stmt.setString(1, unidadeMedida.getDescricao());
            stmt.setString(2, unidadeMedida.getSigla());
            stmt.setInt(3, unidadeMedida.getIdUnidadeMedida());
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
        // Check if UnidadeMedida is referenced by any Produto
        String sqlCheck = "SELECT COUNT(*) FROM produto WHERE idunidademedida = ?";
        try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck)) { // Use provided connection
            stmtCheck.setInt(1, id);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Unidade de medida não pode ser excluída pois está associada a produtos.");
            }
        }

        String sql = "DELETE FROM unidadedemedida WHERE idunidademedida = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) { // Use provided connection
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<UnidadeMedidaModel> getByDescricaoSigla(String filtro) {
        List<UnidadeMedidaModel> list = new ArrayList<>();
        String sql = "SELECT * FROM unidadedemedida WHERE UPPER(descricao) LIKE UPPER(?) OR UPPER(sigla) LIKE UPPER(?)";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            stmt.setString(2, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();
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
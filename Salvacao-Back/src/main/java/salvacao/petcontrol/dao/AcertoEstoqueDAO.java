package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.AcertoEstoqueModel;
import salvacao.petcontrol.model.ItemAcertoEstoqueModel;
import salvacao.petcontrol.model.EstoqueModel; // EstoqueModel will be passed from service to access its DAO
import salvacao.petcontrol.model.ProdutoModel; // ProdutoModel will be passed from service to access its DAO

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Repository
public class AcertoEstoqueDAO {

    // Removed @Autowired EstoqueModel estoqueModel = new EstoqueModel();
    // It will be passed from the service.

    public AcertoEstoqueModel getId(Integer id) {
        AcertoEstoqueModel acerto = null;
        String sql = "SELECT * FROM acertoestoque WHERE idacerto = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Date dataSQL = rs.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                acerto = new AcertoEstoqueModel(
                        rs.getInt("idacerto"),
                        data,
                        rs.getInt("usuario_pessoa_id"),
                        rs.getString("motivo"),
                        rs.getString("observacao")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return acerto;
    }

    public List<AcertoEstoqueModel> getAll() {
        List<AcertoEstoqueModel> acertosList = new ArrayList<>();
        String sql = "SELECT * FROM acertoestoque ORDER BY data DESC";

        try {
            ResultSet rs = SingletonDB.getConexao().consultar(sql);

            while (rs.next()) {
                Date dataSQL = rs.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                AcertoEstoqueModel acerto = new AcertoEstoqueModel(
                        rs.getInt("idacerto"),
                        data,
                        rs.getInt("usuario_pessoa_id"),
                        rs.getString("motivo"),
                        rs.getString("observacao")
                );
                acertosList.add(acerto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return acertosList;
    }


    public List<AcertoEstoqueModel> getByPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        List<AcertoEstoqueModel> acertosList = new ArrayList<>();
        String sql = "SELECT * FROM acertoestoque WHERE data BETWEEN ? AND ? ORDER BY data DESC";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(dataInicio));
            stmt.setDate(2, java.sql.Date.valueOf(dataFim));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Date dataSQL = rs.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                AcertoEstoqueModel acerto = new AcertoEstoqueModel(
                        rs.getInt("idacerto"),
                        data,
                        rs.getInt("usuario_pessoa_id"),
                        rs.getString("motivo"),
                        rs.getString("observacao")
                );
                acertosList.add(acerto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return acertosList;
    }


    public List<AcertoEstoqueModel> getByUsuario(Integer usuarioId) {
        List<AcertoEstoqueModel> acertosList = new ArrayList<>();
        String sql = "SELECT * FROM acertoestoque WHERE usuario_pessoa_id = ? ORDER BY data DESC";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Date dataSQL = rs.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                AcertoEstoqueModel acerto = new AcertoEstoqueModel(
                        rs.getInt("idacerto"),
                        data,
                        rs.getInt("usuario_pessoa_id"),
                        rs.getString("motivo"),
                        rs.getString("observacao")
                );
                acertosList.add(acerto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return acertosList;
    }


    public AcertoEstoqueModel gravarAcerto(AcertoEstoqueModel acerto, Connection conn) throws SQLException {
        String sql = "INSERT INTO acertoestoque (data, usuario_pessoa_id, motivo, observacao) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Use provided connection

            if (acerto.getData() != null) {
                stmt.setDate(1, java.sql.Date.valueOf(acerto.getData()));
            } else {
                stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            }

            stmt.setInt(2, acerto.getUsuario_pessoa_id());
            stmt.setString(3, acerto.getMotivo());
            stmt.setString(4, acerto.getObservacao());

            int linhasMod = stmt.executeUpdate();
            if (linhasMod > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    acerto.setIdacerto(rs.getInt(1));
                }
            } else {
                throw new SQLException("Falha ao inserir acerto de estoque");
            }
        }
        return acerto;
    }


    public List<ItemAcertoEstoqueModel> getItensAcerto(Integer acertoId) {
        List<ItemAcertoEstoqueModel> itensList = new ArrayList<>();
        String sql = "SELECT * FROM itemacertoestoque WHERE acerto_id = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, acertoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ItemAcertoEstoqueModel item = new ItemAcertoEstoqueModel(
                        rs.getInt("iditem"),
                        rs.getInt("acerto_id"),
                        rs.getInt("produto_id"),
                        rs.getBigDecimal("quantidade_antes"),
                        rs.getBigDecimal("quantidade_depois"),
                        rs.getString("tipoajuste")
                );
                itensList.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itensList;
    }


    public ItemAcertoEstoqueModel gravarItemAcerto(ItemAcertoEstoqueModel item, Connection conn) throws SQLException {
        String sql = "INSERT INTO itemacertoestoque (acerto_id, produto_id, quantidade_antes, " +
                "quantidade_depois, tipoajuste) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Use provided connection
            stmt.setInt(1, item.getAcerto_id());
            stmt.setInt(2, item.getProduto_id());
            stmt.setBigDecimal(3, item.getQuantidade_antes());
            stmt.setBigDecimal(4, item.getQuantidade_depois());
            stmt.setString(5, item.getTipoajuste());

            int linhasMod = stmt.executeUpdate();
            if (linhasMod > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    item.setIditem(rs.getInt(1));
                }
            } else {
                throw new SQLException("Falha ao inserir item de acerto de estoque");
            }
        }
        return item;
    }

    // This method is no longer responsible for transaction management.
    // It's broken down into smaller, single-responsibility methods to be orchestrated by the Service.
    // The previous 'efetuarAcertoEstoque' logic is now in AcertoEstoqueService.gravar.
}
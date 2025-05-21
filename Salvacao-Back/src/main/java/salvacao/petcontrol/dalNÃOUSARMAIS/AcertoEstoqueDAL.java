package salvacao.petcontrol.dalNÃOUSARMAIS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.AcertoEstoqueModel;
import salvacao.petcontrol.model.ItemAcertoEstoqueModel;
import salvacao.petcontrol.model.EstoqueModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AcertoEstoqueDAL {

    @Autowired
    private EstoqueDAL estoqueDAL;

    public AcertoEstoqueModel findById(Integer id) {
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

    public List<AcertoEstoqueModel> findAll() {
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


    public List<AcertoEstoqueModel> findByPeriodo(LocalDate dataInicio, LocalDate dataFim) {
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


    public List<AcertoEstoqueModel> findByUsuario(Integer usuarioId) {
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


    public AcertoEstoqueModel inserirAcerto(AcertoEstoqueModel acerto) throws SQLException {
        String sql = "INSERT INTO acertoestoque (data, usuario_pessoa_id, motivo, observacao) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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


    public List<ItemAcertoEstoqueModel> findItensAcerto(Integer acertoId) {
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


    public ItemAcertoEstoqueModel inserirItemAcerto(ItemAcertoEstoqueModel item) throws SQLException {
        String sql = "INSERT INTO itemacertoestoque (acerto_id, produto_id, quantidade_antes, " +
                "quantidade_depois, tipoajuste) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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


    public AcertoEstoqueModel efetuarAcertoEstoque(AcertoEstoqueModel acerto,
                                                   List<ItemAcertoEstoqueModel> itens) throws SQLException {

        SingletonDB.getConexao().getConnection().setAutoCommit(false);

        try {
            AcertoEstoqueModel acertoInserido = inserirAcerto(acerto);

            for (ItemAcertoEstoqueModel item : itens) {
                item.setAcerto_id(acertoInserido.getIdacerto());

                EstoqueModel estoque = estoqueDAL.findByProdutoId(item.getProduto_id());
                if (estoque == null) {
                    throw new SQLException("Produto não encontrado no estoque: " + item.getProduto_id());
                }

                item.setQuantidade_antes(estoque.getQuantidade());

                if (item.getQuantidade_depois().compareTo(item.getQuantidade_antes()) > 0) {
                    item.setTipoajuste("ENTRADA");
                } else {
                    item.setTipoajuste("SAIDA");
                }

                estoque.setQuantidade(item.getQuantidade_depois());
                boolean estoqueAtualizado = estoqueDAL.atualizarEstoque(estoque);

                if (!estoqueAtualizado) {
                    throw new SQLException("Falha ao atualizar estoque para o produto: " + item.getProduto_id());
                }

                inserirItemAcerto(item);
            }

            SingletonDB.getConexao().getConnection().commit();
            return acertoInserido;

        } catch (SQLException e) {
            SingletonDB.getConexao().getConnection().rollback();
            throw e;
        } finally {
            SingletonDB.getConexao().getConnection().setAutoCommit(true);
        }
    }
}
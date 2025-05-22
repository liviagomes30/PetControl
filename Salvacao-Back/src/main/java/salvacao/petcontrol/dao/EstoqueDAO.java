package salvacao.petcontrol.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.model.ProdutoModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Repository
public class EstoqueDAO {
    public EstoqueModel getId(Integer id) {
        EstoqueModel estoque = null;
        String sql = "SELECT * FROM estoque WHERE idestoque = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                estoque = new EstoqueModel(
                        rs.getInt("idestoque"),
                        rs.getInt("idproduto"),
                        rs.getBigDecimal("quantidade")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estoque;
    }

    public EstoqueModel getByProdutoId(Integer idProduto) { // Renamed from findByProdutoId
        EstoqueModel estoque = null;
        String sql = "SELECT * FROM estoque WHERE idproduto = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idProduto);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                estoque = new EstoqueModel(
                        rs.getInt("idestoque"),
                        rs.getInt("idproduto"),
                        rs.getBigDecimal("quantidade")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estoque;
    }

    public List<EstoqueModel> getAll() { // Renamed from findAll
        List<EstoqueModel> estoqueList = new ArrayList<>();
        String sql = "SELECT * FROM estoque";

        try {
            ResultSet rs = SingletonDB.getConexao().consultar(sql);

            while (rs.next()) {
                EstoqueModel estoque = new EstoqueModel(
                        rs.getInt("idestoque"),
                        rs.getInt("idproduto"),
                        rs.getBigDecimal("quantidade")
                );
                estoqueList.add(estoque);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estoqueList;
    }

    public List<EstoqueModel> getEstoqueAbaixoMinimo() {
        List<EstoqueModel> estoqueList = new ArrayList<>();
        String sql = "SELECT e.* FROM estoque e " +
                "JOIN produto p ON e.idproduto = p.idproduto " +
                "WHERE e.quantidade < p.estoque_minimo";

        try {
            ResultSet rs = SingletonDB.getConexao().consultar(sql);

            while (rs.next()) {
                EstoqueModel estoque = new EstoqueModel(
                        rs.getInt("idestoque"),
                        rs.getInt("idproduto"),
                        rs.getBigDecimal("quantidade")
                );
                estoqueList.add(estoque);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estoqueList;
    }

    public EstoqueModel gravar(EstoqueModel estoque) throws SQLException {
        String sql = "INSERT INTO estoque (idestoque, idproduto, quantidade) VALUES (nextval('seq_estoque'), ?, ?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, estoque.getIdproduto());
            stmt.setBigDecimal(2, estoque.getQuantidade());

            ResultSet linhasMod = stmt.executeQuery();
            if (linhasMod.next()) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    estoque.setIdestoque(rs.getInt(1));
                }
            } else {
                throw new SQLException("Falha ao inserir estoque");
            }
        }
        return estoque;
    }

    public boolean alterar(EstoqueModel estoque) {
        String sql = "UPDATE estoque SET quantidade = ? WHERE idestoque = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setBigDecimal(1, estoque.getQuantidade());
            stmt.setInt(2, estoque.getIdestoque());

            int linhasMod = stmt.executeUpdate();
            return linhasMod > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean apagar(Integer id) {
        String sql = "DELETE FROM estoque WHERE idestoque = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);

            int linhasMod = stmt.executeUpdate();
            return linhasMod > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean decrementarEstoque(Integer idProduto, BigDecimal quantidade) {
        EstoqueModel estoque = getByProdutoId(idProduto);
        if (estoque == null) {
            return false;
        }

        if (estoque.getQuantidade().compareTo(quantidade) < 0) {
            return false;
        }

        String sql = "UPDATE estoque SET quantidade = quantidade - ? WHERE idproduto = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setBigDecimal(1, quantidade);
            stmt.setInt(2, idProduto);

            int linhasMod = stmt.executeUpdate();
            return linhasMod > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean decrementarEstoque(Integer idProduto, Integer quantidade) {
        return decrementarEstoque(idProduto, new BigDecimal(quantidade));
    }

    public boolean incrementarEstoque(Integer idProduto, BigDecimal quantidade) {
        EstoqueModel estoque = getByProdutoId(idProduto); // Calls refactored method

        if (estoque == null) {
            try {
                EstoqueModel novoEstoque = new EstoqueModel(null, idProduto, quantidade);
                gravar(novoEstoque); // Calls refactored method
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            String sql = "UPDATE estoque SET quantidade = quantidade + ? WHERE idproduto = ?";

            try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
                stmt.setBigDecimal(1, quantidade);
                stmt.setInt(2, idProduto);

                int linhasMod = stmt.executeUpdate();
                return linhasMod > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean incrementarEstoque(Integer idProduto, Integer quantidade) {
        return incrementarEstoque(idProduto, new BigDecimal(quantidade));
    }

    public boolean verificarEstoqueSuficiente(Integer idProduto, BigDecimal quantidadeNecessaria) {
        EstoqueModel estoque = getByProdutoId(idProduto);

        if (estoque == null) {
            return false;
        }

        return estoque.getQuantidade().compareTo(quantidadeNecessaria) >= 0;
    }

    public boolean verificarEstoqueSuficiente(Integer idProduto, Integer quantidadeNecessaria) {
        return verificarEstoqueSuficiente(idProduto, new BigDecimal(quantidadeNecessaria));
    }

    public List<EstoqueModel> getByNomeProduto(String nomeProduto) { // Renamed from buscarPorNomeProduto
        List<EstoqueModel> estoqueList = new ArrayList<>();
        String sql = "SELECT e.* FROM estoque e " +
                "JOIN produto p ON e.idproduto = p.idproduto " +
                "WHERE UPPER(p.nome) LIKE UPPER(?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + nomeProduto + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                EstoqueModel estoque = new EstoqueModel(
                        rs.getInt("idestoque"),
                        rs.getInt("idproduto"),
                        rs.getBigDecimal("quantidade")
                );
                estoqueList.add(estoque);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estoqueList;
    }

    public List<EstoqueModel> getByTipoProduto(Integer idTipoProduto) {
        List<EstoqueModel> estoqueList = new ArrayList<>();
        String sql = "SELECT e.* FROM estoque e " +
                "JOIN produto p ON e.idproduto = p.idproduto " +
                "WHERE p.idtipoproduto = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idTipoProduto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                EstoqueModel estoque = new EstoqueModel(
                        rs.getInt("idestoque"),
                        rs.getInt("idproduto"),
                        rs.getBigDecimal("quantidade")
                );
                estoqueList.add(estoque);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estoqueList;
    }
}
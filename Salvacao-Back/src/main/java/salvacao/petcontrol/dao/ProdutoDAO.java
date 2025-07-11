package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.dto.ProdutoCompletoDTO;
import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.model.UnidadeMedidaModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProdutoDAO {

    private ProdutoCompletoDTO buildDTOFromResultSet(ResultSet rs) throws SQLException {
        ProdutoModel produto = new ProdutoModel(
                rs.getInt("idproduto"),
                rs.getString("nome"),
                rs.getInt("idtipoproduto"),
                rs.getInt("idunidademedida"),
                rs.getString("fabricante"),
                rs.getBigDecimal("preco"),
                rs.getInt("estoque_minimo"),
                rs.getDate("data_cadastro")
        );
        produto.setAtivo(rs.getBoolean("ativo"));

        TipoProdutoModel tipoProduto = new TipoProdutoModel(
                rs.getInt("idtipoproduto"),
                rs.getString("tipo_descricao")
        );

        UnidadeMedidaModel unidadeMedida = new UnidadeMedidaModel(
                rs.getInt("idunidademedida"),
                rs.getString("unidade_descricao"),
                rs.getString("unidade_sigla")
        );

        return new ProdutoCompletoDTO(produto, tipoProduto, unidadeMedida);
    }

    public ProdutoModel getId(Integer id) {
        ProdutoModel produto = null;
        String sql = "SELECT * FROM produto WHERE idproduto = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                produto = new ProdutoModel(
                        rs.getInt("idproduto"),
                        rs.getString("nome"),
                        rs.getInt("idtipoproduto"),
                        rs.getInt("idunidademedida"),
                        rs.getString("fabricante"),
                        rs.getBigDecimal("preco"),
                        rs.getInt("estoque_minimo"),
                        rs.getDate("data_cadastro")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produto;
    }

    public ProdutoCompletoDTO findProdutoCompleto(Integer id) {
        ProdutoCompletoDTO dto = null;
        String sql = "SELECT p.idproduto, p.nome, p.idtipoproduto, p.idunidademedida, " +
                "p.fabricante, p.preco, p.estoque_minimo, p.data_cadastro, " +
                "t.descricao AS tipo_descricao, " +
                "u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM produto p " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE p.idproduto = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ProdutoModel produto = new ProdutoModel(
                        rs.getInt("idproduto"),
                        rs.getString("nome"),
                        rs.getInt("idtipoproduto"),
                        rs.getInt("idunidademedida"),
                        rs.getString("fabricante"),
                        rs.getBigDecimal("preco"),
                        rs.getInt("estoque_minimo"),
                        rs.getDate("data_cadastro")
                );

                TipoProdutoModel tipoProduto = new TipoProdutoModel(
                        rs.getInt("idtipoproduto"),
                        rs.getString("tipo_descricao")
                );

                UnidadeMedidaModel unidadeMedida = new UnidadeMedidaModel(
                        rs.getInt("idunidademedida"),
                        rs.getString("unidade_descricao"),
                        rs.getString("unidade_sigla")
                );

                dto = new ProdutoCompletoDTO(produto, tipoProduto, unidadeMedida);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dto;
    }

    public ProdutoModel gravar(ProdutoModel produto, Connection conn) throws SQLException {
        System.out.println("Iniciando gravação do produto: " + produto.getNome());
        String sql = "INSERT INTO produto (nome, idtipoproduto, idunidademedida, fabricante, preco, estoque_minimo, data_cadastro, ativo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING idproduto";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setInt(2, produto.getIdtipoproduto());
            stmt.setInt(3, produto.getIdunidademedida());
            stmt.setString(4, produto.getFabricante());

            if (produto.getPreco() != null) {
                stmt.setBigDecimal(5, produto.getPreco());
            } else {
                stmt.setNull(5, java.sql.Types.DECIMAL);
            }

            // Definindo um valor padrão de 0 para estoque_minimo se for nulo
            if (produto.getEstoqueMinimo() != null) {
                stmt.setInt(6, produto.getEstoqueMinimo());
            } else {
                stmt.setInt(6, 0); // Valor padrão para estoque mínimo
            }

            if (produto.getDataCadastro() != null) {
                stmt.setDate(7, new java.sql.Date(produto.getDataCadastro().getTime()));
            } else {
                stmt.setDate(7, new java.sql.Date(System.currentTimeMillis()));
            }
            stmt.setBoolean(8, true);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                produto.setIdproduto(rs.getInt("idproduto"));
                System.out.println("Produto '" + produto.getNome() + "' gravado com sucesso. ID: " + produto.getIdproduto());
                return produto;
            }
            throw new SQLException("Falha ao obter ID do produto após inserção");
        } catch (SQLException e) {
            System.err.println("Erro ao gravar produto '" + produto.getNome() + "': " + e.getMessage());
            throw e;
        }
    }

    public boolean alterar(Integer id, ProdutoModel produto) {
        String sql = "UPDATE produto SET nome = ?, idtipoproduto = ?, idunidademedida = ?, " +
                "fabricante = ?, preco = ?, estoque_minimo = ? WHERE idproduto = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setInt(2, produto.getIdtipoproduto());
            stmt.setInt(3, produto.getIdunidademedida());
            stmt.setString(4, produto.getFabricante());

            if (produto.getPreco() != null) {
                stmt.setBigDecimal(5, produto.getPreco());
            } else {
                stmt.setNull(5, java.sql.Types.NUMERIC);
            }

            if (produto.getEstoqueMinimo() != null) {
                stmt.setInt(6, produto.getEstoqueMinimo());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            stmt.setInt(7, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar produto: " + e.getMessage(), e);
        }
    }

    public boolean apagar(Integer id, Connection conn) throws SQLException {
        System.out.println("Iniciando exclusão do produto ID na DAO: " + id);

        String sqlMedicamento = "DELETE FROM medicamento WHERE idproduto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlMedicamento)) {
            stmt.setInt(1, id);
            int excluidos = stmt.executeUpdate();
            System.out.println("Registros de medicamento excluídos: " + excluidos);
        }

        String sqlVacina = "DELETE FROM vacina WHERE idproduto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlVacina)) {
            stmt.setInt(1, id);
            int excluidos = stmt.executeUpdate();
            System.out.println("Registros de vacina excluídos: " + excluidos);
        }

        String sqlEstoque = "DELETE FROM estoque WHERE idproduto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlEstoque)) {
            stmt.setInt(1, id);
            int excluidos = stmt.executeUpdate();
            System.out.println("Registros de estoque excluídos: " + excluidos);
        }

        String sqlProduto = "DELETE FROM produto WHERE idproduto = ?";
        int produtoExcluido = 0;
        try (PreparedStatement stmt = conn.prepareStatement(sqlProduto)) {
            stmt.setInt(1, id);
            produtoExcluido = stmt.executeUpdate();
            System.out.println("Produto excluído: " + (produtoExcluido > 0 ? "Sim" : "Não"));

            if (produtoExcluido <= 0) {
                return false;
            }
        }
        return true;
    }


    public List<ProdutoCompletoDTO> getAllProdutos() {
        List<ProdutoCompletoDTO> list = new ArrayList<>();
        String sql = "SELECT p.*, t.descricao AS tipo_descricao, u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM produto p " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE p.ativo = true " +
                "ORDER BY p.nome";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(buildDTOFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ProdutoCompletoDTO> getProdutosByTipo(Integer idTipo) {
        List<ProdutoCompletoDTO> list = new ArrayList<>();

        String sql = "SELECT p.idproduto, p.nome, p.idtipoproduto, p.idunidademedida, " +
                "p.fabricante, p.preco, p.estoque_minimo, p.data_cadastro, " +
                "t.descricao AS tipo_descricao, " +
                "u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM produto p " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE p.idtipoproduto = ? " +
                "ORDER BY p.nome";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idTipo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProdutoModel produto = new ProdutoModel(
                        rs.getInt("idproduto"),
                        rs.getString("nome"),
                        rs.getInt("idtipoproduto"),
                        rs.getInt("idunidademedida"),
                        rs.getString("fabricante"),
                        rs.getBigDecimal("preco"),
                        rs.getInt("estoque_minimo"),
                        rs.getDate("data_cadastro")
                );

                TipoProdutoModel tipoProduto = new TipoProdutoModel(
                        rs.getInt("idtipoproduto"),
                        rs.getString("tipo_descricao")
                );

                UnidadeMedidaModel unidadeMedida = new UnidadeMedidaModel(
                        rs.getInt("idunidademedida"),
                        rs.getString("unidade_descricao"),
                        rs.getString("unidade_sigla")
                );

                ProdutoCompletoDTO dto = new ProdutoCompletoDTO(produto, tipoProduto, unidadeMedida);
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ProdutoCompletoDTO> getByName(String searchTerm) {
        List<ProdutoCompletoDTO> list = new ArrayList<>();

        String sql = "SELECT p.idproduto, p.nome, p.idtipoproduto, p.idunidademedida, " +
                "p.fabricante, p.preco, p.estoque_minimo, p.data_cadastro, " +
                "t.descricao AS tipo_descricao, " +
                "u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM produto p " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE LOWER(p.nome) LIKE LOWER(?) " +
                "ORDER BY p.nome";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProdutoModel produto = new ProdutoModel(
                        rs.getInt("idproduto"),
                        rs.getString("nome"),
                        rs.getInt("idtipoproduto"),
                        rs.getInt("idunidademedida"),
                        rs.getString("fabricante"),
                        rs.getBigDecimal("preco"),
                        rs.getInt("estoque_minimo"),
                        rs.getDate("data_cadastro")
                );

                TipoProdutoModel tipoProduto = new TipoProdutoModel(
                        rs.getInt("idtipoproduto"),
                        rs.getString("tipo_descricao")
                );

                UnidadeMedidaModel unidadeMedida = new UnidadeMedidaModel(
                        rs.getInt("idunidademedida"),
                        rs.getString("unidade_descricao"),
                        rs.getString("unidade_sigla")
                );

                ProdutoCompletoDTO dto = new ProdutoCompletoDTO(produto, tipoProduto, unidadeMedida);
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ProdutoCompletoDTO> getByTipoDescricao(String filtro) {
        List<ProdutoCompletoDTO> list = new ArrayList<>();

        String sql = "SELECT p.idproduto, p.nome, p.idtipoproduto, p.idunidademedida, " +
                "p.fabricante, p.preco, p.estoque_minimo, p.data_cadastro, " +
                "t.descricao AS tipo_descricao, " +
                "u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM produto p " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE UPPER(t.descricao) LIKE UPPER(?) " +
                "ORDER BY p.nome";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProdutoModel produto = new ProdutoModel(
                        rs.getInt("idproduto"),
                        rs.getString("nome"),
                        rs.getInt("idtipoproduto"),
                        rs.getInt("idunidademedida"),
                        rs.getString("fabricante"),
                        rs.getBigDecimal("preco"),
                        rs.getInt("estoque_minimo"),
                        rs.getDate("data_cadastro")
                );

                TipoProdutoModel tipoProduto = new TipoProdutoModel(
                        rs.getInt("idtipoproduto"),
                        rs.getString("tipo_descricao")
                );

                UnidadeMedidaModel unidadeMedida = new UnidadeMedidaModel(
                        rs.getInt("idunidademedida"),
                        rs.getString("unidade_descricao"),
                        rs.getString("unidade_sigla")
                );

                ProdutoCompletoDTO dto = new ProdutoCompletoDTO(produto, tipoProduto, unidadeMedida);
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ProdutoCompletoDTO> getByFabricante(String filtro) {
        List<ProdutoCompletoDTO> list = new ArrayList<>();

        String sql = "SELECT p.idproduto, p.nome, p.idtipoproduto, p.idunidademedida, " +
                "p.fabricante, p.preco, p.estoque_minimo, p.data_cadastro, " +
                "t.descricao AS tipo_descricao, " +
                "u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM produto p " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE UPPER(p.fabricante) LIKE UPPER(?) " +
                "ORDER BY p.nome";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProdutoModel produto = new ProdutoModel(
                        rs.getInt("idproduto"),
                        rs.getString("nome"),
                        rs.getInt("idtipoproduto"),
                        rs.getInt("idunidademedida"),
                        rs.getString("fabricante"),
                        rs.getBigDecimal("preco"),
                        rs.getInt("estoque_minimo"),
                        rs.getDate("data_cadastro")
                );

                TipoProdutoModel tipoProduto = new TipoProdutoModel(
                        rs.getInt("idtipoproduto"),
                        rs.getString("tipo_descricao")
                );

                UnidadeMedidaModel unidadeMedida = new UnidadeMedidaModel(
                        rs.getInt("idunidademedida"),
                        rs.getString("unidade_descricao"),
                        rs.getString("unidade_sigla")
                );

                ProdutoCompletoDTO dto = new ProdutoCompletoDTO(produto, tipoProduto, unidadeMedida);
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    public boolean produtoPodeSerExcluido(Integer id) throws SQLException {
        String sqlMedicamento = "SELECT COUNT(*) FROM medicamento WHERE idproduto = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlMedicamento)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                String sqlMedicacao = "SELECT COUNT(*) FROM medicacao WHERE posologia_medicamento_idproduto = ?";
                try (PreparedStatement stmt2 = SingletonDB.getConexao().getPreparedStatement(sqlMedicacao)) {
                    stmt2.setInt(1, id);
                    ResultSet rs2 = stmt2.executeQuery();
                    if (rs2.next() && rs2.getInt(1) > 0) {
                        System.out.println("Produto é medicamento e possui " + rs2.getInt(1) + " medicações associadas");
                        return false;
                    }
                }
            }
        }

        String sqlVacina = "SELECT COUNT(*) FROM vacina WHERE idproduto = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlVacina)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                String sqlVacinacao = "SELECT COUNT(*) FROM vacinacao WHERE idvacina = ?";
                try (PreparedStatement stmt2 = SingletonDB.getConexao().getPreparedStatement(sqlVacinacao)) {
                    stmt2.setInt(1, id);
                    ResultSet rs2 = stmt2.executeQuery();
                    if (rs2.next() && rs2.getInt(1) > 0) {
                        System.out.println("Produto é vacina e possui " + rs2.getInt(1) + " vacinações associadas");
                        return false;
                    }
                }

                String sqlAgenda = "SELECT COUNT(*) FROM agendavacinacao WHERE vacina_idproduto = ?";
                try (PreparedStatement stmt2 = SingletonDB.getConexao().getPreparedStatement(sqlAgenda)) {
                    stmt2.setInt(1, id);
                    ResultSet rs2 = stmt2.executeQuery();
                    if (rs2.next() && rs2.getInt(1) > 0) {
                        System.out.println("Produto é vacina e possui " + rs2.getInt(1) + " agendamentos associados");
                        return false;
                    }
                }
            }
        }

        String sqlMovimentacao = "SELECT COUNT(*) FROM itemmovimentacao WHERE produto_id = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlMovimentacao)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Produto possui " + rs.getInt(1) + " itens de movimentação associados");
                return false;
            }
        }

        String sqlAcerto = "SELECT COUNT(*) FROM itemacertoestoque WHERE produto_id = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlAcerto)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Produto possui " + rs.getInt(1) + " itens de acerto de estoque associados");
                return false;
            }
        }

        return true;
    }

    public boolean desativarProduto(Integer id) {
        try {

            String sql = "UPDATE produto SET ativo = false WHERE idproduto = ?";
            try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
                stmt.setInt(1, id);
                int linhasAfetadas = stmt.executeUpdate();

                if (linhasAfetadas > 0) {
                    System.out.println("Produto ID " + id + " desativado com sucesso");
                    return true;
                } else {
                    System.out.println("Produto ID " + id + " não encontrado");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao desativar produto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean reativarProduto(Integer id) {
        try {
            String sql = "UPDATE produto SET ativo = true WHERE idproduto = ?";
            try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
                stmt.setInt(1, id);
                int linhasAfetadas = stmt.executeUpdate();

                if (linhasAfetadas > 0) {
                    System.out.println("Produto ID " + id + " reativado com sucesso");
                    return true;
                } else {
                    System.out.println("Produto ID " + id + " não encontrado");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao reativar produto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<ProdutoCompletoDTO> getAllInactiveProdutos() {
        List<ProdutoCompletoDTO> list = new ArrayList<>();
        String sql = "SELECT p.*, t.descricao AS tipo_descricao, u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM produto p " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE p.ativo = false " + // A principal mudança está aqui
                "ORDER BY p.nome";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(buildDTOFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

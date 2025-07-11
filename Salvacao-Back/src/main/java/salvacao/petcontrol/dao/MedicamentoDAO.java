package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.dto.MedicamentoCompletoDTO;
import salvacao.petcontrol.model.MedicamentoModel;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.model.UnidadeMedidaModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MedicamentoDAO {

    private MedicamentoCompletoDTO buildDTOFromResultSet(ResultSet rs) throws SQLException {
        ProdutoModel produto = new ProdutoModel();
        produto.setIdproduto(rs.getInt("idproduto"));
        produto.setNome(rs.getString("nome"));
        produto.setIdtipoproduto(rs.getInt("idtipoproduto"));
        produto.setIdunidademedida(rs.getInt("idunidademedida"));
        produto.setFabricante(rs.getString("fabricante"));
        produto.setPreco(rs.getBigDecimal("preco"));
        produto.setEstoqueMinimo(rs.getInt("estoque_minimo"));
        produto.setDataCadastro(rs.getDate("data_cadastro"));
        produto.setAtivo(rs.getBoolean("ativo"));

        MedicamentoModel medicamento = new MedicamentoModel(
                rs.getInt("idproduto"),
                rs.getString("composicao")
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

        return new MedicamentoCompletoDTO(produto, medicamento, tipoProduto, unidadeMedida);
    }

    public MedicamentoModel getId(Integer id) {
        MedicamentoModel medicamento = null;
        String sql = "SELECT * FROM medicamento WHERE idproduto = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                medicamento = new MedicamentoModel(
                        rs.getInt("idproduto"),
                        rs.getString("composicao")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicamento;
    }

    public MedicamentoCompletoDTO findMedicamentoCompleto(Integer id) {
        MedicamentoCompletoDTO dto = null;
        String sql = "SELECT m.idproduto, m.composicao, " +
                "p.nome, p.idtipoproduto, p.idunidademedida, p.fabricante, p.preco, p.estoque_minimo, p.data_cadastro, p.ativo, " + // Adicionada vírgula após p.ativo
                "t.descricao AS tipo_descricao, " +
                "u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM medicamento m " +
                "JOIN produto p ON m.idproduto = p.idproduto " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE m.idproduto = ? AND p.ativo = true";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                dto = buildDTOFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dto;
    }

    public boolean alterar(Integer id, MedicamentoModel medicamento, ProdutoModel produtoModel, Connection conn) throws SQLException {
        boolean produtoAtualizado = produtoModel.getProdDAO().alterar(id, produtoModel);

        if (!produtoAtualizado) {
            throw new SQLException("Não foi possível atualizar o produto associado ao medicamento.");
        }

        String sql = "UPDATE medicamento SET composicao = ? WHERE idproduto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicamento.getComposicao());
            stmt.setInt(2, id);

            boolean result = stmt.executeUpdate() > 0;
            if (!result) {
                throw new SQLException("Falha ao atualizar medicamento");
            }
            return result;
        } catch (SQLException e) {
            throw e;
        }
    }

    public boolean apagar(Integer id, ProdutoModel produtoModel, Connection conn) throws SQLException {
        if (!medicamentoPodeSerExcluido(id)) {
            throw new SQLException("Este medicamento não pode ser excluído porque está sendo utilizado em medicações ou movimentações de estoque. Use a função de desativação em vez de exclusão.");
        }

        String sqlPosologia = "DELETE FROM posologia WHERE medicamento_idproduto = ?";
        int posologiasExcluidas = 0;
        try (PreparedStatement stmt = conn.prepareStatement(sqlPosologia)) {
            stmt.setInt(1, id);
            posologiasExcluidas = stmt.executeUpdate();
            System.out.println("Posologias excluídas: " + posologiasExcluidas);
        }

        String sqlMedicamento = "DELETE FROM medicamento WHERE idproduto = ?";
        int medicamentoExcluido = 0;
        try (PreparedStatement stmt = conn.prepareStatement(sqlMedicamento)) {
            stmt.setInt(1, id);
            medicamentoExcluido = stmt.executeUpdate();
            System.out.println("Medicamento excluído: " + (medicamentoExcluido > 0 ? "Sim" : "Não"));
            if (medicamentoExcluido <= 0) {
                throw new SQLException("Medicamento não encontrado para exclusão.");
            }
        }

        boolean produtoDeletado = produtoModel.getProdDAO().apagar(id, conn);
        if (!produtoDeletado) {
            throw new SQLException("Falha ao excluir o produto associado ao medicamento.");
        }

        return true;
    }

    public List<MedicamentoCompletoDTO> getAllInactiveMedicamentos() {
        List<MedicamentoCompletoDTO> list = new ArrayList<>();
        String sql = "SELECT " +
                "  p.idproduto, p.nome, p.idtipoproduto, p.idunidademedida, p.fabricante, p.preco, p.estoque_minimo, p.data_cadastro, p.ativo, " +
                "  m.composicao, " +
                "  t.descricao AS tipo_descricao, " +
                "  u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM medicamento m " +
                "JOIN produto p ON m.idproduto = p.idproduto " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE p.ativo = false " +
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


    public boolean desativarMedicamento(Integer id) {
        try {
            String sql = "UPDATE produto SET ativo = false WHERE idproduto = ?";
            try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
                stmt.setInt(1, id);
                int linhasAfetadas = stmt.executeUpdate();

                if (linhasAfetadas > 0) {
                    System.out.println("Medicamento ID " + id + " desativado com sucesso");
                    return true;
                } else {
                    System.out.println("Medicamento ID " + id + " não encontrado");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao desativar medicamento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean reativarMedicamento(Integer id) {
        try {
            String sql = "UPDATE produto SET ativo = true WHERE idproduto = ?";
            try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
                stmt.setInt(1, id);
                int linhasAfetadas = stmt.executeUpdate();

                if (linhasAfetadas > 0) {
                    System.out.println("Medicamento ID " + id + " reativado com sucesso");
                    return true;
                } else {
                    System.out.println("Medicamento ID " + id + " não encontrado");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao reativar medicamento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<MedicamentoCompletoDTO> getAllMedicamentos() {
        List<MedicamentoCompletoDTO> list = new ArrayList<>();
        // SQL CORRIGIDO: Removida a cláusula WHERE que estava faltando
        String sql = "SELECT " +
                "  p.idproduto, p.nome, p.idtipoproduto, p.idunidademedida, p.fabricante, p.preco, p.estoque_minimo, p.data_cadastro, p.ativo, " +
                "  m.composicao, " +
                "  t.descricao AS tipo_descricao, " +
                "  u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM medicamento m " +
                "JOIN produto p ON m.idproduto = p.idproduto " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE p.ativo = true " + // FILTRO ADICIONADO AQUI
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

    public boolean medicamentoPodeSerExcluido(Integer id) throws SQLException {
        String sqlMedicacao = "SELECT COUNT(*) FROM medicacao WHERE posologia_medicamento_idproduto = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlMedicacao)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Medicamento possui " + rs.getInt(1) + " medicações associadas");
                return false;
            }
        }

        String sqlPosologia = "SELECT COUNT(*) FROM posologia WHERE medicamento_idproduto = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlPosologia)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Medicamento possui " + rs.getInt(1) + " posologias associadas");
            }
        }

        String sqlMovimentacao = "SELECT COUNT(*) FROM itemmovimentacao WHERE produto_id = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlMovimentacao)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Medicamento possui " + rs.getInt(1) + " itens de movimentação associados");
                return false;
            }
        }
        String sqlAcerto = "SELECT COUNT(*) FROM itemacertoestoque WHERE produto_id = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlAcerto)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Medicamento possui " + rs.getInt(1) + " itens de acerto de estoque associados");
                return false;
            }
        }

        return true;
    }

    public List<MedicamentoCompletoDTO> getNome(String filtro) {
        List<MedicamentoCompletoDTO> list = new ArrayList<>();

        String sql = "SELECT m.idproduto, m.composicao, " +
                "p.nome, p.idtipoproduto, p.idunidademedida, p.fabricante, " +
                "t.descricao AS tipo_descricao, " +
                "u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM medicamento m " +
                "JOIN produto p ON m.idproduto = p.idproduto " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE UPPER(p.nome) LIKE UPPER(?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProdutoModel produto = new ProdutoModel(
                        rs.getInt("idproduto"),
                        rs.getString("nome"),
                        rs.getInt("idtipoproduto"),
                        rs.getInt("idunidademedida"),
                        rs.getString("fabricante")
                );

                MedicamentoModel medicamento = new MedicamentoModel(
                        rs.getInt("idproduto"),
                        rs.getString("composicao")
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

                MedicamentoCompletoDTO dto = new MedicamentoCompletoDTO(produto, medicamento, tipoProduto, unidadeMedida);
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<MedicamentoCompletoDTO> getComposicao(String filtro) {
        List<MedicamentoCompletoDTO> list = new ArrayList<>();

        String sql = "SELECT m.idproduto, m.composicao, " +
                "p.nome, p.idtipoproduto, p.idunidademedida, p.fabricante, " +
                "t.descricao AS tipo_descricao, " +
                "u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM medicamento m " +
                "JOIN produto p ON m.idproduto = p.idproduto " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE UPPER(m.composicao) LIKE UPPER(?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProdutoModel produto = new ProdutoModel(
                        rs.getInt("idproduto"),
                        rs.getString("nome"),
                        rs.getInt("idtipoproduto"),
                        rs.getInt("idunidademedida"),
                        rs.getString("fabricante")
                );

                MedicamentoModel medicamento = new MedicamentoModel(
                        rs.getInt("idproduto"),
                        rs.getString("composicao")
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

                MedicamentoCompletoDTO dto = new MedicamentoCompletoDTO(produto, medicamento, tipoProduto, unidadeMedida);
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<MedicamentoCompletoDTO> getTipo(String filtro) {
        List<MedicamentoCompletoDTO> list = new ArrayList<>();

        String sql = "SELECT m.idproduto, m.composicao, " +
                "p.nome, p.idtipoproduto, p.idunidademedida, p.fabricante, " +
                "t.descricao AS tipo_descricao, " +
                "u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM medicamento m " +
                "JOIN produto p ON m.idproduto = p.idproduto " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE UPPER(t.descricao) LIKE UPPER(?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProdutoModel produto = new ProdutoModel(
                        rs.getInt("idproduto"),
                        rs.getString("nome"),
                        rs.getInt("idtipoproduto"),
                        rs.getInt("idunidademedida"),
                        rs.getString("fabricante")
                );

                MedicamentoModel medicamento = new MedicamentoModel(
                        rs.getInt("idproduto"),
                        rs.getString("composicao")
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

                MedicamentoCompletoDTO dto = new MedicamentoCompletoDTO(produto, medicamento, tipoProduto, unidadeMedida);
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<MedicamentoCompletoDTO> buscarTodosDisponiveis() {
        List<MedicamentoCompletoDTO> lista = new ArrayList<>();
        String sql = "SELECT p.idproduto, p.nome, p.fabricante, p.preco, p.estoque_minimo, p.data_cadastro, p.ativo, " +
                "m.composicao, " +
                "e.quantidade, " +
                "t.idtipoproduto, t.descricao AS tipo_descricao, " +
                "u.idunidademedida, u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM produto p " +
                "JOIN medicamento m ON p.idproduto = m.idproduto " +
                "JOIN estoque e ON p.idproduto = e.idproduto " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE p.ativo = true AND e.quantidade > 0 " +
                "ORDER BY p.nome";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MedicamentoCompletoDTO dto = buildDTOFromResultSet(rs);
                dto.setQuantidade(rs.getInt("quantidade"));
                lista.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public MedicamentoModel gravarSomenteRelacionamento(MedicamentoModel medicamento, Connection conn) throws SQLException {
        String sql = "INSERT INTO medicamento (idproduto, composicao) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, medicamento.getIdproduto());
            stmt.setString(2, medicamento.getComposicao());

            if (stmt.executeUpdate() > 0) {
                return medicamento;
            } else {
                throw new SQLException("Falha ao inserir medicamento");
            }
        }
    }
}
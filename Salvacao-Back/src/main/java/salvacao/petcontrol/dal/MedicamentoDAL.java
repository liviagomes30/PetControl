package salvacao.petcontrol.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.dto.MedicamentoCompletoDTO;
import salvacao.petcontrol.model.MedicamentoModel;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.model.UnidadeMedidaModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MedicamentoDAL {

    @Autowired
    private ProdutoDAL produtoDAL;

    @Autowired
    private TipoProdutoDAL tipoProdutoDAL;

    @Autowired
    private UnidadeMedidaDAL unidadeMedidaDAL;

    public MedicamentoModel findById(Integer id) {
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
                "p.nome, p.idtipoproduto, p.idunidademedida, p.fabricante, " +
                "t.descricao AS tipo_descricao, " +
                "u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM medicamento m " +
                "JOIN produto p ON m.idproduto = p.idproduto " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida " +
                "WHERE m.idproduto = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
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

                dto = new MedicamentoCompletoDTO(produto, medicamento, tipoProduto, unidadeMedida);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dto;
    }

    public MedicamentoModel addMedicamento(MedicamentoModel medicamento, ProdutoModel produto) {
        try {
            SingletonDB.getConexao().getConnection().setAutoCommit(false);

            ProdutoModel novoProduto = produtoDAL.addProduto(produto);

            String sql = "INSERT INTO medicamento (idproduto, composicao) VALUES (?, ?)";
            try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
                stmt.setInt(1, novoProduto.getIdproduto());
                stmt.setString(2, medicamento.getComposicao());

                if (stmt.executeUpdate() > 0) {
                    medicamento.setIdproduto(novoProduto.getIdproduto());
                    SingletonDB.getConexao().getConnection().commit();
                } else {
                    SingletonDB.getConexao().getConnection().rollback();
                    throw new RuntimeException("Falha ao inserir medicamento");
                }
            }

            SingletonDB.getConexao().getConnection().setAutoCommit(true);

        } catch (SQLException e) {
            try {
                SingletonDB.getConexao().getConnection().rollback();
                SingletonDB.getConexao().getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Erro ao adicionar medicamento: " + e.getMessage(), e);
        }

        return medicamento;
    }

    public boolean updateMedicamento(Integer id, MedicamentoModel medicamento, ProdutoModel produto) {
        try {
            SingletonDB.getConexao().getConnection().setAutoCommit(false);

            boolean produtoAtualizado = produtoDAL.updateProduto(id, produto);

            if (!produtoAtualizado) {
                SingletonDB.getConexao().getConnection().rollback();
                SingletonDB.getConexao().getConnection().setAutoCommit(true);
                return false;
            }

            String sql = "UPDATE medicamento SET composicao = ? WHERE idproduto = ?";
            try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
                stmt.setString(1, medicamento.getComposicao());
                stmt.setInt(2, id);

                boolean result = stmt.executeUpdate() > 0;

                if (result) {
                    SingletonDB.getConexao().getConnection().commit();
                } else {
                    SingletonDB.getConexao().getConnection().rollback();
                }

                SingletonDB.getConexao().getConnection().setAutoCommit(true);

                return result;
            }

        } catch (SQLException e) {
            try {
                SingletonDB.getConexao().getConnection().rollback();
                SingletonDB.getConexao().getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteMedicamento(Integer id) {
        try {
            if (!medicamentoPodeSerExcluido(id)) {
                throw new RuntimeException("Este medicamento não pode ser excluído porque está sendo utilizado em medicações ou movimentações de estoque. Use a função de desativação em vez de exclusão.");
            }

            boolean autoCommitOriginal = SingletonDB.getConexao().getConnection().getAutoCommit();

            try {
                SingletonDB.getConexao().getConnection().setAutoCommit(false);

                String sqlPosologia = "DELETE FROM posologia WHERE medicamento_idproduto = ?";
                int posologiasExcluidas = 0;
                try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlPosologia)) {
                    stmt.setInt(1, id);
                    posologiasExcluidas = stmt.executeUpdate();
                    System.out.println("Posologias excluídas: " + posologiasExcluidas);
                }

                String sqlEstoque = "DELETE FROM estoque WHERE idproduto = ?";
                int estoquesExcluidos = 0;
                try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlEstoque)) {
                    stmt.setInt(1, id);
                    estoquesExcluidos = stmt.executeUpdate();
                    System.out.println("Registros de estoque excluídos: " + estoquesExcluidos);
                }

                String sqlMedicamento = "DELETE FROM medicamento WHERE idproduto = ?";
                int medicamentoExcluido = 0;
                try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlMedicamento)) {
                    stmt.setInt(1, id);
                    medicamentoExcluido = stmt.executeUpdate();
                    System.out.println("Medicamento excluído: " + (medicamentoExcluido > 0 ? "Sim" : "Não"));

                    if (medicamentoExcluido <= 0) {
                        System.out.println("Medicamento não encontrado, realizando rollback");
                        SingletonDB.getConexao().getConnection().rollback();
                        return false;
                    }
                }

                boolean produtoDeletado = produtoDAL.deleteProduto(id);
                System.out.println("Produto excluído: " + (produtoDeletado ? "Sim" : "Não"));

                if (produtoDeletado) {
                    System.out.println("Exclusão concluída com sucesso, realizando commit");
                    SingletonDB.getConexao().getConnection().commit();
                } else {
                    System.out.println("Falha ao excluir produto, realizando rollback");
                    SingletonDB.getConexao().getConnection().rollback();
                }

                return produtoDeletado;

            } finally {
                try {
                    SingletonDB.getConexao().getConnection().setAutoCommit(autoCommitOriginal);
                    System.out.println("AutoCommit restaurado para: " + autoCommitOriginal);
                } catch (SQLException ex) {
                    System.out.println("Erro ao restaurar autoCommit: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

        } catch (SQLException e) {
            try {
                System.out.println("Erro durante exclusão: " + e.getMessage() + ", realizando rollback");
                if (!SingletonDB.getConexao().getConnection().getAutoCommit()) {
                    SingletonDB.getConexao().getConnection().rollback();
                    System.out.println("Rollback executado após erro");
                }

                SingletonDB.getConexao().getConnection().setAutoCommit(true);
                System.out.println("AutoCommit restaurado para true após erro");

            } catch (SQLException ex) {
                System.out.println("Erro durante rollback: " + ex.getMessage());
                ex.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao excluir medicamento: " + e.getMessage(), e);
        }
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

        String sql = "SELECT m.idproduto, m.composicao, " +
                "p.nome, p.idtipoproduto, p.idunidademedida, p.fabricante, p.preco, p.estoque_minimo, p.data_cadastro, " +
                "t.descricao AS tipo_descricao, " +
                "u.descricao AS unidade_descricao, u.sigla AS unidade_sigla " +
                "FROM medicamento m " +
                "JOIN produto p ON m.idproduto = p.idproduto " +
                "JOIN tipoproduto t ON p.idtipoproduto = t.idtipoproduto " +
                "JOIN unidadedemedida u ON p.idunidademedida = u.idunidademedida";

        try {
            ResultSet rs = SingletonDB.getConexao().consultar(sql);

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

    public boolean medicamentoPodeSerExcluido(Integer id) throws SQLException {
        String sqlMedicacao = "SELECT COUNT(*) FROM medicacao WHERE posologia_medicamento_idproduto = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlMedicacao)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Medicamento possui " + rs.getInt(1) + " medicações associadas");
                return false; // Não pode ser excluído, pois está em medicações
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
                return false; // Não pode ser excluído, pois está em movimentações
            }
        }
        String sqlAcerto = "SELECT COUNT(*) FROM itemacertoestoque WHERE produto_id = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlAcerto)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Medicamento possui " + rs.getInt(1) + " itens de acerto de estoque associados");
                return false; // Não pode ser excluído, pois está em acertos de estoque
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
}
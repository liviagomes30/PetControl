package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.MovimentacaoEstoqueModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MovimentacaoEstoqueDAO {

    public MovimentacaoEstoqueModel gravar(MovimentacaoEstoqueModel movimentacao) {
        String sql = "INSERT INTO movimentacaoestoque(tipomovimentacao, data, usuario_pessoa_id, obs, fornecedor) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, movimentacao.getTipomovimentacao());
            stmt.setDate(2, java.sql.Date.valueOf(movimentacao.getData()));
            stmt.setInt(3, movimentacao.getUsuarioPessoaId());

            if (movimentacao.getObs() != null) {
                stmt.setString(4, movimentacao.getObs());
            } else {
                stmt.setNull(4, java.sql.Types.VARCHAR);
            }

            if (movimentacao.getFornecedor() != null) {
                stmt.setString(5, movimentacao.getFornecedor());
            } else {
                stmt.setNull(5, java.sql.Types.VARCHAR);
            }

            int linhasMod = stmt.executeUpdate();
            if (linhasMod > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    movimentacao.setIdmovimentacao(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar movimentação de estoque: " + e.getMessage(), e);
        }

        return movimentacao;
    }

    public MovimentacaoEstoqueModel getId(Integer id) {
        String sql = "SELECT * FROM movimentacaoestoque WHERE idmovimentacao = ?";
        MovimentacaoEstoqueModel movimentacao = null;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                LocalDate data = resultset.getDate("data").toLocalDate();

                movimentacao = new MovimentacaoEstoqueModel(
                        resultset.getInt("idmovimentacao"),
                        resultset.getString("tipomovimentacao"),
                        data,
                        resultset.getInt("usuario_pessoa_id"),
                        resultset.getString("obs"),
                        resultset.getString("fornecedor")
                );
            } else {
                System.out.println("Nenhuma movimentação encontrada com ID: " + id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movimentacao;
    }

    public List<MovimentacaoEstoqueModel> getTipo(String tipo) {
        List<MovimentacaoEstoqueModel> movimentacaoList = new ArrayList<>();
        String sql = "SELECT * FROM movimentacaoestoque WHERE tipomovimentacao = ?";
        MovimentacaoEstoqueModel movimentacao = null;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, tipo);
            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                LocalDate data = resultset.getDate("data").toLocalDate();

                movimentacao = new MovimentacaoEstoqueModel(
                        resultset.getInt("idmovimentacao"),
                        resultset.getString("tipomovimentacao"),
                        data,
                        resultset.getInt("usuario_pessoa_id"),
                        resultset.getString("obs"),
                        resultset.getString("fornecedor")
                );
                movimentacaoList.add(movimentacao);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movimentacaoList;
    }

    public boolean apagar(Integer id){
        String sql = "DELETE FROM movimentacaoestoque WHERE idmovimentacao = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<MovimentacaoEstoqueModel> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim, String tipoMovimentacao) {
        List<MovimentacaoEstoqueModel> lista = new ArrayList<>();

        String sql = "SELECT * FROM movimentacaoestoque WHERE data BETWEEN ? AND ? AND tipomovimentacao = ? ORDER BY data";
        MovimentacaoEstoqueModel mov = null;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(dataInicio));
            stmt.setDate(2, java.sql.Date.valueOf(dataFim));
            stmt.setString(3, tipoMovimentacao);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mov = new MovimentacaoEstoqueModel(
                            rs.getInt("idmovimentacao"),
                            rs.getString("tipomovimentacao"),
                            rs.getDate("data").toLocalDate(),
                            rs.getInt("usuario_pessoa_id"),
                            rs.getString("obs"),
                            rs.getString("fornecedor")
                    );

                    lista.add(mov);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar movimentações por período e tipo: " + e.getMessage());
        }
        return lista;
    }




}

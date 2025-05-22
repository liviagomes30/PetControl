// salvacao.petcontrol.dal.HistoricoDAO.java
package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.HistoricoModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

@Repository
public class HistoricoDAO { // Renamed from HistoricoDAL

    public HistoricoModel gravar(HistoricoModel historico) {
        String sql = "INSERT INTO historico (descricao, data, animal_idanimal, vacinacao_idvacinacao, medicacao_idmedicacao) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, historico.getDescricao());

            if (historico.getData() != null) {
                stmt.setDate(2, java.sql.Date.valueOf(historico.getData()));
            } else {
                stmt.setNull(2, java.sql.Types.DATE);
            }

            if (historico.getAnimal_idanimal() != null) {
                stmt.setInt(3, historico.getAnimal_idanimal());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }

            if (historico.getVacinacao_idvacinacao() != null) {
                stmt.setInt(4, historico.getVacinacao_idvacinacao());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            if (historico.getMedicacao_idmedicacao() != null) {
                stmt.setInt(5, historico.getMedicacao_idmedicacao());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            int linhasMod = stmt.executeUpdate();
            if (linhasMod > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    historico.setIdhistorico(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar histórico: " + e.getMessage(), e);
        }
        return historico;
    }


    public boolean alterar(HistoricoModel historico) {
        String sql = "UPDATE historico SET descricao = ?, data = ?, animal_idanimal = ?, " +
                "vacinacao_idvacinacao = ?, medicacao_idmedicacao = ? WHERE idhistorico = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, historico.getDescricao());

            if (historico.getData() != null) {
                stmt.setDate(2, java.sql.Date.valueOf(historico.getData()));
            } else {
                stmt.setNull(2, java.sql.Types.DATE);
            }

            if (historico.getAnimal_idanimal() != null) {
                stmt.setInt(3, historico.getAnimal_idanimal());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }

            if (historico.getVacinacao_idvacinacao() != null) {
                stmt.setInt(4, historico.getVacinacao_idvacinacao());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            if (historico.getMedicacao_idmedicacao() != null) {
                stmt.setInt(5, historico.getMedicacao_idmedicacao());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            stmt.setInt(6, historico.getIdhistorico());

            int linhasMod = stmt.executeUpdate();

            if (linhasMod == 0) {
                throw new RuntimeException("Nenhum histórico foi atualizado.");
            }
            else
                return true;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar histórico: " + e.getMessage(), e);
        }
    }

    public boolean apagar(Integer id) throws SQLException { // Renamed from apagar(HistoricoModel historicoModel) and added ID parameter
        // Check for dependencies before deleting
        String sqlCheckVacinacao = "SELECT COUNT(*) FROM vacinacao WHERE idhistorico = ?";
        try (PreparedStatement stmtCheck = SingletonDB.getConexao().getPreparedStatement(sqlCheckVacinacao)) {
            stmtCheck.setInt(1, id);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Histórico não pode ser excluído pois está associado a vacinações.");
            }
        }

        String sqlCheckMedicacao = "SELECT COUNT(*) FROM medicacao WHERE idhistorico = ?";
        try (PreparedStatement stmtCheck = SingletonDB.getConexao().getPreparedStatement(sqlCheckMedicacao)) {
            stmtCheck.setInt(1, id);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Histórico não pode ser excluído pois está associado a medicações.");
            }
        }

        String sql = "DELETE FROM historico WHERE idhistorico = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public HistoricoModel getId(Integer id) {
        String sql = "SELECT * FROM historico WHERE idhistorico = ?";
        HistoricoModel historico = null;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                historico = new HistoricoModel(
                        resultset.getInt("idhistorico"),
                        resultset.getString("descricao"),
                        data,
                        resultset.getInt("animal_idanimal"),
                        resultset.getObject("vacinacao_idvacinacao") != null ? resultset.getInt("vacinacao_idvacinacao") : null,
                        resultset.getObject("medicacao_idmedicacao") != null ? resultset.getInt("medicacao_idmedicacao") : null
                );
            } else {
                System.out.println("Nenhum histórico encontrado com ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return historico;
    }

    public List<HistoricoModel> getByAnimal(Integer idAnimal) {
        String sql = "SELECT * FROM historico WHERE animal_idanimal = ? ORDER BY data DESC";
        List<HistoricoModel> historicoList = new ArrayList<>();

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                HistoricoModel historico = new HistoricoModel(
                        resultset.getInt("idhistorico"),
                        resultset.getString("descricao"),
                        data,
                        resultset.getInt("animal_idanimal"),
                        resultset.getObject("vacinacao_idvacinacao") != null ? resultset.getInt("vacinacao_idvacinacao") : null,
                        resultset.getObject("medicacao_idmedicacao") != null ? resultset.getInt("medicacao_idmedicacao") : null
                );
                historicoList.add(historico);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return historicoList;
    }

    public List<HistoricoModel> getByAnimalAndPeriodo(Integer idAnimal, LocalDate dataInicio, LocalDate dataFim) {
        String sql = "SELECT * FROM historico WHERE animal_idanimal = ? AND data BETWEEN ? AND ? ORDER BY data DESC";
        List<HistoricoModel> historicoList = new ArrayList<>();

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            stmt.setDate(2, java.sql.Date.valueOf(dataInicio));
            stmt.setDate(3, java.sql.Date.valueOf(dataFim));
            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                HistoricoModel historico = new HistoricoModel(
                        resultset.getInt("idhistorico"),
                        resultset.getString("descricao"),
                        data,
                        resultset.getInt("animal_idanimal"),
                        resultset.getObject("vacinacao_idvacinacao") != null ? resultset.getInt("vacinacao_idvacinacao") : null,
                        resultset.getObject("medicacao_idmedicacao") != null ? resultset.getInt("medicacao_idmedicacao") : null
                );
                historicoList.add(historico);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return historicoList;
    }


    public List<HistoricoModel> getByVacinacao(Integer idVacinacao) {
        String sql = "SELECT * FROM historico WHERE vacinacao_idvacinacao = ? ORDER BY data DESC";
        List<HistoricoModel> historicoList = new ArrayList<>();

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idVacinacao);
            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                HistoricoModel historico = new HistoricoModel(
                        resultset.getInt("idhistorico"),
                        resultset.getString("descricao"),
                        data,
                        resultset.getInt("animal_idanimal"),
                        resultset.getObject("vacinacao_idvacinacao") != null ? resultset.getInt("vacinacao_idvacinacao") : null,
                        resultset.getObject("medicacao_idmedicacao") != null ? resultset.getInt("medicacao_idmedicacao") : null
                );
                historicoList.add(historico);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return historicoList;
    }


    public List<HistoricoModel> getByMedicacao(Integer idMedicacao) {
        String sql = "SELECT * FROM historico WHERE medicacao_idmedicacao = ? ORDER BY data DESC";
        List<HistoricoModel> historicoList = new ArrayList<>();

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idMedicacao);
            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                HistoricoModel historico = new HistoricoModel(
                        resultset.getInt("idhistorico"),
                        resultset.getString("descricao"),
                        data,
                        resultset.getInt("animal_idanimal"),
                        resultset.getObject("vacinacao_idvacinacao") != null ? resultset.getInt("vacinacao_idvacinacao") : null,
                        resultset.getObject("medicacao_idmedicacao") != null ? resultset.getInt("medicacao_idmedicacao") : null
                );
                historicoList.add(historico);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return historicoList;
    }

    public List<HistoricoModel> getAll() {
        String sql = "SELECT * FROM historico ORDER BY data DESC";
        List<HistoricoModel> historicoList = new ArrayList<>();

        try {
            ResultSet resultset = SingletonDB.getConexao().consultar(sql);

            while (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                HistoricoModel historico = new HistoricoModel(
                        resultset.getInt("idhistorico"),
                        resultset.getString("descricao"),
                        data,
                        resultset.getInt("animal_idanimal"),
                        resultset.getObject("vacinacao_idvacinacao") != null ? resultset.getInt("vacinacao_idvacinacao") : null,
                        resultset.getObject("medicacao_idmedicacao") != null ? resultset.getInt("medicacao_idmedicacao") : null
                );
                historicoList.add(historico);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return historicoList;
    }


    public List<HistoricoModel> getByPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        String sql = "SELECT * FROM historico WHERE data BETWEEN ? AND ? ORDER BY data DESC";
        List<HistoricoModel> historicoList = new ArrayList<>();

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(dataInicio));
            stmt.setDate(2, java.sql.Date.valueOf(dataFim));
            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                HistoricoModel historico = new HistoricoModel(
                        resultset.getInt("idhistorico"),
                        resultset.getString("descricao"),
                        data,
                        resultset.getInt("animal_idanimal"),
                        resultset.getObject("vacinacao_idvacinacao") != null ? resultset.getInt("vacinacao_idvacinacao") : null,
                        resultset.getObject("medicacao_idmedicacao") != null ? resultset.getInt("medicacao_idmedicacao") : null
                );
                historicoList.add(historico);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return historicoList;
    }


    public boolean existeHistoricoPorAnimal(int idAnimal) {
        String sql = "SELECT * FROM historico WHERE animal_idanimal = ?";
        boolean flag = false;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }


    public List<HistoricoModel> getByDescricao(String filtro) {
        List<HistoricoModel> historicoList = new ArrayList<>();
        String sql = "SELECT * FROM historico WHERE descricao ILIKE ? ORDER BY data DESC";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                HistoricoModel historico = new HistoricoModel(
                        resultset.getInt("idhistorico"),
                        resultset.getString("descricao"),
                        data,
                        resultset.getInt("animal_idanimal"),
                        resultset.getObject("vacinacao_idvacinacao") != null ? resultset.getInt("vacinacao_idvacinacao") : null,
                        resultset.getObject("medicacao_idmedicacao") != null ? resultset.getInt("medicacao_idmedicacao") : null
                );
                historicoList.add(historico);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return historicoList;
    }
}
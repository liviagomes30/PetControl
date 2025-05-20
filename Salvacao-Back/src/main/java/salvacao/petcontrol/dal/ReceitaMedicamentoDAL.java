package salvacao.petcontrol.dal;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.ReceitaMedicamentoModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

@Repository
public class ReceitaMedicamentoDAL {

    public ReceitaMedicamentoModel gravar(ReceitaMedicamentoModel receitaMedicamento) {
        String sql = "INSERT INTO receitamedicamento (data, medico, clinica, animal_idanimal) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (receitaMedicamento.getData() != null) {
                stmt.setDate(1, java.sql.Date.valueOf(receitaMedicamento.getData()));
            } else {
                stmt.setNull(1, java.sql.Types.DATE);
            }

            stmt.setString(2, receitaMedicamento.getMedico());
            stmt.setString(3, receitaMedicamento.getClinica());

            if (receitaMedicamento.getAnimal_idanimal() != null) {
                stmt.setInt(4, receitaMedicamento.getAnimal_idanimal());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            int linhasMod = stmt.executeUpdate();
            if (linhasMod > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    receitaMedicamento.setIdreceita(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar receita médica: " + e.getMessage(), e);
        }
        return receitaMedicamento;
    }


    public boolean alterar(ReceitaMedicamentoModel receitaMedicamento) {
        String sql = "UPDATE receitamedicamento SET data = ?, medico = ?, clinica = ?, " +
                "animal_idanimal = ? WHERE idreceita = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            if (receitaMedicamento.getData() != null) {
                stmt.setDate(1, java.sql.Date.valueOf(receitaMedicamento.getData()));
            } else {
                stmt.setNull(1, java.sql.Types.DATE);
            }

            stmt.setString(2, receitaMedicamento.getMedico());
            stmt.setString(3, receitaMedicamento.getClinica());

            if (receitaMedicamento.getAnimal_idanimal() != null) {
                stmt.setInt(4, receitaMedicamento.getAnimal_idanimal());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            stmt.setInt(5, receitaMedicamento.getIdreceita());

            int linhasMod = stmt.executeUpdate();

            if (linhasMod == 0) {
                throw new RuntimeException("Nenhuma receita médica foi atualizada.");
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar receita médica: " + e.getMessage(), e);
        }
    }


    public boolean apagar(ReceitaMedicamentoModel receitaMedicamentoModel) {
        String sql = "DELETE FROM receitamedicamento WHERE idreceita = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, receitaMedicamentoModel.getIdreceita());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ReceitaMedicamentoModel getId(Integer id) {
        String sql = "SELECT * FROM receitamedicamento WHERE idreceita = ?";
        ReceitaMedicamentoModel receitaMedicamento = null;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                receitaMedicamento = new ReceitaMedicamentoModel(
                        resultset.getInt("idreceita"),
                        data,
                        resultset.getString("medico"),
                        resultset.getString("clinica"),
                        resultset.getInt("animal_idanimal")
                );
            } else {
                System.out.println("Nenhuma receita médica encontrada com ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return receitaMedicamento;
    }


    public List<ReceitaMedicamentoModel> getByAnimal(Integer idAnimal) {
        String sql = "SELECT * FROM receitamedicamento WHERE animal_idanimal = ? ORDER BY data DESC";
        List<ReceitaMedicamentoModel> receitaList = new ArrayList<>();

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                ReceitaMedicamentoModel receita = new ReceitaMedicamentoModel(
                        resultset.getInt("idreceita"),
                        data,
                        resultset.getString("medico"),
                        resultset.getString("clinica"),
                        resultset.getInt("animal_idanimal")
                );
                receitaList.add(receita);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return receitaList;
    }


    public List<ReceitaMedicamentoModel> getByAnimalAndPeriodo(Integer idAnimal, LocalDate dataInicio, LocalDate dataFim) {
        String sql = "SELECT * FROM receitamedicamento WHERE animal_idanimal = ? AND data BETWEEN ? AND ? ORDER BY data DESC";
        List<ReceitaMedicamentoModel> receitaList = new ArrayList<>();

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            stmt.setDate(2, java.sql.Date.valueOf(dataInicio));
            stmt.setDate(3, java.sql.Date.valueOf(dataFim));
            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                ReceitaMedicamentoModel receita = new ReceitaMedicamentoModel(
                        resultset.getInt("idreceita"),
                        data,
                        resultset.getString("medico"),
                        resultset.getString("clinica"),
                        resultset.getInt("animal_idanimal")
                );
                receitaList.add(receita);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return receitaList;
    }


    public List<ReceitaMedicamentoModel> getByMedico(String medico) {
        String sql = "SELECT * FROM receitamedicamento WHERE medico ILIKE ? ORDER BY data DESC";
        List<ReceitaMedicamentoModel> receitaList = new ArrayList<>();

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + medico + "%");
            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                ReceitaMedicamentoModel receita = new ReceitaMedicamentoModel(
                        resultset.getInt("idreceita"),
                        data,
                        resultset.getString("medico"),
                        resultset.getString("clinica"),
                        resultset.getInt("animal_idanimal")
                );
                receitaList.add(receita);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return receitaList;
    }

    public List<ReceitaMedicamentoModel> getByClinica(String clinica) {
        String sql = "SELECT * FROM receitamedicamento WHERE clinica ILIKE ? ORDER BY data DESC";
        List<ReceitaMedicamentoModel> receitaList = new ArrayList<>();

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + clinica + "%");
            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                ReceitaMedicamentoModel receita = new ReceitaMedicamentoModel(
                        resultset.getInt("idreceita"),
                        data,
                        resultset.getString("medico"),
                        resultset.getString("clinica"),
                        resultset.getInt("animal_idanimal")
                );
                receitaList.add(receita);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return receitaList;
    }


    public List<ReceitaMedicamentoModel> getAll() {
        String sql = "SELECT * FROM receitamedicamento ORDER BY data DESC";
        List<ReceitaMedicamentoModel> receitaList = new ArrayList<>();

        try {
            ResultSet resultset = SingletonDB.getConexao().consultar(sql);

            while (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                ReceitaMedicamentoModel receita = new ReceitaMedicamentoModel(
                        resultset.getInt("idreceita"),
                        data,
                        resultset.getString("medico"),
                        resultset.getString("clinica"),
                        resultset.getInt("animal_idanimal")
                );
                receitaList.add(receita);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return receitaList;
    }


    public List<ReceitaMedicamentoModel> getByPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        String sql = "SELECT * FROM receitamedicamento WHERE data BETWEEN ? AND ? ORDER BY data DESC";
        List<ReceitaMedicamentoModel> receitaList = new ArrayList<>();

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(dataInicio));
            stmt.setDate(2, java.sql.Date.valueOf(dataFim));
            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                ReceitaMedicamentoModel receita = new ReceitaMedicamentoModel(
                        resultset.getInt("idreceita"),
                        data,
                        resultset.getString("medico"),
                        resultset.getString("clinica"),
                        resultset.getInt("animal_idanimal")
                );
                receitaList.add(receita);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return receitaList;
    }


    public boolean existeReceitaPorAnimal(int idAnimal) {
        String sql = "SELECT * FROM receitamedicamento WHERE animal_idanimal = ?";
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

    public boolean existePosologiaPorReceita(int idReceita) {
        String sql = "SELECT * FROM posologia WHERE receitamedicamento_idreceita = ?";
        boolean flag = false;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idReceita);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public List<ReceitaMedicamentoModel> getByData(LocalDate data) {
        String sql = "SELECT * FROM receitamedicamento WHERE data = ?";
        List<ReceitaMedicamentoModel> receitaList = new ArrayList<>();

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(data));
            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                Date dataSQL = resultset.getDate("data");
                LocalDate dataReceita = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                ReceitaMedicamentoModel receita = new ReceitaMedicamentoModel(
                        resultset.getInt("idreceita"),
                        dataReceita,
                        resultset.getString("medico"),
                        resultset.getString("clinica"),
                        resultset.getInt("animal_idanimal")
                );
                receitaList.add(receita);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return receitaList;
    }
}
package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.ReceitaMedicamentoModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReceitaMedicamentoDAO {

    public ReceitaMedicamentoModel getId(Integer id) {
        ReceitaMedicamentoModel receita = null;
        String sql = "SELECT * FROM receitamedicamento WHERE idreceita = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Date dataSql = rs.getDate("data");
                LocalDate data = (dataSql != null) ? dataSql.toLocalDate() : null;

                receita = new ReceitaMedicamentoModel(
                        rs.getInt("idreceita"),
                        data,
                        rs.getString("medico"),
                        rs.getString("clinica"),
                        rs.getInt("animal_idanimal")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receita;
    }

    public ReceitaMedicamentoModel gravar(ReceitaMedicamentoModel receita, Connection conn) throws SQLException {
        String sql = "INSERT INTO receitamedicamento (data, medico, clinica, animal_idanimal) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (receita.getData() != null) {
                stmt.setDate(1, java.sql.Date.valueOf(receita.getData()));
            } else {
                stmt.setNull(1, java.sql.Types.DATE);
            }
            stmt.setString(2, receita.getMedico());
            stmt.setString(3, receita.getClinica());
            stmt.setInt(4, receita.getAnimal_idanimal());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    receita.setIdreceita(rs.getInt(1));
                }
            } else {
                throw new SQLException("Falha ao gravar receita de medicamento.");
            }
        } catch (SQLException e) {
            throw e;
        }
        return receita;
    }

    public boolean alterar(ReceitaMedicamentoModel receita, Connection conn) throws SQLException {
        String sql = "UPDATE receitamedicamento SET data = ?, medico = ?, clinica = ?, animal_idanimal = ? " +
                "WHERE idreceita = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (receita.getData() != null) {
                stmt.setDate(1, java.sql.Date.valueOf(receita.getData()));
            } else {
                stmt.setNull(1, java.sql.Types.DATE);
            }
            stmt.setString(2, receita.getMedico());
            stmt.setString(3, receita.getClinica());
            stmt.setInt(4, receita.getAnimal_idanimal());
            stmt.setInt(5, receita.getIdreceita());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    public boolean apagar(Integer id, Connection conn) throws SQLException {
        String sqlCheckPosologia = "SELECT COUNT(*) FROM posologia WHERE receitamedicamento_idreceita = ?";
        try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheckPosologia)) {
            stmtCheck.setInt(1, id);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Receita de medicamento não pode ser excluída pois está associada a posologias.");
            }
        }
        String sqlCheckMedicacao = "SELECT COUNT(*) FROM medicacao WHERE posologia_receitamedicamento_idreceita = ?";
        try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheckMedicacao)) {
            stmtCheck.setInt(1, id);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Receita de medicamento não pode ser excluída pois está associada a medicações.");
            }
        }

        String sql = "DELETE FROM receitamedicamento WHERE idreceita = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<ReceitaMedicamentoModel> getAll() {
        List<ReceitaMedicamentoModel> receitas = new ArrayList<>();
        String sql = "SELECT * FROM receitamedicamento ORDER BY data DESC";
        try (ResultSet rs = SingletonDB.getConexao().consultar(sql)) {
            while (rs.next()) {
                Date dataSql = rs.getDate("data");
                LocalDate data = (dataSql != null) ? dataSql.toLocalDate() : null;

                ReceitaMedicamentoModel receita = new ReceitaMedicamentoModel(
                        rs.getInt("idreceita"),
                        data,
                        rs.getString("medico"),
                        rs.getString("clinica"),
                        rs.getInt("animal_idanimal")
                );
                receitas.add(receita);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receitas;
    }

    public List<ReceitaMedicamentoModel> searchReceitas(String searchTerm) {
        List<ReceitaMedicamentoModel> receitas = new ArrayList<>();
        String sql = "SELECT * FROM receitamedicamento WHERE UPPER(medico) LIKE UPPER(?) OR UPPER(clinica) LIKE UPPER(?) ORDER BY data DESC";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Date dataSql = rs.getDate("data");
                LocalDate data = (dataSql != null) ? dataSql.toLocalDate() : null;

                ReceitaMedicamentoModel receita = new ReceitaMedicamentoModel(
                        rs.getInt("idreceita"),
                        data,
                        rs.getString("medico"),
                        rs.getString("clinica"),
                        rs.getInt("animal_idanimal")
                );
                receitas.add(receita);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receitas;
    }

    public List<ReceitaMedicamentoModel> getReceitasByAnimal(Integer animalId) {
        List<ReceitaMedicamentoModel> receitas = new ArrayList<>();
        String sql = "SELECT * FROM receitamedicamento WHERE animal_idanimal = ? AND status = 'ATIVA' ORDER BY data DESC";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, animalId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Date dataSql = rs.getDate("data");
                LocalDate data = (dataSql != null) ? dataSql.toLocalDate() : null;

                ReceitaMedicamentoModel receita = new ReceitaMedicamentoModel(
                        rs.getInt("idreceita"),
                        data,
                        rs.getString("medico"),
                        rs.getString("clinica"),
                        rs.getInt("animal_idanimal"),
                        rs.getString("status")
                );
                receitas.add(receita);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receitas;
    }

    public void inativarReceita(Integer idReceita, Connection conn) throws SQLException {
        String sql = "UPDATE receitamedicamento SET status = 'CONCLUIDA' WHERE idreceita = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idReceita);
            stmt.executeUpdate();
        }
    }

    public void reativarReceita(Integer idReceita, Connection conn) throws SQLException {
        String sql = "UPDATE receitamedicamento SET status = 'ATIVA' WHERE idreceita = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idReceita);
            stmt.executeUpdate();
        }
    }
}
package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.PosologiaModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PosologiaDAO {

    /**
     * Busca uma posologia pelo ID do medicamento e ID da receita.
     * @param medicamentoId O ID do medicamento.
     * @param receitaId O ID da receita de medicamento.
     * @return O objeto PosologiaModel correspondente, ou null se não encontrado.
     */
    public PosologiaModel getId(Integer medicamentoId, Integer receitaId) {
        PosologiaModel posologia = null;
        String sql = "SELECT * FROM posologia WHERE medicamento_idproduto = ? AND receitamedicamento_idreceita = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, medicamentoId);
            stmt.setInt(2, receitaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                posologia = new PosologiaModel(
                        rs.getString("dose"),
                        rs.getInt("quantidadedias"),
                        rs.getInt("intervalohoras"),
                        rs.getInt("medicamento_idproduto"),
                        rs.getInt("receitamedicamento_idreceita")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posologia;
    }

    /**
     * Grava uma nova posologia no banco de dados.
     * @param posologia O objeto PosologiaModel a ser gravado.
     * @param conn A conexão JDBC a ser usada para a transação.
     * @return O objeto PosologiaModel gravado.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public PosologiaModel gravar(PosologiaModel posologia, Connection conn) throws SQLException {
        String sql = "INSERT INTO posologia (dose, quantidadedias, intervalohoras, medicamento_idproduto, receitamedicamento_idreceita) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, posologia.getDose());
            stmt.setInt(2, posologia.getQuantidadedias());
            stmt.setInt(3, posologia.getIntervalohoras());
            stmt.setInt(4, posologia.getMedicamento_idproduto());
            stmt.setInt(5, posologia.getReceitamedicamento_idreceita());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Falha ao gravar posologia, nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            throw e;
        }
        return posologia;
    }

    /**
     * Atualiza uma posologia existente no banco de dados.
     * @param posologia O objeto PosologiaModel com os dados a serem atualizados.
     * @param conn A conexão JDBC a ser usada para a transação.
     * @return true se a posologia foi atualizada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public boolean alterar(PosologiaModel posologia, Connection conn) throws SQLException {
        String sql = "UPDATE posologia SET dose = ?, quantidadedias = ?, intervalohoras = ? " +
                "WHERE medicamento_idproduto = ? AND receitamedicamento_idreceita = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, posologia.getDose());
            stmt.setInt(2, posologia.getQuantidadedias());
            stmt.setInt(3, posologia.getIntervalohoras());
            stmt.setInt(4, posologia.getMedicamento_idproduto());
            stmt.setInt(5, posologia.getReceitamedicamento_idreceita());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Exclui uma posologia do banco de dados.
     * @param medicamentoId O ID do medicamento associado à posologia.
     * @param receitaId O ID da receita associada à posologia.
     * @param conn A conexão JDBC a ser usada para a transação.
     * @return true se a posologia foi excluída com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de banco de dados (ex: violação de FK).
     */
    public boolean apagar(Integer medicamentoId, Integer receitaId, Connection conn) throws SQLException {
        String sqlCheckMedicacao = "SELECT COUNT(*) FROM medicacao WHERE posologia_medicamento_idproduto = ? AND posologia_receitamedicamento_idreceita = ?";
        try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheckMedicacao)) {
            stmtCheck.setInt(1, medicamentoId);
            stmtCheck.setInt(2, receitaId);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Posologia não pode ser excluída pois está associada a medicações existentes.");
            }
        }

        String sql = "DELETE FROM posologia WHERE medicamento_idproduto = ? AND receitamedicamento_idreceita = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, medicamentoId);
            stmt.setInt(2, receitaId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Lista todas as posologias.
     * @return Uma lista de objetos PosologiaModel.
     */
    public List<PosologiaModel> getAll() {
        List<PosologiaModel> posologias = new ArrayList<>();
        String sql = "SELECT * FROM posologia";
        try (ResultSet rs = SingletonDB.getConexao().consultar(sql)) {
            while (rs.next()) {
                PosologiaModel posologia = new PosologiaModel(
                        rs.getString("dose"),
                        rs.getInt("quantidadedias"),
                        rs.getInt("intervalohoras"),
                        rs.getInt("medicamento_idproduto"),
                        rs.getInt("receitamedicamento_idreceita")
                );
                posologias.add(posologia);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posologias;
    }

    /**
     * Lista posologias para um determinado medicamento.
     * @param medicamentoId O ID do medicamento.
     * @return Uma lista de objetos PosologiaModel.
     */
    public List<PosologiaModel> getByMedicamento(Integer medicamentoId) {
        List<PosologiaModel> posologias = new ArrayList<>();
        String sql = "SELECT * FROM posologia WHERE medicamento_idproduto = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, medicamentoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PosologiaModel posologia = new PosologiaModel(
                        rs.getString("dose"),
                        rs.getInt("quantidadedias"),
                        rs.getInt("intervalohoras"),
                        rs.getInt("medicamento_idproduto"),
                        rs.getInt("receitamedicamento_idreceita")
                );
                posologias.add(posologia);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posologias;
    }

    /**
     * Lista posologias para uma determinada receita.
     * @param receitaId O ID da receita.
     * @return Uma lista de objetos PosologiaModel.
     */
    public List<PosologiaModel> getByReceita(Integer receitaId) {
        List<PosologiaModel> posologias = new ArrayList<>();
        String sql = "SELECT * FROM posologia WHERE receitamedicamento_idreceita = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, receitaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PosologiaModel posologia = new PosologiaModel(
                        rs.getString("dose"),
                        rs.getInt("quantidadedias"),
                        rs.getInt("intervalohoras"),
                        rs.getInt("medicamento_idproduto"),
                        rs.getInt("receitamedicamento_idreceita")
                );
                posologias.add(posologia);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posologias;
    }
}
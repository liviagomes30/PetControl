package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.MedicacaoModel;

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
public class MedicacaoDAO {

    /**
     * Grava uma nova medicação no banco de dados.
     * @param medicacao O objeto MedicacaoModel a ser gravado.
     * @param conn A conexão JDBC a ser usada para a transação.
     * @return O objeto MedicacaoModel com o ID gerado pelo banco de dados.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public MedicacaoModel gravar(MedicacaoModel medicacao, Connection conn) throws SQLException {
        String sql = "INSERT INTO medicacao (idanimal, idhistorico, posologia_medicamento_idproduto, posologia_receitamedicamento_idreceita, data) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Use provided connection
            stmt.setInt(1, medicacao.getIdanimal());
            if (medicacao.getIdhistorico() != null) {
                stmt.setInt(2, medicacao.getIdhistorico());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setInt(3, medicacao.getPosologia_medicamento_idproduto());
            stmt.setInt(4, medicacao.getPosologia_receitamedicamento_idreceita());
            if (medicacao.getData() != null) {
                stmt.setDate(5, java.sql.Date.valueOf(medicacao.getData()));
            } else {
                stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
            }

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    medicacao.setIdmedicacao(rs.getInt(1));
                }
            } else {
                throw new SQLException("Falha ao registrar medicação.");
            }
        } catch (SQLException e) {
            throw e; // Re-throw for service to handle transaction
        }
        return medicacao;
    }

    /**
     * Busca uma medicação pelo seu ID.
     * @param id O ID da medicação.
     * @return O objeto MedicacaoModel correspondente, ou null se não encontrado.
     */
    public MedicacaoModel getId(Integer id) {
        MedicacaoModel medicacao = null;
        String sql = "SELECT * FROM medicacao WHERE idmedicacao = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Date dataSql = rs.getDate("data");
                LocalDate data = (dataSql != null) ? dataSql.toLocalDate() : null;

                medicacao = new MedicacaoModel(
                        rs.getInt("idmedicacao"),
                        rs.getInt("idanimal"),
                        rs.getObject("idhistorico") != null ? rs.getInt("idhistorico") : null,
                        rs.getInt("posologia_medicamento_idproduto"),
                        rs.getInt("posologia_receitamedicamento_idreceita"),
                        data
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicacao;
    }

    /**
     * Lista todas as medicações registradas no sistema.
     * @return Uma lista de objetos MedicacaoModel.
     */
    public List<MedicacaoModel> getAll() {
        List<MedicacaoModel> medicacoes = new ArrayList<>();
        String sql = "SELECT * FROM medicacao ORDER BY data DESC";
        try (ResultSet rs = SingletonDB.getConexao().consultar(sql)) {
            while (rs.next()) {
                Date dataSql = rs.getDate("data");
                LocalDate data = (dataSql != null) ? dataSql.toLocalDate() : null;

                MedicacaoModel medicacao = new MedicacaoModel(
                        rs.getInt("idmedicacao"),
                        rs.getInt("idanimal"),
                        rs.getObject("idhistorico") != null ? rs.getInt("idhistorico") : null,
                        rs.getInt("posologia_medicamento_idproduto"),
                        rs.getInt("posologia_receitamedicamento_idreceita"),
                        data
                );
                medicacoes.add(medicacao);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicacoes;
    }

    /**
     * Atualiza uma medicação existente no banco de dados.
     * @param medicacao O objeto MedicacaoModel com os dados a serem atualizados.
     * @param conn A conexão JDBC a ser usada para a transação.
     * @return true se a medicação foi atualizada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public boolean alterar(MedicacaoModel medicacao, Connection conn) throws SQLException {
        String sql = "UPDATE medicacao SET idanimal = ?, idhistorico = ?, posologia_medicamento_idproduto = ?, " +
                "posologia_receitamedicamento_idreceita = ?, data = ? WHERE idmedicacao = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, medicacao.getIdanimal());
            if (medicacao.getIdhistorico() != null) {
                stmt.setInt(2, medicacao.getIdhistorico());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setInt(3, medicacao.getPosologia_medicamento_idproduto());
            stmt.setInt(4, medicacao.getPosologia_receitamedicamento_idreceita());
            if (medicacao.getData() != null) {
                stmt.setDate(5, java.sql.Date.valueOf(medicacao.getData()));
            } else {
                stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
            }
            stmt.setInt(6, medicacao.getIdmedicacao());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Exclui uma medicação do banco de dados pelo seu ID.
     * Antes de excluir, verifica se a medicação está associada a algum histórico.
     * Se estiver associada a um histórico, lança uma SQLException.
     * @param id O ID da medicação a ser excluída.
     * @param conn A conexão JDBC a ser usada para a transação.
     * @return true se a medicação foi excluída com sucesso, false caso contrário.
     * @throws SQLException Se a medicação estiver associada a um histórico ou ocorrer um erro de banco de dados.
     */
    public boolean apagar(Integer id, Connection conn) throws SQLException {
        // Check if Medicacao is referenced by Historico (if Historico has idmedicacao as FK)
        String sqlCheckHistorico = "SELECT COUNT(*) FROM historico WHERE medicacao_idmedicacao = ?";
        try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheckHistorico)) {
            stmtCheck.setInt(1, id);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Medicação não pode ser excluída pois está associada a registros de histórico.");
            }
        }

        String sql = "DELETE FROM medicacao WHERE idmedicacao = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Lista medicações para um animal específico.
     * @param idAnimal O ID do animal.
     * @return Uma lista de objetos MedicacaoModel.
     */
    public List<MedicacaoModel> getMedicacoesByAnimal(Integer idAnimal) {
        List<MedicacaoModel> medicacoes = new ArrayList<>();
        String sql = "SELECT * FROM medicacao WHERE idanimal = ? ORDER BY data DESC";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Date dataSql = rs.getDate("data");
                LocalDate data = (dataSql != null) ? dataSql.toLocalDate() : null;

                MedicacaoModel medicacao = new MedicacaoModel(
                        rs.getInt("idmedicacao"),
                        rs.getInt("idanimal"),
                        rs.getObject("idhistorico") != null ? rs.getInt("idhistorico") : null,
                        rs.getInt("posologia_medicamento_idproduto"),
                        rs.getInt("posologia_receitamedicamento_idreceita"),
                        data
                );
                medicacoes.add(medicacao);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicacoes;
    }

    /**
     * Busca medicações com base em um termo de busca na composição do medicamento.
     * Esta é uma função de exemplo e pode ser expandida para outros campos se necessário.
     * @param searchTerm O termo de busca (parcial ou completo).
     * @return Uma lista de MedicacaoModel que correspondem ao termo.
     */
    public List<MedicacaoModel> searchMedicacoesByComposicao(String searchTerm) {
        List<MedicacaoModel> medicacoes = new ArrayList<>();
        String sql = "SELECT m.* FROM medicacao m " +
                "JOIN medicamento med ON m.posologia_medicamento_idproduto = med.idproduto " +
                "WHERE UPPER(med.composicao) LIKE UPPER(?) ORDER BY m.data DESC";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Date dataSql = rs.getDate("data");
                LocalDate data = (dataSql != null) ? dataSql.toLocalDate() : null;

                MedicacaoModel medicacao = new MedicacaoModel(
                        rs.getInt("idmedicacao"),
                        rs.getInt("idanimal"),
                        rs.getObject("idhistorico") != null ? rs.getInt("idhistorico") : null,
                        rs.getInt("posologia_medicamento_idproduto"),
                        rs.getInt("posologia_receitamedicamento_idreceita"),
                        data
                );
                medicacoes.add(medicacao);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicacoes;
    }

    /**
     * Verifica se existe alguma medicação associada a um determinado histórico.
     * @param idHistorico O ID do histórico.
     * @return true se existir alguma medicação associada, false caso contrário.
     */
    public boolean existeMedicacaoPorHistorico(Integer idHistorico) {
        String sql = "SELECT COUNT(*) FROM medicacao WHERE idhistorico = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idHistorico);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
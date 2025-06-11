package salvacao.petcontrol.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.dto.PosologiaDTO;
import salvacao.petcontrol.model.PosologiaModel;

public class PosologiaDAO {
    public boolean inserir(PosologiaModel posologia) {
        String sql = "INSERT INTO posologia (dose, quantidadedias, intervalohoras, medicamento_idproduto, receitamedicamento_idreceita) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, posologia.getDose());
            stmt.setInt(2, posologia.getQuantidadedias());
            stmt.setInt(3, posologia.getIntervalohoras());
            stmt.setInt(4, posologia.getMedicamento_idproduto());
            stmt.setInt(5, posologia.getReceitamedicamento_idreceita());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean alterar(PosologiaModel posologia, Integer oldMedicamentoId, Integer oldReceitaId) {
        String sql = "UPDATE posologia SET dose = ?, quantidadedias = ?, intervalohoras = ?, " +
                    "medicamento_idproduto = ?, receitamedicamento_idreceita = ? " +
                    "WHERE medicamento_idproduto = ? AND receitamedicamento_idreceita = ?";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, posologia.getDose());
            stmt.setInt(2, posologia.getQuantidadedias());
            stmt.setInt(3, posologia.getIntervalohoras());
            stmt.setInt(4, posologia.getMedicamento_idproduto());
            stmt.setInt(5, posologia.getReceitamedicamento_idreceita());
            stmt.setInt(6, oldMedicamentoId);
            stmt.setInt(7, oldReceitaId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean apagar(Integer medicamentoId, Integer receitaId) {
        String sql = "DELETE FROM posologia WHERE medicamento_idproduto = ? AND receitamedicamento_idreceita = ?";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, medicamentoId);
            stmt.setInt(2, receitaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean apagarPorReceita(Integer receitaId) {
        String sql = "DELETE FROM posologia WHERE receitamedicamento_idreceita = ?";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, receitaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public PosologiaDTO findPosologia(Integer medicamentoId, Integer receitaId) {
        String sql = "SELECT p.dose, p.quantidadedias, p.intervalohoras, p.medicamento_idproduto, " +
                    "p.receitamedicamento_idreceita, pr.nome as medicamento_nome, m.composicao " +
                    "FROM posologia p " +
                    "JOIN medicamento m ON p.medicamento_idproduto = m.idproduto " +
                    "JOIN produto pr ON m.idproduto = pr.idproduto " +
                    "WHERE p.medicamento_idproduto = ? AND p.receitamedicamento_idreceita = ?";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, medicamentoId);
            stmt.setInt(2, receitaId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                PosologiaDTO posologia = new PosologiaDTO();
                posologia.setDose(rs.getString("dose"));
                posologia.setQuantidadedias(rs.getInt("quantidadedias"));
                posologia.setIntervalohoras(rs.getInt("intervalohoras"));
                posologia.setMedicamento_idproduto(rs.getInt("medicamento_idproduto"));
                posologia.setReceitamedicamento_idreceita(rs.getInt("receitamedicamento_idreceita"));
                posologia.setMedicamentoNome(rs.getString("medicamento_nome"));
                posologia.setMedicamentoComposicao(rs.getString("composicao"));
                
                return posologia;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // public List<PosologiaDTO> listarPorReceita(Integer receitaId) {
    //     List<PosologiaDTO> lista = new ArrayList<>();
    //     String sql = "SELECT p.dose, p.quantidadedias, p.intervalohoras, p.medicamento_idproduto, " +
    //                 "p.receitamedicamento_idreceita, pr.nome as medicamento_nome, m.composicao " +
    //                 "FROM posologia p " +
    //                 "JOIN medicamento m ON p.medicamento_idproduto = m.idproduto " +
    //                 "JOIN produto pr ON m.idproduto = pr.idproduto " +
    //                 "WHERE p.receitamedicamento_idreceita = ? " +
    //                 "ORDER BY pr.nome";
        
    //     try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
    //         stmt.setInt(1, receitaId);
    //         ResultSet rs = stmt.executeQuery();
            
    //         while (rs.next()) {
    //             PosologiaDTO posologia = new PosologiaDTO();
    //             posologia.setDose(rs.getString("dose"));
    //             posologia.setQuantidadedias(rs.getInt("quantidadedias"));
    //             posologia.setIntervalohoras(rs.getInt("intervalohoras"));
    //             posologia.setMedicamento_idproduto(rs.getInt("medicamento_idproduto"));
    //             posologia.setReceitamedicamento_idreceita(rs.getInt("receitamedicamento_idreceita"));
    //             posologia.setMedicamentoNome(rs.getString("medicamento_nome"));
    //             posologia.setMedicamentoComposicao(rs.getString("composicao"));
                
    //             lista.add(posologia);
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    //     return lista;
    // }

    public List<PosologiaDTO> listarPorMedicamento(Integer medicamentoId) {
        List<PosologiaDTO> lista = new ArrayList<>();
        String sql = "SELECT p.dose, p.quantidadedias, p.intervalohoras, p.medicamento_idproduto, " +
                    "p.receitamedicamento_idreceita, pr.nome as medicamento_nome, m.composicao " +
                    "FROM posologia p " +
                    "JOIN medicamento m ON p.medicamento_idproduto = m.idproduto " +
                    "JOIN produto pr ON m.idproduto = pr.idproduto " +
                    "WHERE p.medicamento_idproduto = ? " +
                    "ORDER BY p.receitamedicamento_idreceita DESC";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, medicamentoId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                PosologiaDTO posologia = new PosologiaDTO();
                posologia.setDose(rs.getString("dose"));
                posologia.setQuantidadedias(rs.getInt("quantidadedias"));
                posologia.setIntervalohoras(rs.getInt("intervalohoras"));
                posologia.setMedicamento_idproduto(rs.getInt("medicamento_idproduto"));
                posologia.setReceitamedicamento_idreceita(rs.getInt("receitamedicamento_idreceita"));
                posologia.setMedicamentoNome(rs.getString("medicamento_nome"));
                posologia.setMedicamentoComposicao(rs.getString("composicao"));
                
                lista.add(posologia);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean posologiaPodeSerExcluida(Integer medicamentoId, Integer receitaId) {
        String sqlMedicacao = "SELECT COUNT(*) FROM medicacao " +
                             "WHERE posologia_medicamento_idproduto = ? " +
                             "AND posologia_receitamedicamento_idreceita = ?";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlMedicacao)) {
            stmt.setInt(1, medicamentoId);
            stmt.setInt(2, receitaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }


    private PosologiaModel buildPosologiaFromResultSet(ResultSet rs) throws SQLException {
        return new PosologiaModel(
                rs.getString("dose"),
                rs.getInt("quantidadedias"),
                rs.getInt("intervalohoras"),
                rs.getObject("frequencia_diaria") != null ? rs.getInt("frequencia_diaria") : null,
                rs.getInt("medicamento_idproduto"),
                rs.getInt("receitamedicamento_idreceita")
        );
    }

    public PosologiaModel getId(Integer medicamentoId, Integer receitaId) {
        PosologiaModel posologia = null;
        String sql = "SELECT * FROM posologia WHERE medicamento_idproduto = ? AND receitamedicamento_idreceita = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, medicamentoId);
            stmt.setInt(2, receitaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                posologia = buildPosologiaFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posologia;
    }

    public PosologiaModel gravar(PosologiaModel posologia, Connection conn) throws SQLException {
        String sql = "INSERT INTO posologia (dose, quantidadedias, intervalohoras, frequencia_diaria, medicamento_idproduto, receitamedicamento_idreceita) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, posologia.getDose());
            stmt.setInt(2, posologia.getQuantidadedias());
            stmt.setInt(3, posologia.getIntervalohoras());
            stmt.setObject(4, posologia.getFrequencia_diaria());
            stmt.setInt(5, posologia.getMedicamento_idproduto());
            stmt.setInt(6, posologia.getReceitamedicamento_idreceita());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Falha ao gravar posologia, nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            throw e;
        }
        return posologia;
    }

    public boolean alterar(PosologiaModel posologia, Connection conn) throws SQLException {
        String sql = "UPDATE posologia SET dose = ?, quantidadedias = ?, intervalohoras = ?, frequencia_diaria = ? " +
                "WHERE medicamento_idproduto = ? AND receitamedicamento_idreceita = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, posologia.getDose());
            stmt.setInt(2, posologia.getQuantidadedias());
            stmt.setInt(3, posologia.getIntervalohoras());
            stmt.setObject(4, posologia.getFrequencia_diaria());
            stmt.setInt(5, posologia.getMedicamento_idproduto());
            stmt.setInt(6, posologia.getReceitamedicamento_idreceita());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

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

    public List<PosologiaModel> getAll() {
        List<PosologiaModel> posologias = new ArrayList<>();
        String sql = "SELECT * FROM posologia";
        try (ResultSet rs = SingletonDB.getConexao().consultar(sql)) {
            while (rs.next()) {
                posologias.add(buildPosologiaFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posologias;
    }

    public List<PosologiaModel> getByMedicamento(Integer medicamentoId) {
        List<PosologiaModel> posologias = new ArrayList<>();
        String sql = "SELECT * FROM posologia WHERE medicamento_idproduto = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, medicamentoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posologias.add(buildPosologiaFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posologias;
    }

    public List<PosologiaModel> getByReceita(Integer receitaId) {
        List<PosologiaModel> posologias = new ArrayList<>();
        String sql = "SELECT * FROM posologia WHERE receitamedicamento_idreceita = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, receitaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posologias.add(buildPosologiaFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posologias;
    }

    public List<PosologiaDTO> listarPorReceita(Integer receitaId) {
        List<PosologiaDTO> lista = new ArrayList<>();

        String sql = "SELECT p.dose, p.quantidadedias, p.intervalohoras, p.frequencia_diaria, " +
                "p.medicamento_idproduto, p.receitamedicamento_idreceita, " +
                "pr.nome AS medicamento_nome, m.composicao " +
                "FROM posologia p " +
                "JOIN produto pr ON p.medicamento_idproduto = pr.idproduto " +
                "JOIN medicamento m ON pr.idproduto = m.idproduto " +
                "WHERE p.receitamedicamento_idreceita = ? AND pr.ativo = true " +
                "ORDER BY pr.nome";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, receitaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PosologiaDTO posologia = new PosologiaDTO();
                posologia.setDose(rs.getString("dose"));
                posologia.setQuantidadedias(rs.getInt("quantidadedias"));
                posologia.setIntervalohoras(rs.getInt("intervalohoras"));
                posologia.setFrequencia_diaria(rs.getObject("frequencia_diaria") != null ? rs.getInt("frequencia_diaria") : null);
                posologia.setMedicamento_idproduto(rs.getInt("medicamento_idproduto"));
                posologia.setReceitamedicamento_idreceita(receitaId);
                posologia.setMedicamentoNome(rs.getString("medicamento_nome"));
                posologia.setMedicamentoComposicao(rs.getString("composicao"));

                lista.add(posologia);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
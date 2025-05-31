package salvacao.petcontrol.dao;

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

    public List<PosologiaDTO> listarPorReceita(Integer receitaId) {
        List<PosologiaDTO> lista = new ArrayList<>();
        String sql = "SELECT p.dose, p.quantidadedias, p.intervalohoras, p.medicamento_idproduto, " +
                    "p.receitamedicamento_idreceita, pr.nome as medicamento_nome, m.composicao " +
                    "FROM posologia p " +
                    "JOIN medicamento m ON p.medicamento_idproduto = m.idproduto " +
                    "JOIN produto pr ON m.idproduto = pr.idproduto " +
                    "WHERE p.receitamedicamento_idreceita = ? " +
                    "ORDER BY pr.nome";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, receitaId);
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
} 
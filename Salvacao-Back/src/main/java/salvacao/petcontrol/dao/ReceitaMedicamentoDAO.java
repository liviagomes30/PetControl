package salvacao.petcontrol.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.dto.ReceitaMedicamentoDTO;
import salvacao.petcontrol.dto.PosologiaDTO;
import salvacao.petcontrol.model.ReceitaMedicamentoModel;

public class ReceitaMedicamentoDAO {

    public boolean inserir(ReceitaMedicamentoModel receita) {
        String sql = "INSERT INTO receitamedicamento (data, medico, clinica, animal_idanimal) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setObject(1, receita.getData());
            stmt.setString(2, receita.getMedico());
            stmt.setString(3, receita.getClinica());
            stmt.setInt(4, receita.getAnimal_idanimal());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean alterar(ReceitaMedicamentoModel receita) {
        String sql = "UPDATE receitamedicamento SET data = ?, medico = ?, clinica = ?, animal_idanimal = ? WHERE idreceita = ?";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setObject(1, receita.getData());
            stmt.setString(2, receita.getMedico());
            stmt.setString(3, receita.getClinica());
            stmt.setInt(4, receita.getAnimal_idanimal());
            stmt.setInt(5, receita.getIdreceita());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean apagar(Integer id) {
        String sql = "DELETE FROM receitamedicamento WHERE idreceita = ?";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ReceitaMedicamentoDTO findReceitaCompleta(Integer id) {
        String sql = "SELECT r.idreceita, r.data, r.medico, r.clinica, r.animal_idanimal, a.nome as animal_nome " +
                    "FROM receitamedicamento r " +
                    "JOIN animal a ON r.animal_idanimal = a.idanimal " +
                    "WHERE r.idreceita = ?";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                ReceitaMedicamentoDTO receita = new ReceitaMedicamentoDTO();
                receita.setIdreceita(rs.getInt("idreceita"));
                receita.setData(rs.getObject("data", LocalDate.class));
                receita.setMedico(rs.getString("medico"));
                receita.setClinica(rs.getString("clinica"));
                receita.setAnimal_idanimal(rs.getInt("animal_idanimal"));
                receita.setAnimalNome(rs.getString("animal_nome"));
                
                receita.setPosologias(getPosologiasByReceita(id));
                
                return receita;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ReceitaMedicamentoDTO> listarTodas() {
        List<ReceitaMedicamentoDTO> lista = new ArrayList<>();
        String sql = "SELECT r.idreceita, r.data, r.medico, r.clinica, r.animal_idanimal, a.nome as animal_nome " +
                    "FROM receitamedicamento r " +
                    "JOIN animal a ON r.animal_idanimal = a.idanimal " +
                    "ORDER BY r.data DESC";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ReceitaMedicamentoDTO receita = new ReceitaMedicamentoDTO();
                receita.setIdreceita(rs.getInt("idreceita"));
                receita.setData(rs.getObject("data", LocalDate.class));
                receita.setMedico(rs.getString("medico"));
                receita.setClinica(rs.getString("clinica"));
                receita.setAnimal_idanimal(rs.getInt("animal_idanimal"));
                receita.setAnimalNome(rs.getString("animal_nome"));
                
                lista.add(receita);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<ReceitaMedicamentoDTO> buscarPorAnimal(Integer animalId) {
        List<ReceitaMedicamentoDTO> lista = new ArrayList<>();
        String sql = "SELECT r.idreceita, r.data, r.medico, r.clinica, r.animal_idanimal, a.nome as animal_nome " +
                    "FROM receitamedicamento r " +
                    "JOIN animal a ON r.animal_idanimal = a.idanimal " +
                    "WHERE r.animal_idanimal = ? " +
                    "ORDER BY r.data DESC";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, animalId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ReceitaMedicamentoDTO receita = new ReceitaMedicamentoDTO();
                receita.setIdreceita(rs.getInt("idreceita"));
                receita.setData(rs.getObject("data", LocalDate.class));
                receita.setMedico(rs.getString("medico"));
                receita.setClinica(rs.getString("clinica"));
                receita.setAnimal_idanimal(rs.getInt("animal_idanimal"));
                receita.setAnimalNome(rs.getString("animal_nome"));
                
                lista.add(receita);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<ReceitaMedicamentoDTO> buscarPorMedico(String medico) {
        List<ReceitaMedicamentoDTO> lista = new ArrayList<>();
        String sql = "SELECT r.idreceita, r.data, r.medico, r.clinica, r.animal_idanimal, a.nome as animal_nome " +
                    "FROM receitamedicamento r " +
                    "JOIN animal a ON r.animal_idanimal = a.idanimal " +
                    "WHERE UPPER(r.medico) LIKE UPPER(?) " +
                    "ORDER BY r.data DESC";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + medico + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ReceitaMedicamentoDTO receita = new ReceitaMedicamentoDTO();
                receita.setIdreceita(rs.getInt("idreceita"));
                receita.setData(rs.getObject("data", LocalDate.class));
                receita.setMedico(rs.getString("medico"));
                receita.setClinica(rs.getString("clinica"));
                receita.setAnimal_idanimal(rs.getInt("animal_idanimal"));
                receita.setAnimalNome(rs.getString("animal_nome"));
                
                lista.add(receita);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<ReceitaMedicamentoDTO> buscarPorData(LocalDate data) {
        List<ReceitaMedicamentoDTO> lista = new ArrayList<>();
        String sql = "SELECT r.idreceita, r.data, r.medico, r.clinica, r.animal_idanimal, a.nome as animal_nome " +
                    "FROM receitamedicamento r " +
                    "JOIN animal a ON r.animal_idanimal = a.idanimal " +
                    "WHERE r.data = ? " +
                    "ORDER BY r.data DESC";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setObject(1, data);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ReceitaMedicamentoDTO receita = new ReceitaMedicamentoDTO();
                receita.setIdreceita(rs.getInt("idreceita"));
                receita.setData(rs.getObject("data", LocalDate.class));
                receita.setMedico(rs.getString("medico"));
                receita.setClinica(rs.getString("clinica"));
                receita.setAnimal_idanimal(rs.getInt("animal_idanimal"));
                receita.setAnimalNome(rs.getString("animal_nome"));
                
                lista.add(receita);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<PosologiaDTO> getPosologiasByReceita(Integer receitaId) {
        List<PosologiaDTO> lista = new ArrayList<>();
        String sql = "SELECT p.dose, p.quantidadedias, p.intervalohoras, p.medicamento_idproduto, " +
                    "p.receitamedicamento_idreceita, pr.nome as medicamento_nome, m.composicao " +
                    "FROM posologia p " +
                    "JOIN medicamento m ON p.medicamento_idproduto = m.idproduto " +
                    "JOIN produto pr ON m.idproduto = pr.idproduto " +
                    "WHERE p.receitamedicamento_idreceita = ?";
        
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

    public Integer getUltimoId() {
        String sql = "SELECT currval('seq_receitamedicamento') as ultimo_id";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ultimo_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean receitaPodeSerExcluida(Integer id) {
        String sqlPosologia = "SELECT COUNT(*) FROM posologia WHERE receitamedicamento_idreceita = ?";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlPosologia)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        String sqlMedicacao = "SELECT COUNT(*) FROM medicacao m " +
                             "JOIN posologia p ON m.posologia_medicamento_idproduto = p.medicamento_idproduto " +
                             "AND m.posologia_receitamedicamento_idreceita = p.receitamedicamento_idreceita " +
                             "WHERE p.receitamedicamento_idreceita = ?";
        
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sqlMedicacao)) {
            stmt.setInt(1, id);
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
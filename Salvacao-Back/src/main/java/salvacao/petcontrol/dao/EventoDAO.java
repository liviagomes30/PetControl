package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.EventoModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventoDAO {

    public EventoModel findById(int id) {
        String sql = "SELECT * FROM evento WHERE idevento = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractEventoFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Erro ao buscar evento por ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<EventoModel> findByData(LocalDate data) {
        List<EventoModel> eventos = new ArrayList<>();
        String sql = "SELECT * FROM evento WHERE data = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(data));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                eventos.add(extractEventoFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar eventos por data: " + e.getMessage());
            e.printStackTrace();
        }
        return eventos;
    }

    public List<EventoModel> findByAnimal(int animalId) {
        List<EventoModel> eventos = new ArrayList<>();
        String sql = "SELECT * FROM evento WHERE animal_idanimal = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, animalId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                eventos.add(extractEventoFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar eventos por animal: " + e.getMessage());
            e.printStackTrace();
        }
        return eventos;
    }

    public List<EventoModel> findByStatus(String status) {
        List<EventoModel> eventos = new ArrayList<>();
        String sql = "SELECT * FROM evento WHERE status = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                eventos.add(extractEventoFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar eventos por status: " + e.getMessage());
            e.printStackTrace();
        }
        return eventos;
    }

    public EventoModel addEvento(EventoModel evento) {
        String sql = "INSERT INTO evento (descricao, data, foto, animal_idanimal, local, responsavel, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            SingletonDB.getConexao().getConnection().setAutoCommit(false);

            try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
                stmt.setString(1, evento.getDescricao());
                stmt.setDate(2, java.sql.Date.valueOf(evento.getData()));
                if (evento.getFoto() == null) {
                    stmt.setNull(3, java.sql.Types.SQLXML);
                } else {
                    stmt.setObject(3, evento.getFoto(), java.sql.Types.SQLXML);
                }
                stmt.setInt(4, evento.getAnimalIdAnimal());
                stmt.setString(5, evento.getLocal());
                stmt.setString(6, evento.getResponsavel());
                stmt.setString(7, evento.getStatus());

                if (stmt.executeUpdate() > 0) {
                    SingletonDB.getConexao().getConnection().commit();
                } else {
                    SingletonDB.getConexao().getConnection().rollback();
                    throw new RuntimeException("Falha ao inserir evento");
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
            throw new RuntimeException("Erro ao adicionar evento: " + e.getMessage(), e);
        }

        return evento;
    }

    public boolean updateEvento(int id, EventoModel evento) {
        String sql = "UPDATE evento SET descricao = ?, data = ?, foto = ?, animal_idanimal = ?, local = ?, responsavel = ?, status = ? WHERE idevento = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, evento.getDescricao());
            stmt.setDate(2, java.sql.Date.valueOf(evento.getData()));
            if (evento.getFoto() == null) {
                stmt.setNull(3, java.sql.Types.SQLXML);
            } else {
                stmt.setObject(3, evento.getFoto(), java.sql.Types.SQLXML);
            }
            stmt.setInt(4, evento.getAnimalIdAnimal());
            stmt.setString(5, evento.getLocal());
            stmt.setString(6, evento.getResponsavel());
            stmt.setString(7, evento.getStatus());
            stmt.setInt(8, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar evento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEvento(int id) {
        String sql = "DELETE FROM evento WHERE idevento = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar evento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<EventoModel> getAll() {
        List<EventoModel> eventos = new ArrayList<>();
        String sql = "SELECT * FROM evento ORDER BY data DESC";

        try (ResultSet rs = SingletonDB.getConexao().consultar(sql)) {
            while (rs.next()) {
                eventos.add(extractEventoFromResultSet(rs));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar todos os eventos: " + e.getMessage());
            e.printStackTrace();
        }
        return eventos;
    }

    private EventoModel extractEventoFromResultSet(ResultSet rs) throws SQLException {
        EventoModel evento = new EventoModel();
        evento.setIdEvento(rs.getInt("idevento"));
        evento.setDescricao(rs.getString("descricao"));

        // Tratamento para data
        java.sql.Date sqlDate = rs.getDate("data");
        if (sqlDate != null) {
            evento.setData(sqlDate.toLocalDate());
        }

        evento.setFoto(rs.getString("foto"));
        evento.setAnimalIdAnimal(rs.getInt("animal_idanimal"));
        evento.setLocal(rs.getString("local"));
        evento.setResponsavel(rs.getString("responsavel"));
        evento.setStatus(rs.getString("status"));

        return evento;
    }
}
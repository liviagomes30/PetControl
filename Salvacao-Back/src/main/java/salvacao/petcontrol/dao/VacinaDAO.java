package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.Conexao;
import salvacao.petcontrol.model.Vacina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VacinaDAO {

    public Vacina cadastrar(Vacina vacina) {
        String sql = "INSERT INTO vacina (descricao_vacina) VALUES (?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, vacina.getDescricaoVacina());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        vacina.setIdVacina(generatedKeys.getInt(1));
                        return vacina;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Vacina> listarTodas() {
        String sql = "SELECT id_vacina, descricao_vacina FROM vacina ORDER BY descricao_vacina";
        List<Vacina> vacinas = new ArrayList<>();
        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vacina vacina = new Vacina();
                vacina.setIdVacina(rs.getInt("id_vacina"));
                vacina.setDescricaoVacina(rs.getString("descricao_vacina"));
                vacinas.add(vacina);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vacinas;
    }

    public Vacina buscarPorId(int id) {
        String sql = "SELECT id_vacina, descricao_vacina FROM vacina WHERE id_vacina = ?";
        Vacina vacina = null;
        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    vacina = new Vacina();
                    vacina.setIdVacina(rs.getInt("id_vacina"));
                    vacina.setDescricaoVacina(rs.getString("descricao_vacina"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vacina;
    }

    public List<Vacina> buscarPorDescricao(String descricao) {
        String sql = "SELECT id_vacina, descricao_vacina FROM vacina WHERE descricao_vacina ILIKE ? ORDER BY descricao_vacina";
        List<Vacina> vacinas = new ArrayList<>();
        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + descricao + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Vacina vacina = new Vacina();
                    vacina.setIdVacina(rs.getInt("id_vacina"));
                    vacina.setDescricaoVacina(rs.getString("descricao_vacina"));
                    vacinas.add(vacina);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vacinas;
    }
}
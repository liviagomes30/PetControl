package salvacao.petcontrol.dao;

import salvacao.petcontrol.config.Conexao; // Alterado para usar Conexao
import salvacao.petcontrol.model.Vacina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VacinaDAO {

    /**
     * Cadastra uma nova vacina no banco de dados.
     * A tabela 'vacina' tem as colunas: id_vacina (SERIAL PRIMARY KEY), descricao_vacina (TEXT)
     *
     * @param vacina O objeto Vacina contendo a descrição. O ID será gerado pelo banco.
     * @return O objeto Vacina com o ID preenchido após o cadastro, ou null em caso de falha.
     */
    public Vacina cadastrar(Vacina vacina) {
        String sql = "INSERT INTO vacina (descricao_vacina) VALUES (?)";
        System.out.println("DAO: Tentando cadastrar vacina com descrição: " + vacina.getDescricaoVacina()); // DEBUG

        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (conn == null) { // ALTERAR
                System.err.println("DAO: Conexão com o banco é NULA!");
                return null;
            }
            System.out.println("DAO: Conexão obtida com sucesso."); // DEBUG

            pstmt.setString(1, vacina.getDescricaoVacina());
            int affectedRows = pstmt.executeUpdate();
            System.out.println("DAO: Linhas afetadas pelo insert: " + affectedRows); // DEBUG

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGerado = generatedKeys.getInt(1);
                        vacina.setIdVacina(idGerado);
                        System.out.println("DAO: Vacina cadastrada com ID: " + idGerado); // DEBUG
                        return vacina;
                    } else {
                        System.err.println("DAO: Nenhuma chave gerada retornada pelo banco."); // DEBUG
                    }
                }
            } else {
                System.err.println("DAO: Nenhuma linha afetada pelo insert, cadastro falhou."); //DEBUG
            }
            return null;
        } catch (SQLException e) {
            System.err.println("DAO: Erro SQLException ao cadastrar vacina: " + e.getMessage()); // DEBUG
            e.printStackTrace();
            return null;
        } catch (Exception e) { // DEBUG - Captura geral para ver outros possíveis erros
            System.err.println("DAO: Erro GERAL inesperado ao cadastrar vacina: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Vacina> listarTodas() {
        String sql = "SELECT id_vacina, descricao_vacina FROM vacina ORDER BY descricao_vacina";
        List<Vacina> vacinas = new ArrayList<>();
        try (Connection conn = Conexao.getConexao(); // CORRIGIDO AQUI
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vacina vacina = new Vacina();
                vacina.setIdVacina(rs.getInt("id_vacina"));
                vacina.setDescricaoVacina(rs.getString("descricao_vacina"));
                vacinas.add(vacina);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar vacinas: " + e.getMessage());
            e.printStackTrace();
        }
        return vacinas;
    }

    public Vacina buscarPorId(int id) {
        String sql = "SELECT id_vacina, descricao_vacina FROM vacina WHERE id_vacina = ?";
        Vacina vacina = null;
        try (Connection conn = Conexao.getConexao(); // CORRIGIDO AQUI
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
            System.err.println("Erro ao buscar vacina por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return vacina;
    }

    public boolean atualizar(Vacina vacina) {
        String sql = "UPDATE vacina SET descricao_vacina = ? WHERE id_vacina = ?";
        try (Connection conn = Conexao.getConexao(); // CORRIGIDO AQUI
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vacina.getDescricaoVacina());
            pstmt.setInt(2, vacina.getIdVacina());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar vacina: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM vacina WHERE id_vacina = ?";
        try (Connection conn = Conexao.getConexao(); // CORRIGIDO AQUI
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar vacina: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
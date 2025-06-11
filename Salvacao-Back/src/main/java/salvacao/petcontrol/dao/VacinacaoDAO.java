package salvacao.petcontrol.dao;

import salvacao.petcontrol.config.Conexao; //
import salvacao.petcontrol.model.Vacinacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types; // Para setar NULL para tipos de data
import java.util.ArrayList;
import java.util.List;

public class VacinacaoDAO {

    /**
     * Cadastra um novo registro de vacinação no banco de dados.
     * Tabela 'vacinacao': id_vacinacao (SERIAL PK), id_vacina, id_animal,
     * data_vacinacao, local_vacinacao, lote_vacina, data_validade_vacina, laboratorio_vacina
     *
     * @param vacinacao O objeto Vacinacao contendo os dados. O ID será gerado pelo banco.
     * @return O objeto Vacinacao com o ID preenchido, ou null em caso de falha.
     */
    public Vacinacao cadastrar(Vacinacao vacinacao) {
        String sql = "INSERT INTO vacinacao (id_vacina, id_animal, data_vacinacao, local_vacinacao, " +
                "lote_vacina, data_validade_vacina, laboratorio_vacina) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, vacinacao.getIdVacina());
            pstmt.setInt(2, vacinacao.getIdAnimal());
            pstmt.setDate(3, vacinacao.getDataVacinacao()); // java.sql.Date
            pstmt.setString(4, vacinacao.getLocalVacinacao());

            // Campos opcionais
            if (vacinacao.getLoteVacina() != null && !vacinacao.getLoteVacina().isEmpty()) {
                pstmt.setString(5, vacinacao.getLoteVacina());
            } else {
                pstmt.setNull(5, Types.VARCHAR);
            }

            if (vacinacao.getDataValidadeVacina() != null) {
                pstmt.setDate(6, vacinacao.getDataValidadeVacina()); // java.sql.Date
            } else {
                pstmt.setNull(6, Types.DATE);
            }

            if (vacinacao.getLaboratorioVacina() != null && !vacinacao.getLaboratorioVacina().isEmpty()) {
                pstmt.setString(7, vacinacao.getLaboratorioVacina());
            } else {
                pstmt.setNull(7, Types.VARCHAR);
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        vacinacao.setIdVacinacao(generatedKeys.getInt(1));
                        return vacinacao;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar vacinação no DAO: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Busca um registro de vacinação pelo seu ID.
     * @param id O ID da vacinação.
     * @return O objeto Vacinacao encontrado, ou null.
     */
    public Vacinacao buscarPorId(int id) {
        String sql = "SELECT * FROM vacinacao WHERE id_vacinacao = ?";
        Vacinacao vacinacao = null;

        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    vacinacao = new Vacinacao();
                    vacinacao.setIdVacinacao(rs.getInt("id_vacinacao"));
                    vacinacao.setIdVacina(rs.getInt("id_vacina"));
                    vacinacao.setIdAnimal(rs.getInt("id_animal"));
                    vacinacao.setDataVacinacao(rs.getDate("data_vacinacao"));
                    vacinacao.setLocalVacinacao(rs.getString("local_vacinacao"));
                    vacinacao.setLoteVacina(rs.getString("lote_vacina"));
                    vacinacao.setDataValidadeVacina(rs.getDate("data_validade_vacina"));
                    vacinacao.setLaboratorioVacina(rs.getString("laboratorio_vacina"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar vacinação por ID no DAO: " + e.getMessage());
            e.printStackTrace();
        }
        return vacinacao;
    }

    /**
     * Lista todos os registros de vacinação de um animal específico.
     * @param idAnimal O ID do animal.
     * @return Lista de objetos Vacinacao.
     */
    public List<Vacinacao> listarPorAnimal(int idAnimal) {
        String sql = "SELECT * FROM vacinacao WHERE id_animal = ? ORDER BY data_vacinacao DESC";
        List<Vacinacao> vacinacoes = new ArrayList<>();

        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAnimal);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Vacinacao vacinacao = new Vacinacao();
                    vacinacao.setIdVacinacao(rs.getInt("id_vacinacao"));
                    vacinacao.setIdVacina(rs.getInt("id_vacina"));
                    vacinacao.setIdAnimal(rs.getInt("id_animal"));
                    vacinacao.setDataVacinacao(rs.getDate("data_vacinacao"));
                    vacinacao.setLocalVacinacao(rs.getString("local_vacinacao"));
                    vacinacao.setLoteVacina(rs.getString("lote_vacina"));
                    vacinacao.setDataValidadeVacina(rs.getDate("data_validade_vacina"));
                    vacinacao.setLaboratorioVacina(rs.getString("laboratorio_vacina"));
                    vacinacoes.add(vacinacao);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar vacinações por animal no DAO: " + e.getMessage());
            e.printStackTrace();
        }
        return vacinacoes;
    }

    /**
     * Lista todos os registros de vacinação.
     * @return Lista de objetos Vacinacao.
     */
    public List<Vacinacao> listarTodas() {
        String sql = "SELECT * FROM vacinacao ORDER BY data_vacinacao DESC, id_vacinacao DESC";
        List<Vacinacao> vacinacoes = new ArrayList<>();

        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vacinacao vacinacao = new Vacinacao();
                vacinacao.setIdVacinacao(rs.getInt("id_vacinacao"));
                vacinacao.setIdVacina(rs.getInt("id_vacina"));
                vacinacao.setIdAnimal(rs.getInt("id_animal"));
                vacinacao.setDataVacinacao(rs.getDate("data_vacinacao"));
                vacinacao.setLocalVacinacao(rs.getString("local_vacinacao"));
                vacinacao.setLoteVacina(rs.getString("lote_vacina"));
                vacinacao.setDataValidadeVacina(rs.getDate("data_validade_vacina"));
                vacinacao.setLaboratorioVacina(rs.getString("laboratorio_vacina"));
                vacinacoes.add(vacinacao);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todas as vacinações no DAO: " + e.getMessage());
            e.printStackTrace();
        }
        return vacinacoes;
    }


    /**
     * Atualiza um registro de vacinação existente.
     * @param vacinacao O objeto Vacinacao com os dados atualizados.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     */
    public boolean atualizar(Vacinacao vacinacao) {
        String sql = "UPDATE vacinacao SET id_vacina = ?, id_animal = ?, data_vacinacao = ?, " +
                "local_vacinacao = ?, lote_vacina = ?, data_validade_vacina = ?, laboratorio_vacina = ? " +
                "WHERE id_vacinacao = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, vacinacao.getIdVacina());
            pstmt.setInt(2, vacinacao.getIdAnimal());
            pstmt.setDate(3, vacinacao.getDataVacinacao());
            pstmt.setString(4, vacinacao.getLocalVacinacao());

            if (vacinacao.getLoteVacina() != null && !vacinacao.getLoteVacina().isEmpty()) {
                pstmt.setString(5, vacinacao.getLoteVacina());
            } else {
                pstmt.setNull(5, Types.VARCHAR);
            }

            if (vacinacao.getDataValidadeVacina() != null) {
                pstmt.setDate(6, vacinacao.getDataValidadeVacina());
            } else {
                pstmt.setNull(6, Types.DATE);
            }

            if (vacinacao.getLaboratorioVacina() != null && !vacinacao.getLaboratorioVacina().isEmpty()) {
                pstmt.setString(7, vacinacao.getLaboratorioVacina());
            } else {
                pstmt.setNull(7, Types.VARCHAR);
            }

            pstmt.setInt(8, vacinacao.getIdVacinacao());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar vacinação no DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deleta um registro de vacinação pelo seu ID.
     * @param id O ID da vacinação a ser deletada.
     * @return true se a exclusão foi bem-sucedida, false caso contrário.
     */
    public boolean deletar(int id) {
        String sql = "DELETE FROM vacinacao WHERE id_vacinacao = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar vacinação no DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


}
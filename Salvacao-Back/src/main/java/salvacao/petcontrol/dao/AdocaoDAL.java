package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.AdocaoModel;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AdocaoDAL {

    public AdocaoModel findById(int id) {
        String sql = "SELECT * FROM adocao WHERE idadocao = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractAdocaoFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Erro ao buscar adoção por ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<AdocaoModel> findByAnimal(int idAnimal) {
        List<AdocaoModel> adocoes = new ArrayList<>();
        String sql = "SELECT * FROM adocao WHERE idanimal = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                adocoes.add(extractAdocaoFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar adoções por animal: " + e.getMessage());
            e.printStackTrace();
        }
        return adocoes;
    }

    public List<AdocaoModel> findByAdotante(int idAdotante) {
        List<AdocaoModel> adocoes = new ArrayList<>();
        String sql = "SELECT * FROM adocao WHERE idadotante = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAdotante);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                adocoes.add(extractAdocaoFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar adoções por adotante: " + e.getMessage());
            e.printStackTrace();
        }
        return adocoes;
    }

    public AdocaoModel addAdocao(AdocaoModel adocao) {
        String sql = "INSERT INTO adocao (idadotante, idanimal, dataadocao, pessoa_idpessoa, obs, status_acompanhamento, data_acompanhamento) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            SingletonDB.getConexao().getConnection().setAutoCommit(false);

            try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
                stmt.setInt(1, adocao.getIdAdotante());
                stmt.setInt(2, adocao.getIdAnimal());
                // Alterado para usar o novo tipo LocalDate
                stmt.setDate(3, Date.valueOf(adocao.getDataAdocao()));
                stmt.setInt(4, adocao.getPessoaIdPessoa());
                stmt.setString(5, adocao.getObs());
                stmt.setString(6, adocao.getStatusAcompanhamento());
                if (adocao.getDataAcompanhamento() != null) {
                    stmt.setDate(7, Date.valueOf(adocao.getDataAcompanhamento()));
                } else {
                    stmt.setNull(7, java.sql.Types.DATE);
                }

                if (stmt.executeUpdate() > 0) {
                    SingletonDB.getConexao().getConnection().commit();
                } else {
                    SingletonDB.getConexao().getConnection().rollback();
                    throw new RuntimeException("Falha ao inserir adoção");
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
            throw new RuntimeException("Erro ao adicionar adoção: " + e.getMessage(), e);
        }

        return adocao;
    }

    public boolean updateAdocao(int id, AdocaoModel adocao) {
        String sql = "UPDATE adocao SET idadotante = ?, idanimal = ?, dataadocao = ?, pessoa_idpessoa = ?, obs = ?, status_acompanhamento = ?, data_acompanhamento = ? WHERE idadocao = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, adocao.getIdAdotante());
            stmt.setInt(2, adocao.getIdAnimal());
            // Alterado para usar o novo tipo LocalDate
            stmt.setDate(3, Date.valueOf(adocao.getDataAdocao()));
            stmt.setInt(4, adocao.getPessoaIdPessoa());
            stmt.setString(5, adocao.getObs());
            stmt.setString(6, adocao.getStatusAcompanhamento());
            if (adocao.getDataAcompanhamento() != null) {
                stmt.setDate(7, Date.valueOf(adocao.getDataAcompanhamento()));
            } else {
                stmt.setNull(7, java.sql.Types.DATE);
            }
            stmt.setInt(8, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar adoção: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAdocao(int id) {
        String sql = "DELETE FROM adocao WHERE idadocao = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<AdocaoModel> getAll() {
        List<AdocaoModel> adocoes = new ArrayList<>();
        String sql = "SELECT * FROM adocao";

        try (ResultSet rs = SingletonDB.getConexao().consultar(sql)) {
            while (rs.next()) {
                adocoes.add(extractAdocaoFromResultSet(rs));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar todas as adoções: " + e.getMessage());
            e.printStackTrace();
        }
        return adocoes;
    }

    private AdocaoModel extractAdocaoFromResultSet(ResultSet rs) throws SQLException {
        AdocaoModel adocao = new AdocaoModel();
        adocao.setIdAdocao(rs.getInt("idadocao"));
        adocao.setIdAdotante(rs.getInt("idadotante"));
        adocao.setIdAnimal(rs.getInt("idanimal"));
        // Alterado para converter de Date (SQL) para LocalDate (Java)
        adocao.setDataAdocao(rs.getDate("dataadocao").toLocalDate());
        adocao.setPessoaIdPessoa(rs.getInt("pessoa_idpessoa"));
        adocao.setObs(rs.getString("obs"));
        adocao.setStatusAcompanhamento(rs.getString("status_acompanhamento"));

        Date dataAcompanhamentoSQL = rs.getDate("data_acompanhamento");
        if (dataAcompanhamentoSQL != null) {
            adocao.setDataAcompanhamento(dataAcompanhamentoSQL.toLocalDate());
        } else {
            adocao.setDataAcompanhamento(null);
        }

        return adocao;
    }
}

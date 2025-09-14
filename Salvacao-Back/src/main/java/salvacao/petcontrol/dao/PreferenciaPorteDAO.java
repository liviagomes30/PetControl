package salvacao.petcontrol.dao;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.PreferenciaPorteModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PreferenciaPorteDAO {

    public PreferenciaPorteModel gravar(PreferenciaPorteModel preferenciaPorte){
        String sql = "INSERT INTO preferenciaporte(idpessoa, idporte) VALUES (?, ?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, preferenciaPorte.getIdpessoa());
            stmt.setInt(2, preferenciaPorte.getIdporte());

            int linhasMod = stmt.executeUpdate();
            if (linhasMod > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    preferenciaPorte.setIdpreferencia(rs.getInt(1));
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar PreferenciaPorte: " + e.getMessage(), e);
        }
        return preferenciaPorte;
    }

    public boolean buscarPessoaPorte(PreferenciaPorteModel preferenciaPorte){
        String sql = "SELECT FROM preferenciaporte WHERE idpessoa = ? AND idporte = ?";
        boolean flag = false;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, preferenciaPorte.getIdpessoa());
            stmt.setInt(2, preferenciaPorte.getIdporte());
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public List<PreferenciaPorteModel> buscarPorte(int idporte){
        String sql = "SELECT * FROM preferenciaporte WHERE idporte = ?";
        List<PreferenciaPorteModel> preferencias = new ArrayList<>();
        PreferenciaPorteModel resultado;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idporte);

            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                resultado = new PreferenciaPorteModel(resultset.getInt("idpreferencia"),
                        resultset.getInt("idpessoa"),
                        resultset.getInt("idporte"));
                preferencias.add(resultado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preferencias;
    }

    public List<PreferenciaPorteModel> buscarPessoa(int idpessoa){
        String sql = "SELECT * FROM preferenciaporte WHERE idpessoa = ?";
        List<PreferenciaPorteModel> preferencias = new ArrayList<>();
        PreferenciaPorteModel resultado;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idpessoa);

            ResultSet resultset = stmt.executeQuery();

            while (resultset.next()) {
                resultado = new PreferenciaPorteModel(resultset.getInt("idpreferencia"),
                        resultset.getInt("idpessoa"),
                        resultset.getInt("idporte"));
                preferencias.add(resultado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preferencias;
    }

    public boolean apagar(PreferenciaPorteModel preferenciaPorte){
        String sql = "DELETE FROM preferenciaporte WHERE idpessoa = ? AND idporte = ?";

        try(PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, preferenciaPorte.getIdpessoa());
            stmt.setInt(2, preferenciaPorte.getIdporte());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao apagar Preferencia de Porte: " +e.getMessage());
        }
    }
}

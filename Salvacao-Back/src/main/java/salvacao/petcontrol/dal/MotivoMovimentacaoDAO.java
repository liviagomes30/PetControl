package salvacao.petcontrol.dal;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.MotivoMovimentacaoModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class MotivoMovimentacaoDAO {

    public MotivoMovimentacaoModel gravar(MotivoMovimentacaoModel motivo) {
        String sql = "INSERT INTO motivomovimentacao(descricao, tipo) VALUES (?, ?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, motivo.getDescricao());
            stmt.setString(2, motivo.getTipo());

            int linhasMod = stmt.executeUpdate();
            if (linhasMod > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    motivo.setIdmotivo(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar motivo de movimentação: " + e.getMessage(), e);
        }

        return motivo;
    }

    public MotivoMovimentacaoModel getId(Integer id) {
        String sql = "SELECT * FROM motivomovimentacao WHERE idmotivo = ?";
        MotivoMovimentacaoModel motivo = null;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                motivo = new MotivoMovimentacaoModel(
                        resultset.getInt("idmotivo"),
                        resultset.getString("descricao"),
                        resultset.getString("tipo")
                );
            } else {
                System.out.println("Nenhum motivo de movimentação encontrado com ID: " + id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return motivo;
    }


}

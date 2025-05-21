package salvacao.petcontrol.dalNÃOUSARMAIS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.dto.MedicacaoCompletaDTO;
import salvacao.petcontrol.model.AnimalModel;
import salvacao.petcontrol.model.HistoricoModel;
import salvacao.petcontrol.model.MedicacaoModel;
import salvacao.petcontrol.model.MedicamentoModel;
import salvacao.petcontrol.model.PosologiaModel;
import salvacao.petcontrol.model.ReceitaMedicamentoModel;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MedicacaoDAL {

    @Autowired
    private AnimalDAL animalDAL;

    @Autowired
    private HistoricoDAL historicoDAL;

    @Autowired
    private MedicamentoDAL medicamentoDAL;

    @Autowired
    private EstoqueDAL estoqueDAL;

    public MedicacaoModel findById(Integer id) {
        MedicacaoModel medicacao = null;
        String sql = "SELECT * FROM medicacao WHERE idmedicacao = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Date dataSQL = rs.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                medicacao = new MedicacaoModel(
                        rs.getInt("idmedicacao"),
                        rs.getInt("idanimal"),
                        rs.getInt("idhistorico"),
                        rs.getInt("posologia_medicamento_idproduto"),
                        rs.getInt("posologia_receitamedicamento_idreceita"),
                        data
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicacao;
    }

    public MedicacaoCompletaDTO findMedicacaoCompleta(Integer id) {
        String sql = "SELECT m.idmedicacao, m.idanimal, m.idhistorico, " +
                "m.posologia_medicamento_idproduto, m.posologia_receitamedicamento_idreceita, m.data, " +
                "h.descricao AS historico_descricao, h.data AS historico_data, " +
                "p.dose, p.quantidadedias, p.intervalohoras " +
                "FROM medicacao m " +
                "LEFT JOIN historico h ON m.idhistorico = h.idhistorico " +
                "LEFT JOIN posologia p ON m.posologia_medicamento_idproduto = p.medicamento_idproduto " +
                "AND m.posologia_receitamedicamento_idreceita = p.receitamedicamento_idreceita " +
                "WHERE m.idmedicacao = ?";

        MedicacaoCompletaDTO dto = null;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Criar objeto medicação
                Date dataSQL = rs.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                MedicacaoModel medicacao = new MedicacaoModel(
                        rs.getInt("idmedicacao"),
                        rs.getInt("idanimal"),
                        rs.getInt("idhistorico"),
                        rs.getInt("posologia_medicamento_idproduto"),
                        rs.getInt("posologia_receitamedicamento_idreceita"),
                        data
                );

                // Buscar animal relacionado
                AnimalModel animal = animalDAL.getId(medicacao.getIdanimal());

                // Buscar medicamento relacionado
                MedicamentoModel medicamento = medicamentoDAL.findById(medicacao.getPosologia_medicamento_idproduto());

                // Buscar histórico se existir
                HistoricoModel historico = null;
                if (medicacao.getIdhistorico() != null) {
                    historico = new HistoricoModel();
                    historico.setIdhistorico(medicacao.getIdhistorico());
                    historico.setDescricao(rs.getString("historico_descricao"));

                    Date historicoDataSQL = rs.getDate("historico_data");
                    LocalDate historicoData = (historicoDataSQL != null) ? historicoDataSQL.toLocalDate() : null;
                    historico.setData(historicoData);
                    historico.setAnimal_idanimal(medicacao.getIdanimal());
                }

                // Obter posologia
                PosologiaModel posologia = new PosologiaModel();
                posologia.setDose(rs.getString("dose"));
                posologia.setQuantidadedias(rs.getInt("quantidadedias"));
                posologia.setIntervalohoras(rs.getInt("intervalohoras"));
                posologia.setMedicamento_idproduto(medicacao.getPosologia_medicamento_idproduto());
                posologia.setReceitamedicamento_idreceita(medicacao.getPosologia_receitamedicamento_idreceita());

                // Buscar a receita
                ReceitaMedicamentoModel receita = buscarReceitaPorId(medicacao.getPosologia_receitamedicamento_idreceita());

                dto = new MedicacaoCompletaDTO(medicacao, animal, medicamento, receita, posologia, historico);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dto;
    }

    private ReceitaMedicamentoModel buscarReceitaPorId(Integer id) {
        ReceitaMedicamentoModel receita = null;
        String sql = "SELECT * FROM receitamedicamento WHERE idreceita = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Date dataSQL = rs.getDate("data");
                LocalDate data = (dataSQL != null) ? dataSQL.toLocalDate() : null;

                receita = new ReceitaMedicamentoModel(
                        rs.getInt("idreceita"),
                        data,
                        rs.getString("medico"),
                        rs.getString("clinica"),
                        rs.getInt("animal_idanimal")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receita;
    }

    public MedicacaoModel gravarMedicacao(MedicacaoModel medicacao, String descricaoHistorico) throws SQLException {
        SingletonDB.getConexao().getConnection().setAutoCommit(false);

        try {
            // 1. Criar o histórico se houver descrição
            Integer idHistorico = null;
            if (descricaoHistorico != null && !descricaoHistorico.trim().isEmpty()) {
                idHistorico = criarHistorico(medicacao.getIdanimal(), descricaoHistorico);
                medicacao.setIdhistorico(idHistorico);
            }

            // 2. Gravar a medicação
            String sql = "INSERT INTO medicacao (idanimal, idhistorico, " +
                    "posologia_medicamento_idproduto, posologia_receitamedicamento_idreceita, data) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, medicacao.getIdanimal());

                if (medicacao.getIdhistorico() != null) {
                    stmt.setInt(2, medicacao.getIdhistorico());
                } else {
                    stmt.setNull(2, java.sql.Types.INTEGER);
                }

                stmt.setInt(3, medicacao.getPosologia_medicamento_idproduto());
                stmt.setInt(4, medicacao.getPosologia_receitamedicamento_idreceita());

                if (medicacao.getData() != null) {
                    stmt.setDate(5, java.sql.Date.valueOf(medicacao.getData()));
                } else {
                    stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
                }

                int linhasMod = stmt.executeUpdate();

                if (linhasMod > 0) {
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        medicacao.setIdmedicacao(rs.getInt(1));
                    }

                    // 3. Atualizar o estoque (decrementar a quantidade do medicamento)
                    boolean estoqueAtualizado = estoqueDAL.decrementarEstoque(
                            medicacao.getPosologia_medicamento_idproduto(), 1
                    );

                    if (!estoqueAtualizado) {
                        throw new SQLException("Falha ao atualizar o estoque");
                    }

                    // 4. Atualizar o histórico com a relação à medicação, se existir
                    if (idHistorico != null) {
                        atualizarHistoricoComMedicacao(idHistorico, medicacao.getIdmedicacao());
                    }

                    SingletonDB.getConexao().getConnection().commit();
                    return medicacao;
                } else {
                    throw new SQLException("Falha ao inserir medicação");
                }
            }
        } catch (SQLException e) {
            SingletonDB.getConexao().getConnection().rollback();
            throw e;
        } finally {
            SingletonDB.getConexao().getConnection().setAutoCommit(true);
        }
    }

    private Integer criarHistorico(Integer idAnimal, String descricao) throws SQLException {
        String sql = "INSERT INTO historico (descricao, data, animal_idanimal) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, descricao);
            stmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setInt(3, idAnimal);

            int linhasMod = stmt.executeUpdate();
            if (linhasMod > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return null;
    }

    private void atualizarHistoricoComMedicacao(Integer idHistorico, Integer idMedicacao) throws SQLException {
        String sql = "UPDATE historico SET medicacao_idmedicacao = ? WHERE idhistorico = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idMedicacao);
            stmt.setInt(2, idHistorico);
            stmt.executeUpdate();
        }
    }

    public List<MedicacaoCompletaDTO> findMedicacoesPorAnimal(Integer idAnimal) {
        List<MedicacaoCompletaDTO> medicacoes = new ArrayList<>();
        String sql = "SELECT m.idmedicacao FROM medicacao m WHERE m.idanimal = ? ORDER BY m.data DESC";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MedicacaoCompletaDTO medicacao = findMedicacaoCompleta(rs.getInt("idmedicacao"));
                if (medicacao != null) {
                    medicacoes.add(medicacao);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return medicacoes;
    }

    public List<MedicacaoCompletaDTO> findMedicacoesPorData(LocalDate dataInicio, LocalDate dataFim) {
        List<MedicacaoCompletaDTO> medicacoes = new ArrayList<>();
        String sql = "SELECT m.idmedicacao FROM medicacao m WHERE m.data BETWEEN ? AND ? ORDER BY m.data DESC";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(dataInicio));
            stmt.setDate(2, java.sql.Date.valueOf(dataFim));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MedicacaoCompletaDTO medicacao = findMedicacaoCompleta(rs.getInt("idmedicacao"));
                if (medicacao != null) {
                    medicacoes.add(medicacao);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return medicacoes;
    }

    public List<MedicacaoCompletaDTO> findTodasMedicacoes() {
        List<MedicacaoCompletaDTO> medicacoes = new ArrayList<>();
        String sql = "SELECT m.idmedicacao FROM medicacao m ORDER BY m.data DESC";

        try {
            ResultSet rs = SingletonDB.getConexao().consultar(sql);

            while (rs.next()) {
                MedicacaoCompletaDTO medicacao = findMedicacaoCompleta(rs.getInt("idmedicacao"));
                if (medicacao != null) {
                    medicacoes.add(medicacao);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return medicacoes;
    }
}
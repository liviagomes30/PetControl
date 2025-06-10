package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.dto.MedicamentoCompletoDTO;
import salvacao.petcontrol.model.MedicamentoModel;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.model.UnidadeMedidaModel;
import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoModel medicamentoModel;

    @Autowired
    private ProdutoModel produtoModel;

    @Autowired
    private TipoProdutoModel tipoProdutoModel;

    @Autowired
    private UnidadeMedidaModel unidadeMedidaModel;

    @Autowired
    private EstoqueModel estoqueModel;

    public MedicamentoCompletoDTO getId(Integer id) {
        return medicamentoModel.getMedDAO().findMedicamentoCompleto(id);
    }

    public List<MedicamentoCompletoDTO> getAll() {
        return medicamentoModel.getMedDAO().getAllMedicamentos();
    }

    public MedicamentoModel gravar(MedicamentoCompletoDTO dto) throws Exception {
        if (dto.getProduto() == null || dto.getMedicamento() == null) {
            throw new Exception("Dados do medicamento incompletos");
        }

        if (dto.getProduto().getNome() == null || dto.getProduto().getNome().trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }

        if (dto.getMedicamento().getComposicao() == null || dto.getMedicamento().getComposicao().trim().isEmpty()) {
            throw new Exception("Composição do medicamento é obrigatória");
        }

        if (tipoProdutoModel.getTpDAO().getId(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }
        if (unidadeMedidaModel.getUnDAO().getId(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        MedicamentoModel novoMedicamento = null;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            novoMedicamento = medicamentoModel.getMedDAO().gravar(dto.getMedicamento(), dto.getProduto(), conn);


            if (novoMedicamento != null && novoMedicamento.getIdproduto() != null) {
                if (!estoqueModel.getEstDAO().inicializarEstoqueComConexao(novoMedicamento.getIdproduto(), conn)) {
                    throw new SQLException("Erro ao inicializar estoque para o novo medicamento.");
                }
            } else {
                throw new SQLException("ID do produto não obtido após a gravação do medicamento.");
            }

            conn.commit();
            return novoMedicamento;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao adicionar medicamento e inicializar estoque: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public boolean alterar(Integer id, MedicamentoCompletoDTO dto) throws Exception {
        if (dto.getProduto() == null || dto.getMedicamento() == null) {
            throw new Exception("Dados do medicamento incompletos");
        }

        if (dto.getProduto().getNome() == null || dto.getProduto().getNome().trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }

        if (dto.getMedicamento().getComposicao() == null || dto.getMedicamento().getComposicao().trim().isEmpty()) {
            throw new Exception("Composição do medicamento é obrigatória");
        }

        MedicamentoCompletoDTO existente = medicamentoModel.getMedDAO().findMedicamentoCompleto(id);
        if (existente == null) {
            throw new Exception("Medicamento não encontrado");
        }

        if (tipoProdutoModel.getTpDAO().getId(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaModel.getUnDAO().getId(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean atualizado = medicamentoModel.getMedDAO().alterar(id, dto.getMedicamento(), dto.getProduto(), conn);

            if (atualizado) {
                conn.commit();
            } else {
                conn.rollback();
            }
            return atualizado;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao atualizar medicamento: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public ResultadoOperacao apagarMedicamento(Integer id) throws Exception {
        MedicamentoCompletoDTO existente = medicamentoModel.getMedDAO().findMedicamentoCompleto(id);
        if (existente == null) {
            throw new Exception("Medicamento não encontrado");
        }

        ResultadoOperacao resultado = new ResultadoOperacao();
        Connection conn = null;
        boolean autoCommitOriginal = true;

        try {
            boolean podeExcluir = medicamentoModel.getMedDAO().medicamentoPodeSerExcluido(id);

            if (podeExcluir) {
                conn = SingletonDB.getConexao().getConnection();
                autoCommitOriginal = conn.getAutoCommit();
                conn.setAutoCommit(false);

                boolean sucesso = medicamentoModel.getMedDAO().apagar(id, produtoModel, conn);

                if (sucesso) {
                    conn.commit(); // Commit transaction
                    resultado.setOperacao("excluido");
                    resultado.setSucesso(true);
                    resultado.setMensagem("Medicamento excluído com sucesso");
                } else {
                    conn.rollback();
                    resultado.setOperacao("excluido");
                    resultado.setSucesso(false);
                    resultado.setMensagem("Falha ao excluir o medicamento");
                }
            } else {
                boolean sucesso = medicamentoModel.getMedDAO().desativarMedicamento(id);
                resultado.setOperacao("desativado");
                resultado.setSucesso(sucesso);
                resultado.setMensagem(sucesso
                        ? "Medicamento desativado com sucesso. Este item está sendo utilizado no sistema e não pode ser excluído completamente."
                        : "Falha ao desativar o medicamento");
            }

            return resultado;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao processar a exclusão: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public boolean reativarMedicamento(Integer id) throws Exception {
        MedicamentoCompletoDTO existente = medicamentoModel.getMedDAO().findMedicamentoCompleto(id);
        if (existente == null) {
            throw new Exception("Medicamento não encontrado");
        }

        return medicamentoModel.getMedDAO().reativarMedicamento(id);
    }

    public List<MedicamentoCompletoDTO> getNome(String filtro){
        return medicamentoModel.getMedDAO().getNome(filtro);
    }

    public List<MedicamentoCompletoDTO> getComposicao(String filtro){
        return medicamentoModel.getMedDAO().getComposicao(filtro);
    }

    public List<MedicamentoCompletoDTO> listarTodosDisponiveis() {
        return medicamentoModel.getMedDAO().buscarTodosDisponiveis();
    }

    public List<MedicamentoCompletoDTO> getTipo(String filtro){
        return medicamentoModel.getMedDAO().getTipo(filtro);
    }
}
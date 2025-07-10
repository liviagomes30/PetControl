package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.dto.ProdutoCompletoDTO;
import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.model.UnidadeMedidaModel;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoModel produtoModel = new ProdutoModel();

    @Autowired
    private TipoProdutoModel tipoProdutoModel = new TipoProdutoModel();

    @Autowired
    private UnidadeMedidaModel unidadeMedidaModel = new UnidadeMedidaModel();

    @Autowired
    private EstoqueModel estoqueModel = new EstoqueModel();

    public ProdutoCompletoDTO getId(Integer id) {
        return produtoModel.getProdDAO().findProdutoCompleto(id);
    }

    public List<ProdutoCompletoDTO> getAll() {
        return produtoModel.getProdDAO().getAllProdutos();
    }

    public List<ProdutoCompletoDTO> getProdutosByTipo(Integer idTipo) {
        return produtoModel.getProdDAO().getProdutosByTipo(idTipo);
    }

    public List<ProdutoCompletoDTO> getByName(String searchTerm) {
        return produtoModel.getProdDAO().getByName(searchTerm);
    }

    public ProdutoModel gravar(ProdutoCompletoDTO dto) throws Exception {
        if (dto.getProduto() == null) {
            throw new Exception("Dados do produto incompletos");
        }

        if (dto.getProduto().getNome() == null || dto.getProduto().getNome().trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }

        // Access DAO via Model instance
        if (tipoProdutoModel.getTpDAO().getId(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        // Access DAO via Model instance
        if (unidadeMedidaModel.getUnDAO().getId(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            ProdutoModel novoProduto = produtoModel.getProdDAO().gravar(dto.getProduto(), conn);

            if (novoProduto != null && novoProduto.getIdproduto() != null) {
                if (!estoqueModel.getEstDAO().inicializarEstoqueComConexao(novoProduto.getIdproduto(), conn)) {
                    throw new SQLException("Erro ao gravar estoque inicial");
                }
                conn.commit();
                return novoProduto;
            } else {
                conn.rollback();
                return null;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao adicionar produto e inicializar estoque: " + e.getMessage(), e);
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


    public boolean alterar(Integer id, ProdutoCompletoDTO dto) throws Exception {
        if (dto.getProduto() == null) {
            throw new Exception("Dados do produto incompletos");
        }

        if (dto.getProduto().getNome() == null || dto.getProduto().getNome().trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }

        ProdutoCompletoDTO existente = produtoModel.getProdDAO().findProdutoCompleto(id);
        if (existente == null) {
            throw new Exception("Produto não encontrado");
        }

        if (tipoProdutoModel.getTpDAO().getId(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaModel.getUnDAO().getId(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }

        return produtoModel.getProdDAO().alterar(id, dto.getProduto());
    }

    public ResultadoOperacao apagarProduto(Integer id) throws Exception {
        ProdutoCompletoDTO existente = produtoModel.getProdDAO().findProdutoCompleto(id);
        if (existente == null) {
            throw new Exception("Produto não encontrado");
        }

        ResultadoOperacao resultado = new ResultadoOperacao();
        Connection conn = null;
        boolean autoCommitOriginal = true;

        try {
            // Access DAO via Model instance
            boolean podeExcluir = produtoModel.getProdDAO().produtoPodeSerExcluido(id);

            if (podeExcluir) {
                conn = SingletonDB.getConexao().getConnection();
                autoCommitOriginal = conn.getAutoCommit();
                conn.setAutoCommit(false);

                boolean sucesso = produtoModel.getProdDAO().apagar(id, conn);

                if (sucesso) {
                    conn.commit(); // Commit transaction
                    resultado.setOperacao("excluido");
                    resultado.setSucesso(true);
                    resultado.setMensagem("Produto excluído com sucesso");
                } else {
                    conn.rollback();
                    resultado.setOperacao("excluido");
                    resultado.setSucesso(false);
                    resultado.setMensagem("Falha ao excluir o produto");
                }
            } else {
                boolean sucesso = produtoModel.getProdDAO().desativarProduto(id);
                resultado.setOperacao("desativado");
                resultado.setSucesso(sucesso);

                if (sucesso) {
                    resultado.setMensagem("Produto desativado com sucesso. Este item está sendo utilizado no sistema e não pode ser excluído completamente.");
                } else {
                    resultado.setMensagem("Falha ao desativar o produto");
                }
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

    public boolean reativarProduto(Integer id) throws Exception {
        ProdutoCompletoDTO existente = produtoModel.getProdDAO().findProdutoCompleto(id);
        if (existente == null) {
            throw new Exception("Produto não encontrado");
        }

        return produtoModel.getProdDAO().reativarProduto(id);
    }

    public List<ProdutoCompletoDTO> getByFabricante(String filtro) {
        return produtoModel.getProdDAO().getByFabricante(filtro);
    }

    public List<ProdutoCompletoDTO> getByTipoDescricao(String filtro) {
        return produtoModel.getProdDAO().getByTipoDescricao(filtro);
    }

    public List<ProdutoCompletoDTO> getAllInactive() {
        return produtoModel.getProdDAO().getAllInactiveProdutos();
    }
}
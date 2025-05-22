// salvacao.petcontrol.service.ProdutoService.java
package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dao.ProdutoDAO;
import salvacao.petcontrol.dao.TipoProdutoDAO;
import salvacao.petcontrol.dao.UnidadeMedidaDAO;
import salvacao.petcontrol.dao.EstoqueDAO;
import salvacao.petcontrol.dto.ProdutoCompletoDTO;
import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import salvacao.petcontrol.config.SingletonDB;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoDAO produtoDAO;

    @Autowired
    private TipoProdutoDAO tipoProdutoDAO;

    @Autowired
    private UnidadeMedidaDAO unidadeMedidaDAO;

    @Autowired
    private EstoqueDAO estoqueDAO;

    public ProdutoCompletoDTO getId(Integer id) {
        return produtoDAO.findProdutoCompleto(id);
    }

    public List<ProdutoCompletoDTO> getAll() {
        return produtoDAO.getAllProdutos();
    }

    public List<ProdutoCompletoDTO> getProdutosByTipo(Integer idTipo) {
        return produtoDAO.getProdutosByTipo(idTipo);
    }

    public List<ProdutoCompletoDTO> getByName(String searchTerm) {
        return produtoDAO.getByName(searchTerm);
    }

    public ProdutoModel gravar(ProdutoCompletoDTO dto) throws Exception {
        if (dto.getProduto() == null) {
            throw new Exception("Dados do produto incompletos");
        }

        if (dto.getProduto().getNome() == null || dto.getProduto().getNome().trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }

        if (tipoProdutoDAO.getId(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaDAO.getId(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }
        Connection conn = SingletonDB.getConexao().getConnection();
        boolean autoCommitOriginal = true;
        try {
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            ProdutoModel novoProduto = produtoDAO.gravar(dto.getProduto());

            EstoqueModel newStock = new EstoqueModel();
            newStock.setIdproduto(novoProduto.getIdproduto());
            newStock.setQuantidade(BigDecimal.ZERO);

            EstoqueModel estoqueSalvo = estoqueDAO.gravar(newStock);

            if (estoqueSalvo == null) {
                throw new Exception();
            }

            conn.commit();
            return novoProduto;

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new Exception("Erro ao adicionar produto e inicializar estoque: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(autoCommitOriginal);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
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

        ProdutoCompletoDTO existente = produtoDAO.findProdutoCompleto(id);
        if (existente == null) {
            throw new Exception("Produto não encontrado");
        }

        if (tipoProdutoDAO.getId(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaDAO.getId(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }

        return produtoDAO.alterar(id, dto.getProduto());
    }

    public ResultadoOperacao apagarProduto(Integer id) throws Exception {
        ProdutoCompletoDTO existente = produtoDAO.findProdutoCompleto(id);
        if (existente == null) {
            throw new Exception("Produto não encontrado");
        }

        ResultadoOperacao resultado = new ResultadoOperacao();

        try {
            boolean podeExcluir = produtoDAO.produtoPodeSerExcluido(id);

            if (podeExcluir) {
                boolean sucesso = produtoDAO.apagar(id);
                resultado.setOperacao("excluido");
                resultado.setSucesso(sucesso);

                if (sucesso) {
                    resultado.setMensagem("Produto excluído com sucesso");
                } else {
                    resultado.setMensagem("Falha ao excluir o produto");
                }
            } else {
                boolean sucesso = produtoDAO.desativarProduto(id);
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
            e.printStackTrace();
            throw new Exception("Erro ao processar a exclusão: " + e.getMessage());
        }
    }

    public boolean reativarProduto(Integer id) throws Exception {
        ProdutoCompletoDTO existente = produtoDAO.findProdutoCompleto(id);
        if (existente == null) {
            throw new Exception("Produto não encontrado");
        }

        return produtoDAO.reativarProduto(id);
    }

    public List<ProdutoCompletoDTO> getByFabricante(String filtro) {
        return produtoDAO.getByFabricante(filtro);
    }

    public List<ProdutoCompletoDTO> getByTipoDescricao(String filtro) {
        return produtoDAO.getByTipoDescricao(filtro);
    }
}
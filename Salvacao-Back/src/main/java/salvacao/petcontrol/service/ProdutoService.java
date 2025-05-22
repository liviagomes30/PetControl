// salvacao.petcontrol.service.ProdutoService.java
package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dto.ProdutoCompletoDTO;
import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.model.UnidadeMedidaModel;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import salvacao.petcontrol.config.SingletonDB;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoModel produtoModel = new ProdutoModel();

    @Autowired
    private TipoProdutoModel TipoProdutoModel = new TipoProdutoModel();

    @Autowired
    private UnidadeMedidaModel UnidadeMedidaModel = new UnidadeMedidaModel();

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

        if (TipoProdutoModel.getTpDAO().getId(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        if (UnidadeMedidaModel.getUnDAO().getId(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }

        try {
            return produtoModel.getProdDAO().gravar(dto.getProduto());
        } catch (RuntimeException e) {
            throw new Exception("Erro ao adicionar produto: " + e.getMessage(), e);
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

        if (TipoProdutoModel.getTpDAO().getId(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        if (UnidadeMedidaModel.getUnDAO().getId(dto.getProduto().getIdunidademedida()) == null) {
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

        try {
            boolean podeExcluir = produtoModel.getProdDAO().produtoPodeSerExcluido(id);

            if (podeExcluir) {
                boolean sucesso = produtoModel.getProdDAO().apagar(id);
                resultado.setOperacao("excluido");
                resultado.setSucesso(sucesso);

                if (sucesso) {
                    resultado.setMensagem("Produto excluído com sucesso");
                } else {
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
            e.printStackTrace();
            throw new Exception("Erro ao processar a exclusão: " + e.getMessage());
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
}
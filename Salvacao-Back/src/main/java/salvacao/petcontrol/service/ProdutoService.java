package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dal.ProdutoDAL;
import salvacao.petcontrol.dal.TipoProdutoDAL;
import salvacao.petcontrol.dal.UnidadeMedidaDAL;
import salvacao.petcontrol.dto.ProdutoCompletoDTO;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.sql.SQLException;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoDAL produtoDAL;

    @Autowired
    private TipoProdutoDAL tipoProdutoDAL;

    @Autowired
    private UnidadeMedidaDAL unidadeMedidaDAL;

    public ProdutoCompletoDTO getProdutoById(Integer id) {
        return produtoDAL.findProdutoCompleto(id);
    }

    public List<ProdutoCompletoDTO> getAllProdutos() {
        return produtoDAL.getAllProdutos();
    }

    public List<ProdutoCompletoDTO> getProdutosByTipo(Integer idTipo) {
        return produtoDAL.getProdutosByTipo(idTipo);
    }

    public List<ProdutoCompletoDTO> getByName(String searchTerm) {
        return produtoDAL.getByName(searchTerm);
    }

    public ProdutoModel addProduto(ProdutoCompletoDTO dto) throws Exception {
        if (dto.getProduto() == null) {
            throw new Exception("Dados do produto incompletos");
        }

        if (dto.getProduto().getNome() == null || dto.getProduto().getNome().trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }

        if (tipoProdutoDAL.findById(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaDAL.findById(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }

        return produtoDAL.addProduto(dto.getProduto());
    }

    public boolean updateProduto(Integer id, ProdutoCompletoDTO dto) throws Exception {
        if (dto.getProduto() == null) {
            throw new Exception("Dados do produto incompletos");
        }

        if (dto.getProduto().getNome() == null || dto.getProduto().getNome().trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }

        ProdutoCompletoDTO existente = produtoDAL.findProdutoCompleto(id);
        if (existente == null) {
            throw new Exception("Produto não encontrado");
        }

        if (tipoProdutoDAL.findById(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaDAL.findById(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }

        return produtoDAL.updateProduto(id, dto.getProduto());
    }

    public boolean deleteProduto(Integer id) throws Exception {
        ProdutoCompletoDTO existente = produtoDAL.findProdutoCompleto(id);
        if (existente == null) {
            throw new Exception("Produto não encontrado");
        }

        return produtoDAL.deleteProduto(id);
    }

    public ResultadoOperacao gerenciarExclusaoProduto(Integer id) throws Exception {
        ProdutoCompletoDTO existente = produtoDAL.findProdutoCompleto(id);
        if (existente == null) {
            throw new Exception("Produto não encontrado");
        }

        ResultadoOperacao resultado = new ResultadoOperacao();

        try {
            boolean podeExcluir = produtoDAL.produtoPodeSerExcluido(id);

            if (podeExcluir) {
                boolean sucesso = produtoDAL.deleteProduto(id);
                resultado.setOperacao("excluido");
                resultado.setSucesso(sucesso);

                if (sucesso) {
                    resultado.setMensagem("Produto excluído com sucesso");
                } else {
                    resultado.setMensagem("Falha ao excluir o produto");
                }
            } else {
                boolean sucesso = produtoDAL.desativarProduto(id);
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
        ProdutoCompletoDTO existente = produtoDAL.findProdutoCompleto(id);
        if (existente == null) {
            throw new Exception("Produto não encontrado");
        }

        return produtoDAL.reativarProduto(id);
    }

    public List<ProdutoCompletoDTO> getByFabricante(String filtro) {
        return produtoDAL.getByFabricante(filtro);
    }

    public List<ProdutoCompletoDTO> getByTipoDescricao(String filtro) {
        return produtoDAL.getByTipoDescricao(filtro);
    }
}
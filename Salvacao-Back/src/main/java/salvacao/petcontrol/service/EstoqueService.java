package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dal.EstoqueDAL;
import salvacao.petcontrol.dal.ProdutoDAL;
import salvacao.petcontrol.dal.TipoProdutoDAL;
import salvacao.petcontrol.dal.UnidadeMedidaDAL;
import salvacao.petcontrol.dto.EstoqueProdutoDTO;
import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.model.UnidadeMedidaModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueDAL estoqueDAL;

    @Autowired
    private ProdutoDAL produtoDAL;

    @Autowired
    private TipoProdutoDAL tipoProdutoDAL;

    @Autowired
    private UnidadeMedidaDAL unidadeMedidaDAL;

    public EstoqueModel getEstoqueById(Integer id) {
        return estoqueDAL.findById(id);
    }

    public EstoqueModel getEstoqueByProdutoId(Integer idProduto) {
        return estoqueDAL.findByProdutoId(idProduto);
    }

    public List<EstoqueModel> getAllEstoque() {
        return estoqueDAL.findAll();
    }

    public List<EstoqueModel> getEstoqueAbaixoMinimo() {
        return estoqueDAL.findEstoqueAbaixoMinimo();
    }

    public EstoqueProdutoDTO getEstoqueProdutoCompleto(Integer idEstoque) throws Exception {
        EstoqueModel estoque = estoqueDAL.findById(idEstoque);
        if (estoque == null) {
            throw new Exception("Estoque não encontrado");
        }

        ProdutoModel produto = produtoDAL.findById(estoque.getIdproduto());
        if (produto == null) {
            throw new Exception("Produto não encontrado");
        }

        TipoProdutoModel tipoProduto = tipoProdutoDAL.findById(produto.getIdtipoproduto());
        UnidadeMedidaModel unidadeMedida = unidadeMedidaDAL.findById(produto.getIdunidademedida());

        return new EstoqueProdutoDTO(estoque, produto, tipoProduto, unidadeMedida);
    }

    public List<EstoqueProdutoDTO> getAllEstoqueProduto() {
        List<EstoqueProdutoDTO> estoqueCompleto = new ArrayList<>();
        List<EstoqueModel> estoqueList = estoqueDAL.findAll();

        for (EstoqueModel estoque : estoqueList) {
            try {
                EstoqueProdutoDTO dto = getEstoqueProdutoCompleto(estoque.getIdestoque());
                estoqueCompleto.add(dto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return estoqueCompleto;
    }

    public List<EstoqueProdutoDTO> getEstoqueProdutoAbaixoMinimo() {
        List<EstoqueProdutoDTO> estoqueAbaixoMinimo = new ArrayList<>();
        List<EstoqueModel> estoqueList = estoqueDAL.findEstoqueAbaixoMinimo();

        for (EstoqueModel estoque : estoqueList) {
            try {
                EstoqueProdutoDTO dto = getEstoqueProdutoCompleto(estoque.getIdestoque());
                estoqueAbaixoMinimo.add(dto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return estoqueAbaixoMinimo;
    }

    public EstoqueModel addEstoque(EstoqueModel estoque) throws Exception {
        if (estoque.getIdproduto() == null) {
            throw new Exception("Produto é obrigatório");
        }

        if (estoque.getQuantidade() == null || estoque.getQuantidade().intValue() < 0) {
            throw new Exception("Quantidade deve ser maior ou igual a zero");
        }

        if (produtoDAL.findById(estoque.getIdproduto()) == null) {
            throw new Exception("Produto não encontrado");
        }

        EstoqueModel estoqueExistente = estoqueDAL.findByProdutoId(estoque.getIdproduto());
        if (estoqueExistente != null) {
            throw new Exception("Já existe registro de estoque para este produto. Utilize atualização de estoque.");
        }

        try {
            return estoqueDAL.adicionarEstoque(estoque);
        } catch (SQLException e) {
            throw new Exception("Erro ao adicionar estoque: " + e.getMessage());
        }
    }

    public boolean updateEstoque(Integer id, EstoqueModel estoque) throws Exception {
        EstoqueModel existente = estoqueDAL.findById(id);
        if (existente == null) {
            throw new Exception("Estoque não encontrado");
        }

        if (estoque.getQuantidade() == null || estoque.getQuantidade().intValue() < 0) {
            throw new Exception("Quantidade deve ser maior ou igual a zero");
        }

        estoque.setIdestoque(id);
        estoque.setIdproduto(existente.getIdproduto()); // Não permite alterar o produto

        return estoqueDAL.atualizarEstoque(estoque);
    }

    public boolean deleteEstoque(Integer id) throws Exception {
        EstoqueModel existente = estoqueDAL.findById(id);
        if (existente == null) {
            throw new Exception("Estoque não encontrado");
        }

        return estoqueDAL.removerEstoque(id);
    }

    public boolean decrementarEstoque(Integer idProduto, Integer quantidade) throws Exception {
        if (quantidade <= 0) {
            throw new Exception("Quantidade para decrementar deve ser maior que zero");
        }

        EstoqueModel estoque = estoqueDAL.findByProdutoId(idProduto);
        if (estoque == null) {
            throw new Exception("Estoque não encontrado para o produto");
        }

        if (estoque.getQuantidade().intValue() < quantidade) {
            throw new Exception("Quantidade em estoque insuficiente");
        }

        return estoqueDAL.decrementarEstoque(idProduto, quantidade);
    }

    public boolean incrementarEstoque(Integer idProduto, Integer quantidade) throws Exception {
        if (quantidade <= 0) {
            throw new Exception("Quantidade para incrementar deve ser maior que zero");
        }

        if (produtoDAL.findById(idProduto) == null) {
            throw new Exception("Produto não encontrado");
        }

        return estoqueDAL.incrementarEstoque(idProduto, quantidade);
    }

    public boolean verificarEstoqueSuficiente(Integer idProduto, Integer quantidade) {
        return estoqueDAL.verificarEstoqueSuficiente(idProduto, quantidade);
    }

    public List<EstoqueProdutoDTO> buscarEstoquePorNomeProduto(String nomeProduto) {
        List<EstoqueProdutoDTO> resultado = new ArrayList<>();
        List<EstoqueModel> estoqueList = estoqueDAL.buscarPorNomeProduto(nomeProduto);

        for (EstoqueModel estoque : estoqueList) {
            try {
                EstoqueProdutoDTO dto = getEstoqueProdutoCompleto(estoque.getIdestoque());
                resultado.add(dto);
            } catch (Exception e) {
                // Ignora item com erro
                e.printStackTrace();
            }
        }

        return resultado;
    }

    public List<EstoqueProdutoDTO> buscarEstoquePorTipoProduto(Integer idTipoProduto) throws Exception {
        if (tipoProdutoDAL.findById(idTipoProduto) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        List<EstoqueProdutoDTO> resultado = new ArrayList<>();
        List<EstoqueModel> estoqueList = estoqueDAL.buscarPorTipoProduto(idTipoProduto);

        for (EstoqueModel estoque : estoqueList) {
            try {
                EstoqueProdutoDTO dto = getEstoqueProdutoCompleto(estoque.getIdestoque());
                resultado.add(dto);
            } catch (Exception e) {
                // Ignora item com erro
                e.printStackTrace();
            }
        }

        return resultado;
    }
}
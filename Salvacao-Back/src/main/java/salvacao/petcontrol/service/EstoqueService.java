package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private EstoqueModel estoqueModel = new EstoqueModel();
    @Autowired
    private ProdutoModel produtoModel = new ProdutoModel();
    @Autowired
    private TipoProdutoModel tipoProdutoModel = new TipoProdutoModel();
    @Autowired
    private UnidadeMedidaModel unidadeMedidaModel = new UnidadeMedidaModel();

    public EstoqueModel getId(Integer id) {
        return estoqueModel.getEstDAO().getId(id);
    }

    public EstoqueModel getByProdutoId(Integer idProduto) {
        return estoqueModel.getEstDAO().getByProdutoId(idProduto);
    }

    public List<EstoqueModel> getAll() {
        return estoqueModel.getEstDAO().getAll();
    }

    public List<EstoqueModel> getAbaixoMinimo() {
        return estoqueModel.getEstDAO().getEstoqueAbaixoMinimo();
    }

    public EstoqueProdutoDTO getEstoqueProdutoCompleto(Integer idEstoque) throws Exception {
        EstoqueModel estoque = estoqueModel.getEstDAO().getId(idEstoque);
        if (estoque == null) throw new Exception("Estoque não encontrado");

        ProdutoModel produto = produtoModel.getProdDAO().getId(estoque.getIdproduto());
        if (produto == null) throw new Exception("Produto não encontrado");

        var tipoProduto = tipoProdutoModel.getTpDAO().getId(produto.getIdtipoproduto());
        var unidadeMedida = unidadeMedidaModel.getUnDAO().getId(produto.getIdunidademedida());

        return new EstoqueProdutoDTO(estoque, produto, tipoProduto, unidadeMedida);
    }

    public List<EstoqueProdutoDTO> getAllEstoqueProduto() {
        List<EstoqueProdutoDTO> lista = new ArrayList<>();
        for (EstoqueModel estoque : estoqueModel.getEstDAO().getAll()) {
            try {
                lista.add(getEstoqueProdutoCompleto(estoque.getIdestoque()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lista;
    }

    public List<EstoqueProdutoDTO> getEstoqueProdutoAbaixoMinimo() {
        List<EstoqueProdutoDTO> lista = new ArrayList<>();
        for (EstoqueModel estoque : estoqueModel.getEstDAO().getEstoqueAbaixoMinimo()) {
            try {
                lista.add(getEstoqueProdutoCompleto(estoque.getIdestoque()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lista;
    }

    public EstoqueModel gravar(EstoqueModel estoque) throws Exception {
        if (estoque.getIdproduto() == null) throw new Exception("Produto é obrigatório");
        if (estoque.getQuantidade() == null || estoque.getQuantidade().intValue() < 0)
            throw new Exception("Quantidade deve ser maior ou igual a zero");

        if (produtoModel.getProdDAO().getId(estoque.getIdproduto()) == null)
            throw new Exception("Produto não encontrado");

        if (estoqueModel.getEstDAO().getByProdutoId(estoque.getIdproduto()) != null)
            throw new Exception("Já existe registro de estoque para este produto. Utilize atualização de estoque.");

        try {
            return estoqueModel.getEstDAO().gravar(estoque);
        } catch (SQLException e) {
            throw new Exception("Erro ao adicionar estoque: " + e.getMessage());
        }
    }

    public boolean alterar(Integer id, EstoqueModel estoque) throws Exception {
        var existente = estoqueModel.getEstDAO().getId(id);
        if (existente == null) throw new Exception("Estoque não encontrado");

        if (estoque.getQuantidade() == null || estoque.getQuantidade().intValue() < 0)
            throw new Exception("Quantidade deve ser maior ou igual a zero");

        estoque.setIdestoque(id);
        estoque.setIdproduto(existente.getIdproduto());

        return estoqueModel.getEstDAO().alterar(estoque);
    }

    public boolean apagar(Integer id) throws Exception {
        var existente = estoqueModel.getEstDAO().getId(id);
        if (existente == null) throw new Exception("Estoque não encontrado");

        return estoqueModel.getEstDAO().apagar(id);
    }

    public boolean decrementar(Integer idProduto, Integer quantidade) throws Exception {
        if (quantidade <= 0) throw new Exception("Quantidade para decrementar deve ser maior que zero");

        var estoque = estoqueModel.getEstDAO().getByProdutoId(idProduto);
        if (estoque == null) throw new Exception("Estoque não encontrado para o produto");

        if (estoque.getQuantidade().intValue() < quantidade)
            throw new Exception("Quantidade em estoque insuficiente");

        return estoqueModel.getEstDAO().decrementarEstoque(idProduto, quantidade);
    }

    public boolean incrementar(Integer idProduto, Integer quantidade) throws Exception {
        if (quantidade <= 0) throw new Exception("Quantidade para incrementar deve ser maior que zero");

        if (produtoModel.getProdDAO().getId(idProduto) == null)
            throw new Exception("Produto não encontrado");

        return estoqueModel.getEstDAO().incrementarEstoque(idProduto, quantidade);
    }

    public boolean verificarSuficiente(Integer idProduto, Integer quantidade) {
        return estoqueModel.getEstDAO().verificarEstoqueSuficiente(idProduto, quantidade);
    }

    public List<EstoqueProdutoDTO> getByNomeProduto(String nomeProduto) {
        List<EstoqueProdutoDTO> lista = new ArrayList<>();
        for (EstoqueModel estoque : estoqueModel.getEstDAO().getByNomeProduto(nomeProduto)) {
            try {
                lista.add(getEstoqueProdutoCompleto(estoque.getIdestoque()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lista;
    }

    public List<EstoqueProdutoDTO> getByTipoProduto(Integer idTipoProduto) throws Exception {
        if (tipoProdutoModel.getTpDAO().getId(idTipoProduto) == null)
            throw new Exception("Tipo de produto não encontrado");

        List<EstoqueProdutoDTO> lista = new ArrayList<>();
        for (EstoqueModel estoque : estoqueModel.getEstDAO().getByTipoProduto(idTipoProduto)) {
            try {
                lista.add(getEstoqueProdutoCompleto(estoque.getIdestoque()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
}

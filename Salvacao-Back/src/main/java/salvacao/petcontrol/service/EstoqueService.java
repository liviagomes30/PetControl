// salvacao.petcontrol.service.EstoqueService.java
package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dao.EstoqueDAO; // Changed from EstoqueDAL
import salvacao.petcontrol.dao.ProdutoDAO; // Changed from ProdutoDAL
import salvacao.petcontrol.dao.TipoProdutoDAO; // Changed from TipoProdutoDAL
import salvacao.petcontrol.dao.UnidadeMedidaDAO; // Changed from UnidadeMedidaDAL
import salvacao.petcontrol.dto.EstoqueProdutoDTO;
import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.model.UnidadeMedidaModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueDAO estoqueDAO; // Changed from EstoqueDAL

    @Autowired
    private ProdutoDAO produtoDAO; // Changed from ProdutoDAL

    @Autowired
    private TipoProdutoDAO tipoProdutoDAO; // Changed from TipoProdutoDAL

    @Autowired
    private UnidadeMedidaDAO unidadeMedidaDAO; // Changed from UnidadeMedidaDAL

    public EstoqueModel getId(Integer id) { // Renamed from getEstoqueById
        return estoqueDAO.getId(id); // Calls DAO.getId()
    }

    public EstoqueModel getByProdutoId(Integer idProduto) { // Renamed from getEstoqueByProdutoId
        return estoqueDAO.getByProdutoId(idProduto); // Calls DAO.getByProdutoId()
    }

    public List<EstoqueModel> getAll() { // Renamed from getAllEstoque
        return estoqueDAO.getAll(); // Calls DAO.getAll()
    }

    public List<EstoqueModel> getAbaixoMinimo() { // Renamed from getEstoqueAbaixoMinimo
        return estoqueDAO.getEstoqueAbaixoMinimo(); // Calls DAO.getEstoqueAbaixoMinimo()
    }

    public EstoqueProdutoDTO getEstoqueProdutoCompleto(Integer idEstoque) throws Exception {
        EstoqueModel estoque = estoqueDAO.getId(idEstoque); // Calls DAO.getId()
        if (estoque == null) {
            throw new Exception("Estoque não encontrado");
        }

        ProdutoModel produto = produtoDAO.getId(estoque.getIdproduto()); // Calls DAO.getId()
        if (produto == null) {
            throw new Exception("Produto não encontrado");
        }

        TipoProdutoModel tipoProduto = tipoProdutoDAO.getId(produto.getIdtipoproduto()); // Calls DAO.getId()
        UnidadeMedidaModel unidadeMedida = unidadeMedidaDAO.getId(produto.getIdunidademedida()); // Calls DAO.getId()

        return new EstoqueProdutoDTO(estoque, produto, tipoProduto, unidadeMedida);
    }

    public List<EstoqueProdutoDTO> getAllEstoqueProduto() {
        List<EstoqueProdutoDTO> estoqueCompleto = new ArrayList<>();
        List<EstoqueModel> estoqueList = estoqueDAO.getAll(); // Calls DAO.getAll()

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
        List<EstoqueModel> estoqueList = estoqueDAO.getEstoqueAbaixoMinimo(); // Calls DAO.getEstoqueAbaixoMinimo()

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

    public EstoqueModel gravar(EstoqueModel estoque) throws Exception { // Renamed from addEstoque
        if (estoque.getIdproduto() == null) {
            throw new Exception("Produto é obrigatório");
        }

        if (estoque.getQuantidade() == null || estoque.getQuantidade().intValue() < 0) {
            throw new Exception("Quantidade deve ser maior ou igual a zero");
        }

        if (produtoDAO.getId(estoque.getIdproduto()) == null) { // Calls DAO.getId()
            throw new Exception("Produto não encontrado");
        }

        EstoqueModel estoqueExistente = estoqueDAO.getByProdutoId(estoque.getIdproduto()); // Calls DAO.getByProdutoId()
        if (estoqueExistente != null) {
            throw new Exception("Já existe registro de estoque para este produto. Utilize atualização de estoque.");
        }

        try {
            return estoqueDAO.gravar(estoque); // Calls DAO.gravar()
        } catch (SQLException e) {
            throw new Exception("Erro ao adicionar estoque: " + e.getMessage());
        }
    }

    public boolean alterar(Integer id, EstoqueModel estoque) throws Exception { // Renamed from updateEstoque
        EstoqueModel existente = estoqueDAO.getId(id); // Calls DAO.getId()
        if (existente == null) {
            throw new Exception("Estoque não encontrado");
        }

        if (estoque.getQuantidade() == null || estoque.getQuantidade().intValue() < 0) {
            throw new Exception("Quantidade deve ser maior ou igual a zero");
        }

        estoque.setIdestoque(id);
        estoque.setIdproduto(existente.getIdproduto()); // Não permite alterar o produto

        return estoqueDAO.alterar(estoque); // Calls DAO.alterar()
    }

    public boolean apagar(Integer id) throws Exception { // Renamed from deleteEstoque
        EstoqueModel existente = estoqueDAO.getId(id); // Calls DAO.getId()
        if (existente == null) {
            throw new Exception("Estoque não encontrado");
        }

        return estoqueDAO.apagar(id); // Calls DAO.apagar()
    }

    public boolean decrementar(Integer idProduto, Integer quantidade) throws Exception { // Renamed from decrementarEstoque
        if (quantidade <= 0) {
            throw new Exception("Quantidade para decrementar deve ser maior que zero");
        }

        EstoqueModel estoque = estoqueDAO.getByProdutoId(idProduto); // Calls DAO.getByProdutoId()
        if (estoque == null) {
            throw new Exception("Estoque não encontrado para o produto");
        }

        if (estoque.getQuantidade().intValue() < quantidade) {
            throw new Exception("Quantidade em estoque insuficiente");
        }

        return estoqueDAO.decrementarEstoque(idProduto, quantidade); // Calls DAO.decrementarEstoque()
    }

    public boolean incrementar(Integer idProduto, Integer quantidade) throws Exception { // Renamed from incrementarEstoque
        if (quantidade <= 0) {
            throw new Exception("Quantidade para incrementar deve ser maior que zero");
        }

        if (produtoDAO.getId(idProduto) == null) { // Calls DAO.getId()
            throw new Exception("Produto não encontrado");
        }

        return estoqueDAO.incrementarEstoque(idProduto, quantidade); // Calls DAO.incrementarEstoque()
    }

    public boolean verificarSuficiente(Integer idProduto, Integer quantidade) { // Renamed from verificarEstoqueSuficiente
        return estoqueDAO.verificarEstoqueSuficiente(idProduto, quantidade); // Calls DAO.verificarEstoqueSuficiente()
    }

    public List<EstoqueProdutoDTO> getByNomeProduto(String nomeProduto) { // Renamed from buscarEstoquePorNomeProduto
        List<EstoqueProdutoDTO> resultado = new ArrayList<>();
        List<EstoqueModel> estoqueList = estoqueDAO.getByNomeProduto(nomeProduto); // Calls DAO.getByNomeProduto()

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

    public List<EstoqueProdutoDTO> getByTipoProduto(Integer idTipoProduto) throws Exception { // Renamed from buscarEstoquePorTipoProduto
        if (tipoProdutoDAO.getId(idTipoProduto) == null) { // Calls DAO.getId()
            throw new Exception("Tipo de produto não encontrado");
        }

        List<EstoqueProdutoDTO> resultado = new ArrayList<>();
        List<EstoqueModel> estoqueList = estoqueDAO.getByTipoProduto(idTipoProduto); // Calls DAO.getByTipoProduto()

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
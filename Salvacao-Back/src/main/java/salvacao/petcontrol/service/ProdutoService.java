// salvacao.petcontrol.service.ProdutoService.java
package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dao.ProdutoDAO; // Updated from ProdutoDAL
import salvacao.petcontrol.dalNÃOUSARMAIS.TipoProdutoDAL; // Will be TipoProdutoDAO in future refactoring
import salvacao.petcontrol.dalNÃOUSARMAIS.UnidadeMedidaDAL; // Will be UnidadeMedidaDAO in future refactoring
import salvacao.petcontrol.dto.ProdutoCompletoDTO;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.sql.SQLException;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoDAO produtoDAO; // Updated from ProdutoDAL

    @Autowired
    private TipoProdutoDAL tipoProdutoDAL; // Dependency for now

    @Autowired
    private UnidadeMedidaDAL unidadeMedidaDAL; // Dependency for now

    public ProdutoCompletoDTO getId(Integer id) { // Renamed from getProdutoById
        return produtoDAO.findProdutoCompleto(id);
    }

    public List<ProdutoCompletoDTO> getAll() { // Renamed from getAllProdutos
        return produtoDAO.getAllProdutos();
    }

    public List<ProdutoCompletoDTO> getProdutosByTipo(Integer idTipo) { // Kept original method name as it's a specific filter
        return produtoDAO.getProdutosByTipo(idTipo);
    }

    public List<ProdutoCompletoDTO> getByName(String searchTerm) { // Kept original method name
        return produtoDAO.getByName(searchTerm);
    }

    public ProdutoModel gravar(ProdutoCompletoDTO dto) throws Exception { // Renamed from addProduto
        if (dto.getProduto() == null) {
            throw new Exception("Dados do produto incompletos");
        }

        if (dto.getProduto().getNome() == null || dto.getProduto().getNome().trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }

        // Validate if TipoProdutoDAL and UnidadeMedidaDAL will be DAOs and their methods renamed
        if (tipoProdutoDAL.findById(dto.getProduto().getIdtipoproduto()) == null) { // Calls existing findById
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaDAL.findById(dto.getProduto().getIdunidademedida()) == null) { // Calls existing findById
            throw new Exception("Unidade de medida não encontrada");
        }

        // No direct date validation methods are needed from AnimalService for ProdutoModel based on its fields.
        // DataCadastro is usually set at DAO level or passed as java.util.Date.

        return produtoDAO.gravar(dto.getProduto()); // Updated method call
    }

    public boolean alterar(Integer id, ProdutoCompletoDTO dto) throws Exception { // Renamed from updateProduto
        if (dto.getProduto() == null) {
            throw new Exception("Dados do produto incompletos");
        }

        if (dto.getProduto().getNome() == null || dto.getProduto().getNome().trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }

        ProdutoCompletoDTO existente = produtoDAO.findProdutoCompleto(id); // Calls existing findProdutoCompleto
        if (existente == null) {
            throw new Exception("Produto não encontrado");
        }

        if (tipoProdutoDAL.findById(dto.getProduto().getIdtipoproduto()) == null) { // Calls existing findById
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaDAL.findById(dto.getProduto().getIdunidademedida()) == null) { // Calls existing findById
            throw new Exception("Unidade de medida não encontrada");
        }

        return produtoDAO.alterar(id, dto.getProduto()); // Updated method call
    }

    public ResultadoOperacao apagarProduto(Integer id) throws Exception { // Renamed from gerenciarExclusaoProduto (Matches controller now)
        ProdutoCompletoDTO existente = produtoDAO.findProdutoCompleto(id); // Calls existing findProdutoCompleto
        if (existente == null) {
            throw new Exception("Produto não encontrado");
        }

        ResultadoOperacao resultado = new ResultadoOperacao();

        try {
            boolean podeExcluir = produtoDAO.produtoPodeSerExcluido(id); // Calls existing method

            if (podeExcluir) {
                boolean sucesso = produtoDAO.apagar(id); // Updated method call
                resultado.setOperacao("excluido");
                resultado.setSucesso(sucesso);

                if (sucesso) {
                    resultado.setMensagem("Produto excluído com sucesso");
                } else {
                    resultado.setMensagem("Falha ao excluir o produto");
                }
            } else {
                boolean sucesso = produtoDAO.desativarProduto(id); // Calls existing method
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

    // This method was originally in the service, but in AnimalService, this logic is handled by a direct DAO call in apagarAnimal.
    // However, since it has complex logic, it might be better to keep it in the service as a helper.
    // For now, I will keep the reativarProduto method as is, consistent with the previous logic.
    public boolean reativarProduto(Integer id) throws Exception { // Kept original method name
        ProdutoCompletoDTO existente = produtoDAO.findProdutoCompleto(id); // Calls existing findProdutoCompleto
        if (existente == null) {
            throw new Exception("Produto não encontrado");
        }

        return produtoDAO.reativarProduto(id); // Calls existing method
    }

    public List<ProdutoCompletoDTO> getByFabricante(String filtro) { // Kept original method name
        return produtoDAO.getByFabricante(filtro); // Calls existing method
    }

    public List<ProdutoCompletoDTO> getByTipoDescricao(String filtro) { // Kept original method name
        return produtoDAO.getByTipoDescricao(filtro); // Calls existing method
    }
}
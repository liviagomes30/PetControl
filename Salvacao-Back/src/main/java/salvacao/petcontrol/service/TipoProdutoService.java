// salvacao.petcontrol.service.TipoProdutoService.java
package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dao.TipoProdutoDAO; // Updated from TipoProdutoDAL
import salvacao.petcontrol.model.TipoProdutoModel;

import java.sql.SQLException;
import java.util.List;

@Service
public class TipoProdutoService {

    @Autowired
    private TipoProdutoDAO tipoProdutoDAO; // Updated from TipoProdutoDAL

    public TipoProdutoModel getId(Integer id) { // Renamed from getTipoProdutoById
        return tipoProdutoDAO.getId(id); // Calls the new DAO method
    }

    public List<TipoProdutoModel> getAll() { // Renamed from getAllTiposProduto
        return tipoProdutoDAO.getAll(); // Calls the new DAO method
    }

    public TipoProdutoModel gravar(TipoProdutoModel tipoProduto) throws Exception { // Added gravar method
        if (tipoProduto.getDescricao() == null || tipoProduto.getDescricao().trim().isEmpty()) {
            throw new Exception("A descrição é obrigatória.");
        }
        if (tipoProduto.getDescricao().length() > 100) {
            throw new Exception("A descrição não pode ter mais de 100 caracteres.");
        }
        return tipoProdutoDAO.gravar(tipoProduto); // Calls the new DAO method
    }

    public boolean alterar(TipoProdutoModel tipoProduto) throws Exception { // Added alterar method
        if (tipoProduto.getDescricao() == null || tipoProduto.getDescricao().trim().isEmpty()) {
            throw new Exception("A descrição é obrigatória.");
        }
        if (tipoProduto.getDescricao().length() > 100) {
            throw new Exception("A descrição não pode ter mais de 100 caracteres.");
        }
        TipoProdutoModel existente = tipoProdutoDAO.getId(tipoProduto.getIdtipoproduto());
        if (existente == null) {
            throw new Exception("Tipo de produto não encontrado para atualização.");
        }
        return tipoProdutoDAO.alterar(tipoProduto); // Calls the new DAO method
    }

    public boolean apagar(Integer id) throws Exception { // Added apagar method
        TipoProdutoModel existente = tipoProdutoDAO.getId(id);
        if (existente == null) {
            throw new Exception("Tipo de produto não encontrado para exclusão.");
        }
        try {
            return tipoProdutoDAO.apagar(id); // Calls the new DAO method
        } catch (SQLException e) {
            // Catch specific SQL exceptions if needed, e.g., foreign key constraints
            throw new Exception("Erro ao excluir tipo de produto: " + e.getMessage());
        }
    }

    public List<TipoProdutoModel> getByDescricao(String filtro) { // Implemented the method
        return tipoProdutoDAO.getByDescricao(filtro); // Calls the corresponding DAO method
    }
}
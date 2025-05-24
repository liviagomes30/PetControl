// salvacao.petcontrol.service.TipoProdutoService.java
package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.model.TipoProdutoModel;

import java.sql.SQLException;
import java.util.List;

@Service
public class TipoProdutoService {

    @Autowired
    private TipoProdutoModel tipoProdutoModel = new TipoProdutoModel();

    public TipoProdutoModel getId(Integer id) {
        return tipoProdutoModel.getTpDAO().getId(id); // Access DAO via Model
    }

    public List<TipoProdutoModel> getAll() {
        return tipoProdutoModel.getTpDAO().getAll(); // Access DAO via Model
    }

    public TipoProdutoModel gravar(TipoProdutoModel tipoProduto) throws Exception {
        if (tipoProduto.getDescricao() == null || tipoProduto.getDescricao().trim().isEmpty()) {
            throw new Exception("A descrição é obrigatória.");
        }
        if (tipoProduto.getDescricao().length() > 100) {
            throw new Exception("A descrição não pode ter mais de 100 caracteres.");
        }
        return tipoProdutoModel.getTpDAO().gravar(tipoProduto); // Access DAO via Model
    }

    public boolean alterar(TipoProdutoModel tipoProduto) throws Exception {
        if (tipoProduto.getDescricao() == null || tipoProduto.getDescricao().trim().isEmpty()) {
            throw new Exception("A descrição é obrigatória.");
        }
        if (tipoProduto.getDescricao().length() > 100) {
            throw new Exception("A descrição não pode ter mais de 100 caracteres.");
        }
        TipoProdutoModel existente = tipoProdutoModel.getTpDAO().getId(tipoProduto.getIdtipoproduto()); // Access DAO via Model
        if (existente == null) {
            throw new Exception("Tipo de produto não encontrado para atualização.");
        }
        return tipoProdutoModel.getTpDAO().alterar(tipoProduto); // Access DAO via Model
    }

    public boolean apagar(Integer id) throws Exception {
        TipoProdutoModel existente = tipoProdutoModel.getTpDAO().getId(id); // Access DAO via Model
        if (existente == null) {
            throw new Exception("Tipo de produto não encontrado para exclusão.");
        }
        try {
            return tipoProdutoModel.getTpDAO().apagar(id); // Access DAO via Model
        } catch (SQLException e) {
            // Catch specific SQL exceptions if needed, e.g., foreign key constraints
            throw new Exception("Erro ao excluir tipo de produto: " + e.getMessage());
        }
    }

    public List<TipoProdutoModel> getByDescricao(String filtro) {
        return tipoProdutoModel.getTpDAO().getByDescricao(filtro); // Access DAO via Model
    }
}
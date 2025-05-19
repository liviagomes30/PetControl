package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dal.TipoProdutoDAL;
import salvacao.petcontrol.model.TipoProdutoModel;

import java.util.List;

@Service
public class TipoProdutoService {

    @Autowired
    private TipoProdutoDAL tipoProdutoDAL;

    public TipoProdutoModel getTipoProdutoById(Integer id) {
        return tipoProdutoDAL.findById(id);
    }

    public List<TipoProdutoModel> getAllTiposProduto() {
        return tipoProdutoDAL.getAll();
    }
}
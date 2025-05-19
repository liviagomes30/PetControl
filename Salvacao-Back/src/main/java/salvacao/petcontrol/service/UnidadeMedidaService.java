package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dal.UnidadeMedidaDAL;
import salvacao.petcontrol.model.UnidadeMedidaModel;

import java.util.List;

@Service
public class UnidadeMedidaService {

    @Autowired
    private UnidadeMedidaDAL unidadeMedidaDAL;

    public UnidadeMedidaModel getUnidadeMedidaById(Integer id) {
        return unidadeMedidaDAL.findById(id);
    }

    public List<UnidadeMedidaModel> getAllUnidadesMedida() {
        return unidadeMedidaDAL.getAll();
    }
}
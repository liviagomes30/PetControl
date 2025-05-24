// salvacao.petcontrol.service.UnidadeMedidaService.java
package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.model.UnidadeMedidaModel;

import java.sql.SQLException;
import java.util.List;

@Service
public class UnidadeMedidaService {

    @Autowired
    private UnidadeMedidaModel unidadeMedidaModel = new UnidadeMedidaModel();

    public UnidadeMedidaModel getId(Integer id) {
        return unidadeMedidaModel.getUnDAO().getId(id);
    }

    public List<UnidadeMedidaModel> getAll() {
        return unidadeMedidaModel.getUnDAO().getAll();
    }

    public UnidadeMedidaModel gravar(UnidadeMedidaModel unidadeMedida) throws Exception {
        if (unidadeMedida.getDescricao() == null || unidadeMedida.getDescricao().trim().isEmpty()) {
            throw new Exception("A descrição é obrigatória.");
        }
        if (unidadeMedida.getDescricao().length() > 100) {
            throw new Exception("A descrição não pode ter mais de 100 caracteres.");
        }
        if (unidadeMedida.getSigla() == null || unidadeMedida.getSigla().trim().isEmpty()) {
            throw new Exception("A sigla é obrigatória.");
        }
        if (unidadeMedida.getSigla().length() > 10) {
            throw new Exception("A sigla não pode ter mais de 10 caracteres.");
        }
        return unidadeMedidaModel.getUnDAO().gravar(unidadeMedida);
    }

    public boolean alterar(UnidadeMedidaModel unidadeMedida) throws Exception {
        if (unidadeMedida.getDescricao() == null || unidadeMedida.getDescricao().trim().isEmpty()) {
            throw new Exception("A descrição é obrigatória.");
        }
        if (unidadeMedida.getDescricao().length() > 100) {
            throw new Exception("A descrição não pode ter mais de 100 caracteres.");
        }
        if (unidadeMedida.getSigla() == null || unidadeMedida.getSigla().trim().isEmpty()) {
            throw new Exception("A sigla é obrigatória.");
        }
        if (unidadeMedida.getSigla().length() > 10) {
            throw new Exception("A sigla não pode ter mais de 10 caracteres.");
        }
        UnidadeMedidaModel existente = unidadeMedidaModel.getUnDAO().getId(unidadeMedida.getIdUnidadeMedida());
        if (existente == null) {
            throw new Exception("Unidade de medida não encontrada para atualização.");
        }
        return unidadeMedidaModel.getUnDAO().alterar(unidadeMedida);
    }

    public boolean apagar(Integer id) throws Exception {
        UnidadeMedidaModel existente = unidadeMedidaModel.getUnDAO().getId(id);
        if (existente == null) {
            throw new Exception("Unidade de medida não encontrada para exclusão.");
        }
        try {
            return unidadeMedidaModel.getUnDAO().apagar(id);
        } catch (SQLException e) {
            throw new Exception("Erro ao excluir unidade de medida: " + e.getMessage());
        }
    }

    public List<UnidadeMedidaModel> getByDescricaoSigla(String filtro) {
        return unidadeMedidaModel.getUnDAO().getByDescricaoSigla(filtro);
    }
}
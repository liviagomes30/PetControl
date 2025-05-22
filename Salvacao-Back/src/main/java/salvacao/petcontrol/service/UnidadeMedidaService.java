// salvacao.petcontrol.service.UnidadeMedidaService.java
package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dao.UnidadeMedidaDAO; // Updated from UnidadeMedidaDAL
import salvacao.petcontrol.model.UnidadeMedidaModel; //

import java.sql.SQLException; //
import java.util.List; //

@Service
public class UnidadeMedidaService {

    @Autowired
    private UnidadeMedidaDAO unidadeMedidaDAO; // Updated from UnidadeMedidaDAL

    public UnidadeMedidaModel getId(Integer id) { // Renamed from getUnidadeMedidaById
        return unidadeMedidaDAO.getId(id); // Calls the new DAO method
    }

    public List<UnidadeMedidaModel> getAll() { // Renamed from getAllUnidadesMedida
        return unidadeMedidaDAO.getAll(); // Calls the new DAO method
    }

    public UnidadeMedidaModel gravar(UnidadeMedidaModel unidadeMedida) throws Exception { // Added gravar method
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
        return unidadeMedidaDAO.gravar(unidadeMedida); // Calls the new DAO method
    }

    public boolean alterar(UnidadeMedidaModel unidadeMedida) throws Exception { // Added alterar method
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
        UnidadeMedidaModel existente = unidadeMedidaDAO.getId(unidadeMedida.getIdUnidadeMedida()); // Calls the new DAO method
        if (existente == null) {
            throw new Exception("Unidade de medida não encontrada para atualização.");
        }
        return unidadeMedidaDAO.alterar(unidadeMedida); // Calls the new DAO method
    }

    public boolean apagar(Integer id) throws Exception { // Added apagar method
        UnidadeMedidaModel existente = unidadeMedidaDAO.getId(id); // Calls the new DAO method
        if (existente == null) {
            throw new Exception("Unidade de medida não encontrada para exclusão.");
        }
        try {
            return unidadeMedidaDAO.apagar(id); // Calls the new DAO method
        } catch (SQLException e) {
            throw new Exception("Erro ao excluir unidade de medida: " + e.getMessage());
        }
    }

    public List<UnidadeMedidaModel> getByDescricaoSigla(String filtro) { // Added getByDescricaoSigla method
        return unidadeMedidaDAO.getByDescricaoSigla(filtro); // Calls the new DAO method
    }
}
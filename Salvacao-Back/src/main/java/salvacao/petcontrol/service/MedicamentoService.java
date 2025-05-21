// salvacao.petcontrol.service.MedicamentoService.java
package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dao.MedicamentoDAO;
import salvacao.petcontrol.dao.TipoProdutoDAO; // Changed from TipoProdutoDAL
import salvacao.petcontrol.dao.UnidadeMedidaDAO; // Changed from UnidadeMedidaDAL
import salvacao.petcontrol.dto.MedicamentoCompletoDTO;
import salvacao.petcontrol.model.MedicamentoModel;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.util.List;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoDAO medicamentoDAO;

    @Autowired
    private TipoProdutoDAO tipoProdutoDAO; // Changed from TipoProdutoDAL

    @Autowired
    private UnidadeMedidaDAO unidadeMedidaDAO; // Changed from UnidadeMedidaDAL

    public MedicamentoCompletoDTO getId(Integer id) {
        return medicamentoDAO.findMedicamentoCompleto(id);
    }

    public List<MedicamentoCompletoDTO> getAll() {
        return medicamentoDAO.getAllMedicamentos();
    }

    public MedicamentoModel gravar(MedicamentoCompletoDTO dto) throws Exception {
        // Validações
        if (dto.getProduto() == null || dto.getMedicamento() == null) {
            throw new Exception("Dados do medicamento incompletos");
        }

        if (dto.getProduto().getNome() == null || dto.getProduto().getNome().trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }

        if (dto.getMedicamento().getComposicao() == null || dto.getMedicamento().getComposicao().trim().isEmpty()) {
            throw new Exception("Composição do medicamento é obrigatória");
        }

        if (tipoProdutoDAO.getId(dto.getProduto().getIdtipoproduto()) == null) { // Updated method call
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaDAO.getId(dto.getProduto().getIdunidademedida()) == null) { // Updated method call
            throw new Exception("Unidade de medida não encontrada");
        }


        return medicamentoDAO.gravar(dto.getMedicamento(), dto.getProduto());
    }

    public boolean alterar(Integer id, MedicamentoCompletoDTO dto) throws Exception {
        if (dto.getProduto() == null || dto.getMedicamento() == null) {
            throw new Exception("Dados do medicamento incompletos");
        }

        if (dto.getProduto().getNome() == null || dto.getProduto().getNome().trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }

        if (dto.getMedicamento().getComposicao() == null || dto.getMedicamento().getComposicao().trim().isEmpty()) {
            throw new Exception("Composição do medicamento é obrigatória");
        }

        MedicamentoCompletoDTO existente = medicamentoDAO.findMedicamentoCompleto(id);
        if (existente == null) {
            throw new Exception("Medicamento não encontrado");
        }

        if (tipoProdutoDAO.getId(dto.getProduto().getIdtipoproduto()) == null) { // Updated method call
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaDAO.getId(dto.getProduto().getIdunidademedida()) == null) { // Updated method call
            throw new Exception("Unidade de medida não encontrada");
        }

        return medicamentoDAO.alterar(id, dto.getMedicamento(), dto.getProduto());
    }


    public ResultadoOperacao apagarMedicamento(Integer id) throws Exception {
        MedicamentoCompletoDTO existente = medicamentoDAO.findMedicamentoCompleto(id);
        if (existente == null) {
            throw new Exception("Medicamento não encontrado");
        }

        try {
            boolean podeExcluir = medicamentoDAO.medicamentoPodeSerExcluido(id);

            ResultadoOperacao resultado = new ResultadoOperacao();

            if (podeExcluir) {
                boolean sucesso = medicamentoDAO.apagar(id);
                resultado.setOperacao("excluido");
                resultado.setSucesso(sucesso);

                if (sucesso) {
                    resultado.setMensagem("Medicamento excluído com sucesso");
                } else {
                    resultado.setMensagem("Falha ao excluir o medicamento");
                }
            } else {
                boolean sucesso = medicamentoDAO.desativarMedicamento(id);
                resultado.setOperacao("desativado");
                resultado.setSucesso(sucesso);

                if (sucesso) {
                    resultado.setMensagem("Medicamento desativado com sucesso. Este item está sendo utilizado no sistema e não pode ser excluído completamente.");
                } else {
                    resultado.setMensagem("Falha ao desativar o medicamento");
                }
            }

            return resultado;

        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<MedicamentoCompletoDTO> getNome(String filtro){
        return medicamentoDAO.getNome(filtro);
    }

    public List<MedicamentoCompletoDTO> getComposicao(String filtro){
        return medicamentoDAO.getComposicao(filtro);
    }

    public List<MedicamentoCompletoDTO> getTipo(String filtro){
        return medicamentoDAO.getTipo(filtro);
    }
}
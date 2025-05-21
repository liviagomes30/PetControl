// salvacao.petcontrol.service.MedicamentoService.java
package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dao.MedicamentoDAO;
import salvacao.petcontrol.dalNÃOUSARMAIS.TipoProdutoDAL; // This will be refactored to TipoProdutoDAO later
import salvacao.petcontrol.dalNÃOUSARMAIS.UnidadeMedidaDAL; // This will be refactored to UnidadeMedidaDAO later
import salvacao.petcontrol.dto.MedicamentoCompletoDTO;
import salvacao.petcontrol.model.MedicamentoModel;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.util.List;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoDAO medicamentoDAO;

    @Autowired
    private TipoProdutoDAL tipoProdutoDAL; // Dependency, will be changed to TipoProdutoDAO later

    @Autowired
    private UnidadeMedidaDAL unidadeMedidaDAL; // Dependency, will be changed to UnidadeMedidaDAO later

    public MedicamentoCompletoDTO getId(Integer id) { // Renamed from getMedicamentoById
        return medicamentoDAO.findMedicamentoCompleto(id);
    }

    public List<MedicamentoCompletoDTO> getAll() { // Renamed from getAllMedicamentos
        return medicamentoDAO.getAllMedicamentos();
    }

    public MedicamentoModel gravar(MedicamentoCompletoDTO dto) throws Exception { // Renamed from addMedicamento
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

        if (tipoProdutoDAL.findById(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaDAL.findById(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }


        return medicamentoDAO.gravar(dto.getMedicamento(), dto.getProduto());
    }

    public boolean alterar(Integer id, MedicamentoCompletoDTO dto) throws Exception { // Renamed from updateMedicamento
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

        if (tipoProdutoDAL.findById(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaDAL.findById(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }

        return medicamentoDAO.alterar(id, dto.getMedicamento(), dto.getProduto());
    }


    public ResultadoOperacao apagarMedicamento(Integer id) throws Exception { // Renamed from gerenciarExclusaoMedicamento
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
// salvacao.petcontrol.service.MedicamentoService.java
package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dto.MedicamentoCompletoDTO;
import salvacao.petcontrol.model.MedicamentoModel;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.model.UnidadeMedidaModel;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.util.List;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoModel medicamentoModel = new MedicamentoModel();

    @Autowired
    private TipoProdutoModel tipoProdutoModel = new TipoProdutoModel();

    @Autowired
    private UnidadeMedidaModel unidadeMedidaModel = new UnidadeMedidaModel();

    public MedicamentoCompletoDTO getId(Integer id) {
        return medicamentoModel.getMedDAO().findMedicamentoCompleto(id);
    }

    public List<MedicamentoCompletoDTO> getAll() {
        return medicamentoModel.getMedDAO().getAllMedicamentos();
    }

    public MedicamentoModel gravar(MedicamentoCompletoDTO dto) throws Exception {
        if (dto.getProduto() == null || dto.getMedicamento() == null) {
            throw new Exception("Dados do medicamento incompletos");
        }

        if (dto.getProduto().getNome() == null || dto.getProduto().getNome().trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }

        if (dto.getMedicamento().getComposicao() == null || dto.getMedicamento().getComposicao().trim().isEmpty()) {
            throw new Exception("Composição do medicamento é obrigatória");
        }

        if (tipoProdutoModel.getTpDAO().getId(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaModel.getUnDAO().getId(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }

        return medicamentoModel.getMedDAO().gravar(dto.getMedicamento(), dto.getProduto());
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

        MedicamentoCompletoDTO existente = medicamentoModel.getMedDAO().findMedicamentoCompleto(id);
        if (existente == null) {
            throw new Exception("Medicamento não encontrado");
        }

        if (tipoProdutoModel.getTpDAO().getId(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaModel.getUnDAO().getId(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }

        return medicamentoModel.getMedDAO().alterar(id, dto.getMedicamento(), dto.getProduto());
    }

    public ResultadoOperacao apagarMedicamento(Integer id) throws Exception {
        MedicamentoCompletoDTO existente = medicamentoModel.getMedDAO().findMedicamentoCompleto(id);
        if (existente == null) {
            throw new Exception("Medicamento não encontrado");
        }

        ResultadoOperacao resultado = new ResultadoOperacao();

        try {
            boolean podeExcluir = medicamentoModel.getMedDAO().medicamentoPodeSerExcluido(id);

            if (podeExcluir) {
                boolean sucesso = medicamentoModel.getMedDAO().apagar(id);
                resultado.setOperacao("excluido");
                resultado.setSucesso(sucesso);
                resultado.setMensagem(sucesso ? "Medicamento excluído com sucesso" : "Falha ao excluir o medicamento");
            } else {
                boolean sucesso = medicamentoModel.getMedDAO().desativarMedicamento(id);
                resultado.setOperacao("desativado");
                resultado.setSucesso(sucesso);
                resultado.setMensagem(sucesso
                        ? "Medicamento desativado com sucesso. Este item está sendo utilizado no sistema e não pode ser excluído completamente."
                        : "Falha ao desativar o medicamento");
            }

            return resultado;

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Erro ao processar a exclusão: " + e.getMessage());
        }
    }

    public boolean reativarMedicamento(Integer id) throws Exception {
        MedicamentoCompletoDTO existente = medicamentoModel.getMedDAO().findMedicamentoCompleto(id);
        if (existente == null) {
            throw new Exception("Medicamento não encontrado");
        }

        return medicamentoModel.getMedDAO().reativarMedicamento(id);
    }


    public List<MedicamentoCompletoDTO> getNome(String filtro){
        return medicamentoModel.getMedDAO().getNome(filtro);
    }

    public List<MedicamentoCompletoDTO> getComposicao(String filtro){
        return medicamentoModel.getMedDAO().getComposicao(filtro);
    }

    public List<MedicamentoCompletoDTO> getTipo(String filtro){
        return medicamentoModel.getMedDAO().getTipo(filtro);
    }
}
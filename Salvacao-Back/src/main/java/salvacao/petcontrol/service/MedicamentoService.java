package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dal.MedicamentoDAL;
import salvacao.petcontrol.dal.TipoProdutoDAL;
import salvacao.petcontrol.dal.UnidadeMedidaDAL;
import salvacao.petcontrol.dto.MedicamentoCompletoDTO;
import salvacao.petcontrol.model.MedicamentoModel;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.util.List;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoDAL medicamentoDAL;

    @Autowired
    private TipoProdutoDAL tipoProdutoDAL;

    @Autowired
    private UnidadeMedidaDAL unidadeMedidaDAL;

    public MedicamentoCompletoDTO getMedicamentoById(Integer id) {
        return medicamentoDAL.findMedicamentoCompleto(id);
    }

    public List<MedicamentoCompletoDTO> getAllMedicamentos() {
        return medicamentoDAL.getAllMedicamentos();
    }

    public MedicamentoModel addMedicamento(MedicamentoCompletoDTO dto) throws Exception {
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

        // Verificar se tipo de produto e unidade de medida existem
        if (tipoProdutoDAL.findById(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaDAL.findById(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }

        // Adicionar o medicamento
        return medicamentoDAL.addMedicamento(dto.getMedicamento(), dto.getProduto());
    }

    public boolean updateMedicamento(Integer id, MedicamentoCompletoDTO dto) throws Exception {
        if (dto.getProduto() == null || dto.getMedicamento() == null) {
            throw new Exception("Dados do medicamento incompletos");
        }

        if (dto.getProduto().getNome() == null || dto.getProduto().getNome().trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }

        if (dto.getMedicamento().getComposicao() == null || dto.getMedicamento().getComposicao().trim().isEmpty()) {
            throw new Exception("Composição do medicamento é obrigatória");
        }

        MedicamentoCompletoDTO existente = medicamentoDAL.findMedicamentoCompleto(id);
        if (existente == null) {
            throw new Exception("Medicamento não encontrado");
        }

        if (tipoProdutoDAL.findById(dto.getProduto().getIdtipoproduto()) == null) {
            throw new Exception("Tipo de produto não encontrado");
        }

        if (unidadeMedidaDAL.findById(dto.getProduto().getIdunidademedida()) == null) {
            throw new Exception("Unidade de medida não encontrada");
        }

        return medicamentoDAL.updateMedicamento(id, dto.getMedicamento(), dto.getProduto());
    }


    public ResultadoOperacao gerenciarExclusaoMedicamento(Integer id) throws Exception {
        MedicamentoCompletoDTO existente = medicamentoDAL.findMedicamentoCompleto(id);
        if (existente == null) {
            throw new Exception("Medicamento não encontrado");
        }

        try {
            boolean podeExcluir = medicamentoDAL.medicamentoPodeSerExcluido(id);

            ResultadoOperacao resultado = new ResultadoOperacao();

            if (podeExcluir) {
                boolean sucesso = medicamentoDAL.deleteMedicamento(id);
                resultado.setOperacao("excluido");
                resultado.setSucesso(sucesso);

                if (sucesso) {
                    resultado.setMensagem("Medicamento excluído com sucesso");
                } else {
                    resultado.setMensagem("Falha ao excluir o medicamento");
                }
            } else {
                boolean sucesso = medicamentoDAL.desativarMedicamento(id);
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
        return medicamentoDAL.getNome(filtro);
    }

    public List<MedicamentoCompletoDTO> getComposicao(String filtro){
        return medicamentoDAL.getComposicao(filtro);
    }

    public List<MedicamentoCompletoDTO> getTipo(String filtro){
        return medicamentoDAL.getTipo(filtro);
    }
}

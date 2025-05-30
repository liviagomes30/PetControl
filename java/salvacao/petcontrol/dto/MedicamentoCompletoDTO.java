package salvacao.petcontrol.dto;

import salvacao.petcontrol.model.MedicamentoModel;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.model.UnidadeMedidaModel;

public class MedicamentoCompletoDTO {
    private ProdutoModel produto;
    private MedicamentoModel medicamento;
    private TipoProdutoModel tipoProduto;
    private UnidadeMedidaModel unidadeMedida;

    public MedicamentoCompletoDTO() {
    }

    public MedicamentoCompletoDTO(ProdutoModel produto, MedicamentoModel medicamento,
                                  TipoProdutoModel tipoProduto, UnidadeMedidaModel unidadeMedida) {
        this.produto = produto;
        this.medicamento = medicamento;
        this.tipoProduto = tipoProduto;
        this.unidadeMedida = unidadeMedida;
    }

    public ProdutoModel getProduto() {
        return produto;
    }

    public void setProduto(ProdutoModel produto) {
        this.produto = produto;
    }

    public MedicamentoModel getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(MedicamentoModel medicamento) {
        this.medicamento = medicamento;
    }

    public TipoProdutoModel getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProdutoModel tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public UnidadeMedidaModel getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedidaModel unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }
}
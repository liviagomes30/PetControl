class MedicamentoModel {
  constructor(data = {}) {
    this.produto = {
      idproduto: data.produto?.idproduto || null,
      nome: data.produto?.nome || "",
      idtipoproduto: data.produto?.idtipoproduto || null,
      idunidademedida: data.produto?.idunidademedida || null,
      fabricante: data.produto?.fabricante || "",
      preco: data.produto?.preco !== undefined ? data.produto.preco : null,
      estoqueMinimo:
        data.produto?.estoqueMinimo !== undefined
          ? data.produto.estoqueMinimo
          : null,
    };

    this.medicamento = {
      idproduto: data.medicamento?.idproduto || null,
      composicao: data.medicamento?.composicao || "",
    };

    this.tipoProduto = data.tipoProduto || null;
    this.unidadeMedida = data.unidadeMedida || null;
  }

  validar() {
    const erros = {};

    if (!this.produto.nome) {
      erros.nome = "Campo obrigatório não preenchido";
    }

    if (!this.medicamento.composicao) {
      erros.composicao = "Campo obrigatório não preenchido";
    }

    if (!this.produto.idtipoproduto) {
      erros.tipoProduto = "Campo obrigatório não preenchido";
    }

    if (!this.produto.idunidademedida) {
      erros.unidadeMedida = "Campo obrigatório não preenchido";
    }

    if (this.produto.preco !== null && isNaN(parseFloat(this.produto.preco))) {
      erros.preco = "Preço deve ser um valor numérico";
    }

    if (
      this.produto.estoqueMinimo !== null &&
      isNaN(parseInt(this.produto.estoqueMinimo))
    ) {
      erros.estoqueMinimo = "Estoque mínimo deve ser um valor numérico";
    }

    return {
      valido: Object.keys(erros).length === 0,
      erros,
    };
  }

  toJSON() {
    return {
      produto: this.produto,
      medicamento: this.medicamento,
    };
  }
}

export default MedicamentoModel;

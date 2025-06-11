class TipoProdutoModel {
  constructor(idtipoproduto = null, descricao = "") {
    this.idtipoproduto = idtipoproduto;
    this.descricao = descricao;
  }

  static fromJson(json) {
    return new TipoProdutoModel(json.idtipoproduto, json.descricao);
  }

  toJson() {
    return {
      idtipoproduto: this.idtipoproduto,
      descricao: this.descricao,
    };
  }

  validate() {
    const errors = {};
    let isValid = true;

    if (!this.descricao || this.descricao.trim() === "") {
      errors.descricao = "A descrição é obrigatória.";
      isValid = false;
    } else if (this.descricao.length > 100) {
      errors.descricao = "A descrição não pode ter mais de 100 caracteres.";
      isValid = false;
    }

    return {
      isValid,
      errors,
    };
  }

  clone() {
    return new TipoProdutoModel(this.idtipoproduto, this.descricao);
  }

  clear() {
    this.idtipoproduto = null;
    this.descricao = "";
  }

  update(data) {
    if (data.idtipoproduto !== undefined)
      this.idtipoproduto = data.idtipoproduto;
    if (data.descricao !== undefined) this.descricao = data.descricao;
  }

  hasValidId() {
    return (
      this.idtipoproduto !== null &&
      this.idtipoproduto !== undefined &&
      this.idtipoproduto > 0
    );
  }

  equals(other) {
    if (!(other instanceof TipoProdutoModel)) return false;

    return (
      this.idtipoproduto === other.idtipoproduto &&
      this.descricao === other.descricao
    );
  }
}

export default TipoProdutoModel;

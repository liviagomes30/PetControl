// UnidadeMedidaModel.js
class UnidadeMedidaModel {
  constructor(idunidademedida = null, descricao = "", sigla = "") {
    this.idunidademedida = idunidademedida;
    this.descricao = descricao;
    this.sigla = sigla;
  }

  static fromJson(json) {
    return new UnidadeMedidaModel(
      json.idunidademedida,
      json.descricao,
      json.sigla
    );
  }

  toJson() {
    return {
      idunidademedida: this.idunidademedida,
      descricao: this.descricao,
      sigla: this.sigla,
    };
  }

  validate() {
    const errors = {};
    let isValid = true;

    // Validar descrição
    if (!this.descricao || this.descricao.trim() === "") {
      errors.descricao = "A descrição é obrigatória.";
      isValid = false;
    } else if (this.descricao.length > 100) {
      errors.descricao = "A descrição não pode ter mais de 100 caracteres.";
      isValid = false;
    }

    // Validar sigla
    if (!this.sigla || this.sigla.trim() === "") {
      errors.sigla = "A sigla é obrigatória.";
      isValid = false;
    } else if (this.sigla.length > 10) {
      errors.sigla = "A sigla não pode ter mais de 10 caracteres.";
      isValid = false;
    }

    return {
      isValid,
      errors,
    };
  }

  clone() {
    return new UnidadeMedidaModel(
      this.idunidademedida,
      this.descricao,
      this.sigla
    );
  }

  clear() {
    this.idunidademedida = null;
    this.descricao = "";
    this.sigla = "";
  }

  update(data) {
    if (data.idunidademedida !== undefined)
      this.idunidademedida = data.idunidademedida;
    if (data.descricao !== undefined) this.descricao = data.descricao;
    if (data.sigla !== undefined) this.sigla = data.sigla;
  }

  hasValidId() {
    return (
      this.idunidademedida !== null &&
      this.idunidademedida !== undefined &&
      this.idunidademedida > 0
    );
  }

  equals(other) {
    if (!(other instanceof UnidadeMedidaModel)) return false;

    return (
      this.idunidademedida === other.idunidademedida &&
      this.descricao === other.descricao &&
      this.sigla === other.sigla
    );
  }
}

export default UnidadeMedidaModel;

class EntradaProdutoModel {
    constructor(data = {}) {
        this.idprod = data.idprod;
        this.idusu = data.idusu || null;
        this.quantidade = data.quantidade;
        this.fornecedor = data.fornecedor || "";
        this.observacao = data.observacao || "";
        this.date = data.date || null;
    }

}
export default EntradaProdutoModel;
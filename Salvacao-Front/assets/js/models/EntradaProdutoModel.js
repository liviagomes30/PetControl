class EntradaProdutoModel {
    constructor(data = {}) {
        this.usuarioId = data.usuario_pessoa_id || 1; // Default para testes
        this.observacao = data.observacao || "";
        this.data = data.data || null;
        this.itens = data.itens || [];
    }

}
export default EntradaProdutoModel;
// AcertoEstoqueModel.js
class AcertoEstoqueModel {
  constructor(data = {}) {
    this.usuario_pessoa_id = data.usuario_pessoa_id || 1; // Default para testes
    this.motivo = data.motivo || "";
    this.observacao = data.observacao || "";
    this.itens = data.itens || [];
  }

  validar() {
    const erros = {};

    // Validar campos obrigatórios do acerto
    if (!this.motivo) {
      erros.motivo = "O motivo do acerto é obrigatório";
    }

    // Validar itens
    if (!this.itens || this.itens.length === 0) {
      erros.itens = "É necessário adicionar pelo menos um item ao acerto";
    } else {
      // Verificar se há produtos repetidos
      const produtosIds = this.itens.map((item) => item.produto_id);
      const produtosUnicos = new Set(produtosIds);

      if (produtosIds.length !== produtosUnicos.size) {
        erros.itens =
          "Existem produtos duplicados. Cada produto deve aparecer apenas uma vez.";
      }

      // Verificar se as quantidades são válidas
      for (let i = 0; i < this.itens.length; i++) {
        const item = this.itens[i];

        if (
          item.quantidade_nova === undefined ||
          item.quantidade_nova === null
        ) {
          erros[`item_${i}`] = "A nova quantidade é obrigatória";
        } else if (isNaN(parseFloat(item.quantidade_nova))) {
          erros[`item_${i}`] = "A nova quantidade deve ser um número válido";
        } else if (parseFloat(item.quantidade_nova) < 0) {
          erros[`item_${i}`] = "A nova quantidade não pode ser negativa";
        }
      }
    }

    return {
      valido: Object.keys(erros).length === 0,
      erros,
    };
  }

  toJSON() {
    return {
      usuario_pessoa_id: this.usuario_pessoa_id,
      motivo: this.motivo,
      observacao: this.observacao,
      itens: this.itens.map((item) => ({
        produto_id: item.produto_id,
        quantidade_nova: parseFloat(item.quantidade_nova),
      })),
    };
  }
}

export default AcertoEstoqueModel;

class ProdutoService {
  constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/produtos";
  }

  async listarTodosComEstoque() {
    try {
      const response = await fetch(`${this.baseUrl}/estoque`);
      if (response.status === 404) return [];
      if (!response.ok)
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar produtos com estoque:", error);
      throw error;
    }
  }

  // **CORREÇÃO APLICADA AQUI**: O método de cadastro agora só aceita um argumento
  async cadastrar(produtoData) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/cadastro`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(produtoData),
      });
      if (!response.ok) throw new Error(await response.text());
      return await response.json();
    } catch (error) {
      console.error("Erro ao cadastrar produto:", error);
      throw error;
    }
  }

  async buscarPorId(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`);
      if (response.status === 404) return null;
      if (!response.ok)
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar produto por ID:", error);
      throw error;
    }
  }

  async atualizar(id, produtoData) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(produtoData),
      });
      if (!response.ok) throw new Error(await response.text());
      return { success: true };
    } catch (error) {
      console.error("Erro ao atualizar produto:", error);
      throw error;
    }
  }

  async excluir(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
        method: "DELETE",
      });

      const resultado = await response.json();

      if (!response.ok) {
        throw new Error(resultado.mensagem || `Erro ${response.status}`);
      }

      return resultado;
    } catch (error) {
      console.error(`Erro ao excluir produto ${id}:`, error);
      throw error;
    }
  }

  async listarTodosInativos() {
    try {
      // Assumindo que o endpoint para inativos seja /produtos/inativos
      const response = await fetch(`${this.baseUrl}${this.endpoint}/inativos`);
      if (response.status === 404) return [];
      if (!response.ok)
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar produtos inativos:", error);
      throw error;
    }
  }

  async reativar(id) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.endpoint}/${id}/reativar`,
        {
          method: "PUT",
        }
      );
      if (!response.ok) throw new Error(await response.text());
      return { success: true, message: "Produto reativado com sucesso!" };
    } catch (error) {
      console.error(`Erro ao reativar produto ${id}:`, error);
      throw error;
    }
  }
}

export default ProdutoService;

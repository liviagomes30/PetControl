class TipoProdutoService {
  constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/tipos-produto";
  }

  async listarTodos() {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`);
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar tipos de produto:", error);
      throw error;
    }
  }

  async buscarPorId(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`);
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Erro ao buscar tipo de produto ${id}:`, error);
      throw error;
    }
  }

  async cadastrar(tipoProdutoData) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(tipoProdutoData),
      });
      if (!response.ok) throw new Error(await response.text());
      return await response.json();
    } catch (error) {
      console.error("Erro ao cadastrar tipo de produto:", error);
      throw error;
    }
  }

  async atualizar(id, tipoProdutoData) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(tipoProdutoData),
      });
      if (!response.ok) throw new Error(await response.text());
      return await response.text();
    } catch (error) {
      console.error(`Erro ao atualizar tipo de produto ${id}:`, error);
      throw error;
    }
  }

  async excluir(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
        method: "DELETE",
      });
      if (!response.ok) throw new Error(await response.text());
      return true;
    } catch (error) {
      console.error(`Erro ao excluir tipo de produto ${id}:`, error);
      throw error;
    }
  }
}

export default TipoProdutoService;

class UnidadeMedidaService {
  constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/unidades-medida";
  }

  async getAll() {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`);
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar unidades de medida:", error);
      throw error;
    }
  }

  async getById(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`);
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Erro ao buscar unidade de medida ${id}:`, error);
      throw error;
    }
  }

  async create(unidadeMedidaData) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(unidadeMedidaData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao cadastrar unidade de medida:", error);
      throw error;
    }
  }

  async update(unidadeMedidaData) {
    try {
      const id = unidadeMedidaData.idunidademedida;
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(unidadeMedidaData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      return (await response.text()) || {};
    } catch (error) {
      console.error(`Erro ao atualizar unidade de medida:`, error);
      throw error;
    }
  }

  async delete(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      return true;
    } catch (error) {
      console.error(`Erro ao excluir unidade de medida ${id}:`, error);
      throw error;
    }
  }

  async search(termo) {
    try {
      try {
        const response = await fetch(
          `${this.baseUrl}${this.endpoint}/buscar?termo=${encodeURIComponent(
            termo
          )}`
        );
        if (response.ok) {
          return await response.json();
        }
        throw new Error("Endpoint de busca não encontrado");
      } catch (e) {
        const todos = await this.getAll();
        return todos.filter(
          (u) =>
            u.descricao.toLowerCase().includes(termo.toLowerCase()) ||
            u.sigla.toLowerCase().includes(termo.toLowerCase())
        );
      }
    } catch (error) {
      console.error(
        `Erro ao buscar unidades de medida com termo '${termo}':`,
        error
      );
      throw error;
    }
  }
}

export default UnidadeMedidaService;

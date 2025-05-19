class UnidadeMedidaService {
  constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/unidades-medida";
  }

  async getAll() {
    try {
      // Corrigido para usar o endpoint correto
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
      // Nota: Este endpoint pode precisar ser adaptado conforme backend
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
      // Nota: Este endpoint pode não existir no backend atual, implementando busca no cliente
      try {
        // Tentar usar API de busca se existir
        const response = await fetch(
          `${this.baseUrl}${this.endpoint}/buscar?termo=${encodeURIComponent(
            termo
          )}`
        );
        if (response.ok) {
          return await response.json();
        }
        // Se API retornar erro (404), fazer busca no cliente
        throw new Error("Endpoint de busca não encontrado");
      } catch (e) {
        // Busca no cliente
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

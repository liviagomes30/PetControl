class MedicamentoService {
  constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/medicamentos";
  }

  async listarTodos() {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/listar`);
      if (response.status === 404) {
        console.warn(
          "Nenhum medicamento encontrado (status 404). Retornando array vazio."
        );
        return [];
      }
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      const data = await response.json();
      console.log("Dados recebidos da API listarTodos:", data);
      return data;
    } catch (error) {
      console.error("Erro ao listar medicamentos:", error);
      throw error;
    }
  }

  async listarTodosDisponiveis() {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.endpoint}/disponiveis`
      );
      if (response.status === 404) {
        return []; // Retorna um array vazio se nenhum for encontrado
      }
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar medicamentos disponíveis:", error);
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
      console.error(`Erro ao buscar medicamento ${id}:`, error);
      throw error;
    }
  }

  async cadastrar(medicamentoData) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/cadastro`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(medicamentoData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao cadastrar medicamento:", error);
      throw error;
    }
  }

  async atualizar(id, medicamentoData) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(medicamentoData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      return (await response.text()) || {};
    } catch (error) {
      console.error(`Erro ao atualizar medicamento ${id}:`, error);
      throw error;
    }
  }

  async excluir(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      return await response.json();
    } catch (error) {
      console.error(`Erro ao excluir medicamento ${id}:`, error);
      throw error;
    }
  }

  async listarTiposProduto() {
    try {
      const response = await fetch(`${this.baseUrl}/tipos-produto`);
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar tipos de produto:", error);
      throw error;
    }
  }

  async listarUnidadesMedida() {
    try {
      const response = await fetch(`${this.baseUrl}/unidades-medida`);
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar unidades de medida:", error);
      throw error;
    }
  }
}

export default MedicamentoService;

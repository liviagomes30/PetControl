class MedicamentoService {
  constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/api/medicamentos";
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

  async listarTodosInativos() {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/inativos`);
      if (response.status === 404) return [];
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar medicamentos inativos:", error);
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
    console.log("=== MÉTODO CADASTRAR CHAMADO ===");
    console.log("Timestamp:", Date.now());
    console.log("Stack trace:", new Error().stack);
    try {
      console.log(
        "Dados sendo enviados para o backend:",
        JSON.stringify(medicamentoData, null, 2)
      );
      const response = await fetch(`${this.baseUrl}${this.endpoint}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(medicamentoData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error("Erro retornado pelo backend:", errorText);
        throw new Error(errorText);
      }

      const result = await response.json();
      console.log("Resposta do backend:", result);
      return result;
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

  async reativar(id) {
    try {
      // A rota de reativação está no controlador de PRODUTOS
      const response = await fetch(`${this.baseUrl}/produtos/${id}/reativar`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
      });
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }
      return await response.text();
    } catch (error) {
      console.error(`Erro ao reativar medicamento ${id}:`, error);
      throw error;
    }
  }

  async listarTiposProduto() {
    try {
      const response = await fetch(`${this.baseUrl}/tipos-produto`);
      if (response.status === 404) {
        console.warn(
          "Nenhum tipo de produto encontrado (status 404). Retornando array vazio."
        );
        return [];
      }
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
      if (response.status === 404) {
        console.warn(
          "Nenhuma unidade de medida encontrada (status 404). Retornando array vazio."
        );
        return [];
      }
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

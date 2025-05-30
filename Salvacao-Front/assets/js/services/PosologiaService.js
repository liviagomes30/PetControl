// assets/js/services/PosologiaService.js
// Este arquivo não existe, então será criado.

class PosologiaService {
  constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/posologias";
  }

  async getAll() {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`);
      if (!response.ok) {
        if (response.status === 404) return []; // No posologies found
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar posologias:", error);
      throw error;
    }
  }

  async getById(medicamentoId, receitaId) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.endpoint}/${medicamentoId}/${receitaId}`
      );
      if (!response.ok) {
        if (response.status === 404) return null; // No posology found
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error(
        `Erro ao buscar posologia ${medicamentoId}/${receitaId}:`,
        error
      );
      throw error;
    }
  }

  async create(posologiaData) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(posologiaData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Erro ao cadastrar posologia.");
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao cadastrar posologia:", error);
      throw error;
    }
  }

  async update(medicamentoId, receitaId, posologiaData) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.endpoint}/${medicamentoId}/${receitaId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(posologiaData),
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Erro ao atualizar posologia.");
      }

      return (await response.text()) || {}; // Assuming PUT might return empty body
    } catch (error) {
      console.error(
        `Erro ao atualizar posologia ${medicamentoId}/${receitaId}:`,
        error
      );
      throw error;
    }
  }

  async delete(medicamentoId, receitaId) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.endpoint}/${medicamentoId}/${receitaId}`,
        {
          method: "DELETE",
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Erro ao excluir posologia.");
      }

      return true;
    } catch (error) {
      console.error(
        `Erro ao excluir posologia ${medicamentoId}/${receitaId}:`,
        error
      );
      throw error;
    }
  }
}

export default PosologiaService;

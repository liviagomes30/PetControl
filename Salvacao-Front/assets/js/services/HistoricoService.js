// assets/js/services/HistoricoService.js

class HistoricoService {
  constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/historicos";
  }

  async getAll() {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`);
      if (!response.ok) {
        if (response.status === 404) return []; // Retorna array vazio se não houver históricos
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar históricos:", error);
      throw error;
    }
  }

  async getId(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`);
      if (!response.ok) {
        if (response.status === 404) return null; // Retorna null se histórico não encontrado
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Erro ao buscar histórico ${id}:`, error);
      throw error;
    }
  }

  async getByAnimal(idAnimal) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.endpoint}/animal/${idAnimal}`
      );
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Erro ao buscar histórico por animal ${idAnimal}:`, error);
      throw error;
    }
  }

  async getByPeriodo(dataInicio, dataFim) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.endpoint}/periodo?dataInicio=${dataInicio}&dataFim=${dataFim}`
      );
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Erro ao buscar histórico por período:`, error);
      throw error;
    }
  }

  // Adicione outros métodos conforme necessário, baseando-se no HistoricoService.java
}

export default HistoricoService;

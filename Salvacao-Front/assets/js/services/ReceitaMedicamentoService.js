class ReceitaMedicamentoService {
  constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/medicacoes";
  }

  async getAll() {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`);
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar receitas de medicamento:", error);
      throw error;
    }
  }

  async getById(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`);
      if (!response.ok) {
        if (response.status === 404) return null;
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Erro ao buscar receita de medicamento ${id}:`, error);
      throw error;
    }
  }

  async listarReceitasPorAnimal(animalId) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.endpoint}/animal/${animalId}`
      );
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error(
        `Erro ao listar receitas para o animal ${animalId}:`,
        error
      );
      throw error;
    }
  }
}

export default ReceitaMedicamentoService;

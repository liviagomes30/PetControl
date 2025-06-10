class MedicacaoService {
  constructor() {
    this.baseUrl = "http://localhost:8080";
    this.endpoint = "/medicacoes";
  }

  async listarTodos() {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`);
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar todas as medicações:", error);
      throw error;
    }
  }

  async listarTodosDisponiveis() {
    try {
      const response = await fetch(`${this.baseUrl}/medicamentos/disponiveis`);
      if (response.status === 404) {
        return [];
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

  async listarReceitasPorAnimal(animalId) {
    try {
      const response = await fetch(
        `${this.baseUrl}/receitas-medicamento/animal/${animalId}`
      );
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar receitas por animal:", error);
      throw error;
    }
  }

  // ===== MÉTODO APAGAR ADICIONADO NOVAMENTE =====
  async apagar(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
        method: "DELETE",
      });

      // Retorna a resposta para que o controller possa verificar se foi bem-sucedido (response.ok)
      return response;
    } catch (error) {
      console.error(`Erro ao apagar medicação ${id}:`, error);
      throw error;
    }
  }
}

export default MedicacaoService;

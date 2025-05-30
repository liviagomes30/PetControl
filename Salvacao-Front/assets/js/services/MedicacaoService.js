class MedicacaoService {
  constructor() {
    this.baseUrl = "http://localhost:8080";
    this.endpoint = "/medicacoes";
  }

  // NOVO MÉTODO: Para listar todas as medicações
  async listarTodos() {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`); // Chamada GET para /medicacoes
      if (!response.ok) {
        if (response.status === 404) return []; // Se 404, retorna array vazio (sem dados)
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar todas as medicações:", error);
      throw error;
    }
  }

  async listarReceitasPorAnimal(animalId) {
    try {
      const response = await fetch(
        `${this.baseUrl}/receitas-medicamento/animal/${animalId}`
      );
      if (!response.ok) {
        if (response.status === 404) return []; // No recipes found
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar receitas por animal:", error);
      throw error;
    }
  }

  async buscarPosologia(medicamentoId, receitaId) {
    try {
      const response = await fetch(
        `${this.baseUrl}/posologias/${medicamentoId}/${receitaId}`
      );
      if (!response.ok) {
        if (response.status === 404) return null; // No posology found
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar posologia:", error);
      throw error;
    }
  }

  async obterEstoqueAtual(produtoId) {
    try {
      const response = await fetch(
        `${this.baseUrl}/estoque/produto/${produtoId}`
      );
      if (!response.ok) {
        if (response.status === 404) return { quantidade: 0 };
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Erro ao obter estoque do produto ${produtoId}:`, error);
      throw error;
    }
  }

  async efetuarMedicacao(data) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/efetuar`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao efetuar medicação:", error);
      throw error;
    }
  }

  // NOVO MÉTODO: Para apagar uma medicação (reaproveitado de MedicacaoService.java)
  async apagar(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
        method: "DELETE",
      });

      // Retorna a resposta, pois o controller verificará se response.ok
      return response;
    } catch (error) {
      console.error(`Erro ao excluir medicação ${id}:`, error);
      throw error;
    }
  }
}

export default MedicacaoService;

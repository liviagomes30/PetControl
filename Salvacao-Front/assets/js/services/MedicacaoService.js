class MedicacaoService {
  constructor() {
    this.baseUrl = "http://localhost:8080";
    this.medicacaoEndpoint = "/medicacoes";
    this.receituarioEndpoint = "/api/receituario";
  }

  // ==================== MEDICACAO ENDPOINTS ====================
  
  async listarTodos() {
    try {
      const response = await fetch(`${this.baseUrl}${this.medicacaoEndpoint}`);
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

  async buscarPorId(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.medicacaoEndpoint}/${id}`);
      if (!response.ok) {
        if (response.status === 404) return null;
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Erro ao buscar medicação ${id}:`, error);
      throw error;
    }
  }

  async apagar(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.medicacaoEndpoint}/${id}`, {
        method: "DELETE",
      });
      return response;
    } catch (error) {
      console.error(`Erro ao apagar medicação ${id}:`, error);
      throw error;
    }
  }

  async buscarPorAnimal(animalId) {
    try {
      const response = await fetch(`${this.baseUrl}${this.medicacaoEndpoint}/animal/${animalId}`);
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Erro ao buscar medicações por animal ${animalId}:`, error);
      throw error;
    }
  }

  async buscarPorComposicao(searchTerm) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.medicacaoEndpoint}/search-by-composicao?searchTerm=${encodeURIComponent(searchTerm)}`
      );
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar medicações por composição:", error);
      throw error;
    }
  }

  async efetuarMedicacaoEmLote(batchRequest) {
    try {
      const response = await fetch(`${this.baseUrl}${this.medicacaoEndpoint}/efetuar-lote`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(batchRequest),
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Erro ${response.status}: ${errorText}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao efetuar medicação em lote:", error);
      throw error;
    }
  }

  // ==================== RECEITUARIO ENDPOINTS ====================

  async cadastrarReceita(receita) {
    try {
      const response = await fetch(`${this.baseUrl}${this.receituarioEndpoint}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(receita),
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Erro ${response.status}: ${errorText}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao cadastrar receita:", error);
      throw error;
    }
  }

  async listarTodasReceitas() {
    try {
      const response = await fetch(`${this.baseUrl}${this.receituarioEndpoint}`);
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar todas as receitas:", error);
      throw error;
    }
  }

  async buscarReceitaPorId(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.receituarioEndpoint}/${id}`);
      if (!response.ok) {
        if (response.status === 404) return null;
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Erro ao buscar receita ${id}:`, error);
      throw error;
    }
  }

  async alterarReceita(id, receita) {
    try {
      const response = await fetch(`${this.baseUrl}${this.receituarioEndpoint}/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(receita),
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Erro ${response.status}: ${errorText}`);
      }
      return await response.text();
    } catch (error) {
      console.error(`Erro ao alterar receita ${id}:`, error);
      throw error;
    }
  }

  async apagarReceita(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.receituarioEndpoint}/${id}`, {
        method: "DELETE",
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Erro ${response.status}: ${errorText}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Erro ao apagar receita ${id}:`, error);
      throw error;
    }
  }

  async listarReceitasPorAnimal(animalId) {
    try {
      const response = await fetch(`${this.baseUrl}${this.receituarioEndpoint}/animal/${animalId}`);
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

  async buscarReceitasPorMedico(medico) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.receituarioEndpoint}/medico/${encodeURIComponent(medico)}`
      );
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar receitas por médico:", error);
      throw error;
    }
  }

  async buscarReceitasPorData(data) {
    try {
      const response = await fetch(`${this.baseUrl}${this.receituarioEndpoint}/data/${data}`);
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar receitas por data:", error);
      throw error;
    }
  }

  // ==================== POSOLOGIA ENDPOINTS ====================

  async buscarPosologiasPorReceita(receitaId) {
    try {
      const response = await fetch(`${this.baseUrl}${this.receituarioEndpoint}/${receitaId}/posologias`);
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar posologias por receita:", error);
      throw error;
    }
  }

  async buscarPosologiasPorMedicamento(medicamentoId) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.receituarioEndpoint}/medicamento/${medicamentoId}/posologias`
      );
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar posologias por medicamento:", error);
      throw error;
    }
  }

  async buscarMedicamentosDaReceita(receitaId) {
    try {
      const response = await fetch(`${this.baseUrl}${this.receituarioEndpoint}/${receitaId}/medicamentos`);
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar medicamentos da receita:", error);
      throw error;
    }
  }

  // ==================== SUPPORT ENDPOINTS ====================

  async buscarAnimais() {
    try {
      const response = await fetch(`${this.baseUrl}${this.receituarioEndpoint}/animais`);
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar animais:", error);
      throw error;
    }
  }

  async buscarMedicamentos() {
    try {
      const response = await fetch(`${this.baseUrl}${this.receituarioEndpoint}/medicamentos`);
      if (!response.ok) {
        if (response.status === 404) return [];
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar medicamentos:", error);
      throw error;
    }
  }

  // ==================== LEGACY METHODS (for backward compatibility) ====================

  async listarTodosDisponiveis() {
    try {
      const response = await fetch(`${this.baseUrl}/api/medicamentos`);
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
}

export default MedicacaoService;

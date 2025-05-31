class ReceituarioService {
  async cadastrar(receituario) {
    try {
      const response = await fetch(`${API_BASE_URL}/api/receituario`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(receituario),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Erro ao cadastrar receituário");
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao cadastrar receituário:", error);
      throw error;
    }
  }

  async listarTodos() {
    try {
      const response = await fetch(`${API_BASE_URL}/api/receituario`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error("Erro ao buscar receituários");
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao listar receituários:", error);
      throw error;
    }
  }

  async buscarPorId(id) {
    try {
      const response = await fetch(`${API_BASE_URL}/api/receituario/${id}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        if (response.status === 404) {
          throw new Error("Receituário não encontrado");
        }
        throw new Error("Erro ao buscar receituário");
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar receituário:", error);
      throw error;
    }
  }

  async atualizar(id, receituario) {
    try {
      const response = await fetch(`${API_BASE_URL}/api/receituario/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(receituario),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Erro ao atualizar receituário");
      }

      return await response.text();
    } catch (error) {
      console.error("Erro ao atualizar receituário:", error);
      throw error;
    }
  }

  async excluir(id) {
    try {
      const response = await fetch(`${API_BASE_URL}/api/receituario/${id}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Erro ao excluir receituário");
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao excluir receituário:", error);
      throw error;
    }
  }

  async buscarPorAnimal(animalId) {
    try {
      if (!animalId || isNaN(parseInt(animalId))) {
        throw new Error("ID do animal deve ser um número válido");
      }

      const response = await fetch(
        `${API_BASE_URL}/api/receituario/animal/${parseInt(animalId)}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        if (response.status === 404) {
          return [];
        }
        throw new Error("Erro ao buscar receituários por animal");
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar receituários por animal:", error);
      throw error;
    }
  }

  async buscarPorMedico(medico) {
    try {
      const response = await fetch(
        `${API_BASE_URL}/api/receituario/medico/${encodeURIComponent(medico)}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        if (response.status === 404) {
          return [];
        }
        throw new Error("Erro ao buscar receituários por médico");
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar receituários por médico:", error);
      throw error;
    }
  }

  async buscarPorData(data) {
    try {

      
      const response = await fetch(
        `${API_BASE_URL}/api/receituario/data/${data}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        if (response.status === 404) {
          return [];
        }
        throw new Error("Erro ao buscar receituários por data");
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar receituários por data:", error);
      throw error;
    }
  }

  async buscarPosologiasPorReceita(receitaId) {
    try {
      const response = await fetch(
        `${API_BASE_URL}/api/receituario/${receitaId}/posologias`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        throw new Error("Erro ao buscar posologias");
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar posologias:", error);
      throw error;
    }
  }

  async buscarPosologiasPorMedicamento(medicamentoId) {
    try {
      const response = await fetch(
        `${API_BASE_URL}/api/receituario/medicamento/${medicamentoId}/posologias`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        throw new Error("Erro ao buscar posologias por medicamento");
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar posologias por medicamento:", error);
      throw error;
    }
  }

  async buscarAnimais() {
    try {
      const response = await fetch(`${API_BASE_URL}/api/receituario/animais`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error("Erro ao buscar animais");
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar animais:", error);
      throw error;
    }
  }

  async buscarMedicamentos() {
    try {
      const response = await fetch(
        `${API_BASE_URL}/api/receituario/medicamentos`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        throw new Error("Erro ao buscar medicamentos");
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar medicamentos:", error);
      throw error;
    }
  }
}

export default ReceituarioService;

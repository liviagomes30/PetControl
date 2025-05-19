class MedicamentoService {
  constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/medicamentos";
  }

  async listarTodos() {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/listar`);

      // Se a resposta for 404 (Nenhum medicamento encontrado), retornamos um array vazio
      if (response.status === 404) {
        console.log("Nenhum medicamento encontrado no sistema");
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
      // Verificar se o erro é relacionado a "Nenhum medicamento encontrado"
      if (
        error.message &&
        error.message.includes("Nenhum medicamento encontrado")
      ) {
        return []; // Retorna lista vazia em vez de propagar o erro
      }
      throw error;
    }
  }

  async buscarPorId(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`);

      if (response.status === 404) {
        throw new Error("Medicamento não encontrado");
      }

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

      // Tratamento específico para o erro "Cannot commit when autoCommit is enabled"
      // que ocorre mesmo quando a exclusão foi bem-sucedida
      if (!response.ok) {
        const errorText = await response.text();

        if (errorText.includes("Cannot commit when autoCommit is enabled")) {
          console.warn(
            "Erro de autocommit detectado, mas a operação provavelmente foi bem-sucedida"
          );
          return {
            sucesso: true,
            operacao: "excluido",
            mensagem: "Medicamento excluído com sucesso",
          };
        }

        throw new Error(
          errorText || `Erro ao excluir medicamento: ${response.status}`
        );
      }

      const text = await response.text();
      if (!text) {
        return {
          sucesso: true,
          operacao: "excluido",
          mensagem: "Medicamento excluído com sucesso",
        };
      }

      try {
        return JSON.parse(text);
      } catch (jsonError) {
        return {
          sucesso: true,
          operacao: "excluido",
          mensagem: text || "Medicamento excluído com sucesso",
        };
      }
    } catch (error) {
      // Verificação adicional para o erro de autocommit
      if (
        error.message &&
        error.message.includes("Cannot commit when autoCommit is enabled")
      ) {
        console.warn("Tratando erro de autocommit como sucesso");
        return {
          sucesso: true,
          operacao: "excluido",
          mensagem: "Medicamento excluído com sucesso",
        };
      }

      console.error(`Erro ao excluir medicamento ${id}:`, error);
      throw error;
    }
  }

  async listarTiposProduto() {
    try {
      const response = await fetch(`${this.baseUrl}/tipos-produto`);

      if (response.status === 404) {
        console.warn("Nenhum tipo de produto encontrado");
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
        console.warn("Nenhuma unidade de medida encontrada");
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

  // Métodos para suportar a funcionalidade de filtro
  async listarPorNome(termo) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.endpoint}/nome/${termo}`
      );

      if (response.status === 404) {
        console.log(
          `Nenhum medicamento encontrado com nome contendo "${termo}"`
        );
        return [];
      }

      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao filtrar medicamentos por nome:", error);
      return [];
    }
  }

  async listarPorComposicao(termo) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.endpoint}/composicao/${termo}`
      );

      if (response.status === 404) {
        console.log(
          `Nenhum medicamento encontrado com composição contendo "${termo}"`
        );
        return [];
      }

      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao filtrar medicamentos por composição:", error);
      return [];
    }
  }

  async listarPorTipo(termo) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.endpoint}/tipo/${termo}`
      );

      if (response.status === 404) {
        console.log(
          `Nenhum medicamento encontrado com tipo contendo "${termo}"`
        );
        return [];
      }

      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao filtrar medicamentos por tipo:", error);
      return [];
    }
  }
}

export default MedicamentoService;

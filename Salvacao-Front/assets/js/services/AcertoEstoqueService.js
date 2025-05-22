class AcertoEstoqueService {
  constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/acerto-estoque";
  }

  async listarTodos() {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`);

      if (response.status === 404) {
        console.log("Nenhum acerto de estoque encontrado no sistema");
        return [];
      }

      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }

      const data = await response.json();
      console.log("Dados recebidos da API listarTodos:", data);
      return data;
    } catch (error) {
      console.error("Erro ao listar acertos de estoque:", error);
      if (
        error.message &&
        error.message.includes("Nenhum acerto de estoque encontrado")
      ) {
        return [];
      }
      throw error;
    }
  }

  async buscarPorId(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`);

      if (response.status === 404) {
        throw new Error("Acerto de estoque não encontrado");
      }

      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }

      return await response.json();
    } catch (error) {
      console.error(`Erro ao buscar acerto de estoque ${id}:`, error);
      throw error;
    }
  }

  async buscarPorPeriodo(dataInicio, dataFim) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.endpoint}/periodo?dataInicio=${dataInicio}&dataFim=${dataFim}`
      );

      if (response.status === 404) {
        console.log(
          "Nenhum acerto de estoque encontrado no período especificado"
        );
        return [];
      }

      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar acertos por período:", error);
      return [];
    }
  }

  async buscarPorUsuario(usuarioId) {
    try {
      const response = await fetch(
        `${this.baseUrl}${this.endpoint}/usuario/${usuarioId}`
      );

      if (response.status === 404) {
        console.log(
          `Nenhum acerto de estoque encontrado para o usuário ${usuarioId}`
        );
        return [];
      }

      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }

      return await response.json();
    } catch (error) {
      console.error(`Erro ao buscar acertos do usuário ${usuarioId}:`, error);
      return [];
    }
  }

  async efetuarAcerto(acertoData) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(acertoData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao efetuar acerto de estoque:", error);
      throw error;
    }
  }

  async listarProdutos() {
    try {
      const response = await fetch(`${this.baseUrl}/produtos/listar`);

      if (response.status === 404) {
        console.log("Nenhum produto encontrado no sistema");
        return [];
      }

      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao listar produtos:", error);
      return [];
    }
  }

  async obterEstoqueAtual(produtoId) {
    try {
      const response = await fetch(
        `${this.baseUrl}/estoque/produto/${produtoId}`
      );

      if (response.status === 404) {
        return { quantidade: 0 };
      }

      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }

      return await response.json();
    } catch (error) {
      console.error(`Erro ao obter estoque do produto ${produtoId}:`, error);
      return { quantidade: 0 };
    }
  }
}

export default AcertoEstoqueService;

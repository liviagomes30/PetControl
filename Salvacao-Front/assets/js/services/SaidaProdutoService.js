class EntradaProdutoService{
    constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/saida-produto";
  }

  async listarProdutos() {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`,{
        method: "GET"});
      if (!response.ok) {
        if (response.status == 404) {
            return []; // Retorna array vazio pro renderizarTabela funcionar
        }
        else
            throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      const data = await response.json();
      console.log("Dados recebidos da API listarTodos:", data);
      return data;
    } catch (error) {
      console.error("Erro ao listar produtos:", error);
      throw error;
    }
  }

  async listarTodosRegistros(){
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/registro`,{
        method: "GET"});
      if (!response.ok) {
        if (response.status == 404) {
            return []; // Retorna array vazio pro renderizarTabela funcionar
        }
        else
            throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      const data = await response.json();
      console.log("Dados recebidos da API listarTodosRegistros:", data);
      return data;
    } catch (error) {
      console.error("Erro ao listar registros:", error);
      throw error;
    }
  }

  async listarItens(id){
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/itens/${id}`,{
        method: "GET"});
      if (!response.ok) {
        if (response.status == 404) {
            return []; // Retorna array vazio pro renderizarTabela funcionar
        }
        else
            throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      const data = await response.json();
      console.log("Dados recebidos da API listarItens:", data);
      return data;
    } catch (error) {
      console.error("Erro ao listar Itens:", error);
      throw error;
    }
  }

  async obterEstoque(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/estoque/${id}`,{
        method: "GET"});

      if (!response.ok) {
          throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      const data = await response.json();
      console.log("Dados recebidos da API obterEstoque:", data);
      return data;
    } catch (error) {
        console.error("Erro ao obter estoque:", error);
        throw error;
    }
  }

  async cadastrar(registroData) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(registroData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao cadastrar registro:", error);
      throw error;
    }
  }

  async excluir(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
        method: "DELETE"
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      return response;
    } catch (error) {
      console.error("Erro ao Excluir registro: ", error);
      throw error;
    }
  }

  async buscarPorPeriodo(dataInicio, dataFim) {
    try {
        const response = await fetch(
        `${this.baseUrl}${this.endpoint}/periodo?dataInicio=${dataInicio}&dataFim=${dataFim}`
      );

      if (response.status === 400) {
        const msg = await response.text();
        console.error("Erro de validação: ", msg);
        UIComponents.Toast.erro(`Erro: ${msg}`);
        return [];
      }

      if (response.status === 404) {
        const msg = await response.text();
        console.warn("Nenhum registro encontrado: ", msg);
      //  UIComponents.Toast.aviso("Nenhum registro encontrado no período.");
        return [];
      }

      if (!response.ok) {
        const msg = await response.text();
        throw new Error(`Erro ${response.status}: ${msg}`);
      }

      return await response.json();

    } catch (error) {
      console.error("Erro ao buscar registros por período:", error);
      UIComponents.Toast.erro(
        `Erro ao buscar registros: ${error.message || "Erro desconhecido"}`
      );
      return [];
    }
  }
}

export default EntradaProdutoService;
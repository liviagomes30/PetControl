class EntradaProdutoService{
    constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/entrada-produto";
  }

  async listarTodos() {
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

  async listarPorNome(filtro) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/nome/${filtro}`,{
        method: "GET"});

      if (!response.ok) {
        if (response.status == 404) {
            return []; // Retorna array vazio pro renderizarTabela funcionar
        }
        else
            throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      const data = await response.json();
      console.log("Dados recebidos da API listarPorNome:", data);
      return data;
    } catch (error) {
        if (response.status === 404) {
            console.log("Erro VVVVV 404");
            return []; // Retorna array vazio pro renderizarTabela funcionar
        }
        else{
            console.error("Erro ao listar produtos:", error);
            throw error;
        }
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

}

export default EntradaProdutoService;
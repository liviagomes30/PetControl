class PessoaService{
    constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/pessoa";
  }

  async listarTodos() {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/listar`);
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      const data = await response.json();
      console.log("Dados recebidos da API listarTodos:", data);
      return data;
    } catch (error) {
      console.error("Erro ao listar medicamentos:", error);
      throw error;
    }
  }

  async buscarPorId(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`);
      if (!response.ok) {
        throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Erro ao buscar medicamento ${id}:`, error);
      throw error;
    }
  }

  async cadastrar(Pessoadata) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/cadastro`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(Pessoadata),
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

  async atualizar(pessoaData) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/alterar`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(pessoaData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      return (await response.text()) || {};
    } catch (error) {
      console.error(`Erro ao atualizar adotante ${id}:`, error);
      throw error;
    }
  }

  async excluir(cpf) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${cpf}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      return await response.json();
    } catch (error) {
      console.error(`Erro ao excluir medicamento ${cpf}:`, error);
      throw error;
    }
  }
}

export default PessoaService;
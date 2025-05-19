class AnimalService {
    constructor(baseUrl = "http://localhost:8080") {
    this.baseUrl = baseUrl;
    this.endpoint = "/animais";
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
      console.error("Erro ao listar animais:", error);
      throw error;
    }
  }

  async listarPorNome(filtro) {
    console.log("S: "+filtro);
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
            console.error("Erro ao listar animais:", error);
            throw error;
        }
    }
  }

  async listarPorEspecie(filtro) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/especie/${filtro}`,{
        method: "GET"});
      if (!response.ok) {
        if (response.status == 404) {
            return []; // Retorna array vazio pro renderizarTabela funcionar
        }
        else
            throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      const data = await response.json();
      console.log("Dados recebidos da API listarPorEspecie:", data);
      return data;
    } catch (error) {
      console.error("Erro ao listar animais:", error);
      throw error;
    }
  }

  async listarPorRaca(filtro) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/raca/${filtro}`,{
        method: "GET"});
      if (!response.ok) {
        if (response.status == 404) {
            return []; // Retorna array vazio pro renderizarTabela funcionar
        }
        else
            throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      const data = await response.json();
      console.log("Dados recebidos da API listarPorRaca:", data);
      return data;
    } catch (error) {
      console.error("Erro ao listar animais:", error);
      throw error;
    }
  }

  async cadastrar(animalData) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(animalData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao cadastrar animal:", error);
      throw error;
    }
  }

  async getId(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`,{
        method: "GET"});
      if (!response.ok) {
        if (response.status == 404) {
            return []; // Retorna array vazio pro renderizarTabela funcionar
        }
        else
            throw new Error(`Erro ${response.status}: ${await response.text()}`);
      }
      const data = await response.json();
      console.log("Dados recebidos da API listarPorID:", data);
      return data;
    } catch (error) {
      console.error("Erro ao mostrar animail:", error);
      throw error;
    }
  }

  async atualizar(animal) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(animal),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      return (await response.text()) || {};
    } catch (error) {
      console.error(`Erro ao atualizar animal:`, error);
      throw error;
    }
  }

  async excluir(id) {
    try {
      const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      return await response;
    } catch (error) {
      console.error(`Erro ao excluir animal ${id}:`, error);
      throw error;
    }
  }

}

export default AnimalService;
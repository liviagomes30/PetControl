import ApiService from "./ApiService.js";

class AnimalService extends ApiService {
  constructor() {
    super("/animais");
  }

  async listarTodos() {
    return this.get("/listar");
  }

  async buscarPorId(id) {
    return this.get(`/buscar/${id}`);
  }

  async buscarPorNome(nome) {
    if (!nome || nome.trim() === "") {
      return Promise.resolve([]);
    }
    return this.get(`/buscarPorNome?nome=${encodeURIComponent(nome)}`);
  }

  async cadastrar(animalData) {
    return this.post("/cadastrar", animalData);
  }

  async atualizar(id, animalData) {
    return this.put(`/atualizar/${id}`, animalData);
  }

  async deletar(id) {
    return this.delete(`/deletar/${id}`);
  }
}

export default AnimalService;
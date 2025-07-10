import { API_BASE_URL } from "../config/config.js";

class UnidadeMedidaService {
  constructor() {
    this.baseUrl = `${API_BASE_URL}/unidades-medida`;
  }

  async _handleResponse(response) {
    if (!response.ok) {
      const errorBody = await response.text();
      throw new Error(errorBody || `HTTP error! status: ${response.status}`);
    }
    // Se o status for 204 (No Content), não há corpo para processar
    if (response.status === 204) {
      return null;
    }
    return response.json();
  }

  async listarTodos() {
    const response = await fetch(this.baseUrl);
    return this._handleResponse(response);
  }

  async buscarPorId(id) {
    const response = await fetch(`${this.baseUrl}/${id}`);
    return this._handleResponse(response);
  }

  // Ajustado para o endpoint do seu backend: /search/{filtro}
  async buscarPorTermo(termo) {
    if (!termo) {
      return this.listarTodos();
    }
    const response = await fetch(`${this.baseUrl}/search/${termo}`);
    return this._handleResponse(response);
  }

  async cadastrar(unidade) {
    const response = await fetch(this.baseUrl, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(unidade),
    });
    return this._handleResponse(response);
  }

  async atualizar(id, unidade) {
    const response = await fetch(`${this.baseUrl}/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(unidade),
    });
    return this._handleResponse(response);
  }

  async excluir(id) {
    const response = await fetch(`${this.baseUrl}/${id}`, {
      method: "DELETE",
    });
    // O backend retorna uma resposta de sucesso sem corpo, então tratamos de forma diferente.
    if (!response.ok) {
      const errorBody = await response.text();
      throw new Error(errorBody || `HTTP error! status: ${response.status}`);
    }
    return "Unidade de medida excluída com sucesso"; // Retorna uma mensagem de sucesso para o controller
  }
}

export default UnidadeMedidaService;

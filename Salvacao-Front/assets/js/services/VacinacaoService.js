class VacinacaoService {
    constructor(baseUrl = "http://localhost:8080") {
        this.baseUrl = baseUrl;
        this.endpoint = "/vacinacoes";
    }

    async _handleResponse(response) {
        if (!response.ok) {
            let errorData = { message: `Erro ${response.status} (${response.statusText || 'Erro desconhecido'})` };
            try {
                const backendError = await response.json();
                if (backendError && backendError.message) {
                    errorData.message = backendError.message;
                } else if (typeof backendError === 'string') {
                    errorData.message = backendError;
                }
            } catch (e) {
                console.warn("Resposta de erro não era JSON, usando statusText.", e);
            }
            throw new Error(errorData.message);
        }
        if (response.status === 204) {
            return null;
        }
        return response.json();
    }

    async create(vacinacaoData) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(vacinacaoData),
            });
            return this._handleResponse(response);
        } catch (error) {
            console.error("Erro ao registrar vacinação:", error);
            throw error;
        }
    }

    async getById(id) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
                method: "GET",
                headers: { "Content-Type": "application/json" },
            });
            return this._handleResponse(response);
        } catch (error) {
            console.error(`Erro ao buscar vacinação ${id}:`, error);
            throw error;
        }
    }

    async update(id, vacinacaoData) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(vacinacaoData),
            });
            return this._handleResponse(response);
        } catch (error) {
            console.error(`Erro ao atualizar vacinação ${id}:`, error);
            throw error;
        }
    }

    async delete(id) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
                method: "DELETE",
                headers: { "Content-Type": "application/json" },
            });
            return this._handleResponse(response);
        } catch (error) {
            console.error(`Erro ao deletar vacinação ${id}:`, error);
            throw error;
        }
    }

    async getAll() {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}`, {
                method: "GET",
                headers: { "Content-Type": "application/json" },
            });
            return this._handleResponse(response);
        } catch (error) {
            console.error("Erro ao buscar todas as vacinações:", error);
            throw error;
        }
    }

    async listarPorAnimal(idAnimal) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}/animal/${idAnimal}`);
            if (response.status === 404) return [];
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({
                    message: `Erro ${response.status} ao listar vacinações do animal.`
                }));
                throw new Error(errorData.message || `Erro ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error(`Erro ao listar vacinações para o animal ${idAnimal}:`, error);
            throw error;
        }
    }


    async delete(id) {
        try {
            // Chama DELETE /vacinacoes/{id}
            const response = await this.apiService.delete(`${id}`);
            return response;
        } catch (error) {
            console.error(`Erro ao deletar registro de vacinação com ID ${id}:`, error);
            throw error;
        }
    }
}

export default VacinacaoService;
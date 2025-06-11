class ApiService {
    constructor(endpoint, baseUrl = "http://localhost:8080") {
        this.baseUrl = baseUrl;
        this.endpoint = endpoint;
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

    async get(path = "") {
        try {
            const url = `${this.baseUrl}${this.endpoint}${path}`;
            console.log("ApiService GET:", url);
            const response = await fetch(url, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            });
            return this._handleResponse(response);
        } catch (error) {
            console.error(`Erro no GET para ${this.endpoint}${path}:`, error);
            throw error;
        }
    }

    async post(path = "", data) {
        try {
            const url = `${this.baseUrl}${this.endpoint}${path}`;
            console.log("ApiService POST:", url, data);
            const response = await fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(data),
            });
            return this._handleResponse(response);
        } catch (error) {
            console.error(`Erro no POST para ${this.endpoint}${path}:`, error);
            throw error;
        }
    }

    async put(path = "", data) {
        try {
            const url = `${this.baseUrl}${this.endpoint}${path}`;
            console.log("ApiService PUT:", url, data);
            const response = await fetch(url, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(data),
            });
            return this._handleResponse(response);
        } catch (error) {
            console.error(`Erro no PUT para ${this.endpoint}${path}:`, error);
            throw error;
        }
    }

    async delete(path = "") {
        try {
            const url = `${this.baseUrl}${this.endpoint}${path}`;
            console.log("ApiService DELETE:", url);
            const response = await fetch(url, {
                method: "DELETE",
            });
            return this._handleResponse(response);
        } catch (error) {
            console.error(`Erro no DELETE para ${this.endpoint}${path}:`, error);
            throw error;
        }
    }


}

export default ApiService;
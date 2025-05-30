// Salvacao-Front/assets/js/services/VacinaService.js
class VacinaService {
    constructor(baseUrl = "http://localhost:8080") {
        this.baseUrl = baseUrl;
        // ATENÇÃO: Ajuste o endpoint '/vacinas' conforme a definição da sua API no backend.
        this.endpoint = "/vacinas";
    }

    async create(vacinaData) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(vacinaData),
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: `Erro ${response.status} ao tentar cadastrar vacina.` }));
                throw new Error(errorData.message || `Erro ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error("Erro ao cadastrar vacina:", error);
            throw error;
        }
    }

    async getAll() {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}`);
            if (response.status === 404) { // Nenhum tipo de vacina encontrado
                return [];
            }
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: `Erro ${response.status} ao listar vacinas.` }));
                throw new Error(errorData.message || `Erro ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error("Erro ao listar vacinas:", error);
            throw error;
        }
    }

    async getById(id_vacina) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}/${id_vacina}`);
            if (response.status === 404) {
                throw new Error(`Vacina com ID ${id_vacina} não encontrada.`);
            }
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: `Erro ${response.status} ao buscar vacina.` }));
                throw new Error(errorData.message || `Erro ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error(`Erro ao buscar vacina ${id_vacina}:`, error);
            throw error;
        }
    }

    async update(id_vacina, vacinaData) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}/${id_vacina}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(vacinaData),
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: `Erro ${response.status} ao atualizar vacina.` }));
                throw new Error(errorData.message || `Erro ${response.status}`);
            }
            // Verifica se o backend retorna conteúdo JSON ou apenas status
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.indexOf("application/json") !== -1) {
                return await response.json();
            }
            return { success: true, message: "Vacina atualizada com sucesso." }; // Resposta genérica se não houver JSON
        } catch (error) {
            console.error(`Erro ao atualizar vacina ${id_vacina}:`, error);
            throw error;
        }
    }

    async delete(id_vacina) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}/${id_vacina}`, {
                method: "DELETE",
            });

            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error(`Vacina com ID ${id_vacina} não encontrada para exclusão.`);
                }
                const errorData = await response.json().catch(() => ({ message: `Erro ${response.status} ao excluir vacina.` }));
                throw new Error(errorData.message || `Erro ${response.status}`);
            }
            // Se o backend retorna status 204 (No Content) ou não retorna JSON
            if (response.status === 204) {
                return { success: true, message: "Vacina excluída com sucesso." };
            }
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.indexOf("application/json") !== -1) {
                return await response.json();
            }
            return { success: true, message: "Vacina excluída com sucesso." };
        } catch (error) {
            console.error(`Erro ao excluir vacina ${id_vacina}:`, error);
            throw error;
        }
    }
}

export default VacinaService;
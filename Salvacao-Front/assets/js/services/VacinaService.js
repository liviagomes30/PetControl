import ApiService from './ApiService.js';

class VacinaService extends ApiService {
    constructor() {
        super("/vacinas");
    }

    async getAll() {
        return this.get();
    }

    async getById(id) {
        return this.get(`/${id}`);
    }

    async create(data) {
        return this.post('', data);
    }

    async update(id, data) {
        return this.put(`/${id}`, data);
    }

    async delete(id) {
        return this.delete(`/${id}`);
    }

    async buscarPorDescricao(descricao) {
        if (!descricao || descricao.trim() === "") {
            return Promise.resolve([]);
        }
        return this.get(`/buscar?descricao=${encodeURIComponent(descricao)}`);
    }

    async getAll() {
        return this.get();
    }
}

export default VacinaService;
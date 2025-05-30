import EventoModel from "../models/EventoModel.js";

class EventoService {
    constructor() {
        this.baseURL = '/evento'; // Ajuste conforme sua configuração de backend
    }

    // Método para listar todos os eventos
    async listarTodos() {
        try {
            const response = await fetch(`${this.baseURL}/listar`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error(`Erro HTTP: ${response.status}`);
            }

            const data = await response.json();
            return EventoModel.fromJsonList(data);
        } catch (error) {
            console.error('Erro ao listar eventos:', error);
            throw new Error('Erro ao carregar lista de eventos');
        }
    }

    // Método para buscar evento por ID
    async buscarPorId(id) {
        try {
            const response = await fetch(`${this.baseURL}/${id}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error('Evento não encontrado');
                }
                throw new Error(`Erro HTTP: ${response.status}`);
            }

            const data = await response.json();
            return EventoModel.fromJson(data);
        } catch (error) {
            console.error('Erro ao buscar evento:', error);
            throw error;
        }
    }

    // Método para cadastrar novo evento
    async cadastrar(evento) {
        try {
            const response = await fetch(`${this.baseURL}/cadastro`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(evento.toJSON())
            });

            if (!response.ok) {
                const errorData = await response.text();
                throw new Error(errorData || `Erro HTTP: ${response.status}`);
            }

            const data = await response.json();
            return EventoModel.fromJson(data);
        } catch (error) {
            console.error('Erro ao cadastrar evento:', error);
            throw error;
        }
    }

    // Método para atualizar evento
    async atualizar(evento) {
        try {
            const response = await fetch(`${this.baseURL}/alterar`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(evento.toJSON())
            });

            if (!response.ok) {
                const errorData = await response.text();
                throw new Error(errorData || `Erro HTTP: ${response.status}`);
            }

            return true;
        } catch (error) {
            console.error('Erro ao atualizar evento:', error);
            throw error;
        }
    }

    // Método para excluir evento
    async excluir(id) {
        try {
            const response = await fetch(`${this.baseURL}/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                const errorData = await response.text();
                throw new Error(errorData || `Erro HTTP: ${response.status}`);
            }

            return true;
        } catch (error) {
            console.error('Erro ao excluir evento:', error);
            throw error;
        }
    }

    // Método para listar animais (para popular o select)
    async listarAnimais() {
        try {
            const response = await fetch('/animal/listar', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error(`Erro HTTP: ${response.status}`);
            }

            const data = await response.json();
            return data;
        } catch (error) {
            console.error('Erro ao listar animais:', error);
            throw new Error('Erro ao carregar lista de animais');
        }
    }

    // Método para filtrar eventos
    async filtrar(termo, tipoFiltro) {
        try {
            // Por enquanto, vamos fazer o filtro localmente
            // Você pode implementar um endpoint específico no backend se necessário
            const eventos = await this.listarTodos();
            
            if (!termo || termo.trim() === '') {
                return eventos;
            }

            const termoLower = termo.toLowerCase();
            
            return eventos.filter(evento => {
                switch (tipoFiltro) {
                    case '1': // Descrição
                        return evento.descricao.toLowerCase().includes(termoLower);
                    case '2': // Local
                        return evento.local.toLowerCase().includes(termoLower);
                    case '3': // Data
                        return evento.formatarData().toLowerCase().includes(termoLower);
                    default:
                        return evento.descricao.toLowerCase().includes(termoLower) ||
                               evento.local.toLowerCase().includes(termoLower);
                }
            });
        } catch (error) {
            console.error('Erro ao filtrar eventos:', error);
            throw error;
        }
    }
}

export default EventoService;
import EventoService from "../services/EventoService.js";
import EventoModel from "../models/EventoModel.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";
import UIComponents from "../components/uiComponents.js";

class EventoController {
    constructor() {
        this.service = new EventoService();
        this.eventosCache = [];
    }
    
    async inicializarListagem() {
        try {
            UIComponents.Loading.mostrar("Carregando eventos...");
            const eventos = await this.service.listarTodos();
            this.eventosCache = eventos;
            this.renderizarTabela(eventos);

            // Verificar mensagem na URL
            const urlParams = new URLSearchParams(window.location.search);
            const message = urlParams.get("message");
            if (message) {
                UIComponents.Toast.sucesso(message);
            }
        } catch (error) {
            console.error("Erro ao carregar eventos:", error);
            UIComponents.ModalErro.mostrar(MensagensPadroes.ERRO.CARREGAMENTO);
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    async renderizarTabela(eventos) {
        const tabela = document.getElementById("tabela-eventos");
        tabela.innerHTML = ""; // Limpa a tabela antes de renderizar

        if (!eventos || eventos.length === 0) {
            tabela.innerHTML = `<tr><td colspan="6">Nenhum evento encontrado.</td></tr>`;
            return;
        }

        eventos.forEach(evento => {
            const linha = document.createElement("tr");

            // Formatar data para exibição
            const dataFormatada = evento.formatarData();
            
            // Limitar descrição para exibição na tabela
            const descricaoResumida = evento.descricao.length > 50 
                ? evento.descricao.substring(0, 50) + '...' 
                : evento.descricao;

            linha.innerHTML = `
                <td title="${evento.descricao}">${descricaoResumida}</td>
                <td>${dataFormatada}</td>
                <td>${evento.local}</td>
                <td>${evento.animalNome || 'N/A'}</td>
                <td>
                    ${evento.foto 
                        ? `<img src="${evento.foto}" alt="Foto do evento" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;" onerror="this.style.display='none'">` 
                        : 'N/A'
                    }
                </td>
                <td>
                    <div class="actions">
                        <button class="btn-icon btn-edit" onclick="eventoController.iniciarEdicao(${evento.idEvento})" title="Editar">
                            ✏️
                        </button>
                        <button class="btn-icon btn-delete" onclick="eventoController.confirmarExclusao(${evento.idEvento})" title="Excluir">
                            🗑️
                        </button>
                    </div>
                </td>
            `;

            tabela.appendChild(linha);
        });
    }

    async cadastrar(event) {
        try {
            const formData = new FormData(event.target);
            
            // Criar objeto evento a partir do formulário
            const evento = new EventoModel();
            evento.descricao = document.getElementById('descricao').value.trim();
            evento.data = EventoModel.converterDataParaISO(document.getElementById('data').value);
            evento.local = document.getElementById('local').value.trim();
            evento.animalId = document.getElementById('animalId').value || null;
            evento.foto = document.getElementById('foto').value.trim() || null;

            // Validar dados
            const validacao = evento.validar();
            if (!validacao.valido) {
                this.exibirErrosValidacao(validacao.erros);
                return;
            }

            UIComponents.Loading.mostrar("Cadastrando evento...");
            
            await this.service.cadastrar(evento);
            
            // Redirecionar com mensagem de sucesso
            window.location.href = "gerenciarEvento.html?message=Evento cadastrado com sucesso!";
            
        } catch (error) {
            console.error("Erro ao cadastrar evento:", error);
            UIComponents.Toast.erro("Erro ao cadastrar evento: " + error.message);
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    async carregarAnimais() {
        try {
            const animais = await this.service.listarAnimais();
            const select = document.getElementById('animalId');
            
            // Limpar opções existentes (manter apenas a primeira)
            while (select.children.length > 1) {
                select.removeChild(select.lastChild);
            }
            
            // Adicionar animais ao select
            animais.forEach(animal => {
                const option = document.createElement('option');
                option.value = animal.idAnimal || animal.idpessoa; // Ajustar conforme estrutura do backend
                option.textContent = animal.nome;
                select.appendChild(option);
            });
            
        } catch (error) {
            console.error("Erro ao carregar animais:", error);
            UIComponents.Toast.erro("Erro ao carregar lista de animais");
        }
    }

    async filtrarEventos(termo, tipoFiltro) {
        try {
            if (!termo || termo.trim() === '') {
                this.renderizarTabela(this.eventosCache);
                return;
            }

            const eventosFiltrados = await this.service.filtrar(termo, tipoFiltro);
            this.renderizarTabela(eventosFiltrados);
            
        } catch (error) {
            console.error("Erro ao filtrar eventos:", error);
            UIComponents.Toast.erro("Erro ao filtrar eventos");
        }
    }

    async confirmarExclusao(id) {
        UIComponents.ModalConfirmacao.mostrar(
            "Confirmar exclusão",
            "Tem certeza que deseja excluir este evento? Esta ação não pode ser desfeita.",
            // Callback de confirmação
            () => {
                this.excluir(id).catch((error) => {
                    console.error("Erro ao excluir:", error);
                    UIComponents.Toast.erro(
                        "Erro ao excluir evento" +
                        (error.message ? `: ${error.message}` : "")
                    );
                });
            }
        );
    }

    async excluir(id) {
        try {
            UIComponents.Loading.mostrar("Excluindo evento...");
            
            await this.service.excluir(id);
            
            UIComponents.Toast.sucesso("Evento excluído com sucesso!");
            
            // Recarregar lista
            await this.inicializarListagem();
            
        } catch (error) {
            console.error("Erro ao excluir evento:", error);
            throw error;
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    async iniciarEdicao(id) {
        try {
            // Redirecionar para página de edição
            window.location.href = `editarEvento.html?id=${id}`;
        } catch (error) {
            console.error("Erro ao iniciar edição:", error);
            UIComponents.Toast.erro("Erro ao carregar dados para edição");
        }
    }

    async carregarDadosEdicao(id) {
        try {
            UIComponents.Loading.mostrar("Carregando dados do evento...");
            
            const evento = await this.service.buscarPorId(id);
            
            // Preencher campos do formulário
            document.getElementById('idEvento').value = evento.idEvento;
            document.getElementById('descricao').value = evento.descricao;
            document.getElementById('data').value = evento.formatarDataParaInput();
            document.getElementById('local').value = evento.local;
            document.getElementById('animalId').value = evento.animalId || '';
            document.getElementById('foto').value = evento.foto || '';
            
        } catch (error) {
            console.error("Erro ao carregar dados do evento:", error);
            throw error;
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    async atualizar(event) {
        try {
            // Criar objeto evento a partir do formulário
            const evento = new EventoModel();
            evento.idEvento = parseInt(document.getElementById('idEvento').value);
            evento.descricao = document.getElementById('descricao').value.trim();
            evento.data = EventoModel.converterDataParaISO(document.getElementById('data').value);
            evento.local = document.getElementById('local').value.trim();
            evento.animalId = document.getElementById('animalId').value || null;
            evento.foto = document.getElementById('foto').value.trim() || null;

            // Validar dados
            const validacao = evento.validar();
            if (!validacao.valido) {
                this.exibirErrosValidacao(validacao.erros);
                return;
            }

            UIComponents.Loading.mostrar("Atualizando evento...");
            
            await this.service.atualizar(evento);
            
            // Redirecionar com mensagem de sucesso
            window.location.href = "gerenciarEvento.html?message=Evento atualizado com sucesso!";
            
        } catch (error) {
            console.error("Erro ao atualizar evento:", error);
            UIComponents.Toast.erro("Erro ao atualizar evento: " + error.message);
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    exibirErrosValidacao(erros) {
        // Limpar erros anteriores
        document.querySelectorAll('.input-error').forEach(el => el.textContent = '');
        
        // Mapear erros para campos específicos
        erros.forEach(erro => {
            if (erro.includes('Descrição')) {
                document.getElementById('descricaoError').textContent = erro;
            } else if (erro.includes('Data')) {
                document.getElementById('dataError').textContent = erro;
            } else if (erro.includes('Local')) {
                document.getElementById('localError').textContent = erro;
            } else if (erro.includes('URL da foto')) {
                document.getElementById('fotoError').textContent = erro;
            }
        });
        
        // Mostrar toast com erro geral
        UIComponents.Toast.erro("Por favor, corrija os campos em destaque");
    }
}

const eventoController = new EventoController();
window.EventoController = eventoController;
export { eventoController };
export default EventoController;
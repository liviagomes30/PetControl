import AdocaoService from "../services/AdocaoService.js";
import AdocaoModel from "../models/AdocaoModel.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";
import UIComponents from "../components/uiComponents.js";

class AdocaoController {
    constructor() {
        this.service = new AdocaoService();
        this.adocoes = [];
        this.adocoesFiltradas = [];
    }

    async inicializarListagem() {
        try {
            console.log("Carregando adoções...");
            // Usa o método simples do service
            this.service.listar();

            // Verificar mensagem na URL
            const urlParams = new URLSearchParams(window.location.search);
            const message = urlParams.get("message");
            if (message) {
                console.log("Mensagem de sucesso:", message);
                // UIComponents.Toast.sucesso(message);
            }
        } catch (error) {
            console.error("Erro ao carregar adoções:", error);
            const tabela = document.getElementById("tabela-adocoes");
            tabela.innerHTML = `<tr><td colspan="8" class="text-center text-danger">Erro ao carregar dados.</td></tr>`;
        }
    }

    renderizarTabela(adocoes) {
        const tabela = document.getElementById("tabela-adocoes");
        tabela.innerHTML = ""; // Limpa a tabela antes de renderizar

        if (!adocoes || adocoes.length === 0) {
            tabela.innerHTML = `<tr><td colspan="8" class="text-center">Nenhuma adoção encontrada.</td></tr>`;
            return;
        }

        adocoes.forEach(adocao => {
            const linha = document.createElement("tr");
            linha.innerHTML = `
                <td>${adocao.idadocao}</td>
                <td>${adocao.nomeAnimal || 'N/A'}</td>
                <td>${adocao.nomeAdotante || 'N/A'}</td>
                <td>${adocao.formatarData(adocao.dataadocao)}</td>
                <td>${adocao.getBadgeStatus()}</td>
                <td>${adocao.data_acompanhamento ? adocao.formatarData(adocao.data_acompanhamento) : '-'}</td>
                <td>${adocao.obs ? (adocao.obs.length > 50 ? adocao.obs.substring(0, 50) + '...' : adocao.obs) : '-'}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1 btn-editar" data-id="${adocao.idadocao}">Editar</button>
                    <button class="btn btn-sm btn-outline-danger btn-excluir" data-id="${adocao.idadocao}">Excluir</button>
                </td>
            `;
            tabela.appendChild(linha);
        });
    }

    async receberElementos() {
        try {
            // Selecionando elementos do HTML
            const formulario = document.getElementById('form-adocao');
            const campoAnimal = document.getElementById('animal');
            const campoAdotante = document.getElementById('adotante');
            const campoDataAdocao = document.getElementById('dataAdocao');
            const campoStatus = document.getElementById('statusAcompanhamento');
            const campoDataAcompanhamento = document.getElementById('dataAcompanhamento');
            const campoObservacoes = document.getElementById('observacoes');

            // Criando objeto com os valores dos campos
            const dadosAdocao = {
                idAnimal: campoAnimal.value,
                idAdotante: campoAdotante.value,
                dataAdocao: campoDataAdocao.value,
                statusAcompanhamento: campoStatus.value,
                dataAcompanhamento: campoDataAcompanhamento.value,
                obs: campoObservacoes.value
            };

            return dadosAdocao;

        } catch (erro) {
            console.error("Erro ao receber elementos do formulário:", erro);
            throw erro;
        }
    }

    async cadastrar(event) {
        event.preventDefault();
        
        try {
            console.log("Cadastrando adoção...");

            // Capturar dados do formulário
            const adocaoData = {
                idAdotante: document.getElementById('idadotante').value,
                idAnimal: document.getElementById('idanimal').value,
                dataAdocao: document.getElementById('dataadocao').value,
                pessoaIdPessoa: document.getElementById('pessoa_idpessoa').value || null,
                obs: document.getElementById('obs').value,
                statusAcompanhamento: document.getElementById('status_acompanhamento').value,
                dataAcompanhamento: document.getElementById('data_acompanhamento').value || null
            };

            // Criar modelo e validar
            const adocao = new AdocaoModel(
                null,
                adocaoData.idAdotante,
                adocaoData.idAnimal,
                adocaoData.dataAdocao,
                adocaoData.pessoaIdPessoa,
                adocaoData.obs,
                adocaoData.statusAcompanhamento,
                adocaoData.dataAcompanhamento
            );

            const validacao = adocao.validar();
            if (!validacao.valido) {
                this.exibirErrosValidacao(validacao.erros);
                return;
            }

            // Enviar para o backend
            await this.service.cadastrar(adocao.toJSON());
            
            // Redirecionar com mensagem de sucesso
            window.location.href = 'listarAdocao.html?message=Adoção cadastrada com sucesso!';

        } catch (error) {
            console.error("Erro ao cadastrar adoção:", error);
            alert("Erro ao cadastrar adoção: " + error.message);
        }
    }

    async atualizar(event, id) {
        event.preventDefault();
        
        try {
            UIComponents.Loading.mostrar("Atualizando adoção...");

            // Capturar dados do formulário
            const adocaoData = {
                idadotante: document.getElementById('idadotante').value,
                idanimal: document.getElementById('idanimal').value,
                dataadocao: document.getElementById('dataadocao').value,
                pessoa_idpessoa: document.getElementById('pessoa_idpessoa').value || null,
                obs: document.getElementById('obs').value,
                status_acompanhamento: document.getElementById('status_acompanhamento').value,
                data_acompanhamento: document.getElementById('data_acompanhamento').value || null
            };

            // Criar modelo e validar
            const adocao = new AdocaoModel(
                id,
                adocaoData.idadotante,
                adocaoData.idanimal,
                adocaoData.dataadocao,
                adocaoData.pessoa_idpessoa,
                adocaoData.obs,
                adocaoData.status_acompanhamento,
                adocaoData.data_acompanhamento
            );

            const validacao = adocao.validar();
            if (!validacao.valido) {
                this.exibirErrosValidacao(validacao.erros);
                return;
            }

            // Enviar para o backend
            await this.service.atualizar(id, adocao.toJSON());
            
            // Redirecionar com mensagem de sucesso
            window.location.href = 'listarAdocao.html?message=Adoção atualizada com sucesso!';

        } catch (error) {
            console.error("Erro ao atualizar adoção:", error);
            UIComponents.Toast.erro("Erro ao atualizar adoção: " + error.message);
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    confirmarExclusao(id) {
        UIComponents.ModalConfirmacao.mostrar(
            "Confirmar exclusão",
            MensagensPadroes.CONFIRMACAO.EXCLUSAO,
            // Callback de confirmação
            () => {
                this.excluir(id).catch((error) => {
                    console.error("Erro ao excluir:", error);
                    UIComponents.Toast.erro(
                        MensagensPadroes.ERRO.EXCLUSAO +
                        (error.message ? `: ${error.message}` : "")
                    );
                });
            }
        );
    }

    async excluir(id) {
        try {
            UIComponents.Loading.mostrar("Excluindo adoção...");
            await this.service.excluir(id);
            UIComponents.Toast.sucesso("Adoção excluída com sucesso!");
            
            // Recarregar a listagem
            await this.inicializarListagem();
        } catch (error) {
            console.error("Erro ao excluir adoção:", error);
            throw error;
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    filtrarAdocoes(termo, filtro) {
        if (!termo) {
            this.adocoesFiltradas = [...this.adocoes];
        } else {
            this.adocoesFiltradas = this.adocoes.filter(adocao => {
                switch (filtro) {
                    case '1': // Nome do Animal
                        return adocao.nomeAnimal && adocao.nomeAnimal.toLowerCase().includes(termo.toLowerCase());
                    case '2': // Nome do Adotante
                        return adocao.nomeAdotante && adocao.nomeAdotante.toLowerCase().includes(termo.toLowerCase());
                    case '3': // Status
                        return adocao.status_acompanhamento.toLowerCase().includes(termo.toLowerCase());
                    case '4': // Data de Adoção
                        return adocao.dataadocao.includes(termo);
                    default:
                        return false;
                }
            });
        }
        
        this.renderizarTabela(this.adocoesFiltradas);
    }

    // Método para exibir erros de validação
    exibirErrosValidacao(erros) {
        // Limpar erros anteriores
        document.querySelectorAll('.input-error').forEach(el => el.textContent = '');
        
        erros.forEach(erro => {
            console.error("Erro de validação:", erro);
        });
        
        UIComponents.Toast.erro("Por favor, corrija os erros no formulário");
    }

    // Método para carregar dados de uma adoção específica (para edição)
    async carregarAdocao(id) {
        try {
            UIComponents.Loading.mostrar("Carregando dados da adoção...");
            const adocao = await this.service.buscarPorId(id);
            return AdocaoModel.fromJson(adocao);
        } catch (error) {
            console.error("Erro ao carregar adoção:", error);
            UIComponents.Toast.erro("Erro ao carregar dados da adoção");
            throw error;
        } finally {
            UIComponents.Loading.esconder();
        }
    }
}

const adocaoController = new AdocaoController();
window.AdocaoController = adocaoController;
export { adocaoController };
export default AdocaoController;
import VacinacaoModel from "../models/VacinacaoModel.js";
import VacinacaoService from "../services/VacinacaoService.js";
import VacinaService from "../services/VacinaService.js";
import AnimalService from "../services/AnimalService.js";
import UIComponents from "../components/uiComponents.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";

class VacinacaoController {
    constructor() {
        this.vacinacaoService = new VacinacaoService();
        this.vacinaTipoService = new VacinaService();
        this.animalService = new AnimalService();
        this.tiposDeVacinaMap = new Map();
        this.nomesAnimaisMap = new Map();
        this.todasVacinacoes = []; // Cache para armazenar todos os registros
        this.debounceTimer = null;
    }

    // --- MÉTODOS PARA A PÁGINA DE LISTAGEM GERAL ---

    /**
     * Ponto de entrada para a página listarVacinacoes.html.
     * Carrega todos os dados necessários e renderiza as tabelas.
     */
    async inicializarPaginaListagemGeral() {
        UIComponents.Loading.mostrar("Carregando dados...");
        try {
            // Busca todos os dados necessários em paralelo para otimizar o carregamento
            const [tiposVacina, animais, todasVacinacoes] = await Promise.all([
                this.vacinaTipoService.getAll(),
                this.animalService.listarTodos(),
                this.vacinacaoService.getAll()
            ]);

            // Mapeia os nomes e descrições para evitar buscas repetidas
            this.tiposDeVacinaMap.clear();
            tiposVacina?.forEach(vacina => {
                this.tiposDeVacinaMap.set(vacina.id_vacina, vacina.descricao_vacina);
            });

            this.nomesAnimaisMap.clear();
            animais?.forEach(animal => {
                this.nomesAnimaisMap.set(animal.id, animal.nome);
            });

            this.todasVacinacoes = todasVacinacoes || [];
            this._renderizarTabelasCompletas(this.todasVacinacoes);
            this._configurarFiltroAnimal();

        } catch (error) {
            console.error("Erro detalhado ao inicializar página:", error);
            UIComponents.ModalErro.mostrar("Erro ao inicializar página: " + error.message);
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    /**
     * Filtra e renderiza as tabelas de vacinações agendadas e realizadas.
     * @param {Array} listaDeVacinacoes - A lista de registros de vacinação a ser renderizada.
     */
    _renderizarTabelasCompletas(listaDeVacinacoes) {
        const hoje = new Date();
        hoje.setHours(0, 0, 0, 0);

        const agendadas = listaDeVacinacoes.filter(reg => {
            const dataVacinacao = reg.data_vacinacao || reg.dataVacinacao;
            return new Date(dataVacinacao) >= hoje;
        });

        const realizadas = listaDeVacinacoes.filter(reg => {
            const dataVacinacao = reg.data_vacinacao || reg.dataVacinacao;
            return new Date(dataVacinacao) < hoje;
        });

        this._renderizarTabelaGeral(agendadas, "agendadasTableBody", "noAgendadasMessage", "Nenhuma vacinação agendada encontrada.", true);
        this._renderizarTabelaGeral(realizadas, "realizadasTableBody", "noRealizadasMessage", "Nenhum histórico de vacinação encontrado.", false);
    }

    /**
     * Renderiza uma tabela específica (agendada ou realizada).
     * @param {Array} vacinacoes - Lista de vacinações para a tabela.
     * @param {string} tableBodyId - ID do corpo da tabela (tbody).
     * @param {string} noDataMessageId - ID do elemento para mensagem de "sem dados".
     * @param {string} noDataText - Texto a ser exibido quando não há dados.
     * @param {boolean} isAgendada - Flag para diferenciar a renderização de colunas.
     */
    _renderizarTabelaGeral(vacinacoes, tableBodyId, noDataMessageId, noDataText, isAgendada) {
        const tableBody = document.getElementById(tableBodyId);
        const noDataMessage = document.getElementById(noDataMessageId);

        if (!tableBody || !noDataMessage) {
            console.warn(`Elementos não encontrados: ${tableBodyId} ou ${noDataMessageId}`);
            return;
        }

        tableBody.innerHTML = "";

        if (!vacinacoes || vacinacoes.length === 0) {
            noDataMessage.textContent = noDataText;
            noDataMessage.style.display = "block";
            tableBody.style.display = "none";
            return;
        }

        noDataMessage.style.display = "none";
        tableBody.style.display = "";

        vacinacoes.sort((a, b) => new Date(b.data_vacinacao || b.dataVacinacao) - new Date(a.data_vacinacao || a.dataVacinacao));

        vacinacoes.forEach(reg => {
            const row = tableBody.insertRow();
            const dataVacinacao = reg.data_vacinacao || reg.dataVacinacao;
            const data = dataVacinacao ? new Date(dataVacinacao + 'T00:00:00Z') : null; // Adiciona T00:00:00Z para tratar como UTC
            const dataFormatada = data ? data.toLocaleDateString('pt-BR') : 'N/A';

            const idVacina = reg.id_vacina || reg.idVacina;
            const idAnimal = reg.id_animal || reg.idAnimal;
            const idVacinacao = reg.id_vacinacao || reg.idVacinacao;

            const descricaoTipoVacina = this.tiposDeVacinaMap.get(idVacina) || `ID: ${idVacina}`;
            const nomeAnimal = this.nomesAnimaisMap.get(idAnimal) || `ID: ${idAnimal}`;
            const localVacinacao = reg.local_vacinacao || reg.localVacinacao || 'N/A';

            let linhaHtml;
            if (isAgendada) {
                linhaHtml = `
                    <td>${dataFormatada}</td>
                    <td>${nomeAnimal}</td>
                    <td>${descricaoTipoVacina}</td>
                    <td>${localVacinacao}</td>
                    <td style="text-align: center;">
                        <button class="btn-icon btn-edit" onclick="window.vacinacaoGeralController.editarRegistroVacinacao(${idVacinacao})">
                            <i class="bi bi-pencil"></i>
                        </button>
                    </td>
                `;
            } else {
                const dataValidade = reg.data_validade_vacina || reg.dataValidadeVacina;
                const dataValidadeFormatada = dataValidade ? new Date(dataValidade + 'T00:00:00Z').toLocaleDateString('pt-BR') : 'N/A';
                const loteVacina = reg.lote_vacina || reg.loteVacina || 'N/A';

                linhaHtml = `
                    <td>${dataFormatada}</td>
                    <td>${nomeAnimal}</td>
                    <td>${descricaoTipoVacina}</td>
                    <td>${localVacinacao}</td>
                    <td>${loteVacina}</td>
                    <td>${dataValidadeFormatada}</td>
                    <td style="text-align: center;">
                         <button class="btn-icon btn-edit" onclick="window.vacinacaoGeralController.editarRegistroVacinacao(${idVacinacao})">
                            <i class="bi bi-pencil"></i>
                        </button>
                    </td>
                `;
            }
            row.innerHTML = linhaHtml;
        });
    }

    /**
     * Configura o campo de busca por nome de animal.
     */
    _configurarFiltroAnimal() {
        const searchInput = document.getElementById("animalSearchInput");
        if (!searchInput) return;

        searchInput.addEventListener("input", (event) => {
            clearTimeout(this.debounceTimer);
            this.debounceTimer = setTimeout(() => {
                const termo = event.target.value.toLowerCase().trim();
                const vacinacoesFiltradas = termo ? this.todasVacinacoes.filter(reg => {
                    const idAnimal = reg.id_animal || reg.idAnimal;
                    const nomeAnimal = this.nomesAnimaisMap.get(idAnimal)?.toLowerCase() || '';
                    return nomeAnimal.includes(termo);
                }) : this.todasVacinacoes;

                this._renderizarTabelasCompletas(vacinacoesFiltradas);
            }, 300);
        });
    }

    // --- MÉTODOS PARA O CADASTRO E EDIÇÃO ---

    /**
     * Inicializa o formulário de cadastro/edição e configura a busca por animal e vacina.
     */
    async inicializarFormularioCadastro() {
        this._setupSearch('animalSearchInput', 'animalSearchResults', document.getElementById('id_animal'), this._pesquisarAnimais, this._renderizarResultadosPesquisa);
        this._setupSearch('vacinaSearchInput', 'vacinaSearchResults', document.getElementById('id_vacina'), this._pesquisarVacinas, this._renderizarResultadosPesquisa);
    }

    /**
     * Configuração genérica para campos de busca com dropdown.
     */
    _setupSearch(inputId, resultsId, hiddenInput, searchFunction, renderFunction) {
        const searchInput = document.getElementById(inputId);
        const resultsDiv = document.getElementById(resultsId);

        if (!searchInput || !resultsDiv) return;

        searchInput.addEventListener("input", (event) => {
            clearTimeout(this.debounceTimer);
            if(hiddenInput) hiddenInput.value = "";

            const termo = event.target.value;
            if (termo.length < 2) {
                resultsDiv.style.display = 'none';
                return;
            }
            this.debounceTimer = setTimeout(async () => {
                const data = await searchFunction.call(this, termo);
                renderFunction(data, resultsDiv, searchInput, hiddenInput, inputId.includes('animal') ? 'animal' : 'vacina');
            }, 300);
        });

        document.addEventListener('click', (event) => {
            if (!searchInput.contains(event.target) && !resultsDiv.contains(event.target)) {
                resultsDiv.style.display = 'none';
            }
        });
    }

    async _pesquisarAnimais(termo) {
        try {
            return await this.animalService.buscarPorNome(termo);
        } catch (error) {
            UIComponents.Toast.erro("Erro ao buscar animais.");
            return [];
        }
    }

    async _pesquisarVacinas(termo) {
        try {
            return await this.vacinaTipoService.buscarPorDescricao(termo);
        } catch (error) {
            UIComponents.Toast.erro("Erro ao buscar tipos de vacina.");
            return [];
        }
    }

    _renderizarResultadosPesquisa(items, resultsDiv, searchInput, hiddenInput, type) {
        resultsDiv.innerHTML = "";
        if (items && items.length > 0) {
            resultsDiv.style.display = "block";
            items.forEach(item => {
                const divItem = document.createElement("div");
                divItem.classList.add("search-result-item");

                let text, id;
                if(type === 'animal') {
                    text = `${item.nome} (ID: ${item.id}) - ${item.especie}`;
                    id = item.id;
                } else { // vacina
                    text = `${item.descricao_vacina || item.descricaoVacina} (ID: ${item.id_vacina || item.idVacina})`;
                    id = item.id_vacina || item.idVacina;
                }

                divItem.textContent = text;
                divItem.addEventListener("click", () => {
                    searchInput.value = (type === 'animal') ? item.nome : (item.descricao_vacina || item.descricaoVacina);
                    hiddenInput.value = id;
                    resultsDiv.style.display = "none";
                });
                resultsDiv.appendChild(divItem);
            });
        } else {
            resultsDiv.style.display = "block";
            const noResultItem = document.createElement("div");
            noResultItem.classList.add("search-result-item-none");
            noResultItem.textContent = `Nenhum(a) ${type} encontrado(a).`;
            resultsDiv.appendChild(noResultItem);
        }
    }

    /**
     * Salva os dados de uma vacinação (novo registro).
     */
    async salvarVacinacao(formData) {
        UIComponents.Loading.mostrar(MensagensPadroes.ALERTA.PROCESSANDO);
        const vacinacaoModel = VacinacaoModel.fromForm(formData);
        const validationResult = vacinacaoModel.validate();

        if (!validationResult.isValid) {
            UIComponents.Loading.esconder();
            UIComponents.Toast.erro("Corrija os erros no formulário.");
            return Promise.reject(new Error("Validação falhou"));
        }

        try {
            const dadosParaEnviar = vacinacaoModel.toJSON();
            await this.vacinacaoService.create(dadosParaEnviar);
            UIComponents.Loading.esconder();
            UIComponents.Toast.sucesso("Vacinação registrada com sucesso!");
            return Promise.resolve();
        } catch (error) {
            UIComponents.Loading.esconder();
            UIComponents.ModalErro.mostrar(error.message || "Não foi possível registrar a vacinação.");
            return Promise.reject(error);
        }
    }

    /**
     * Redireciona para a página de edição de um registro.
     */
    editarRegistroVacinacao(id) {
        window.location.href = `editarVacinacao.html?id=${id}`;
    }

    /**
     * Carrega os dados de uma vacinação para preencher o formulário de edição.
     */
    async carregarVacinacaoParaEdicao(id) {
        UIComponents.Loading.mostrar("Carregando dados...");
        try {
            const vacinacao = await this.vacinacaoService.getById(id);
            if (!vacinacao) {
                throw new Error("Registro de vacinação não encontrado.");
            }

            // Popula os campos do formulário de edição
            document.getElementById("id_vacinacao").value = vacinacao.id_vacinacao;
            document.getElementById("local_vacinacao").value = vacinacao.local_vacinacao;
            document.getElementById("data_vacinacao").value = new Date(vacinacao.data_vacinacao).toISOString().split('T')[0];
            document.getElementById("lote_vacina").value = vacinacao.lote_vacina || "";
            document.getElementById("data_validade_vacina").value = vacinacao.data_validade_vacina ? new Date(vacinacao.data_validade_vacina).toISOString().split('T')[0] : "";
            document.getElementById("laboratorio_vacina").value = vacinacao.laboratorio_vacina || "";

            // Carrega e seleciona o animal e a vacina nos selects
            await this._carregarEPreencherSelectsEdicao(vacinacao.id_animal, vacinacao.id_vacina);

        } catch (error) {
            UIComponents.ModalErro.mostrar("Erro ao carregar dados para edição: " + error.message);
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    /**
     * Carrega as opções dos selects e seleciona os valores corretos na tela de edição.
     */
    async _carregarEPreencherSelectsEdicao(animalId, vacinaId) {
        const selectAnimal = document.getElementById("id_animal");
        const selectVacina = document.getElementById("id_vacina");

        // Carregar todos os animais e vacinas
        const [animais, vacinas] = await Promise.all([
            this.animalService.listarTodos(),
            this.vacinaTipoService.getAll()
        ]);

        // Popular select de animais
        selectAnimal.innerHTML = '<option value="">Selecione o animal...</option>';
        animais.forEach(animal => {
            const option = new Option(`${animal.nome} (ID: ${animal.id})`, animal.id);
            selectAnimal.add(option);
        });

        // Popular select de vacinas
        selectVacina.innerHTML = '<option value="">Selecione o tipo de vacina...</option>';
        vacinas.forEach(vacina => {
            const option = new Option(vacina.descricao_vacina, vacina.id_vacina);
            selectVacina.add(option);
        });

        // Selecionar os valores atuais
        selectAnimal.value = animalId;
        selectVacina.value = vacinaId;
    }

    /**
     * Atualiza os dados de uma vacinação existente.
     */
    async atualizarVacinacao(id, formData) {
        UIComponents.Loading.mostrar("Atualizando registro...");
        const vacinacaoModel = VacinacaoModel.fromForm(formData);
        const validationResult = vacinacaoModel.validate();

        if (!validationResult.isValid) {
            UIComponents.Loading.esconder();
            UIComponents.Toast.erro("Corrija os erros no formulário.");
            return Promise.reject(new Error("Validação falhou"));
        }

        try {
            const dadosParaEnviar = vacinacaoModel.toJSON();
            await this.vacinacaoService.update(id, dadosParaEnviar);
            UIComponents.Loading.esconder();
            UIComponents.Toast.sucesso("Registro atualizado com sucesso!");
            return Promise.resolve();
        } catch (error) {
            UIComponents.Loading.esconder();
            UIComponents.ModalErro.mostrar("Erro ao atualizar registro: " + error.message);
            return Promise.reject(error);
        }
    }
}

export default VacinacaoController;
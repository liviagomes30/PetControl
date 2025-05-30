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
    }

    async inicializarFormulario(formType = "cadastro", vacinacaoId = null) {
        UIComponents.Loading.mostrar("Carregando dados do formulário...");
        try {
            const selectVacinaEl = document.getElementById("id_vacina");
            const selectAnimalEl = document.getElementById("id_animal");

            await Promise.all([
                this.carregarOpcoesVacinasTipos(selectVacinaEl),
                this.carregarOpcoesAnimais(selectAnimalEl)
            ]);

            if (formType === "edicao" && vacinacaoId) {
                const vacinacaoData = await this.vacinacaoService.getById(vacinacaoId);
                if (vacinacaoData) {
                    this.preencherFormularioEdicao(vacinacaoData);
                } else {
                    UIComponents.ModalErro.mostrar("Registro de vacinação não encontrado.");
                }
            }
        } catch (error) {
            UIComponents.ModalErro.mostrar("Erro ao carregar opções do formulário: " + error.message);
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    preencherFormularioEdicao(data) {
        document.getElementById("id_vacinacao").value = data.idVacinacao;
        document.getElementById("id_vacina").value = data.idVacina;
        document.getElementById("id_animal").value = data.idAnimal;
        document.getElementById("data_vacinacao").value = data.dataVacinacao ? new Date(data.dataVacinacao).toISOString().split('T')[0] : "";
        document.getElementById("local_vacinacao").value = data.localVacinacao || "";
        document.getElementById("lote_vacina").value = data.loteVacina || "";
        document.getElementById("data_validade_vacina").value = data.dataValidadeVacina ? new Date(data.dataValidadeVacina).toISOString().split('T')[0] : "";
        document.getElementById("laboratorio_vacina").value = data.laboratorioVacina || "";
    }

    async carregarVacinacaoParaEdicao(idVacinacao) {
        await this.inicializarFormulario("edicao", idVacinacao);
    }

    async carregarOpcoesVacinasTipos(selectElement) {
        if (!selectElement) {
            return;
        }
        try {
            const tiposVacina = await this.vacinaTipoService.getAll();
            this.tiposDeVacinaMap.clear();
            if (tiposVacina && tiposVacina.length > 0) {
                tiposVacina.forEach(vacina => this.tiposDeVacinaMap.set(vacina.idVacina, vacina.descricaoVacina));
            }
            selectElement.innerHTML = '<option value="">Selecione o tipo de vacina...</option>';
            if (tiposVacina && tiposVacina.length > 0) {
                tiposVacina.forEach(vacina => {
                    const option = document.createElement("option");
                    option.value = vacina.idVacina;
                    option.textContent = vacina.descricaoVacina;
                    selectElement.appendChild(option);
                });
            } else {
                selectElement.innerHTML = '<option value="">Nenhum tipo de vacina encontrado</option>';
            }
        } catch (error) {
            console.error("Erro ao carregar tipos de vacina:", error);
            selectElement.innerHTML = '<option value="">Erro ao carregar vacinas</option>';
            UIComponents.Toast.erro("Não foi possível carregar os tipos de vacina.");
        }
    }

    async carregarOpcoesAnimais(selectElement) {
        if (!selectElement) {
            return;
        }
        try {
            const animais = await this.animalService.listarTodos();
            selectElement.innerHTML = '<option value="">Selecione o animal...</option>';
            if (animais && animais.length > 0) {
                animais.forEach(animal => {
                    const option = document.createElement("option");
                    option.value = animal.id;
                    option.textContent = `${animal.nome || 'Nome não informado'} (${animal.especie || 'Espécie não informada'})`;
                    selectElement.appendChild(option);
                });
            } else {
                selectElement.innerHTML = '<option value="">Nenhum animal encontrado</option>';
            }
        } catch (error) {
            console.error("Erro ao carregar animais:", error);
            selectElement.innerHTML = '<option value="">Erro ao carregar animais</option>';
            UIComponents.Toast.erro("Não foi possível carregar a lista de animais.");
        }
    }

    async _processarSalvarOuAtualizar(vacinacaoModel, isUpdate = false, id = null) {
        const validationResult = vacinacaoModel.validate();
        document.querySelectorAll('.error-message').forEach(el => el.textContent = '');
        document.querySelectorAll('.form-group input.invalid, .form-group select.invalid').forEach(el => el.classList.remove('invalid'));

        if (!validationResult.isValid) {
            Object.keys(validationResult.errors).forEach(key => {
                let errorFieldId = key.replace(/([A-Z])/g, '-$1').toLowerCase() + '-error';
                if (key === "idVacina") errorFieldId = "vacina-error";
                if (key === "idAnimal") errorFieldId = "animal-error";
                if (key === "dataVacinacao") errorFieldId = "data-vacinacao-error";
                if (key === "localVacinacao") errorFieldId = "local-vacinacao-error";

                const errorElement = document.getElementById(errorFieldId);
                let inputElementId = key;
                if (key === "idVacina") inputElementId = "id_vacina";
                else if (key === "idAnimal") inputElementId = "id_animal";
                else if (key === "dataVacinacao") inputElementId = "data_vacinacao";
                else if (key === "localVacinacao") inputElementId = "local_vacinacao";

                const inputElement = document.getElementById(inputElementId);
                if (errorElement) errorElement.textContent = validationResult.errors[key];
                if (inputElement) inputElement.classList.add("invalid");
            });
            UIComponents.Loading.esconder();
            UIComponents.Toast.erro(MensagensPadroes.ERRO.FORMULARIO_INVALIDO || "Corrija os erros no formulário.");
            return Promise.reject(new Error("Validação do formulário falhou"));
        }

        try {
            const dadosParaEnviar = vacinacaoModel.toJSON();
            let resultado;
            if (isUpdate && id) {
                resultado = await this.vacinacaoService.update(id, dadosParaEnviar);
                UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.ATUALIZACAO || "Vacinação atualizada com sucesso!");
            } else {
                resultado = await this.vacinacaoService.create(dadosParaEnviar);
                UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.CADASTRO || "Vacinação registrada com sucesso!");
                const formCadastro = document.getElementById("vacinacaoForm");
                if (formCadastro) formCadastro.reset();

                const idVacinaSelect = document.getElementById("id_vacina");
                const idAnimalSelect = document.getElementById("id_animal");
                if(idVacinaSelect) idVacinaSelect.value = "";
                if(idAnimalSelect) idAnimalSelect.value = "";
            }
            UIComponents.Loading.esconder();
            return Promise.resolve(resultado);
        } catch (error) {
            UIComponents.Loading.esconder();
            UIComponents.ModalErro.mostrar(error.message || (isUpdate ? MensagensPadroes.ERRO.ATUALIZACAO : MensagensPadroes.ERRO.CADASTRO));
            return Promise.reject(error);
        }
    }

    async salvarVacinacao(formDataFromPage) {
        UIComponents.Loading.mostrar(MensagensPadroes.ALERTA.PROCESSANDO);
        const vacinacaoModel = VacinacaoModel.fromForm(formDataFromPage);
        return this._processarSalvarOuAtualizar(vacinacaoModel, false);
    }

    async atualizarVacinacao(id, formDataFromPage) {
        UIComponents.Loading.mostrar(MensagensPadroes.ALERTA.PROCESSANDO);
        const vacinacaoModel = VacinacaoModel.fromForm(formDataFromPage);
        vacinacaoModel.idVacinacao = id;
        return this._processarSalvarOuAtualizar(vacinacaoModel, true, id);
    }

    async inicializarPaginaHistorico() {
        UIComponents.Loading.mostrar("Carregando dados...");
        try {
            const tiposVacina = await this.vacinaTipoService.getAll();
            this.tiposDeVacinaMap.clear();
            if (tiposVacina && tiposVacina.length > 0) {
                tiposVacina.forEach(vacina => this.tiposDeVacinaMap.set(vacina.idVacina, vacina.descricaoVacina));
            }

            await this.carregarOpcoesAnimais(document.getElementById("animalSelectHistorico"));

            this.renderizarTabelaHistorico([], "Selecione um animal para buscar seu histórico.");
            const noHistoryMessage = document.getElementById("noHistoryMessage");
            const historicoTitle = document.getElementById("historicoTitle");
            if (noHistoryMessage) {
                noHistoryMessage.textContent = "Selecione um animal para ver o histórico.";
                noHistoryMessage.style.display = "block";
            }
            if (historicoTitle) historicoTitle.style.display = "none";
        } catch (error) {
            UIComponents.ModalErro.mostrar("Erro ao inicializar página de histórico: " + error.message);
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    limparHistorico() {
        const tableBody = document.getElementById("vacinacaoHistoryTableBody");
        const noHistoryMessage = document.getElementById("noHistoryMessage");
        const historicoTitle = document.getElementById("historicoTitle");
        const animalNomeHistoricoSpan = document.getElementById("animalNomeHistorico");

        if (tableBody) tableBody.innerHTML = "";
        if (noHistoryMessage) {
            noHistoryMessage.textContent = "Selecione um animal para ver o histórico.";
            noHistoryMessage.style.display = "block";
        }
        if (historicoTitle) historicoTitle.style.display = "none";
        if (animalNomeHistoricoSpan) animalNomeHistoricoSpan.textContent = "";
    }

    async buscarHistoricoPorAnimal(animalId, animalNome) {
        if (!animalId) {
            this.renderizarTabelaHistorico([], "Nenhum animal selecionado.");
            const historicoTitle = document.getElementById("historicoTitle");
            if (historicoTitle) historicoTitle.style.display = "none";
            return;
        }
        UIComponents.Loading.mostrar(`Buscando histórico para ${animalNome || 'o animal selecionado'}...`);
        const historicoTitle = document.getElementById("historicoTitle");
        const animalNomeHistoricoSpan = document.getElementById("animalNomeHistorico");

        if (historicoTitle) historicoTitle.style.display = "block";
        if (animalNomeHistoricoSpan) animalNomeHistoricoSpan.textContent = animalNome || `ID: ${animalId}`;

        try {
            const historico = await this.vacinacaoService.listarPorAnimal(animalId);
            this.renderizarTabelaHistorico(historico);
        } catch (error) {
            UIComponents.ModalErro.mostrar("Erro ao buscar histórico de vacinações: " + error.message);
            this.renderizarTabelaHistorico([], `Erro ao carregar histórico para ${animalNome}.`);
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    renderizarTabelaHistorico(vacinacoes, mensagemCustomizada = null) {
        const tableBody = document.getElementById("vacinacaoHistoryTableBody");
        const noHistoryMessage = document.getElementById("noHistoryMessage");
        const table = document.getElementById("vacinacaoHistoryTable");

        if (!tableBody || !noHistoryMessage || !table) {
            return;
        }
        tableBody.innerHTML = "";

        if (!vacinacoes || vacinacoes.length === 0) {
            noHistoryMessage.textContent = mensagemCustomizada || "Nenhum registro de vacinação encontrado para este animal.";
            noHistoryMessage.style.display = "block";
            return;
        }

        noHistoryMessage.style.display = "none";

        vacinacoes.forEach(reg => {
            const row = tableBody.insertRow();
            const dataAplicacao = reg.dataVacinacao ? new Date(reg.dataVacinacao + 'T00:00:00Z').toLocaleDateString() : 'N/A';
            const dataValidade = reg.dataValidadeVacina ? new Date(reg.dataValidadeVacina + 'T00:00:00Z').toLocaleDateString() : 'N/A';

            const descricaoTipoVacina = this.tiposDeVacinaMap.get(reg.idVacina) || `ID Vacina: ${reg.idVacina}`;

            row.innerHTML = `
                <td>${dataAplicacao}</td>
                <td>${descricaoTipoVacina}</td>
                <td>${reg.localVacinacao || 'N/A'}</td>
                <td>${reg.loteVacina || 'N/A'}</td>
                <td>${dataValidade}</td>
                <td>${reg.laboratorioVacina || 'N/A'}</td>
                <td class="actions">
                    <button class="btn-icon btn-edit" title="Editar Registro de Vacinação" data-id="${reg.idVacinacao}">
                        <i class="bi bi-pencil"></i>
                    </button>
                </td>
            `;
            const btnEdit = row.querySelector(".btn-edit");
            if (btnEdit) {
                btnEdit.addEventListener("click", (e) => {
                    const id = e.currentTarget.dataset.id;
                    this.editarRegistroVacinacao(id);
                });
            }
        });
    }

    editarRegistroVacinacao(id) {
        window.location.href = `editarVacinacao.html?id=${id}`;
    }
}

export default VacinacaoController;
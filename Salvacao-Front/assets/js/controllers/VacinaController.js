// Salvacao-Front/assets/js/controllers/VacinaController.js
import VacinaModel from "../models/VacinaModel.js";
import VacinaService from "../services/VacinaService.js";
import UIComponents from "../components/uiComponents.js"; //
import MensagensPadroes from "../utils/mensagensPadroes.js"; //

class VacinaController {
    constructor() {
        this.vacinaService = new VacinaService();
        this.vacinasCarregadas = []; // Cache para filtro no cliente
    }

    // Método para o formulário de CADASTRO (`cadastrarVacina.html`)
    async salvar(vacinaDataFromForm) {
        UIComponents.Loading.mostrar(MensagensPadroes.ALERTA.PROCESSANDO);

        const idVacina = vacinaDataFromForm.id; // Veio como 'id' do HTML
        const descricaoVacina = vacinaDataFromForm.descricao; // Veio como 'descricao' do HTML

        const vacina = new VacinaModel(idVacina, descricaoVacina);
        const { isValid, errors } = vacina.validate();

        const descricaoInput = document.getElementById("descricao_vacina");
        const descricaoError = document.getElementById("descricao-error");

        if (descricaoError) descricaoError.textContent = "";
        if (descricaoInput) descricaoInput.classList.remove("invalid");

        if (!isValid) {
            if (errors.descricao_vacina && descricaoInput && descricaoError) {
                descricaoError.textContent = errors.descricao_vacina;
                descricaoInput.classList.add("invalid");
            }
            UIComponents.Loading.esconder();
            UIComponents.Toast.erro(MensagensPadroes.ERRO.FORMULARIO_INVALIDO || "Corrija os erros no formulário.");
            return Promise.reject(new Error("Validação falhou"));
        }

        try {
            const vacinaJSON = vacina.toJSON();
            const resultado = await this.vacinaService.create(vacinaJSON);

            UIComponents.Loading.esconder();
            UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.CADASTRO);

            console.log("Vacina cadastrada:", resultado);
            // O redirecionamento é feito no HTML após a promessa resolver
            return Promise.resolve(resultado);

        } catch (error) {
            UIComponents.Loading.esconder();
            UIComponents.ModalErro.mostrar(error.message || MensagensPadroes.ERRO.CADASTRO);
            console.error("Erro ao salvar vacina:", error);
            return Promise.reject(error);
        }
    }

    // --- Métodos para a página de LISTAGEM (`listarVacinas.html`) ---
    async inicializarListagem() {
        UIComponents.Loading.mostrar("Carregando tipos de vacina...");
        try {
            const vacinas = await this.vacinaService.getAll();
            this.vacinasCarregadas = vacinas; // Salva para filtro
            this.renderizarTabela(vacinas);
        } catch (error) {
            UIComponents.ModalErro.mostrar(MensagensPadroes.ERRO.CARREGAMENTO + (error.message ? `: ${error.message}` : ''));
            this.renderizarTabela([]); // Renderiza tabela vazia em caso de erro
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    renderizarTabela(vacinas) {
        const tableBody = document.getElementById("vacinasTableBody");
        if (!tableBody) return;

        tableBody.innerHTML = "";

        const noDataMessage = document.getElementById("noDataMessage");
        const table = document.getElementById("vacinasTable");

        if (!vacinas || vacinas.length === 0) {
            if (noDataMessage) noDataMessage.style.display = "block";
            // if (table) table.style.display = "none"; // Opcional
            // Adiciona uma linha "Nenhum dado" se a tabela estiver visível
            if (tableBody.offsetParent !== null) { // Verifica se tableBody está visível
                const row = tableBody.insertRow();
                const cell = row.insertCell(0);
                cell.colSpan = 3; // Ajuste o colspan conforme o número de colunas
                cell.textContent = "Nenhuma vacina encontrada.";
                cell.className = "no-data"; // Pode usar a classe .no-data para estilização
            }
            return;
        }

        if (noDataMessage) noDataMessage.style.display = "none";
        // if (table) table.style.display = ""; // Opcional

        vacinas.forEach(vacina => {
            const row = tableBody.insertRow();
            row.innerHTML = `
        <td>${vacina.id_vacina || 'N/A'}</td>
        <td>${vacina.descricao_vacina || 'N/A'}</td>
        <td class="actions">
          <button class="btn-icon btn-edit" title="Editar">
            <i class="bi bi-pencil"></i>
          </button>
          <button class="btn-icon btn-delete" title="Excluir">
            <i class="bi bi-trash"></i>
          </button>
        </td>
      `;
            const btnEdit = row.querySelector(".btn-edit");
            if (btnEdit) {
                btnEdit.addEventListener("click", () => this.editarVacina(vacina.id_vacina));
            }

            const btnDelete = row.querySelector(".btn-delete");
            if (btnDelete) {
                btnDelete.addEventListener("click", () => this.confirmarExclusao(vacina.id_vacina, vacina.descricao_vacina));
            }
        });
    }

    editarVacina(id) {
        window.location.href = `editarVacina.html?id=${id}`;
    }

    confirmarExclusao(id, descricao) {
        UIComponents.ModalConfirmacao.mostrar(
            "Confirmar Exclusão",
            MensagensPadroes.formatar(MensagensPadroes.CONFIRMACAO.EXCLUSAO_ITEM_GENERICO || 'Tem certeza que deseja excluir "{0}" (ID: {1})?', descricao, id),
            async () => {
                await this.excluirVacina(id);
            }
        );
    }

    async excluirVacina(id) {
        UIComponents.Loading.mostrar(MensagensPadroes.ALERTA.PROCESSANDO_EXCLUSAO || "Excluindo vacina...");
        try {
            await this.vacinaService.delete(id);
            UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.EXCLUSAO);
            this.inicializarListagem();
        } catch (error) {
            UIComponents.ModalErro.mostrar(MensagensPadroes.ERRO.EXCLUSAO + (error.message ? `: ${error.message}` : ''));
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    async filtrarVacinas(termo) {
        UIComponents.Loading.mostrar("Filtrando...");
        // Para este exemplo, o filtro é client-side.
        // Idealmente, o backend faria o filtro para grandes volumes de dados.
        try {
            const termoLowerCase = termo.toLowerCase().trim();
            if (termoLowerCase === "") {
                this.renderizarTabela(this.vacinasCarregadas);
            } else {
                const filtradas = this.vacinasCarregadas.filter(v =>
                    (v.descricao_vacina && v.descricao_vacina.toLowerCase().includes(termoLowerCase))
                );
                this.renderizarTabela(filtradas);
            }
        } catch (error) {
            UIComponents.ModalErro.mostrar("Erro ao filtrar vacinas: " + error.message);
            this.renderizarTabela([]);
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    // Método para o formulário de EDIÇÃO (futuro `editarVacina.html`)
    async carregarParaEdicao(id_vacina) {
        UIComponents.Loading.mostrar("Carregando dados da vacina...");
        try {
            const vacina = await this.vacinaService.getById(id_vacina);
            // Preencher os campos do formulário de edição (ex: em editarVacina.html)
            // document.getElementById("id_vacina_edit").value = vacina.id_vacina;
            // document.getElementById("descricao_vacina_edit").value = vacina.descricao_vacina;
            UIComponents.Toast.sucesso("Dados da vacina carregados.");
            return vacina; // Retorna para o script da página de edição usar
        } catch (error) {
            UIComponents.ModalErro.mostrar("Erro ao carregar vacina para edição: " + error.message);
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    async atualizar(id_vacina, vacinaDataFromForm) { // vacinaDataFromForm viria do form de edição
        UIComponents.Loading.mostrar("Atualizando vacina...");
        const vacina = new VacinaModel(id_vacina, vacinaDataFromForm.descricao_vacina); // Ajuste para pegar os dados corretos do form
        const { isValid, errors } = vacina.validate();

        // Lógica de validação similar ao 'salvar'
        // ...

        if (!isValid) {
            UIComponents.Loading.esconder();
            UIComponents.Toast.erro("Corrija os erros no formulário.");
            return Promise.reject(new Error("Validação falhou na atualização"));
        }

        try {
            const vacinaJSON = vacina.toJSON();
            await this.vacinaService.update(id_vacina, vacinaJSON);
            UIComponents.Loading.esconder();
            UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.ATUALIZACAO);
            // Redirecionar para a lista
            // window.location.href = "listarVacinas.html";
            return Promise.resolve();
        } catch (error) {
            UIComponents.Loading.esconder();
            UIComponents.ModalErro.mostrar(error.message || MensagensPadroes.ERRO.ATUALIZACAO);
            return Promise.reject(error);
        }
    }
}

// Disponibilizar globalmente se as páginas HTML chamarem métodos diretamente no objeto window.vacinaController
// document.addEventListener('DOMContentLoaded', () => {
//   if (!window.vacinaController) { // Evita recriar se já existir
//     window.vacinaController = new VacinaController();
//   }
//   // A inicialização específica da página (listar ou cadastrar) será chamada no script inline da respectiva página HTML
// });

export default VacinaController;
import UnidadeMedidaService from "../services/UnidadeMedidaService.js";
import UIComponents from "../components/uiComponents.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";

class UnidadeMedidaController {
  constructor() {
    this.service = new UnidadeMedidaService();
    this.tableBody = document.getElementById("unidadeTableBody");
    this.searchInput = document.getElementById("searchInput");
    this.noDataMessage = document.getElementById("noDataMessage");
    this.tableElement = document.getElementById("unidadeTable");
    this.unidadesCache = [];
  }

  inicializarListagem() {
    this.bindListEvents();
    this.loadUnidades();
  }

  inicializarFormulario() {
    const form = document.getElementById("unidadeForm");
    if (form) {
      form.addEventListener("submit", (e) => {
        e.preventDefault();
        this.salvar(form);
      });

      const urlParams = new URLSearchParams(window.location.search);
      const id = urlParams.get("id");
      if (id) {
        document.querySelector("h1").textContent = "Editar Unidade de Medida";
        document.getElementById("idUnidadeMedida").value = id;
        this.carregarDadosParaEdicao(id);
      }
    }
  }

  bindListEvents() {
    if (this.searchInput) {
      // Usamos 'change' ou 'keydown' para buscar ao pressionar Enter ou perder o foco
      this.searchInput.addEventListener("keydown", (event) => {
        if (event.key === "Enter") {
          this.filterTable();
        }
      });
      // Adicionamos um botão de busca para deixar a ação mais explícita
      const searchButton = document.getElementById("searchButton");
      if (searchButton) {
        searchButton.addEventListener("click", () => this.filterTable());
      }
    }
  }

  async loadUnidades() {
    try {
      UIComponents.Loading.mostrar("Carregando unidades...");
      const unidades = await this.service.listarTodos();
      this.unidadesCache = unidades;
      this.renderTable(this.unidadesCache);
    } catch (error) {
      this.renderTable([]); // Limpa a tabela em caso de erro
      UIComponents.ModalErro.mostrar(
        "Erro ao carregar unidades",
        error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  async filterTable() {
    const termo = this.searchInput.value.trim();
    UIComponents.Loading.mostrar("Buscando...");
    try {
      const unidades = await this.service.buscarPorTermo(termo);
      this.renderTable(unidades);
    } catch (error) {
      this.renderTable([]); // Limpa a tabela se a busca falhar ou não retornar resultados
      // Não mostra um modal de erro, pois a falta de resultados é um estado esperado.
      // A mensagem "Nenhuma unidade encontrada" já trata isso.
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  renderTable(unidades) {
    if (!this.tableBody) return;
    this.tableBody.innerHTML = "";

    if (!unidades || unidades.length === 0) {
      if (this.tableElement) this.tableElement.style.display = "none";
      if (this.noDataMessage) this.noDataMessage.style.display = "block";
      return;
    }

    if (this.tableElement) this.tableElement.style.display = "table";
    if (this.noDataMessage) this.noDataMessage.style.display = "none";

    unidades.forEach((unidade) => {
      const row = document.createElement("tr");
      row.innerHTML = `
                <td>${unidade.idUnidadeMedida}</td>
                <td>${unidade.descricao}</td>
                <td>${unidade.sigla}</td>
                <td class="text-center actions">
                    <a href="editarUnidade.html?id=${unidade.idUnidadeMedida}" class="btn btn-sm btn-primary" title="Editar">
                        <i class="bi bi-pencil-fill"></i>
                    </a>
                    <button class="btn btn-sm btn-outline-danger" onclick="window.unidadeController.confirmarExclusao(${unidade.idUnidadeMedida})" title="Excluir">
                        <i class="bi bi-trash-fill"></i>
                    </button>
                </td>
            `;
      this.tableBody.appendChild(row);
    });
  }

  confirmarExclusao(id) {
    UIComponents.ModalConfirmacao.mostrar(
      "Confirmar Exclusão",
      MensagensPadroes.CONFIRMACAO.EXCLUSAO,
      () => this.excluir(id)
    );
  }

  async excluir(id) {
    try {
      UIComponents.Loading.mostrar("Excluindo...");
      await this.service.excluir(id);
      UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.EXCLUSAO);
      this.loadUnidades();
    } catch (error) {
      UIComponents.ModalErro.mostrar("Erro ao excluir", error.message);
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  async carregarDadosParaEdicao(id) {
    try {
      UIComponents.Loading.mostrar("Carregando dados...");
      const unidade = await this.service.buscarPorId(id);
      document.getElementById("descricao").value = unidade.descricao;
      document.getElementById("sigla").value = unidade.sigla;
    } catch (error) {
      UIComponents.ModalErro.mostrar("Erro ao carregar dados", error.message);
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  async salvar(form) {
    if (!form.checkValidity()) {
      form.classList.add("was-validated");
      return;
    }

    const id = document.getElementById("idUnidadeMedida").value;
    const unidade = {
      idUnidadeMedida: id && id !== "0" ? parseInt(id, 10) : null,
      descricao: document.getElementById("descricao").value,
      sigla: document.getElementById("sigla").value,
    };

    const ehEdicao = !!unidade.idUnidadeMedida;
    UIComponents.Loading.mostrar(ehEdicao ? "Atualizando..." : "Salvando...");

    try {
      if (ehEdicao) {
        await this.service.atualizar(unidade.idUnidadeMedida, unidade);
      } else {
        await this.service.cadastrar(unidade);
      }
      UIComponents.Toast.sucesso(
        ehEdicao
          ? MensagensPadroes.SUCESSO.ATUALIZACAO
          : MensagensPadroes.SUCESSO.CADASTRO
      );
      setTimeout(() => (window.location.href = "listarUnidades.html"), 1500);
    } catch (error) {
      UIComponents.ModalErro.mostrar("Erro ao salvar", error.message);
    } finally {
      UIComponents.Loading.esconder();
    }
  }
}

window.UnidadeMedidaController = UnidadeMedidaController;
export default UnidadeMedidaController;

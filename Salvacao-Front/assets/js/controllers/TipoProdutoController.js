import TipoProdutoService from "../services/TipoProdutoService.js";
import UIComponents from "../components/uiComponents.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";

class TipoProdutoController {
  constructor() {
    this.service = new TipoProdutoService();
    this.tableBody = document.getElementById("tipoProdutoTableBody");
    this.searchInput = document.getElementById("searchInput");
    this.noDataMessage = document.getElementById("noDataMessage");
    this.tableElement = document.getElementById("tipoProdutoTable");
    this.tiposCache = []; // Cache para guardar os dados
  }

  // Método para ser chamado na página de listagem
  inicializarListagem() {
    this.bindListEvents();
    this.loadTiposProduto();
  }

  // Método para ser chamado nas páginas de formulário (cadastro/edição)
  inicializarFormulario() {
    const form = document.getElementById("tipoProdutoForm");
    if (form) {
      form.addEventListener("submit", (e) => {
        e.preventDefault();
        this.salvar(form);
      });

      const urlParams = new URLSearchParams(window.location.search);
      const id = urlParams.get("id");
      if (id) {
        document.querySelector("h1").textContent = "Editar Tipo de Produto";
        document.getElementById("idTipoProduto").value = id;
        this.carregarDadosParaEdicao(id);
      }
    }
  }

  bindListEvents() {
    if (this.searchInput) {
      this.searchInput.addEventListener("input", () => this.filterTable());
    }
  }

  async loadTiposProduto() {
    try {
      UIComponents.Loading.mostrar("Carregando tipos...");
      const tipos = await this.service.listarTodos();
      this.tiposCache = tipos;
      this.renderTable(this.tiposCache);
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao carregar tipos de produto: " + error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  renderTable(tipos) {
    if (!this.tableBody) return;
    this.tableBody.innerHTML = "";

    if (!tipos || tipos.length === 0) {
      this.tableElement.style.display = "none";
      this.noDataMessage.style.display = "block";
      return;
    }

    this.tableElement.style.display = "table";
    this.noDataMessage.style.display = "none";

    tipos.forEach((tipo) => {
      const row = document.createElement("tr");

      row.innerHTML = `
                <td>${tipo.idtipoproduto}</td>
                <td>${tipo.descricao}</td>
                <td class="actions">
                    <a href="editarTipo.html?id=${tipo.idtipoproduto}" class="btn btn-sm btn-primary" title="Editar">
                        <i class="bi bi-pencil-fill"></i>
                    </a>
                    <button class="btn btn-sm btn-outline-danger" onclick="window.tipoProdutoController.confirmarExclusao(${tipo.idtipoproduto})" title="Excluir">
                        <i class="bi bi-trash-fill"></i>
                    </button>
                </td>
            `;
      this.tableBody.appendChild(row);
    });
  }

  filterTable() {
    const termo = this.searchInput.value.toLowerCase();
    const filtrados = this.tiposCache.filter((tipo) =>
      tipo.descricao.toLowerCase().includes(termo)
    );
    this.renderTable(filtrados);
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
      this.loadTiposProduto();
    } catch (error) {
      UIComponents.ModalErro.mostrar("Erro ao excluir", error.message);
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  async carregarDadosParaEdicao(id) {
    try {
      UIComponents.Loading.mostrar("Carregando dados...");
      const tipo = await this.service.buscarPorId(id);
      document.getElementById("descricao").value = tipo.descricao;
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao carregar dados para edição",
        error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  async salvar(form) {
    if (!form.checkValidity()) {
      form.classList.add("was-validated");
      return;
    }

    const id = document.getElementById("idTipoProduto").value;
    const tipoProduto = {
      idtipoproduto: id && id !== "0" ? parseInt(id, 10) : null,
      descricao: document.getElementById("descricao").value,
    };

    const ehEdicao = !!tipoProduto.idtipoproduto;
    UIComponents.Loading.mostrar(ehEdicao ? "Atualizando..." : "Salvando...");

    try {
      if (ehEdicao) {
        await this.service.atualizar(tipoProduto.idtipoproduto, tipoProduto);
      } else {
        await this.service.cadastrar(tipoProduto);
      }
      UIComponents.Toast.sucesso(
        ehEdicao
          ? MensagensPadroes.SUCESSO.ATUALIZACAO
          : MensagensPadroes.SUCESSO.CADASTRO
      );
      setTimeout(() => (window.location.href = "listarTipos.html"), 1500);
    } catch (error) {
      UIComponents.ModalErro.mostrar("Erro ao salvar", error.message);
    } finally {
      UIComponents.Loading.esconder();
    }
  }
}

window.TipoProdutoController = TipoProdutoController;
export default TipoProdutoController;

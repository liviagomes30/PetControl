import UnidadeMedidaService from "../services/UnidadeMedidaService.js";
import UIComponents from "../utils/uiComponents.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";

class UnidadeMedidaController {
  constructor() {
    this.service = new UnidadeMedidaService();
    this.tableBody = document.getElementById("unidadeMedidaTableBody");
    this.searchInput = document.getElementById("searchInput");
    this.searchButton = document.querySelector(".search-button");
    this.itemToDelete = null;

    this.bindEvents();
  }

  bindEvents() {
    if (this.searchButton) {
      this.searchButton.addEventListener("click", () => this.filterTable());
    }

    if (this.searchInput) {
      this.searchInput.addEventListener("keyup", (e) => {
        if (e.key === "Enter") {
          this.filterTable();
        }
      });
    }

    const form = document.getElementById("unidadeMedidaForm");
    if (form) {
      form.addEventListener("submit", (e) => {
        e.preventDefault();
        if (this.validateForm()) {
          this.saveUnidadeMedida();
        }
      });

      const urlParams = new URLSearchParams(window.location.search);
      const id = urlParams.get("id");
      if (id) {
        document.querySelector("h1").textContent = "Editar Unidade de Medida";
        document.getElementById("idUnidadeMedida").value = id;
        this.loadUnidadeMedidaData(id);
      }
    }
  }

  async loadUnidadesMedida() {
    try {
      UIComponents.Loading.mostrar("Carregando unidades de medida...");
      const unidadesMedida = await this.service.getAll();
      this.renderTable(unidadesMedida);
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao carregar unidades de medida: " + error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  async loadUnidadeMedidaData(id) {
    try {
      UIComponents.Loading.mostrar("Carregando dados da unidade de medida...");
      const unidadeMedida = await this.service.getById(id);
      document.getElementById("descricao").value = unidadeMedida.descricao;
      document.getElementById("sigla").value = unidadeMedida.sigla;
      UIComponents.Toast.sucesso("Dados carregados com sucesso");
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao carregar dados: " + error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  renderTable(unidadesMedida) {
    if (!this.tableBody) return;

    this.tableBody.innerHTML = "";

    if (unidadesMedida.length === 0) {
      const row = document.createElement("tr");
      row.innerHTML =
        '<td colspan="4" class="no-data">Nenhuma unidade de medida encontrada</td>';
      this.tableBody.appendChild(row);
      return;
    }

    unidadesMedida.forEach((unidade) => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${unidade.idunidademedida}</td>
        <td>${unidade.descricao}</td>
        <td>${unidade.sigla}</td>
        <td class="actions">
          <button class="btn-edit" onclick="unidadeMedidaController.editUnidadeMedida(${unidade.idunidademedida})">
            <i class="bi bi-pencil"></i>
          </button>
          <button class="btn-delete" onclick="unidadeMedidaController.showDeleteModal(${unidade.idunidademedida})">
            <i class="bi bi-trash"></i>
          </button>
        </td>
      `;
      this.tableBody.appendChild(row);
    });
  }

  filterTable() {
    if (!this.searchInput) return;

    const searchTerm = this.searchInput.value.toLowerCase();

    try {
      UIComponents.Loading.mostrar("Filtrando resultados...");
      this.service
        .search(searchTerm)
        .then((filteredResults) => {
          this.renderTable(filteredResults);
        })
        .catch((error) => {
          UIComponents.ModalErro.mostrar("Erro ao filtrar: " + error.message);
        })
        .finally(() => {
          UIComponents.Loading.esconder();
        });
    } catch (error) {
      UIComponents.Loading.esconder();
      UIComponents.ModalErro.mostrar("Erro ao filtrar: " + error.message);
    }
  }

  editUnidadeMedida(id) {
    window.location.href = `editar.html?id=${id}`;
  }

  showDeleteModal(id) {
    this.itemToDelete = id;
    UIComponents.ModalConfirmacao.mostrar(
      "Confirmar exclusão",
      MensagensPadroes.CONFIRMACAO.EXCLUSAO,
      () => this.deleteUnidadeMedida(id)
    );
  }

  async deleteUnidadeMedida(id) {
    try {
      UIComponents.Loading.mostrar("Excluindo unidade de medida...");
      await this.service.delete(id);
      UIComponents.Toast.sucesso("Unidade de medida excluída com sucesso!");
      this.loadUnidadesMedida();
    } catch (error) {
      UIComponents.ModalErro.mostrar("Erro ao excluir: " + error.message);
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  validateForm() {
    let isValid = true;
    const descricao = document.getElementById("descricao");
    const sigla = document.getElementById("sigla");

    UIComponents.Validacao.limparErros("unidadeMedidaForm");

    if (!descricao.value.trim()) {
      UIComponents.Validacao.mostrarErro(
        "descricao",
        MensagensPadroes.VALIDACAO.CAMPO_OBRIGATORIO
      );
      isValid = false;
    }

    if (!sigla.value.trim()) {
      UIComponents.Validacao.mostrarErro(
        "sigla",
        MensagensPadroes.VALIDACAO.CAMPO_OBRIGATORIO
      );
      isValid = false;
    } else if (sigla.value.length > 10) {
      UIComponents.Validacao.mostrarErro(
        "sigla",
        MensagensPadroes.VALIDACAO.TAMANHO_MAXIMO.replace("{0}", "10")
      );
      isValid = false;
    }

    return isValid;
  }

  async saveUnidadeMedida() {
    const id = document.getElementById("idUnidadeMedida").value;
    const descricao = document.getElementById("descricao").value;
    const sigla = document.getElementById("sigla").value;

    const unidadeMedida = {
      idunidademedida: id !== "0" ? parseInt(id) : null,
      descricao: descricao,
      sigla: sigla,
    };

    try {
      UIComponents.Loading.mostrar(
        unidadeMedida.idunidademedida
          ? "Atualizando unidade de medida..."
          : "Cadastrando unidade de medida..."
      );

      if (unidadeMedida.idunidademedida) {
        await this.service.update(unidadeMedida);
        UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.ATUALIZACAO);
      } else {
        await this.service.create(unidadeMedida);
        UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.CADASTRO);
      }

      setTimeout(() => {
        window.location.href = "index.html";
      }, 2000);
    } catch (error) {
      UIComponents.ModalErro.mostrar("Erro ao salvar: " + error.message);
    } finally {
      UIComponents.Loading.esconder();
    }
  }
}

document.addEventListener("DOMContentLoaded", () => {
  window.unidadeMedidaController = new UnidadeMedidaController();

  const tableBody = document.getElementById("unidadeMedidaTableBody");
  if (tableBody) {
    window.unidadeMedidaController.loadUnidadesMedida();
  }
});

export default UnidadeMedidaController;

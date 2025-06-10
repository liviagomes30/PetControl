// TipoProdutoController.js
import TipoProdutoService from "../services/TipoProdutoService.js";
import UIComponents from "../utils/uiComponents.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";

class TipoProdutoController {
  constructor() {
    this.service = new TipoProdutoService();
    this.tableBody = document.getElementById("tipoProdutoTableBody");
    this.searchInput = document.getElementById("searchInput");
    this.searchButton = document.querySelector(".search-button");
    this.itemToDelete = null;

    this.bindEvents();
  }

  bindEvents() {
    // Binding de eventos para a página de listagem
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

    // Binding de evento para o formulário de criação/edição
    const form = document.getElementById("tipoProdutoForm");
    if (form) {
      form.addEventListener("submit", (e) => {
        e.preventDefault();
        if (this.validateForm()) {
          this.saveTipoProduto();
        }
      });

      // Carregar dados para edição se estiver no modo de edição
      const urlParams = new URLSearchParams(window.location.search);
      const id = urlParams.get("id");
      if (id) {
        document.querySelector("h1").textContent = "Editar Tipo de Produto";
        document.getElementById("idTipoProduto").value = id;
        this.loadTipoProdutoData(id);
      }
    }
  }

  async loadTiposProduto() {
    try {
      UIComponents.Loading.mostrar("Carregando tipos de produto...");
      const tiposProduto = await this.service.listarTodos();
      this.renderTable(tiposProduto);
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao carregar tipos de produto: " + error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  async loadTipoProdutoData(id) {
    try {
      UIComponents.Loading.mostrar("Carregando dados do tipo de produto...");
      const tipoProduto = await this.service.buscarPorId(id);
      document.getElementById("descricao").value = tipoProduto.descricao;
      UIComponents.Toast.sucesso("Dados carregados com sucesso");
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao carregar dados: " + error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  renderTable(tiposProduto) {
    if (!this.tableBody) return;

    this.tableBody.innerHTML = "";

    if (tiposProduto.length === 0) {
      const row = document.createElement("tr");
      row.innerHTML =
        '<td colspan="3" class="no-data">Nenhum tipo de produto encontrado</td>';
      this.tableBody.appendChild(row);
      return;
    }

    tiposProduto.forEach((tipo) => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${tipo.idtipoproduto}</td>
        <td>${tipo.descricao}</td>
        <td class="actions">
          <button class="btn-edit" onclick="tipoProdutoController.editTipoProduto(${tipo.idtipoproduto})">
            <i class="bi bi-pencil"></i>
          </button>
          <button class="btn-delete" onclick="tipoProdutoController.showDeleteModal(${tipo.idtipoproduto})">
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
        .buscarPorTermo(searchTerm)
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

  editTipoProduto(id) {
    window.location.href = `editarTipo.html?id=${id}`;
  }

  showDeleteModal(id) {
    this.itemToDelete = id;
    UIComponents.ModalConfirmacao.mostrar(
      "Confirmar exclusão",
      MensagensPadroes.CONFIRMACAO.EXCLUSAO,
      () => this.deleteTipoProduto(id)
    );
  }

  async deleteTipoProduto(id) {
    try {
      UIComponents.Loading.mostrar("Excluindo tipo de produto...");
      await this.service.excluir(id);
      UIComponents.Toast.sucesso("Tipo de produto excluído com sucesso!");
      this.loadTiposProduto(); // Recarrega a tabela
    } catch (error) {
      UIComponents.ModalErro.mostrar("Erro ao excluir: " + error.message);
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  validateForm() {
    let isValid = true;
    const descricao = document.getElementById("descricao");

    // Limpar erros anteriores
    UIComponents.Validacao.limparErros("tipoProdutoForm");

    // Validar campo de descrição
    if (!descricao.value.trim()) {
      UIComponents.Validacao.mostrarErro(
        "descricao",
        MensagensPadroes.VALIDACAO.CAMPO_OBRIGATORIO
      );
      isValid = false;
    }

    return isValid;
  }

  async saveTipoProduto() {
    const id = document.getElementById("idTipoProduto").value;
    const descricao = document.getElementById("descricao").value;

    // Criar objeto com os dados do formulário
    const tipoProduto = {
      idtipoproduto: id !== "0" ? parseInt(id) : null,
      descricao: descricao,
    };

    try {
      UIComponents.Loading.mostrar(
        tipoProduto.idtipoproduto
          ? "Atualizando tipo de produto..."
          : "Cadastrando tipo de produto..."
      );

      if (tipoProduto.idtipoproduto) {
        await this.service.atualizar(tipoProduto.idtipoproduto, tipoProduto);
        UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.ATUALIZACAO);
      } else {
        await this.service.cadastrar(tipoProduto);
        UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.CADASTRO);
      }

      // Após 2 segundos, redirecionar para a lista
      setTimeout(() => {
        window.location.href = "listarTipos.html";
      }, 2000);
    } catch (error) {
      UIComponents.ModalErro.mostrar("Erro ao salvar: " + error.message);
    } finally {
      UIComponents.Loading.esconder();
    }
  }
}

// Inicializar o controlador e carregar dados se estiver na página de listagem
document.addEventListener("DOMContentLoaded", () => {
  window.tipoProdutoController = new TipoProdutoController();

  const tableBody = document.getElementById("tipoProdutoTableBody");
  if (tableBody) {
    window.tipoProdutoController.loadTiposProduto();
  }
});

export default TipoProdutoController;

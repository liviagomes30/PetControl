import MedicacaoService from "../services/MedicacaoService.js";
import AnimalService from "../services/AnimalService.js";
import { medicamentoController } from "../controllers/MedicamentoController.js";
import UIComponents from "../components/uiComponents.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";
import HistoricoService from "../services/HistoricoService.js";
import ReceitaMedicamentoService from "../services/ReceitaMedicamentoService.js";
import PosologiaService from "../services/PosologiaService.js";

class HistoricoMedicacoesController {
  constructor() {
    this.medicacaoService = new MedicacaoService();
    this.animalService = new AnimalService();
    this.medicamentoService = medicamentoController.service;
    this.historicoService = new HistoricoService();
    this.receitaService = new ReceitaMedicamentoService();
    this.posologiaService = new PosologiaService();

    this.tableBody = document.getElementById("historicoMedicacoesTableBody");
    this.animalFilter = document.getElementById("animalFilter");
    this.dateFilter = document.getElementById("dateFilter");
    this.applyFilterButton = document.getElementById("applyFilterButton");
    this.clearFilterButton = document.getElementById("clearFilterButton");
    this.noDataMessage = document.getElementById("noDataMessage");
    this.tableElement = document.getElementById("historicoMedicacoesTable");

    this.allMedications = [];
    this.filteredMedications = [];
    this.cachedData = {
      animals: new Map(),
      medicamentos: new Map(),
      receitas: new Map(),
      posologias: new Map(),
    };

    this.bindEvents();
    this.init();
  }

  async init() {
    await this.loadInitialData();
    const urlParams = new URLSearchParams(window.location.search);
    const message = urlParams.get("message");
    if (message) {
      UIComponents.Toast.sucesso(decodeURIComponent(message));
      const newUrl = new URL(window.location.href);
      newUrl.searchParams.delete("message");
      window.history.replaceState({}, document.title, newUrl.toString());
    }
  }

  bindEvents() {
    if (this.applyFilterButton) {
      this.applyFilterButton.addEventListener("click", () =>
        this.applyFilters()
      );
    }
    if (this.clearFilterButton) {
      this.clearFilterButton.addEventListener("click", () =>
        this.clearFilters()
      );
    }
    if (this.animalFilter) {
      this.animalFilter.addEventListener("change", () => this.applyFilters());
    }
    if (this.dateFilter) {
      this.dateFilter.addEventListener("change", () => this.applyFilters());
    }

    const thSortable = document.querySelectorAll("th.sortable");
    thSortable.forEach((th) => {
      th.addEventListener("click", () => this.handleSort(th));
    });

    const confirmDeleteButton = document.getElementById("confirmDelete");
    if (confirmDeleteButton) {
      confirmDeleteButton.addEventListener("click", () =>
        this.deleteMedicacaoConfirmed()
      );
    }
    const cancelDeleteButton = document.getElementById("cancelDelete");
    if (cancelDeleteButton) {
      cancelDeleteButton.addEventListener("click", () =>
        UIComponents.ModalHelper.fecharModal("deleteModal")
      );
    }
  }

  async loadInitialData() {
    UIComponents.Loading.mostrar("Carregando histórico de medicações...");
    try {
      const medicacoes = await this.medicacaoService.listarTodos();

      const [animals, medicamentos, receitas, posologias] = await Promise.all([
        this.animalService.listarTodos(),
        this.medicamentoService.listarTodos(),
        this.receitaService.getAll(),
        this.posologiaService.getAll(),
      ]);

      animals.forEach((a) => this.cachedData.animals.set(a.id, a));
      medicamentos.forEach((m) =>
        this.cachedData.medicamentos.set(m.produto.idproduto, m)
      );
      receitas.forEach((r) => this.cachedData.receitas.set(r.idreceita, r));
      posologias.forEach((p) => {
        const key = `${p.medicamento_idproduto}-${p.receitamedicamento_idreceita}`;
        this.cachedData.posologias.set(key, p);
      });

      this.allMedications = await Promise.all(
        medicacoes.map(async (med) => this.enrichMedicacao(med))
      );
      this.filteredMedications = [...this.allMedications];

      this.populateAnimalFilter(animals);
      this.renderTable(this.filteredMedications);
    } catch (error) {
      console.error("Erro ao carregar dados iniciais:", error);
      UIComponents.ModalErro.mostrar(
        MensagensPadroes.ERRO.CARREGAMENTO +
          ": " +
          (error.message || "Erro desconhecido.")
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  async enrichMedicacao(medicacao) {
    const enriched = { ...medicacao };

    enriched.animal = this.cachedData.animals.get(medicacao.idanimal);
    enriched.nomeAnimal = enriched.animal ? enriched.animal.nome : "N/A";

    enriched.medicamentoCompleto = this.cachedData.medicamentos.get(
      medicacao.posologia_medicamento_idproduto
    );
    enriched.nomeMedicamento = enriched.medicamentoCompleto
      ? enriched.medicamentoCompleto.produto.nome
      : "N/A";
    enriched.composicao = enriched.medicamentoCompleto
      ? enriched.medicamentoCompleto.medicamento.composicao
      : "N/A";

    if (medicacao.posologia_receitamedicamento_idreceita) {
      enriched.receita = this.cachedData.receitas.get(
        medicacao.posologia_receitamedicamento_idreceita
      );
      enriched.nomeMedicoReceita = enriched.receita
        ? enriched.receita.medico
        : "N/A";
      enriched.nomeClinicaReceita = enriched.receita
        ? enriched.receita.clinica
        : "";

      const posologiaKey = `${medicacao.posologia_medicamento_idproduto}-${medicacao.posologia_receitamedicamento_idreceita}`;
      enriched.posologia = this.cachedData.posologias.get(posologiaKey);
      enriched.dose = enriched.posologia ? enriched.posologia.dose : "N/A";
      enriched.intervalohoras = enriched.posologia
        ? enriched.posologia.intervalohoras
        : "N/A";
      enriched.quantidadedias = enriched.posologia
        ? enriched.posologia.quantidadedias
        : "N/A";
    } else {
      enriched.receita = null;
      enriched.nomeMedicoReceita = "Sem Receita";
      enriched.nomeClinicaReceita = "";
      enriched.posologia = null;
      enriched.dose = "N/A";
      enriched.intervalohoras = "N/A";
      enriched.quantidadedias = "N/A";
    }

    if (medicacao.idhistorico) {
      try {
        const historico = await this.historicoService.getId(
          medicacao.idhistorico
        );
        enriched.descricaoHistorico = historico ? historico.descricao : "N/A";
      } catch (error) {
        console.error(
          "Erro ao carregar histórico para medicação " +
            medicacao.idmedicacao +
            ":",
          error
        );
        enriched.descricaoHistorico = "Erro ao carregar";
      }
    } else {
      enriched.descricaoHistorico = "N/A";
    }

    return enriched;
  }

  renderTable(medications) {
    if (!this.tableBody) return;

    this.tableBody.innerHTML = "";
    if (medications.length === 0) {
      this.tableElement.style.display = "none";
      this.noDataMessage.style.display = "block";
      this.noDataMessage.querySelector("p").textContent =
        "Nenhum histórico de medicação encontrado.";
      return;
    }

    this.tableElement.style.display = "table";
    this.noDataMessage.style.display = "none";

    medications.forEach((med) => {
      const tr = document.createElement("tr");
      const dataFormatada = med.data
        ? new Date(med.data).toLocaleDateString("pt-BR")
        : "N/A";
      const posologiaDisplay = med.posologia
        ? `${med.dose} (${med.quantidadedias} dias, a cada ${med.intervalohoras}h)`
        : "N/A";

      tr.innerHTML = `
        <td>${med.idmedicacao || "N/A"}</td>
        <td>${dataFormatada}</td>
        <td>${med.nomeAnimal}</td>
        <td>${med.nomeMedicamento}</td>
        <td>${med.composicao}</td>
        <td>${posologiaDisplay}</td>
        <td>${med.descricaoHistorico || "N/A"}</td>
        <td class="actions">
          <button class="btn-icon btn-view" data-id="${
            med.idmedicacao
          }" aria-label="Ver detalhes">
            <i class="bi bi-eye"></i>
          </button>
          <button class="btn-icon btn-delete" data-id="${
            med.idmedicacao
          }" aria-label="Excluir medicação">
            <i class="bi bi-trash"></i>
          </button>
        </td>
      `;
      this.tableBody.appendChild(tr);
    });

    this.tableBody.querySelectorAll(".btn-view").forEach((button) => {
      button.addEventListener("click", (e) =>
        this.viewDetails(e.currentTarget.dataset.id)
      );
    });
    this.tableBody.querySelectorAll(".btn-delete").forEach((button) => {
      button.addEventListener("click", (e) =>
        this.showDeleteModal(e.currentTarget.dataset.id)
      );
    });
  }

  populateAnimalFilter(animals) {
    if (!this.animalFilter) return;

    while (this.animalFilter.options.length > 1) {
      this.animalFilter.remove(1);
    }

    animals.forEach((animal) => {
      const option = document.createElement("option");
      option.value = animal.id;
      option.textContent = animal.nome;
      this.animalFilter.appendChild(option);
    });
  }

  applyFilters() {
    const selectedAnimalId = this.animalFilter.value;
    const selectedDate = this.dateFilter.value;

    this.filteredMedications = this.allMedications.filter((med) => {
      let matchesAnimal = true;
      let matchesDate = true;

      if (selectedAnimalId) {
        matchesAnimal = med.idanimal === parseInt(selectedAnimalId);
      }

      if (selectedDate) {
        const medDate = med.data
          ? new Date(med.data).toISOString().split("T")[0]
          : "";
        matchesDate = medDate === selectedDate;
      }
      return matchesAnimal && matchesDate;
    });

    this.renderTable(this.filteredMedications);
  }

  clearFilters() {
    if (this.animalFilter) this.animalFilter.value = "";
    if (this.dateFilter) this.dateFilter.value = "";
    this.filteredMedications = [...this.allMedications];
    this.renderTable(this.filteredMedications);
  }

  handleSort(headerElement) {
    const sortBy = headerElement.dataset.sort;
    let sortOrder = headerElement.dataset.order === "asc" ? "desc" : "asc";

    document.querySelectorAll("th.sortable").forEach((th) => {
      th.classList.remove("asc", "desc");
      th.dataset.order = "";
    });

    headerElement.classList.add(sortOrder);
    headerElement.dataset.order = sortOrder;

    this.filteredMedications.sort((a, b) => {
      const aValue = a[sortBy] !== undefined ? a[sortBy] : "";
      const bValue = b[sortBy] !== undefined ? b[sortBy] : "";

      if (typeof aValue === "string" && typeof bValue === "string") {
        return sortOrder === "asc"
          ? aValue.localeCompare(bValue)
          : bValue.localeCompare(aValue);
      }
      if (aValue < bValue) return sortOrder === "asc" ? -1 : 1;
      if (aValue > bValue) return sortOrder === "asc" ? 1 : -1;
      return 0;
    });

    this.renderTable(this.filteredMedications);
  }

  viewDetails(id) {
    const medicacao = this.allMedications.find((m) => m.idmedicacao == id);
    if (medicacao) {
      const modalBody = document.getElementById("detailsModalBody");
      if (modalBody) {
        modalBody.innerHTML = `
          <div class="row">
            <div class="col-md-6 mb-2"><strong>ID Medicação:</strong> ${
              medicacao.idmedicacao || "N/A"
            }</div>
            <div class="col-md-6 mb-2"><strong>Data:</strong> ${
              medicacao.data
                ? new Date(medicacao.data).toLocaleDateString("pt-BR")
                : "N/A"
            }</div>
            <div class="col-md-12 mb-2"><strong>Animal:</strong> ${
              medicacao.nomeAnimal || "N/A"
            }</div>
            <div class="col-md-12 mb-2"><strong>Medicamento:</strong> ${
              medicacao.nomeMedicamento || "N/A"
            } (${medicacao.composicao || "N/A"})</div>
            <div class="col-md-12 mb-2">
              <strong>Posologia:</strong> ${
                medicacao.posologia
                  ? medicacao.dose +
                    " (" +
                    medicacao.quantidadedias +
                    " dias, a cada " +
                    medicacao.intervalohoras +
                    "h)"
                  : "N/A"
              }
            </div>
            <div class="col-md-12 mb-2">
              <strong>Receita:</strong> ${
                medicacao.receita
                  ? medicacao.nomeMedicoReceita +
                    (medicacao.nomeClinicaReceita
                      ? " - " + medicacao.nomeClinicaReceita
                      : "")
                  : "Sem Receita"
              }
            </div>
            <div class="col-md-12 mb-2"><strong>Descrição Histórico:</strong> ${
              medicacao.descricaoHistorico || "N/A"
            }</div>
          </div>
        `;
        const detailsModalElement = document.getElementById("detailsModal");
        const modal = new bootstrap.Modal(detailsModalElement);
        modal.show();
      } else {
        UIComponents.Toast.alerta(
          "Elemento do modal de detalhes não encontrado."
        );
      }
    } else {
      UIComponents.Toast.alerta(
        "Detalhes da medicação não encontrados para o ID fornecido."
      );
    }
  }

  showDeleteModal(id) {
    this.idToDelete = id;
    UIComponents.ModalConfirmacao.mostrar(
      "Confirmar Exclusão",
      MensagensPadroes.CONFIRMACAO.EXCLUSAO,
      () => this.deleteMedicacaoConfirmed()
    );
  }

  async deleteMedicacaoConfirmed() {
    UIComponents.Loading.mostrar("Excluindo medicação...");
    try {
      const response = await this.medicacaoService.apagar(this.idToDelete);

      if (response.ok) {
        UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.EXCLUSAO);
        await this.loadInitialData();
        this.applyFilters();
      } else {
        const errorText = await response.text();
        throw new Error(errorText || "Erro desconhecido ao excluir.");
      }
    } catch (error) {
      console.error("Erro ao excluir medicação:", error);
      UIComponents.ModalErro.mostrar(
        MensagensPadroes.ERRO.EXCLUSAO +
          ": " +
          (error.message || "Erro desconhecido.")
      );
    } finally {
      UIComponents.Loading.esconder();
      const deleteModalElement = document.getElementById("deleteModal");
      const modalInstance = bootstrap.Modal.getInstance(deleteModalElement);
      if (modalInstance) {
        modalInstance.hide();
      }
    }
  }
}

document.addEventListener("DOMContentLoaded", () => {
  window.historicoMedicacoesController = new HistoricoMedicacoesController();
});

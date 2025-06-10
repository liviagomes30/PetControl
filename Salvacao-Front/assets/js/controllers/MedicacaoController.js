import MedicacaoService from "../services/MedicacaoService.js";
import AnimalService from "../services/AnimalService.js";
import { medicamentoController } from "../controllers/MedicamentoController.js";
import UIComponents from "../components/uiComponents.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";

class MedicacaoController {
  constructor() {
    this.medicacaoService = new MedicacaoService();
    this.animalService = new AnimalService();
    this.medicamentoService = medicamentoController.service;
    this.init();
  }

  init() {
    this.setupEventListeners();
    this.carregarDadosIniciais();
  }

  setupEventListeners() {
    document
      .getElementById("formEfetuarMedicacao")
      .addEventListener("submit", (e) => this.handleSubmit(e));
    document
      .getElementById("animal")
      .addEventListener("change", () => this.handleAnimalChange());
    document
      .getElementById("receita")
      .addEventListener("change", () => this.handleReceitaChange());

    const today = new Date().toISOString().split("T")[0];
    document.getElementById("dataMedicao").value = today;
  }

  async carregarDadosIniciais() {
    UIComponents.Loading.mostrar("Carregando dados...");
    try {
      const animals = await this.animalService.listarTodos();
      this.popularSelect(
        document.getElementById("animal"),
        animals,
        "id",
        "nome"
      );
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao carregar animais: " + error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  async handleAnimalChange() {
    const animalId = document.getElementById("animal").value;
    const selectReceita = document.getElementById("receita");

    this.renderMedicamentosList([]);

    selectReceita.innerHTML = '<option value="">Sem receita</option>';
    if (animalId) {
      UIComponents.Loading.mostrar("Carregando receitas...");
      try {
        const receitas = await this.medicacaoService.listarReceitasPorAnimal(
          animalId
        );
        this.popularSelect(
          selectReceita,
          receitas,
          "idreceita",
          "data",
          " - ",
          "medico",
          true
        );
      } catch (error) {
        UIComponents.Toast.alerta(
          "Não foi possível carregar as receitas para este animal."
        );
      } finally {
        UIComponents.Loading.esconder();
        this.handleReceitaChange();
      }
    } else {
      this.renderMedicamentosList([]);
    }
  }

  async handleReceitaChange() {
    const receitaId = document.getElementById("receita").value;
    const animalId = document.getElementById("animal").value; // Precisamos do ID do animal

    if (!animalId) {
      this.renderMedicamentosList([]);
      return;
    }

    UIComponents.Loading.mostrar("Buscando medicamentos...");
    try {
      if (receitaId) {
        const response = await fetch(
          `http://localhost:8080/posologias/receita/${receitaId}?animalId=${animalId}`
        );

        if (response.status === 204) {
          this.renderMedicamentosList([], true);
          UIComponents.Toast.sucesso(
            "Todos os medicamentos desta receita já foram aplicados!"
          );
        } else if (response.ok) {
          const posologias = await response.json();
          const medicamentosDaReceita = posologias.map((p) => ({
            idproduto: p.medicamento_idproduto,
            nome: p.medicamentoNome,
            composicao: p.medicamentoComposicao,
            doseRecomendada: p.dose,
          }));
          this.renderMedicamentosList(medicamentosDaReceita, true);
        } else {
          this.renderMedicamentosList([], true);
        }
      } else {
        const todosMedicamentos =
          await this.medicamentoService.listarTodosDisponiveis();
        const medicamentosDisponiveis = todosMedicamentos.map((m) => ({
          idproduto: m.produto.idproduto,
          nome: m.produto.nome,
          composicao: m.medicamento.composicao,
          doseRecomendada: null,
          quantidadeEstoque: m.quantidade,
          unidade: m.unidadeMedida.sigla,
        }));
        this.renderMedicamentosList(medicamentosDisponiveis, false);
      }
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao buscar medicamentos: " + error.message
      );
      this.renderMedicamentosList([]);
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  renderMedicamentosList(medicamentos, isFromReceita = false) {
    const container = document.getElementById("medicamentoListContainer");
    if (!container) return;

    if (!medicamentos || medicamentos.length === 0) {
      const placeholderText = isFromReceita
        ? "Nenhum medicamento encontrado para esta receita."
        : "Nenhum medicamento disponível em estoque.";
      container.innerHTML = `<p class="text-muted mb-0">${placeholderText}</p>`;
      return;
    }

    let itemsHtml = "";
    medicamentos.forEach((med) => {
      const estoqueInfo =
        med.quantidadeEstoque !== undefined
          ? `(Estoque: ${med.quantidadeEstoque} ${med.unidade || ""})`
          : "";

      // Nova estrutura usando a classe .form-check do Bootstrap corretamente
      itemsHtml += `
        <div class="border-bottom py-2">
          <div class="form-check">
            <input class="form-check-input" type="checkbox" value="${
              med.idproduto
            }" id="med-${med.idproduto}" data-nome="${med.nome}">
            <label class="form-check-label" for="med-${
              med.idproduto
            }" style="cursor:pointer;">
                <strong>${
                  med.nome
                }</strong> <small class="text-success fw-bold">${estoqueInfo}</small>
                <small class="d-block text-muted">${
                  med.composicao || ""
                }</small>
                ${
                  med.doseRecomendada
                    ? `<small class="d-block text-info">Dose Receitada: ${med.doseRecomendada}</small>`
                    : ""
                }
            </label>
          </div>
          <div class="ps-4 mt-2" id="inputs-container-${
            med.idproduto
          }" style="display: none;">
            <div class="input-group input-group-sm" style="max-width: 400px;">
                <span class="input-group-text">Qtd.</span>
                <input type="number" class="form-control" placeholder="0.0" step="0.1" min="0" id="qtd-${
                  med.idproduto
                }">
                <span class="input-group-text">Obs.</span>
                <input type="text" class="form-control" placeholder="Opcional (para o histórico)" id="obs-${
                  med.idproduto
                }">
            </div>
          </div>
        </div>
      `;
    });

    container.innerHTML = itemsHtml;

    container.querySelectorAll(".form-check-input").forEach((chk) => {
      chk.addEventListener("change", (e) => {
        const id = e.target.value;
        const inputsContainer = document.getElementById(
          `inputs-container-${id}`
        );
        if (inputsContainer) {
          inputsContainer.style.display = e.target.checked ? "block" : "none";
        }
      });
    });
  }

  async handleSubmit(e) {
    e.preventDefault();
    const form = document.getElementById("formEfetuarMedicacao");
    if (!form.checkValidity()) {
      form.classList.add("was-validated");
      return;
    }

    const selectedMeds = [];
    const checkboxes = document.querySelectorAll(
      "#medicamentoListContainer .form-check-input:checked"
    );

    let hasInvalidQuantity = false;
    checkboxes.forEach((chk) => {
      const id = chk.value;
      const quantidadeInput = document.getElementById(`qtd-${id}`);
      const quantidade = quantidadeInput.value;

      if (!quantidade || parseFloat(quantidade) <= 0) {
        quantidadeInput.classList.add("is-invalid");
        hasInvalidQuantity = true;
      } else {
        quantidadeInput.classList.remove("is-invalid");
        selectedMeds.push({
          idMedicamentoProduto: parseInt(id),
          quantidadeAdministrada: parseFloat(quantidade),
          descricaoHistorico: document.getElementById(`obs-${id}`).value.trim(),
        });
      }
    });

    if (hasInvalidQuantity) {
      UIComponents.Toast.alerta(
        "Informe uma quantidade válida para os medicamentos selecionados."
      );
      return;
    }

    if (selectedMeds.length === 0) {
      UIComponents.Toast.alerta("Selecione ao menos um medicamento.");
      return;
    }

    const payload = {
      idAnimal: parseInt(document.getElementById("animal").value),
      idReceitaMedicamento: document.getElementById("receita").value
        ? parseInt(document.getElementById("receita").value)
        : null,
      dataMedicao: document.getElementById("dataMedicao").value,
      medicacoes: selectedMeds,
    };

    UIComponents.Loading.mostrar("Registrando medicação...");
    try {
      const response = await fetch(
        "http://localhost:8080/medicacoes/efetuar-lote",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        }
      );

      const result = await response.json();
      if (!response.ok) {
        throw new Error(result.mensagem || "Erro no servidor.");
      }

      UIComponents.Toast.sucesso(result.mensagem);
      setTimeout(
        () => (window.location.href = "historicoMedicacoes.html"),
        2000
      );
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Falha ao registrar medicação: " + error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  popularSelect(
    select,
    items,
    valueField,
    textField1,
    separator = "",
    textField2 = null,
    append = false
  ) {
    if (!append) {
      const firstOption = select.options[0];
      select.innerHTML = "";
      if (firstOption) select.appendChild(firstOption);
    }

    items.forEach((item) => {
      const option = document.createElement("option");
      option.value = this.getNestedValue(item, valueField);
      let text = this.getNestedValue(item, textField1);
      if (textField2 && this.getNestedValue(item, textField2)) {
        if (textField1 === "data") {
          try {
            text = new Date(text).toLocaleDateString("pt-BR", {
              timeZone: "UTC",
            });
          } catch (e) {}
        }
        text += separator + this.getNestedValue(item, textField2);
      }
      option.textContent = text;
      select.appendChild(option);
    });
  }

  getNestedValue(obj, path) {
    if (!obj || !path) return "";
    return path.split(".").reduce((acc, part) => acc && acc[part], obj);
  }
}

document.addEventListener("DOMContentLoaded", () => {
  new MedicacaoController();
});

export default MedicacaoController;

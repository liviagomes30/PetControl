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
    this.estoqueAtual = 0;
    this.unidadeMedidaSigla = "";
    this.init();
  }

  async init() {
    this.setupEventListeners();
    this.configurarMascarasCampos();
    await this.carregarDadosIniciais();
  }

  setupEventListeners() {
    const form = document.getElementById("formEfetuarMedicacao");
    if (form) {
      form.addEventListener("submit", (e) => this.handleSubmit(e));
    }

    document
      .getElementById("animal")
      .addEventListener("change", () => this.handleAnimalChange());
    document
      .getElementById("medicamento")
      .addEventListener("change", () => this.handleMedicamentoChange());
    document
      .getElementById("receita")
      .addEventListener("change", () => this.handleReceitaChange());
    document
      .getElementById("quantidade")
      .addEventListener("input", () => this.validarQuantidadeEstoque());

    // Set today's date as default for dataMedicao
    const today = new Date().toISOString().split("T")[0];
    document.getElementById("dataMedicao").value = today;

    // Bootstrap validation (client-side)
    (() => {
      "use strict";
      const forms = document.querySelectorAll(".needs-validation");
      Array.from(forms).forEach((form) => {
        form.addEventListener(
          "submit",
          (event) => {
            if (!form.checkValidity()) {
              event.preventDefault();
              event.stopPropagation();
            }
            form.classList.add("was-validated");
          },
          false
        );
      });
    })();
  }

  configurarMascarasCampos() {
    UIComponents.InputMasks.inicializar();
  }

  async carregarDadosIniciais() {
    UIComponents.Loading.mostrar("Carregando dados...");
    try {
      await this.carregarAnimais();
      await this.carregarMedicamentos();
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

  async carregarAnimais() {
    try {
      const animals = await this.animalService.listarTodos();
      const selectAnimal = document.getElementById("animal");
      this.popularSelect(selectAnimal, animals, "id", "nome");
    } catch (error) {
      console.error("Erro ao carregar animais:", error);
      throw new Error("Não foi possível carregar a lista de animais.");
    }
  }

  async carregarMedicamentos() {
    try {
      const medications = await this.medicamentoService.listarTodos();
      const selectMedicamento = document.getElementById("medicamento");
      this.popularSelect(
        selectMedicamento,
        medications,
        "produto.idproduto",
        "produto.nome"
      );
    } catch (error) {
      console.error("Erro ao carregar medicamentos:", error);
      throw new Error("Não foi possível carregar a lista de medicamentos.");
    }
  }

  async handleAnimalChange() {
    const animalId = document.getElementById("animal").value;
    const selectReceita = document.getElementById("receita");
    selectReceita.innerHTML = '<option value="">Selecione uma receita</option>';
    selectReceita.disabled = true; // Disable until a valid animal is selected
    const selectPosologia = document.getElementById("posologia");
    selectPosologia.innerHTML =
      '<option value="">Selecione um medicamento e receita</option>';
    selectPosologia.disabled = true;

    if (animalId) {
      UIComponents.Loading.mostrar("Carregando receitas do animal...");
      try {
        // Assume a service method for fetching recipes by animalId
        const receitas = await this.medicacaoService.listarReceitasPorAnimal(
          parseInt(animalId)
        );
        this.popularSelect(
          selectReceita,
          receitas,
          "idreceita",
          "medico",
          " - ",
          "clinica"
        );
        selectReceita.disabled = false;
      } catch (error) {
        console.error("Erro ao carregar receitas:", error);
        UIComponents.Toast.erro(
          "Não foi possível carregar as receitas para este animal."
        );
      } finally {
        UIComponents.Loading.esconder();
      }
    }
  }

  async handleMedicamentoChange() {
    const medicamentoId = document.getElementById("medicamento").value;
    const animalId = document.getElementById("animal").value;
    const selectReceita = document.getElementById("receita");
    const selectPosologia = document.getElementById("posologia");
    selectPosologia.innerHTML =
      '<option value="">Selecione um medicamento e receita</option>';
    selectPosologia.disabled = true;

    if (medicamentoId && animalId && selectReceita.value) {
      // Ensure recipe is also selected to enable posology
      await this.updatePosologiaOptions(
        parseInt(medicamentoId),
        parseInt(selectReceita.value)
      );
      await this.updateEstoqueDisplay(parseInt(medicamentoId));
    } else {
      // Reset estoque display if no valid medication is selected
      document.getElementById("estoqueDisponivel").textContent = "0,00";
      document.getElementById("unidadeMedidaDisplay").textContent = "";
      this.estoqueAtual = 0;
      this.unidadeMedidaSigla = "";
    }
  }

  async handleReceitaChange() {
    const receitaId = document.getElementById("receita").value;
    const medicamentoId = document.getElementById("medicamento").value;
    const selectPosologia = document.getElementById("posologia");
    selectPosologia.innerHTML =
      '<option value="">Selecione um medicamento e receita</option>';
    selectPosologia.disabled = true;

    if (receitaId && medicamentoId) {
      await this.updatePosologiaOptions(
        parseInt(medicamentoId),
        parseInt(receitaId)
      );
    }
  }

  async updatePosologiaOptions(medicamentoId, receitaId) {
    const selectPosologia = document.getElementById("posologia");
    selectPosologia.innerHTML =
      '<option value="">Carregando posologias...</option>';
    selectPosologia.disabled = true;
    UIComponents.Loading.mostrar("Carregando posologias...");
    try {
      // Assume a service method for fetching posology by medicationId and recipeId
      const posologia = await this.medicacaoService.buscarPosologia(
        medicamentoId,
        receitaId
      );

      selectPosologia.innerHTML =
        '<option value="">Selecione uma posologia</option>'; // Reset
      if (posologia) {
        const option = document.createElement("option");
        option.value = `${posologia.medicamento_idproduto}-${posologia.receitamedicamento_idreceita}`;
        option.textContent = `Dose: ${posologia.dose}, Dias: ${posologia.quantidadedias}, Intervalo: ${posologia.intervalohoras}h`;
        selectPosologia.appendChild(option);
        selectPosologia.value = option.value; // Select it automatically
        selectPosologia.disabled = false;
      } else {
        UIComponents.Toast.alerta(
          "Nenhuma posologia encontrada para a combinação de medicamento e receita selecionada."
        );
      }
    } catch (error) {
      console.error("Erro ao carregar posologias:", error);
      UIComponents.Toast.erro("Não foi possível carregar as posologias.");
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  async updateEstoqueDisplay(medicamentoId) {
    UIComponents.Loading.mostrar("Consultando estoque...");
    try {
      const estoque = await this.medicacaoService.obterEstoqueAtual(
        medicamentoId
      ); // Assume this method in MedicacaoService
      const medicamentoDetalhes = await this.medicamentoService.buscarPorId(
        medicamentoId
      );

      this.estoqueAtual = parseFloat(estoque.quantidade || 0);
      this.unidadeMedidaSigla = medicamentoDetalhes.unidadeMedida?.sigla || "";

      document.getElementById("estoqueDisponivel").textContent =
        this.estoqueAtual.toFixed(2).replace(".", ",");
      document.getElementById("unidadeMedidaDisplay").textContent =
        this.unidadeMedidaSigla;

      this.validarQuantidadeEstoque(); // Re-validate quantity after stock update
    } catch (error) {
      console.error("Erro ao obter estoque:", error);
      this.estoqueAtual = 0;
      this.unidadeMedidaSigla = "";
      document.getElementById("estoqueDisponivel").textContent = "0,00";
      document.getElementById("unidadeMedidaDisplay").textContent = "";
      UIComponents.Toast.erro(
        "Não foi possível consultar o estoque disponível."
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  validarQuantidadeEstoque() {
    const quantidadeInput = document.getElementById("quantidade");
    const quantidadeValue =
      UIComponents.InputMasks.obterValorNumerico(quantidadeInput);
    const alertaEstoque = document.getElementById("alertaEstoqueBaixo");

    if (quantidadeValue !== null && quantidadeValue > this.estoqueAtual) {
      alertaEstoque.style.display = "flex";
      quantidadeInput.classList.add("is-invalid");
      UIComponents.Validacao.mostrarErro(
        "quantidade",
        MensagensPadroes.ERRO.ESTOQUE_INSUFICIENTE
      );
      return false;
    } else {
      alertaEstoque.style.display = "none";
      cantidadInput.classList.remove("is-invalid");
      UIComponents.Validacao.limparErros("formEfetuarMedicacao"); // Clears all errors, consider clearing specific ones
      return true;
    }
  }

  popularSelect(
    selectElement,
    items,
    valuePath,
    textPath1,
    separator = "",
    textPath2 = null
  ) {
    selectElement.innerHTML = '<option value="">Selecione</option>';
    items.forEach((item) => {
      const option = document.createElement("option");
      option.value = this.getNestedValue(item, valuePath);
      let text = this.getNestedValue(item, textPath1);
      if (textPath2 && this.getNestedValue(item, textPath2)) {
        text += separator + this.getNestedValue(item, textPath2);
      }
      option.textContent = text;
      selectElement.appendChild(option);
    });
  }

  getNestedValue(obj, path) {
    return path.split(".").reduce((acc, part) => acc && acc[part], obj);
  }

  async handleSubmit(event) {
    event.preventDefault();
    UIComponents.Validacao.limparErros("formEfetuarMedicacao");

    const form = document.getElementById("formEfetuarMedicacao");
    if (!form.checkValidity() || !this.validarQuantidadeEstoque()) {
      form.classList.add("was-validated");
      UIComponents.Toast.alerta(MensagensPadroes.ALERTA.CAMPOS_OBRIGATORIOS);
      return;
    }

    const animalId = document.getElementById("animal").value;
    const medicamentoId = document.getElementById("medicamento").value;
    const receitaId = document.getElementById("receita").value;
    const quantidade = UIComponents.InputMasks.obterValorNumerico(
      document.getElementById("quantidade")
    );
    const dataMedicao = document.getElementById("dataMedicao").value;
    const descricaoHistorico =
      document.getElementById("descricaoHistorico").value;

    try {
      UIComponents.Loading.mostrar("Efetuando medicação...");
      const result = await this.medicacaoService.efetuarMedicacao({
        idAnimal: parseInt(animalId),
        idMedicamentoProduto: parseInt(medicamentoId),
        idReceitaMedicamento: parseInt(receitaId),
        quantidadeAdministrada: quantidade,
        dataMedicao: dataMedicao,
        descricaoHistorico: descricaoHistorico,
      });

      if (result.sucesso) {
        UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.MEDICACAO);
        setTimeout(() => {
          window.location.href =
            "historicoMedicacoes.html?message=" +
            encodeURIComponent(MensagensPadroes.SUCESSO.MEDICACAO);
        }, 2000);
      } else {
        UIComponents.ModalErro.mostrar(
          result.mensagem || MensagensPadroes.ERRO.GERAL
        );
      }
    } catch (error) {
      console.error("Erro ao efetuar medicação:", error);
      UIComponents.ModalErro.mostrar(
        error.message || MensagensPadroes.ERRO.GERAL
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }
}

const medicacaoController = new MedicacaoController();
window.medicacaoController = medicacaoController;

export { medicacaoController };
export default MedicacaoController;

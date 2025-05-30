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

  vincularLimpezaAutomaticaValidacao() {
    const camposFormPrincipal = [
      { id: "animal", evento: "change" },
      { id: "medicamento", evento: "change" },
      { id: "receita", evento: "change" },
      { id: "posologia", evento: "change" },
      { id: "quantidade", evento: "input" },
      { id: "dataMedicao", evento: "change" },
      { id: "descricaoHistorico", evento: "input" },
    ];

    camposFormPrincipal.forEach((config) => {
      const elemento = document.getElementById(config.id);
      if (elemento) {
        elemento.addEventListener(config.evento, function () {
          if (
            (this.tagName === "SELECT" && this.value) ||
            (this.tagName !== "SELECT" && this.value.trim())
          ) {
            UIComponents.Validacao.limparErroCampo(config.id);
          }
        });
      }
    });

    const camposModalPosologia = [
      { id: "modalPosologiaDose", evento: "input" },
      { id: "modalPosologiaQuantidadeDias", evento: "input" },
      { id: "modalPosologiaIntervaloHoras", evento: "input" },
    ];

    camposModalPosologia.forEach((config) => {
      const elemento = document.getElementById(config.id);
      if (elemento) {
        elemento.addEventListener(config.evento, function () {
          if (this.value.trim()) {
            UIComponents.Validacao.limparErroCampo(config.id);
          }
        });
      }
    });
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

    const quantidadeInput = document.getElementById("quantidade");
    if (quantidadeInput) {
      quantidadeInput.addEventListener("input", () =>
        this.validarQuantidadeEstoque()
      ); //
    }

    // Configuração do botão "Cadastrar Nova Posologia"
    const btnCadastrarPosologia = document.getElementById(
      "btnCadastrarPosologia"
    );
    if (btnCadastrarPosologia) {
      btnCadastrarPosologia.addEventListener("click", () =>
        this.abrirModalCadastrarPosologia()
      );
    }
    const btnSalvarNovaPosologia = document.getElementById(
      "btnSalvarNovaPosologia"
    );
    if (btnSalvarNovaPosologia) {
      btnSalvarNovaPosologia.addEventListener("click", () =>
        this.salvarNovaPosologia()
      );
    }

    // Define a data de hoje como padrão para dataMedicao
    const today = new Date().toISOString().split("T")[0];
    document.getElementById("dataMedicao").value = today;

    this.vincularLimpezaAutomaticaValidacao();
    // Validação Bootstrap (lado do cliente)
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
    // Receitas agora podem ser carregadas mas o select pode estar vazio
    selectReceita.innerHTML = '<option value="">Selecione uma receita</option>';
    selectReceita.disabled = false; // Mantenha habilitado para permitir "Nenhuma Receita"

    const selectPosologia = document.getElementById("posologia");
    selectPosologia.innerHTML =
      '<option value="">Selecione um medicamento e/ou receita</option>';
    selectPosologia.disabled = true; // Posologia desabilitada até que receita e medicamento sejam selecionados ou seja uma medicação sem receita.
    this.toggleBotaoCadastrarPosologia(false);

    if (animalId) {
      UIComponents.Loading.mostrar("Carregando receitas do animal...");
      try {
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
      } catch (error) {
        console.error("Erro ao carregar receitas:", error);
        UIComponents.Toast.erro(
          "Não foi possível carregar as receitas para este animal."
        );
      } finally {
        UIComponents.Loading.esconder();
      }
    }
    // Chame handleMedicamentoChange para reavaliar o estado da posologia
    this.handleMedicamentoChange();
  }

  async handleMedicamentoChange() {
    const medicamentoId = document.getElementById("medicamento").value;
    const animalId = document.getElementById("animal").value;
    const selectReceita = document.getElementById("receita");
    const selectPosologia = document.getElementById("posologia");

    selectPosologia.innerHTML =
      '<option value="">Selecione um medicamento e/ou receita</option>';
    selectPosologia.disabled = true;
    this.toggleBotaoCadastrarPosologia(false);

    if (medicamentoId) {
      await this.updateEstoqueDisplay(parseInt(medicamentoId));
    } else {
      document.getElementById("estoqueDisponivel").textContent = "0,00";
      document.getElementById("unidadeMedidaDisplay").textContent = "";
      this.estoqueAtual = 0;
      this.unidadeMedidaSigla = "";
    }

    // Se medicamento, animal e receita foram selecionados, tente carregar a posologia
    if (medicamentoId && animalId && selectReceita.value) {
      await this.updatePosologiaOptions(
        parseInt(medicamentoId),
        parseInt(selectReceita.value)
      );
    } else if (medicamentoId && animalId && !selectReceita.value) {
      // Se medicamento e animal selecionados, mas SEM receita
      UIComponents.Toast.info(
        "Posologia opcional: Cadastre uma nova posologia ou proceda sem uma."
      );
      // Habilita o botão de cadastrar posologia se houver medicamento e animal e NÃO receita
      this.toggleBotaoCadastrarPosologia(true);
    }
  }

  async handleReceitaChange() {
    const receitaId = document.getElementById("receita").value;
    const medicamentoId = document.getElementById("medicamento").value;
    const animalId = document.getElementById("animal").value;
    const selectPosologia = document.getElementById("posologia");

    selectPosologia.innerHTML =
      '<option value="">Selecione um medicamento e/ou receita</option>';
    selectPosologia.disabled = true;
    this.toggleBotaoCadastrarPosologia(false);

    if (receitaId && medicamentoId && animalId) {
      await this.updatePosologiaOptions(
        parseInt(medicamentoId),
        parseInt(receitaId)
      );
    } else if (!receitaId && medicamentoId && animalId) {
      // Se a receita foi desmarcada, mas medicamento e animal ainda estão selecionados
      UIComponents.Toast.info(
        "Posologia opcional: Cadastre uma nova posologia ou proceda sem uma."
      );
      this.toggleBotaoCadastrarPosologia(true);
    }
  }

  async updatePosologiaOptions(medicamentoId, receitaId) {
    const selectPosologia = document.getElementById("posologia");
    selectPosologia.innerHTML =
      '<option value="">Carregando posologias...</option>';
    selectPosologia.disabled = true;
    this.toggleBotaoCadastrarPosologia(false);

    UIComponents.Loading.mostrar("Carregando posologias...");
    try {
      const posologia = await this.medicacaoService.buscarPosologia(
        medicamentoId,
        receitaId
      );

      selectPosologia.innerHTML =
        '<option value="">Selecione uma posologia</option>'; // Reset
      if (posologia) {
        const option = document.createElement("option");
        // O valor da posologia é a combinação das chaves primárias
        option.value = `${posologia.medicamento_idproduto}-${posologia.receitamedicamento_idreceita}`;
        option.textContent = `Dose: ${posologia.dose}, Dias: ${posologia.quantidadedias}, Intervalo: ${posologia.intervalohoras}h`;
        selectPosologia.appendChild(option);
        selectPosologia.value = option.value; // Seleciona automaticamente
        selectPosologia.disabled = false;
        UIComponents.Toast.sucesso("Posologia carregada com sucesso!");
      } else {
        UIComponents.Toast.alerta(
          "Nenhuma posologia encontrada para a combinação de medicamento e receita selecionada."
        );
        this.toggleBotaoCadastrarPosologia(true); // Habilita o botão para cadastrar nova posologia
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
      );
      const medicamentoDetalhes = await this.medicamentoService.buscarPorId(
        medicamentoId
      );

      this.estoqueAtual = parseFloat(estoque.quantidade || 0);
      this.unidadeMedidaSigla = medicamentoDetalhes.unidadeMedida?.sigla || "";

      document.getElementById("estoqueDisponivel").textContent =
        this.estoqueAtual.toFixed(2).replace(".", ",");
      document.getElementById("unidadeMedidaDisplay").textContent =
        this.unidadeMedidaSigla;

      this.validarQuantidadeEstoque(); // Revalida a quantidade após a atualização do estoque
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

    // Limpa erros anteriores de quantidade
    UIComponents.Validacao.limparErros("formEfetuarMedicacao"); // Limpa todos, pode ser mais específico se necessário
    quantidadeInput.classList.remove("is-invalid");

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

  toggleBotaoCadastrarPosologia(mostrar) {
    const container = document.getElementById("posologiaCadastrarContainer");
    if (container) {
      container.style.display = mostrar ? "block" : "none";
    }
  }

  abrirModalCadastrarPosologia() {
    const medicamentoId = document.getElementById("medicamento").value;
    const receitaId = document.getElementById("receita").value;

    if (!medicamentoId) {
      UIComponents.Toast.alerta("Selecione um medicamento antes.");
      return;
    }
    if (!receitaId) {
      UIComponents.Toast.alerta("Selecione uma receita antes.");
      return;
    }

    document.getElementById("modalMedicamentoId").value = medicamentoId;
    document.getElementById("modalReceitaId").value = receitaId;

    document.getElementById("formCadastrarPosologia").reset();
    UIComponents.Validacao.limparErros("formCadastrarPosologia");
    const form = document.getElementById("formCadastrarPosologia");
    form.classList.remove("was-validated");

    const modalElement = document.getElementById("modalCadastrarPosologia");
    const modal = new bootstrap.Modal(modalElement);
    modal.show();
  }

  async salvarNovaPosologia() {
    const formModal = document.getElementById("formCadastrarPosologia");
    UIComponents.Validacao.limparErros("formCadastrarPosologia");

    const dose = document.getElementById("modalPosologiaDose").value.trim();
    const quantidadeDias = document.getElementById(
      "modalPosologiaQuantidadeDias"
    ).value;
    const intervaloHoras = document.getElementById(
      "modalPosologiaIntervaloHoras"
    ).value;
    const medicamentoId = document.getElementById("modalMedicamentoId").value;
    const receitaId = document.getElementById("modalReceitaId").value;

    let isValid = true;
    if (!dose) {
      UIComponents.Validacao.mostrarErro(
        "modalPosologiaDose",
        "Dose é obrigatória."
      );
      isValid = false;
    }
    if (!quantidadeDias || parseInt(quantidadeDias) < 1) {
      UIComponents.Validacao.mostrarErro(
        "modalPosologiaQuantidadeDias",
        "Quantidade de dias deve ser no mínimo 1."
      );
      isValid = false;
    }
    if (!intervaloHoras || parseInt(intervaloHoras) < 1) {
      UIComponents.Validacao.mostrarErro(
        "modalPosologiaIntervaloHoras",
        "Intervalo em horas deve ser no mínimo 1."
      );
      isValid = false;
    }

    if (!isValid) {
      formModal.classList.add("was-validated");
      return;
    }

    const novaPosologia = {
      dose: dose,
      quantidadedias: parseInt(quantidadeDias),
      intervalohoras: parseInt(intervaloHoras),
      medicamento_idproduto: parseInt(medicamentoId),
      receitamedicamento_idreceita: parseInt(receitaId),
    };

    UIComponents.Loading.mostrar("Salvando posologia...");
    try {
      const response = await fetch("http://localhost:8080/posologias", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(novaPosologia),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Erro ao salvar posologia.");
      }

      const savedPosologia = await response.json(); // Ou parseie o resultado se houver

      UIComponents.Toast.sucesso("Posologia cadastrada com sucesso!");

      const modalElement = document.getElementById("modalCadastrarPosologia");
      const modal = bootstrap.Modal.getInstance(modalElement);
      modal.hide();

      await this.updatePosologiaOptions(
        parseInt(medicamentoId),
        parseInt(receitaId)
      );
    } catch (error) {
      console.error("Erro ao salvar posologia:", error);
      UIComponents.ModalErro.mostrar(
        error.message || "Erro ao cadastrar posologia. Tente novamente."
      );
    } finally {
      UIComponents.Loading.esconder();
    }
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

    const posologiaSelecionada = document.getElementById("posologia").value;

    const quantidade = UIComponents.InputMasks.obterValorNumerico(
      document.getElementById("quantidade")
    );
    const dataMedicao = document.getElementById("dataMedicao").value;
    const descricaoHistorico =
      document.getElementById("descricaoHistorico").value;

    if (receitaId && !posologiaSelecionada) {
      UIComponents.Toast.alerta(
        "Se uma receita for selecionada, uma posologia deve ser escolhida ou cadastrada."
      );
      UIComponents.Validacao.mostrarErro(
        "posologia",
        "Por favor, selecione ou cadastre uma posologia para a receita."
      );
      form.classList.add("was-validated");
      return;
    }

    try {
      UIComponents.Loading.mostrar("Efetuando medicação...");
      const result = await this.medicacaoService.efetuarMedicacao({
        idAnimal: parseInt(animalId),
        idMedicamentoProduto: parseInt(medicamentoId),
        idReceitaMedicamento: receitaId ? parseInt(receitaId) : null,

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

export default MedicacaoController;

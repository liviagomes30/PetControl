import ReceituarioService from "../services/ReceituarioService.js";
import ReceituarioModel from "../models/ReceituarioModel.js";
import ReceituarioUIComponents from "../components/ReceituarioUIComponents.js";

class ReceituarioController {
  constructor() {
    this.service = new ReceituarioService();
    this.receituarioAtual = new ReceituarioModel();
    this.animais = [];
    this.medicamentos = [];
    this.posologiaIndex = 0;
  }

  async inicializarListagem() {
    try {
      ReceituarioUIComponents.Loading.mostrar("Carregando receituários...");

      await this.carregarAnimaisParaFiltro();
      await this.carregarReceituarios();
      this.configurarFiltros();
      this.configurarEventos();
    } catch (error) {
      console.error("Erro ao inicializar listagem:", error);
      Toast.error("Erro ao carregar receituários: " + error.message);
    } finally {
      ReceituarioUIComponents.Loading.esconder();
    }
  }

  async carregarReceituarios() {
    try {
      const receituarios = await this.service.listarTodos();
      this.renderizarTabela(receituarios);
    } catch (error) {
      console.error("Erro ao carregar receituários:", error);
      Toast.error("Erro ao carregar receituários: " + error.message);
    }
  }

  renderizarTabela(receituarios) {
    const tbody = document.getElementById("tabelaReceituarios");
    if (!tbody) return;

    tbody.innerHTML = "";

    if (receituarios.length === 0) {
      tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="no-data">
                        <i class="bi bi-inbox" style="font-size: 2rem; opacity: 0.4; display: block; margin-bottom: 8px;"></i>
                        <p>Nenhum receituário encontrado</p>
                        <p class="mt-2">
                            <a href="cadastrarReceituario.html" class="btn btn-outline-primary">
                                <i class="bi bi-plus-circle"></i> Adicionar Novo
                            </a>
                        </p>
                    </td>
                </tr>
            `;
      return;
    }

    receituarios.forEach((receituario) => {
      const row = document.createElement("tr");
      row.innerHTML = `
                <td>${receituario.idreceita}</td>
                <td>${this.formatarData(receituario.data)}</td>
                <td>${receituario.animalNome || "N/A"}</td>
                <td>${receituario.medico}</td>
                <td>${receituario.clinica || "Não informado"}</td>
                <td>
                    <div class="actions">
                        <button class="btn-icon btn-edit" onclick="receituarioController.visualizar(${
                          receituario.idreceita
                        })" title="Visualizar">
                            <i class="bi bi-eye"></i>
                        </button>
                        <button class="btn-icon btn-edit" onclick="receituarioController.editar(${
                          receituario.idreceita
                        })" title="Editar">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn-icon btn-delete" onclick="receituarioController.confirmarExclusao(${
                          receituario.idreceita
                        })" title="Excluir">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </td>
            `;
      tbody.appendChild(row);
    });
  }

  configurarFiltros() {
    const filtroAnimal = document.getElementById("filtroAnimal");
    const filtroMedico = document.getElementById("filtroMedico");
    const filtroData = document.getElementById("filtroData");
    const btnBuscar = document.getElementById("btnBuscar");

    if (filtroAnimal) {
      filtroAnimal.addEventListener("change", () => this.aplicarFiltros());
    }

    if (filtroMedico) {
      filtroMedico.addEventListener(
        "input",
        ReceituarioUIComponents.Utils.debounce(() => this.aplicarFiltros(), 500)
      );
    }

    if (filtroData) {
      this.configurarDataBrasileira(filtroData);
      filtroData.addEventListener("change", () => this.aplicarFiltros());
    }

    if (btnBuscar) {
      btnBuscar.addEventListener("click", () => this.aplicarFiltros());
    }
  }

  async aplicarFiltros() {
    try {
      ReceituarioUIComponents.Loading.mostrar("Aplicando filtros...");

      const filtroAnimal = document.getElementById("filtroAnimal")?.value;
      const filtroMedico = document
        .getElementById("filtroMedico")
        ?.value.trim();
      const filtroDataBrasileira = document.getElementById("filtroData")?.value;
      let filtroData = null;

      if (filtroDataBrasileira && filtroDataBrasileira !== "") {
        if (this.validarDataBrasileira(filtroDataBrasileira)) {
          filtroData = this.converterDataBrasileiraParaISO(filtroDataBrasileira);
        } else {
          Toast.error("Data inválida. Use o formato DD/MM/AAAA");
          return;
        }
      }

      let receituarios = [];

      if (filtroData && filtroData !== "") {
        receituarios = await this.service.buscarPorData(filtroData);
      } else if (
        filtroAnimal &&
        filtroAnimal !== "undefined" &&
        filtroAnimal !== ""
      ) {
        receituarios = await this.service.buscarPorAnimal(filtroAnimal);
      } else if (filtroMedico && filtroMedico !== "") {
        receituarios = await this.service.buscarPorMedico(filtroMedico);
      } else {
        receituarios = await this.service.listarTodos();
      }

      if (
        filtroAnimal &&
        filtroAnimal !== "undefined" &&
        filtroAnimal !== "" &&
        filtroMedico &&
        filtroMedico !== "" &&
        receituarios.length > 0
      ) {
        receituarios = receituarios.filter((r) => {
          return (
            r.medico &&
            r.medico.toLowerCase().includes(filtroMedico.toLowerCase())
          );
        });
      } else if (
        filtroData &&
        filtroData !== "" &&
        receituarios.length > 0 &&
        filtroAnimal &&
        filtroAnimal !== "undefined" &&
        filtroAnimal !== ""
      ) {
        receituarios = receituarios.filter((r) => {
          return this.formatarDataParaComparacao(r.data) === filtroData;
        });
      } else if (
        filtroData &&
        filtroData !== "" &&
        receituarios.length > 0 &&
        filtroMedico &&
        filtroMedico !== ""
      ) {
        receituarios = receituarios.filter((r) => {
          return this.formatarDataParaComparacao(r.data) === filtroData;
        });
      }

      this.renderizarTabela(receituarios);

      const filtrosAtivos = [];
      if (filtroAnimal && filtroAnimal !== "undefined" && filtroAnimal !== "") {
        filtrosAtivos.push("animal");
      }
      if (filtroMedico && filtroMedico !== "") {
        filtrosAtivos.push("médico");
      }
      if (filtroData && filtroData !== "") {
        filtrosAtivos.push("data");
      }

      if (filtrosAtivos.length > 0) {
        Toast.info(
          `Filtros aplicados: ${filtrosAtivos.join(", ")} (${
            receituarios.length
          } resultado${receituarios.length !== 1 ? "s" : ""})`
        );
      }
    } catch (error) {
      console.error("Erro ao aplicar filtros:", error);

      if (
        error.message &&
        (error.message.includes("animalId") ||
          error.message.includes("ID do animal deve ser um número válido"))
      ) {
        Toast.error(
          "Erro: Animal selecionado é inválido. Por favor, selecione um animal válido ou limpe os filtros."
        );
      } else {
        Toast.error("Erro ao filtrar receituários: " + error.message);
      }
    } finally {
      ReceituarioUIComponents.Loading.esconder();
    }
  }

  formatarDataParaComparacao(data) {
    if (!data) return null;
    try {
      if (typeof data === "string" && data.match(/^\d{4}-\d{2}-\d{2}$/)) {
        return data;
      }

      if (Array.isArray(data) && data.length === 3) {
        const [year, month, day] = data;
        return `${year}-${month.toString().padStart(2, "0")}-${day
          .toString()
          .padStart(2, "0")}`;
      }

      const dateObj = new Date(data);
      if (!isNaN(dateObj.getTime())) {
        return dateObj.toISOString().split("T")[0];
      }
      return null;
    } catch (error) {
      console.error("Erro ao formatar data para comparação:", error);
      return null;
    }
  }



  configurarDataBrasileira(input) {
    input.addEventListener('input', (e) => {
      let value = e.target.value.replace(/\D/g, '');
      
      if (value.length >= 2) {
        value = value.substring(0, 2) + '/' + value.substring(2);
      }
      if (value.length >= 5) {
        value = value.substring(0, 5) + '/' + value.substring(5);
      }
      if (value.length > 10) {
        value = value.substring(0, 10);
      }
      
      e.target.value = value;
    });

    input.addEventListener('blur', (e) => {
      const value = e.target.value;
      if (value && !this.validarDataBrasileira(value)) {
        Toast.error('Data inválida. Use o formato DD/MM/AAAA');
        e.target.classList.add('is-invalid');
      } else {
        e.target.classList.remove('is-invalid');
      }
    });
  }

  validarDataBrasileira(dateString) {
    const regex = /^(\d{2})\/(\d{2})\/(\d{4})$/;
    const match = dateString.match(regex);
    
    if (!match) return false;
    
    const day = parseInt(match[1]);
    const month = parseInt(match[2]);
    const year = parseInt(match[3]);
    
    if (month < 1 || month > 12) return false;
    if (day < 1 || day > 31) return false;
    if (year < 1900 || year > 2100) return false;
    
    const date = new Date(year, month - 1, day);
    return date.getFullYear() === year && 
           date.getMonth() === month - 1 && 
           date.getDate() === day;
  }

  converterDataBrasileiraParaISO(dateString) {
    const regex = /^(\d{2})\/(\d{2})\/(\d{4})$/;
    const match = dateString.match(regex);
    
    if (!match) return null;
    
    const day = match[1];
    const month = match[2];
    const year = match[3];
    
    return `${year}-${month}-${day}`;
  }

  configurarEventos() {
    const btnLimparFiltros = document.getElementById("btnLimparFiltros");
    if (btnLimparFiltros) {
      btnLimparFiltros.addEventListener("click", () => this.limparFiltros());
    }
  }

  limparFiltros() {
    const filtroAnimal = document.getElementById("filtroAnimal");
    const filtroMedico = document.getElementById("filtroMedico");
    const filtroData = document.getElementById("filtroData");

    if (filtroAnimal) filtroAnimal.value = "";
    if (filtroMedico) filtroMedico.value = "";
    if (filtroData) {
      filtroData.value = "";
      filtroData.classList.remove('is-invalid');
    }

    this.carregarReceituarios();
    Toast.info("Filtros limpos");
  }

  async inicializarFormulario() {
    try {
      ReceituarioUIComponents.Loading.mostrar("Carregando formulário...");

      const urlParams = new URLSearchParams(window.location.search);
      const id = urlParams.get("id");

      await this.carregarDadosFormulario();
      this.configurarFormulario();

      if (id) {
        await this.carregarReceituario(id);
      } else {
        this.definirDataAtual();
        this.adicionarPosologia();
      }
    } catch (error) {
      console.error("Erro ao inicializar formulário:", error);
      Toast.error("Erro ao carregar formulário: " + error.message);
    } finally {
      ReceituarioUIComponents.Loading.esconder();
    }
  }

  async carregarDadosFormulario() {
    try {
      [this.animais, this.medicamentos] = await Promise.all([
        this.service.buscarAnimais(),
        this.service.buscarMedicamentos(),
      ]);

      this.popularSelectAnimais();
      this.popularSelectMedicamentos();
    } catch (error) {
      console.error("Erro ao carregar dados do formulário:", error);
      throw error;
    }
  }

  popularSelectAnimais() {
    const selectAnimal = document.getElementById("animal");
    if (!selectAnimal) return;

    selectAnimal.innerHTML = '<option value="">Selecione um animal</option>';

    this.animais.forEach((animal) => {
      const option = document.createElement("option");
      option.value = animal.id;
      option.textContent = `${animal.nome} - ${animal.especie}`;
      selectAnimal.appendChild(option);
    });
  }

  async carregarAnimaisParaFiltro() {
    try {
      this.animais = await this.service.buscarAnimais();
      this.popularFiltroAnimais();
    } catch (error) {
      console.error("Erro ao carregar animais para filtro:", error);
      Toast.error("Erro ao carregar lista de animais: " + error.message);
    }
  }

  popularFiltroAnimais() {
    const filtroAnimal = document.getElementById("filtroAnimal");
    if (!filtroAnimal) return;

    filtroAnimal.innerHTML = '<option value="">Todos os animais</option>';

    if (this.animais && Array.isArray(this.animais)) {
      this.animais.forEach((animal) => {
        if (
          animal &&
          animal.id != null &&
          animal.id !== "" &&
          animal.id !== "undefined"
        ) {
          const option = document.createElement("option");
          option.value = animal.id;
          option.textContent = `${animal.nome || "Nome não informado"} - ${
            animal.especie || "Espécie não informada"
          }`;
          filtroAnimal.appendChild(option);
        }
      });
    } else {
      console.warn("Lista de animais não carregada ou inválida para filtros");
      Toast.warning(
        "Aviso: Não foi possível carregar a lista de animais para filtros."
      );
    }
  }

  popularSelectMedicamentos() {}

  definirDataAtual() {
    const inputData = document.getElementById("data");
    if (inputData) {
      const hoje = new Date();
      inputData.value = hoje.toISOString().split("T")[0];
    }
  }

  configurarFormulario() {
    const form = document.getElementById("formReceituario");
    if (!form) return;

    form.addEventListener("submit", (e) => this.salvar(e));

    const btnAdicionarPosologia = document.getElementById(
      "btnAdicionarPosologia"
    );
    if (btnAdicionarPosologia) {
      btnAdicionarPosologia.addEventListener("click", () =>
        this.adicionarPosologia()
      );
    }
  }

  adicionarPosologia() {
    const container = document.getElementById("posologiasContainer");
    if (!container) return;

    const index = this.posologiaIndex++;

    const posologiaDiv = document.createElement("div");
    posologiaDiv.className = "posologia-item card mb-3";
    posologiaDiv.dataset.index = index;

    posologiaDiv.innerHTML = `
            <div class="card-header d-flex justify-content-between align-items-center">
                <h6 class="mb-0"><i class="bi bi-capsule"></i> Medicamento ${
                  index + 1
                }</h6>
                <button type="button" class="btn btn-sm btn-outline-danger" onclick="receituarioController.removerPosologia(${index})">
                    <i class="bi bi-trash"></i> Remover
                </button>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="medicamento_${index}" class="form-label required">Medicamento</label>
                        <select class="form-select" id="medicamento_${index}" name="medicamento_${index}" required>
                            <option value="">Selecione um medicamento</option>
                        </select>
                        <div class="invalid-feedback" id="medicamento_${index}_error"></div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="dose_${index}" class="form-label required">Dose</label>
                        <input type="text" class="form-control" id="dose_${index}" name="dose_${index}" 
                               placeholder="Ex: 1 comprimido, 10ml, 2 cápsulas" maxlength="50" required>
                        <div class="invalid-feedback" id="dose_${index}_error"></div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="quantidadedias_${index}" class="form-label required">Duração (dias)</label>
                        <input type="number" class="form-control" id="quantidadedias_${index}" 
                               name="quantidadedias_${index}" min="1" max="365" required>
                        <div class="invalid-feedback" id="quantidadedias_${index}_error"></div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="intervalohoras_${index}" class="form-label required">Intervalo (horas)</label>
                        <select class="form-select" id="intervalohoras_${index}" name="intervalohoras_${index}" required>
                            <option value="">Selecione o intervalo</option>
                            <option value="6">A cada 6 horas (4x ao dia)</option>
                            <option value="8">A cada 8 horas (3x ao dia)</option>
                            <option value="12">A cada 12 horas (2x ao dia)</option>
                            <option value="24">Uma vez ao dia</option>
                            <option value="48">A cada 2 dias</option>
                            <option value="72">A cada 3 dias</option>
                        </select>
                        <div class="invalid-feedback" id="intervalohoras_${index}_error"></div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-12">
                        <div class="alert alert-info" id="resumo_${index}" style="display: none;">
                            <small><i class="bi bi-info-circle"></i> <span id="resumoTexto_${index}"></span></small>
                        </div>
                    </div>
                </div>
            </div>
        `;

    container.appendChild(posologiaDiv);
    this.popularSelectMedicamento(index);
    this.configurarEventosPosologia(index);
  }

  popularSelectMedicamento(index) {
    const select = document.getElementById(`medicamento_${index}`);
    if (!select) return;

    select.innerHTML = '<option value="">Selecione um medicamento</option>';

    this.medicamentos.forEach((medicamento) => {
      const option = document.createElement("option");
      option.value = medicamento.produto.idproduto;
      option.textContent = `${medicamento.produto.nome} - ${medicamento.medicamento.composicao}`;
      select.appendChild(option);
    });
  }

  configurarEventosPosologia(index) {
    const quantidadeInput = document.getElementById(`quantidadedias_${index}`);
    const intervaloSelect = document.getElementById(`intervalohoras_${index}`);

    if (quantidadeInput && intervaloSelect) {
      const atualizarResumo = () => this.atualizarResumoPosologia(index);
      quantidadeInput.addEventListener("input", atualizarResumo);
      intervaloSelect.addEventListener("change", atualizarResumo);
    }
  }

  atualizarResumoPosologia(index) {
    const quantidadedias = document.getElementById(
      `quantidadedias_${index}`
    )?.value;
    const intervalohoras = document.getElementById(
      `intervalohoras_${index}`
    )?.value;
    const resumoDiv = document.getElementById(`resumo_${index}`);
    const resumoTexto = document.getElementById(`resumoTexto_${index}`);

    if (!quantidadedias || !intervalohoras || !resumoDiv || !resumoTexto)
      return;

    const totalHoras = quantidadedias * 24;
    const totalDoses = Math.ceil(totalHoras / intervalohoras);

    const frequencia = this.formatarFrequencia(intervalohoras);
    resumoTexto.textContent = `${frequencia} por ${quantidadedias} dia(s) = aproximadamente ${totalDoses} dose(s) no total`;
    resumoDiv.style.display = "block";
  }

  formatarFrequencia(intervalohoras) {
    const intervalo = parseInt(intervalohoras);
    if (intervalo === 24) return "Uma vez ao dia";
    if (intervalo === 12) return "Duas vezes ao dia";
    if (intervalo === 8) return "Três vezes ao dia";
    if (intervalo === 6) return "Quatro vezes ao dia";
    if (intervalo < 24) return `A cada ${intervalo} horas`;
    return `A cada ${Math.round(intervalo / 24)} dia(s)`;
  }

  removerPosologia(index) {
    const posologiaDiv = document.querySelector(`[data-index="${index}"]`);
    if (posologiaDiv) {
      posologiaDiv.remove();
    }

    const posologiasRestantes = document.querySelectorAll(".posologia-item");
    if (posologiasRestantes.length === 0) {
      this.adicionarPosologia();
    }
  }

  async carregarReceituario(id) {
    try {
      const receituario = await this.service.buscarPorId(id);
      this.receituarioAtual = ReceituarioModel.fromAPI(receituario);
      this.preencherFormulario();
    } catch (error) {
      console.error("Erro ao carregar receituário:", error);
      Toast.error("Erro ao carregar receituário: " + error.message);
    }
  }

  preencherFormulario() {
    document.getElementById("data").value = this.receituarioAtual.data;
    document.getElementById("medico").value = this.receituarioAtual.medico;
    document.getElementById("clinica").value = this.receituarioAtual.clinica;
    document.getElementById("animal").value =
      this.receituarioAtual.animal_idanimal;

    const container = document.getElementById("posologiasContainer");
    container.innerHTML = "";
    this.posologiaIndex = 0;

    this.receituarioAtual.posologias.forEach((posologia, index) => {
      this.adicionarPosologia();
      this.preencherPosologia(index, posologia);
    });
  }

  preencherPosologia(index, posologia) {
    document.getElementById(`medicamento_${index}`).value =
      posologia.medicamento_idproduto;
    document.getElementById(`dose_${index}`).value = posologia.dose;
    document.getElementById(`quantidadedias_${index}`).value =
      posologia.quantidadedias;
    document.getElementById(`intervalohoras_${index}`).value =
      posologia.intervalohoras;

    this.atualizarResumoPosologia(index);
  }

  async salvar(event) {
    event.preventDefault();

    try {
      ReceituarioUIComponents.Loading.mostrar("Salvando receituário...");

      const dadosFormulario = this.obterDadosFormulario();
      this.receituarioAtual = new ReceituarioModel(dadosFormulario);

      const validacao = this.receituarioAtual.validar();
      if (!validacao.valido) {
        this.exibirErrosValidacao(validacao.erros);
        return;
      }

      const urlParams = new URLSearchParams(window.location.search);
      const id = urlParams.get("id");

      let resultado;
      if (id) {
        resultado = await this.service.atualizar(
          id,
          this.receituarioAtual.toJSON()
        );
        Toast.success("Receituário atualizado com sucesso!");
      } else {
        resultado = await this.service.cadastrar(
          this.receituarioAtual.toJSON()
        );
        Toast.success(
          resultado.mensagem || "Receituário cadastrado com sucesso!"
        );
      }

      setTimeout(() => {
        window.location.href = "listarReceituario.html";
      }, 1500);
    } catch (error) {
      console.error("Erro ao salvar receituário:", error);
      Toast.error("Erro ao salvar receituário: " + error.message);
    } finally {
      ReceituarioUIComponents.Loading.esconder();
    }
  }

  obterDadosFormulario() {
    const dados = {
      data: document.getElementById("data").value,
      medico: document.getElementById("medico").value,
      clinica: document.getElementById("clinica").value,
      animal_idanimal: document.getElementById("animal").value,
      posologias: [],
    };

    const posologiasElements = document.querySelectorAll(".posologia-item");
    posologiasElements.forEach((element, index) => {
      const realIndex = element.dataset.index;
      const posologia = {
        medicamento_idproduto: document.getElementById(
          `medicamento_${realIndex}`
        ).value,
        dose: document.getElementById(`dose_${realIndex}`).value,
        quantidadedias: document.getElementById(`quantidadedias_${realIndex}`)
          .value,
        intervalohoras: document.getElementById(`intervalohoras_${realIndex}`)
          .value,
        frequencia_diaria: (function() {
          const intervalo = parseInt(document.getElementById(`intervalohoras_${realIndex}`).value);
          if (isNaN(intervalo) || intervalo <= 0) return null;
          if (intervalo <= 24) {
            return Math.round(24 / intervalo);
          }
          return 1;
        })()
      };
      dados.posologias.push(posologia);
    });

    return dados;
  }

  exibirErrosValidacao(erros) {
    this.limparErros();

    Object.keys(erros).forEach((campo) => {
      const elemento = document.getElementById(campo);
      const errorDiv = document.getElementById(campo + "_error");

      if (elemento && errorDiv) {
        elemento.classList.add("is-invalid");
        errorDiv.textContent = erros[campo];
      }
    });

    if (erros.posologiasDetalhes) {
      erros.posologiasDetalhes.forEach((posErros, index) => {
        Object.keys(posErros).forEach((campo) => {
          const elemento = document.getElementById(`${campo}_${index}`);
          const errorDiv = document.getElementById(`${campo}_${index}_error`);

          if (elemento && errorDiv) {
            elemento.classList.add("is-invalid");
            errorDiv.textContent = posErros[campo];
          }
        });
      });
    }

    Toast.error("Por favor, corrija os erros no formulário");
  }

  limparErros() {
    const invalidElements = document.querySelectorAll(".is-invalid");
    invalidElements.forEach((el) => el.classList.remove("is-invalid"));

    const errorDivs = document.querySelectorAll(".invalid-feedback");
    errorDivs.forEach((div) => (div.textContent = ""));
  }

  async visualizar(id) {
    try {
      const receituario = await this.service.buscarPorId(id);
      this.exibirModalVisualizacao(receituario);
    } catch (error) {
      console.error("Erro ao visualizar receituário:", error);
      Toast.error("Erro ao carregar receituário: " + error.message);
    }
  }

  exibirModalVisualizacao(receituario) {
    const modalBody = document.getElementById("modalVisualizacaoBody");
    if (!modalBody) return;

    let posologiasHtml = "";
    if (receituario.posologias && receituario.posologias.length > 0) {
      posologiasHtml = receituario.posologias
        .map(
          (p) => `
                <div class="card mb-2">
                    <div class="card-body py-2">
                        <h6 class="card-title mb-1">${p.medicamentoNome}</h6>
                        <p class="card-text mb-1"><small class="text-muted">${
                          p.medicamentoComposicao
                        }</small></p>
                        <p class="card-text mb-0">
                            <strong>Dose:</strong> ${p.dose}<br>
                            <strong>Duração:</strong> ${
                              p.quantidadedias
                            } dia(s)<br>
                            <strong>Frequência:</strong> ${this.formatarFrequencia(
                              p.intervalohoras
                            )}
                        </p>
                    </div>
                </div>
            `
        )
        .join("");
    } else {
      posologiasHtml = '<p class="text-muted">Nenhuma posologia encontrada</p>';
    }

    modalBody.innerHTML = `
            <div class="row">
                <div class="col-md-6">
                    <p><strong>Data:</strong> ${this.formatarData(
                      receituario.data
                    )}</p>
                    <p><strong>Animal:</strong> ${receituario.animalNome}</p>
                </div>
                <div class="col-md-6">
                    <p><strong>Médico:</strong> ${receituario.medico}</p>
                    <p><strong>Clínica:</strong> ${
                      receituario.clinica || "Não informado"
                    }</p>
                </div>
            </div>
            <hr>
            <h6>Prescrições:</h6>
            ${posologiasHtml}
        `;

    const modal = new bootstrap.Modal(
      document.getElementById("modalVisualizacao")
    );
    modal.show();
  }

  editar(id) {
    window.location.href = `editarReceituario.html?id=${id}`;
  }

  confirmarExclusao(id) {
    ReceituarioUIComponents.ModalConfirmacao.mostrar(
      "Confirmar Exclusão",
      "Tem certeza que deseja excluir este receituário? Esta ação não pode ser desfeita.",
      () => this.excluir(id)
    );
  }

  async excluir(id) {
    try {
      ReceituarioUIComponents.Loading.mostrar("Excluindo receituário...");

      const resultado = await this.service.excluir(id);

      if (resultado.sucesso) {
        Toast.success(resultado.mensagem);
        await this.carregarReceituarios();
      } else {
        Toast.warning(resultado.mensagem);
      }
    } catch (error) {
      console.error("Erro ao excluir receituário:", error);
      Toast.error("Erro ao excluir receituário: " + error.message);
    } finally {
      ReceituarioUIComponents.Loading.esconder();
    }
  }

  formatarData(data) {
    if (!data) return "N/A";
    try {
      if (Array.isArray(data) && data.length === 3) {
        const [year, month, day] = data;
        const dateObj = new Date(year, month - 1, day);
        return dateObj.toLocaleDateString("pt-BR");
      }

      if (typeof data === "string" && data.match(/^\d{4}-\d{2}-\d{2}$/)) {
        return new Date(data + "T00:00:00").toLocaleDateString("pt-BR");
      }

      return new Date(data).toLocaleDateString("pt-BR");
    } catch (error) {
      console.error("Erro ao formatar data:", error);
      return data?.toString() || "N/A";
    }
  }
}

const receituarioController = new ReceituarioController();
window.receituarioController = receituarioController;

export default ReceituarioController;

const PetControlMensagens = {
  sucesso: function (mensagem, duracao = 5000) {
    this._criarToast("Sucesso!", mensagem, "success", duracao);
  },

  erro: function (mensagem, duracao = 5000) {
    this._criarToast("Erro", mensagem, "danger", duracao);
  },

  alerta: function (mensagem, duracao = 5000) {
    this._criarToast("Atenção", mensagem, "warning", duracao);
  },

  info: function (mensagem, duracao = 5000) {
    this._criarToast("Informação", mensagem, "info", duracao);
  },

  // Em mensagens.js, método confirmar
  confirmar: function (
    titulo,
    mensagem,
    onConfirmar,
    onCancelar = null,
    textoBotaoConfirmar = "Sim",
    textoBotaoCancelar = "Não"
  ) {
    const modalExistente = document.getElementById("modalConfirmacao");
    if (modalExistente) {
      modalExistente.remove();
    }

    // Certifique-se de que a mensagem seja tratada como texto, não como código
    const mensagemSegura =
      typeof mensagem === "string" ? mensagem : "Deseja confirmar esta ação?";

    const modalHtml = `
    <div class="modal fade" id="modalConfirmacao" tabindex="-1" aria-labelledby="tituloModalConfirmacao" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header" style="background-color: var(--laranja-suave); color: white;">
            <h5 class="modal-title" id="tituloModalConfirmacao">${titulo}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
          </div>
          <div class="modal-body">
            ${mensagemSegura}
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-outline-secondary" id="btnCancelarConfirmacao" data-bs-dismiss="modal">${textoBotaoCancelar}</button>
            <button type="button" class="btn btn-primary" id="btnConfirmarAcao" style="background-color: var(--laranja-vibrante);">${textoBotaoConfirmar}</button>
          </div>
        </div>
      </div>
    </div>
  `;

    document.body.insertAdjacentHTML("beforeend", modalHtml);

    const modalElement = document.getElementById("modalConfirmacao");
    const btnConfirmar = document.getElementById("btnConfirmarAcao");
    const btnCancelar = document.getElementById("btnCancelarConfirmacao");

    // Certifique-se de que o Bootstrap está disponível
    const modal = new bootstrap.Modal(modalElement);

    btnConfirmar.addEventListener("click", () => {
      modal.hide();
      if (typeof onConfirmar === "function") {
        onConfirmar(true);
      }
    });

    btnCancelar.addEventListener("click", () => {
      modal.hide();
      if (typeof onCancelar === "function") {
        onCancelar();
      }
    });

    modalElement.addEventListener("hidden.bs.modal", (e) => {
      modalElement.remove();
    });

    modal.show();
  },

  erroValidacaoCampo: function (idCampo, mensagem) {
    const campo = document.getElementById(idCampo);
    const divErro =
      document.getElementById(`${idCampo}Error`) ||
      this._criarDivErro(campo, idCampo);

    if (campo && divErro) {
      campo.classList.add("is-invalid");
      divErro.textContent = mensagem;
      divErro.style.display = "block";
    }
  },

  limparErrosValidacao: function (idFormulario) {
    const formulario = document.getElementById(idFormulario);
    if (!formulario) return;

    const camposInvalidos = formulario.querySelectorAll(".is-invalid");
    camposInvalidos.forEach((campo) => {
      campo.classList.remove("is-invalid");
    });

    const mensagensErro = formulario.querySelectorAll(".input-error");
    mensagensErro.forEach((div) => {
      div.textContent = "";
      div.style.display = "none";
    });
  },

  loading: function (mostrar = true, mensagem = "Carregando...") {
    let loadingOverlay = document.getElementById("loadingOverlay");

    if (!loadingOverlay) {
      loadingOverlay = document.createElement("div");
      loadingOverlay.id = "loadingOverlay";
      loadingOverlay.className = "loading-overlay";

      const spinner = document.createElement("div");
      spinner.className = "spinner";

      const textoLoading = document.createElement("div");
      textoLoading.id = "textoLoading";
      textoLoading.className = "mt-3 text-white";

      loadingOverlay.appendChild(spinner);
      loadingOverlay.appendChild(textoLoading);
      document.body.appendChild(loadingOverlay);
    }

    const textoElement = document.getElementById("textoLoading");
    if (textoElement) {
      textoElement.textContent = mensagem;
    }

    if (mostrar) {
      loadingOverlay.classList.add("show");
    } else {
      loadingOverlay.classList.remove("show");
    }
  },

  // ------------------ Métodos auxiliares privados ------------------
  _criarToast: function (titulo, mensagem, tipo, duracao) {
    let toastContainer = document.querySelector(".toast-container");

    if (!toastContainer) {
      toastContainer = document.createElement("div");
      toastContainer.className = "toast-container";
      document.body.appendChild(toastContainer);
    }

    const tipoClasse =
      {
        success: "bg-success",
        danger: "bg-danger",
        warning: "bg-warning",
        info: "bg-info",
      }[tipo] || "bg-secondary";

    const tipoIcone =
      {
        success: "bi-check-circle",
        danger: "bi-x-circle",
        warning: "bi-exclamation-triangle",
        info: "bi-info-circle",
      }[tipo] || "bi-bell";

    const toastId = "toast-" + Date.now();

    const toastHtml = `
            <div id="${toastId}" class="toast align-items-center" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="d-flex">
                    <div class="toast-icon ${tipoClasse} text-white d-flex align-items-center justify-content-center" style="width: 50px;">
                        <i class="bi ${tipoIcone}"></i>
                    </div>
                    <div class="toast-body">
                        <strong>${titulo}</strong><br>
                        ${mensagem}
                    </div>
                    <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Fechar"></button>
                </div>
            </div>
        `;

    toastContainer.insertAdjacentHTML("beforeend", toastHtml);

    const toastElement = document.getElementById(toastId);

    const toastInstance = new bootstrap.Toast(toastElement, {
      autohide: true,
      delay: duracao,
    });

    toastInstance.show();

    toastElement.addEventListener("hidden.bs.toast", () => {
      toastElement.remove();
    });
  },

  _criarDivErro: function (campo, idCampo) {
    const divErro = document.createElement("div");
    divErro.id = `${idCampo}Error`;
    divErro.className = "input-error text-danger mt-1";
    divErro.style.fontSize = "0.875rem";

    if (campo && campo.parentNode) {
      campo.parentNode.insertBefore(divErro, campo.nextSibling);
    }

    return divErro;
  },
};

export default PetControlMensagens;

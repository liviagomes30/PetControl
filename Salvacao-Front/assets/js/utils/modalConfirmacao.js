// modalConfirmacao.js
const ModalConfirmacao = {
  mostrar: function (
    titulo,
    mensagem,
    callbackConfirmar,
    callbackCancelar = null,
    textoBotaoConfirmar = "Sim",
    textoBotaoCancelar = "Não",
    corCabecalho = "#F2A03D",
    corBotaoConfirmar = "#F2541B"
  ) {
    // Remove qualquer modal existente para evitar duplicação
    this.removerModaisExistentes();

    // Cria o modal com estilos explícitos para garantir visibilidade
    const modalHtml = `
      <div class="modal-custom" id="modal-confirmacao" tabindex="-1" style="display: block; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 1050;" aria-modal="true" role="dialog">
        <div class="modal-dialog" style="position: relative; width: auto; margin: 100px auto; max-width: 500px; z-index: 1060;">
          <div class="modal-content" style="background-color: white; color: black; border: none; border-radius: 5px; box-shadow: 0 5px 15px rgba(0,0,0,0.5); position: relative; display: flex; flex-direction: column; width: 100%; pointer-events: auto; outline: 0;">
            <div class="modal-header" style="background-color: ${corCabecalho}; color: white; border-bottom: 1px solid #dee2e6; padding: 1rem; display: flex; align-items: center; justify-content: space-between; border-top-left-radius: 5px; border-top-right-radius: 5px;">
              <h5 class="modal-title" style="margin: 0; font-weight: 500;">${titulo}</h5>
              <button type="button" class="btn-close" style="background: transparent; border: 0; font-size: 1.5rem; font-weight: 700; color: white; text-shadow: 0 1px 0 #fff; opacity: .5; padding: 0; line-height: 1;" aria-label="Fechar" id="fecharModalX">&times;</button>
            </div>
            <div class="modal-body" style="padding: 1rem; color: #212529; position: relative; flex: 1 1 auto;">
              ${mensagem}
            </div>
            <div class="modal-footer" style="border-top: 1px solid #dee2e6; padding: 0.75rem; display: flex; justify-content: flex-end; align-items: center; border-bottom-right-radius: 5px; border-bottom-left-radius: 5px;">
              <button type="button" class="btn-cancelar" id="botao-cancelar" style="padding: 0.375rem 0.75rem; margin-right: 0.5rem; color: #6c757d; background-color: transparent; border: 1px solid #6c757d; cursor: pointer; border-radius: 0.25rem;">${textoBotaoCancelar}</button>
              <button type="button" class="btn-confirmar" id="botao-confirmar" style="padding: 0.375rem 0.75rem; color: white; background-color: ${corBotaoConfirmar}; border: 1px solid ${corBotaoConfirmar}; cursor: pointer; border-radius: 0.25rem;">${textoBotaoConfirmar}</button>
            </div>
          </div>
        </div>
      </div>
    `;

    document.body.insertAdjacentHTML("beforeend", modalHtml);

    this.bloquearScroll();

    this.configurarEventos(callbackConfirmar, callbackCancelar);
  },

  removerModaisExistentes: function () {
    const modaisAntigos = document.querySelectorAll("#modal-confirmacao");
    modaisAntigos.forEach((modal) => {
      if (modal && modal.parentNode) {
        modal.parentNode.removeChild(modal);
      }
    });

    this.restaurarScroll();
  },

  fechar: function () {
    const modal = document.getElementById("modal-confirmacao");
    if (modal && modal.parentNode) {
      modal.parentNode.removeChild(modal);
    }

    this.restaurarScroll();
  },

  bloquearScroll: function () {
    this.scrollTop = window.pageYOffset || document.documentElement.scrollTop;

    document.body.style.overflow = "hidden";
    document.body.style.position = "fixed";
    document.body.style.top = `-${this.scrollTop}px`;
    document.body.style.width = "100%";
  },

  restaurarScroll: function () {
    document.body.style.overflow = "";
    document.body.style.position = "";
    document.body.style.top = "";
    document.body.style.width = "";

    window.scrollTo(0, this.scrollTop || 0);
  },

  configurarEventos: function (callbackConfirmar, callbackCancelar) {
    const modal = document.getElementById("modal-confirmacao");
    const btnConfirmar = document.getElementById("botao-confirmar");
    const btnCancelar = document.getElementById("botao-cancelar");
    const btnFecharX = document.getElementById("fecharModalX");

    if (btnConfirmar) {
      btnConfirmar.addEventListener("click", () => {
        this.fechar();
        if (typeof callbackConfirmar === "function") {
          callbackConfirmar();
        }
      });
    }

    if (btnCancelar) {
      btnCancelar.addEventListener("click", () => {
        this.fechar();
        if (typeof callbackCancelar === "function") {
          callbackCancelar();
        }
      });
    }

    if (btnFecharX) {
      btnFecharX.addEventListener("click", () => {
        this.fechar();
        if (typeof callbackCancelar === "function") {
          callbackCancelar();
        }
      });
    }

    if (modal) {
      modal.addEventListener("click", (e) => {
        if (e.target === modal) {
          this.fechar();
          if (typeof callbackCancelar === "function") {
            callbackCancelar();
          }
        }
      });
    }

    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape" && document.getElementById("modal-confirmacao")) {
        this.fechar();
        if (typeof callbackCancelar === "function") {
          callbackCancelar();
        }
      }
    });
  },
};

export default ModalConfirmacao;

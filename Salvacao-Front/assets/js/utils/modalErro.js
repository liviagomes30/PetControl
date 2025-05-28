const modalErro = {
  mostrar: function (mensagem, callback = null) {
    let modal = document.getElementById("modal-erro");

    if (!modal) {
      const modalHTML = `
        <div class="modal fade" id="modal-erro" tabindex="-1" aria-labelledby="modalErroTitulo" aria-hidden="true">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header bg-danger text-white">
                <h5 class="modal-title" id="modalErroTitulo">
                  <i class="bi bi-exclamation-triangle-fill me-2"></i> Erro
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Fechar"></button>
              </div>
              <div class="modal-body" id="modal-erro-mensagem">
                Ocorreu um erro inesperado.
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="btn-ok">
                  OK
                </button>
              </div>
            </div>
          </div>
        </div>
      `;

      document.body.insertAdjacentHTML("beforeend", modalHTML);
      modal = document.getElementById("modal-erro");
    }

    const modalBody = modal.querySelector("#modal-erro-mensagem");
    if (modalBody) modalBody.textContent = mensagem;

    const btnOk = modal.querySelector("#btn-ok");

    btnOk.replaceWith(btnOk.cloneNode(true));

    const newBtnOk = modal.querySelector("#btn-ok");

    if (typeof callback === "function") {
      newBtnOk.addEventListener("click", callback);
      modal.addEventListener("hidden.bs.modal", callback, { once: true });
    }

    const modalInstance = new bootstrap.Modal(modal);
    modalInstance.show();
  },
};

export default modalErro;

// salvacao.petcontrol.Front/assets/js/utils/modalHelper.js
const ModalHelper = {
  abrirModal: function (modalId) {
    // Acessar via window.bootstrap é mais seguro em módulos JS
    const bootstrapInstance = window.bootstrap;

    if (!bootstrapInstance || typeof bootstrapInstance.Modal === "undefined") {
      console.error(
        "Erro: Objeto Bootstrap ou sua classe Modal não está disponível. Verifique o carregamento do bootstrap.bundle.min.js."
      );
      return;
    }

    const backdrops = document.querySelectorAll(".modal-backdrop");
    backdrops.forEach((backdrop) => {
      if (backdrop.parentNode) {
        backdrop.parentNode.removeChild(backdrop);
      }
    });

    document.body.classList.remove("modal-open");
    document.body.style.overflow = "";
    document.body.style.paddingRight = "";

    const modalElement = document.getElementById(modalId);

    if (modalElement) {
      const existingModalInstance =
        bootstrapInstance.Modal.getInstance(modalElement);
      if (existingModalInstance) {
        existingModalInstance.dispose();
      }

      modalElement.classList.remove("show");
      modalElement.style.display = "none";
      modalElement.setAttribute("aria-hidden", "true");

      // CORREÇÃO: Criar uma nova instância do modal e exibi-la imediatamente (sem setTimeout)
      const modal = new bootstrapInstance.Modal(modalElement);
      modal.show();
    }
  },

  fecharModal: function (modalId) {
    const modalElement = document.getElementById(modalId);
    if (modalElement) {
      const bootstrapInstance = window.bootstrap;

      if (
        !bootstrapInstance ||
        typeof bootstrapInstance.Modal === "undefined"
      ) {
        console.warn(
          "Aviso: Objeto Bootstrap não disponível ao tentar fechar o modal. Fechamento manual."
        );
      } else {
        const modalInstance = bootstrapInstance.Modal.getInstance(modalElement);
        if (modalInstance) {
          modalInstance.hide();
          return;
        }
      }

      modalElement.classList.remove("show");
      modalElement.style.display = "none";
      modalElement.setAttribute("aria-hidden", "true");

      const backdrop = document.querySelector(".modal-backdrop");
      if (backdrop && backdrop.parentNode) {
        backdrop.parentNode.removeChild(backdrop);
      }

      document.body.classList.remove("modal-open");
      document.body.style.overflow = "";
      document.body.style.paddingRight = "";
    }
  },
};

export default ModalHelper;

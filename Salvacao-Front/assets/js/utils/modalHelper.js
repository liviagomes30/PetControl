// modalHelper.js
const ModalHelper = {
  abrirModal: function (modalId) {
    // Limpar qualquer modal travado que possa existir
    const backdrops = document.querySelectorAll(".modal-backdrop");
    backdrops.forEach((backdrop) => {
      backdrop.parentNode.removeChild(backdrop);
    });

    // Limpar classes e estilos do body
    document.body.classList.remove("modal-open");
    document.body.style.overflow = "";
    document.body.style.paddingRight = "";

    // Resetar o modal específico
    const modalElement = document.getElementById(modalId);
    if (modalElement) {
      modalElement.classList.remove("show");
      modalElement.style.display = "none";
      modalElement.setAttribute("aria-hidden", "true");

      // Se existe uma instância anterior, descarta ela
      if (
        bootstrap &&
        bootstrap.Modal &&
        bootstrap.Modal.getInstance(modalElement)
      ) {
        bootstrap.Modal.getInstance(modalElement).dispose();
      }

      // Cria uma nova instância do modal e abre
      setTimeout(() => {
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
      }, 100);
    }
  },

  fecharModal: function (modalId) {
    const modalElement = document.getElementById(modalId);
    if (modalElement) {
      // Tenta obter a instância do modal
      const modalInstance =
        bootstrap && bootstrap.Modal
          ? bootstrap.Modal.getInstance(modalElement)
          : null;

      if (modalInstance) {
        // Fecha usando a API do Bootstrap se disponível
        modalInstance.hide();
      } else {
        // Fechamento manual como fallback
        modalElement.classList.remove("show");
        modalElement.style.display = "none";
        modalElement.setAttribute("aria-hidden", "true");

        // Remove backdrop manualmente
        const backdrop = document.querySelector(".modal-backdrop");
        if (backdrop) {
          backdrop.parentNode.removeChild(backdrop);
        }

        // Restaura o body
        document.body.classList.remove("modal-open");
        document.body.style.overflow = "";
        document.body.style.paddingRight = "";
      }
    }
  },
};

export default ModalHelper;

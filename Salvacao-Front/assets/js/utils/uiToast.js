// Salvacao-Front/assets/js/utils/uiToast.js

const UIToast = {
  mostrar: function (titulo, mensagem, tipo = "success", duracao = 5000) {
    let toastContainer = document.querySelector(".toast-container");

    if (!toastContainer) {
      toastContainer = document.createElement("div");
      toastContainer.className = "toast-container";
      toastContainer.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
      `;
      document.body.appendChild(toastContainer);
    }

    const cores = {
      success: {
        bg: "#28a745",
        icon: "✓",
      },
      error: {
        bg: "#dc3545",
        icon: "✕",
      },
      warning: {
        bg: "#ffc107",
        icon: "⚠",
      },
      info: {
        bg: "#17a2b8",
        icon: "ℹ",
      },
    };

    const cor = cores[tipo] || cores.info;

    const toastId = "toast-" + Date.now();
    const toast = document.createElement("div");
    toast.id = toastId;
    toast.style.cssText = `
      background-color: white;
      color: #333;
      border-radius: 4px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      margin-bottom: 10px;
      max-width: 350px;
      overflow: hidden;
      opacity: 0;
      transition: opacity 0.3s ease-in-out;
      display: flex;
    `;

    // Cria os elementos internos do toast
    const iconDiv = document.createElement("div");
    iconDiv.style.cssText = `width: 30px; background-color: ${cor.bg}; color: white; display: flex; align-items: center; justify-content: center; font-size: 18px;`;
    iconDiv.innerHTML = `${cor.icon}`;

    const contentDiv = document.createElement("div");
    contentDiv.style.cssText = `padding: 10px 15px; flex-grow: 1;`;
    contentDiv.innerHTML = `<strong>${titulo}</strong><div>${mensagem}</div>`;

    // Cria o botão de fechar como um elemento DOM separado
    const closeButton = document.createElement("button");
    closeButton.className = "toast-close-button"; // Adiciona uma classe para identificação
    closeButton.style.cssText = `background: transparent; border: 0; font-size: 16px; padding: 10px; cursor: pointer; opacity: 0.5;`;
    closeButton.innerHTML = `&times;`; // Caractere 'X'

    // Adiciona os elementos ao toast
    toast.appendChild(iconDiv);
    toast.appendChild(contentDiv);
    toast.appendChild(closeButton); // Adiciona o botão de fechar

    toastContainer.appendChild(toast);

    // Adiciona o event listener para o botão de fechar
    closeButton.addEventListener("click", () => {
      // Inicia a transição de saída
      toast.style.opacity = "0";
      setTimeout(() => {
        if (toast.parentNode) {
          toast.parentNode.removeChild(toast);
        }
        // Remove o container se não houver mais toasts
        if (toastContainer.children.length === 0 && toastContainer.parentNode) {
          toastContainer.parentNode.removeChild(toastContainer);
        }
      }, 300); // Deve ser igual ou maior que o tempo de transição (0.3s)
    });

    // Inicia a transição de entrada
    setTimeout(() => {
      toast.style.opacity = "1";
    }, 10);

    // Temporizador para ocultar automaticamente
    setTimeout(() => {
      // Inicia a transição de saída
      toast.style.opacity = "0";

      setTimeout(() => {
        if (toast.parentNode) {
          toast.parentNode.removeChild(toast);
        }
        // Remove o container se não houver mais toasts
        if (toastContainer.children.length === 0 && toastContainer.parentNode) {
          toastContainer.parentNode.removeChild(toastContainer);
        }
      }, 300); // Deve ser igual ou maior que o tempo de transição (0.3s)
    }, duracao);
  },

  sucesso: function (mensagem, duracao = 5000) {
    this.mostrar("Sucesso!", mensagem, "success", duracao);
  },

  erro: function (mensagem, duracao = 5000) {
    this.mostrar("Erro", mensagem, "error", duracao);
  },

  alerta: function (mensagem, duracao = 5000) {
    this.mostrar("Atenção", mensagem, "warning", duracao);
  },

  info: function (mensagem, duracao = 5000) {
    this.mostrar("Informação", mensagem, "info", duracao);
  },
};

export default UIToast;

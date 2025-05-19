// uiToast.js

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

    toast.innerHTML = `
      <div style="width: 30px; background-color: ${cor.bg}; color: white; display: flex; align-items: center; justify-content: center; font-size: 18px;">
        ${cor.icon}
      </div>
      <div style="padding: 10px 15px; flex-grow: 1;">
        <strong>${titulo}</strong>
        <div>${mensagem}</div>
      </div>
      <button style="background: transparent; border: 0; font-size: 16px; padding: 10px; cursor: pointer; opacity: 0.5;" onclick="document.getElementById('${toastId}').remove()">×</button>
    `;

    toastContainer.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = "1";
    }, 10);

    setTimeout(() => {
      toast.style.opacity = "0";

      setTimeout(() => {
        if (toast.parentNode) {
          toast.parentNode.removeChild(toast);
        }

        if (toastContainer.children.length === 0 && toastContainer.parentNode) {
          toastContainer.parentNode.removeChild(toastContainer);
        }
      }, 300);
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

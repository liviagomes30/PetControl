const UILoading = {
  mostrar: function (mensagem = "Carregando...") {
    this.esconder();

    const loadingEl = document.createElement("div");
    loadingEl.id = "loading-overlay";
    loadingEl.className = "loading-overlay";
    loadingEl.style.cssText = `
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.5);
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      z-index: 9999;
    `;

    const spinner = document.createElement("div");
    spinner.className = "spinner";
    spinner.style.cssText = `
      width: 40px;
      height: 40px;
      border: 4px solid rgba(255, 255, 255, 0.3);
      border-radius: 50%;
      border-top: 4px solid #fff;
      animation: spin 1s linear infinite;
    `;

    const style = document.createElement("style");
    style.textContent = `
      @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
      }
    `;
    document.head.appendChild(style);

    const textoLoading = document.createElement("div");
    textoLoading.id = "textoLoading";
    textoLoading.className = "mt-3";
    textoLoading.style.cssText = `
      color: white;
      margin-top: 10px;
      font-size: 16px;
    `;
    textoLoading.textContent = mensagem;

    loadingEl.appendChild(spinner);
    loadingEl.appendChild(textoLoading);
    document.body.appendChild(loadingEl);
  },

  esconder: function () {
    const loadingEl = document.getElementById("loading-overlay");
    // Add a check to see if the element exists before trying to remove it
    if (loadingEl && loadingEl.parentNode) {
      // Check if it exists and has a parent
      loadingEl.parentNode.removeChild(loadingEl);
    }
  },
};

export default UILoading;

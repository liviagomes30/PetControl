// uiModalErro.js

const UIModalErro = {
  mostrar: function (mensagem, callback = null) {
    const modalExistente = document.querySelector(".error-dialog");
    if (modalExistente) {
      modalExistente.parentNode.removeChild(modalExistente);
    }

    const errorDialog = document.createElement("div");
    errorDialog.className = "error-dialog";
    errorDialog.style.cssText = `
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.5);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 9999;
    `;

    errorDialog.innerHTML = `
      <div class="error-content" style="background-color: white; border-radius: 5px; width: 400px; max-width: 90%; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);">
        <div style="background-color: #400101; color: white; padding: 15px; border-top-left-radius: 5px; border-top-right-radius: 5px;">
          <h3 style="margin: 0; font-size: 18px;">Erro</h3>
        </div>
        <div style="padding: 20px;">
          <p id="error-message" style="margin-top: 0; margin-bottom: 20px; color: #333;">${mensagem}</p>
          <button id="error-ok" style="background-color: #F2541B; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; float: right;">OK</button>
        </div>
      </div>
    `;

    document.body.appendChild(errorDialog);

    document.getElementById("error-ok").addEventListener("click", () => {
      if (errorDialog.parentNode) {
        errorDialog.parentNode.removeChild(errorDialog);
      }

      if (typeof callback === "function") {
        callback();
      }
    });
  },
};

export default UIModalErro;

// uiValidacao.js

const UIValidacao = {
  mostrarErro: function (idCampo, mensagem) {
    const campo = document.getElementById(idCampo);
    if (!campo) return;

    let divErro = document.getElementById(`${idCampo}-error`);

    if (!divErro) {
      divErro = document.createElement("div");
      divErro.id = `${idCampo}-error`;
      divErro.className = "input-error";
      divErro.style.cssText = `
        color: #F23E2E;
        font-size: 0.875rem;
        margin-top: 4px;
        font-style: italic;
      `;

      if (campo.parentNode) {
        campo.parentNode.insertBefore(divErro, campo.nextSibling);
      }
    }

    campo.classList.add("is-invalid");
    divErro.textContent = mensagem;
    divErro.style.display = "block";
  },

  /**
   * Limpa todas as mensagens de erro de um formulário
   * @param {string} idFormulario - ID do formulário
   */
  limparErros: function (idFormulario) {
    const form = document.getElementById(idFormulario);
    if (!form) return;

    // Limpar classes de erro
    const camposInvalidos = form.querySelectorAll(".is-invalid");
    camposInvalidos.forEach((campo) => campo.classList.remove("is-invalid"));

    // Limpar mensagens de erro
    const mensagensErro = form.querySelectorAll("[id$='-error']");
    mensagensErro.forEach((msg) => {
      msg.textContent = "";
      msg.style.display = "none";
    });
  },
};

export default UIValidacao;

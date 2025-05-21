// uiValidacao.js - Componente para validação de formulários seguindo o padrão Server-side do Bootstrap

const UIValidacao = {
  // Limpa todas as mensagens de erro de um formulário
  limparErros: function (formId) {
    const form = document.getElementById(formId);
    if (!form) return;

    // Remove mensagens de erro antigas (suporte a sistema legado)
    const erros = form.querySelectorAll(".input-error");
    erros.forEach((element) => {
      element.textContent = "";
      element.style.display = "none";
    });

    // Remove classes de validação do Bootstrap
    const camposInvalidos = form.querySelectorAll(".is-invalid");
    camposInvalidos.forEach((campo) => {
      campo.classList.remove("is-invalid");
      campo.classList.remove("is-valid");
    });

    // Remove também as classes de campo válido
    const camposValidos = form.querySelectorAll(".is-valid");
    camposValidos.forEach((campo) => {
      campo.classList.remove("is-valid");
    });

    // Limpa todos os feedbacks
    const feedbacks = form.querySelectorAll(
      ".invalid-feedback, .valid-feedback"
    );
    feedbacks.forEach((feedback) => {
      feedback.textContent = "";
    });
  },

  // Exibe uma mensagem de erro para um campo específico (método para validação server-side)
  mostrarErro: function (campoId, mensagem) {
    const input = document.getElementById(campoId);
    if (!input) return;

    // Marca o campo como inválido no Bootstrap
    input.classList.remove("is-valid"); // Remove classe de válido se existir
    input.classList.add("is-invalid");

    // Gera o ID para o feedback
    const feedbackId = `validation${
      campoId.charAt(0).toUpperCase() + campoId.slice(1)
    }Feedback`;

    // Adiciona o aria-describedby para acessibilidade
    if (input.getAttribute("aria-describedby")) {
      // Se já existe, apenas adiciona o novo ID
      if (!input.getAttribute("aria-describedby").includes(feedbackId)) {
        input.setAttribute(
          "aria-describedby",
          input.getAttribute("aria-describedby") + " " + feedbackId
        );
      }
    } else {
      input.setAttribute("aria-describedby", feedbackId);
    }

    // Verificar se existe um elemento para exibir erro (modo legado)
    const erroId = `${campoId}Error`;
    const erroElement = document.getElementById(erroId);

    if (erroElement) {
      erroElement.textContent = mensagem;
      erroElement.style.display = "block";
    }

    // Verificar se existe um feedback de validação do Bootstrap
    let feedbackElement = document.getElementById(feedbackId);

    if (!feedbackElement) {
      // Se não existir, criar um novo
      feedbackElement = document.createElement("div");
      feedbackElement.className = "invalid-feedback";
      feedbackElement.id = feedbackId;

      // Verifica se é um input-group
      const inputGroup = input.closest(".input-group");
      if (inputGroup) {
        // Se for input group, adiciona a classe has-validation e adiciona o feedback após o grupo
        inputGroup.classList.add("has-validation");
        inputGroup.parentNode.appendChild(feedbackElement);
      } else {
        // Senão, adiciona após o input
        input.parentNode.appendChild(feedbackElement);
      }
    }

    // Atualizar a mensagem de feedback
    feedbackElement.textContent = mensagem;
  },

  // Exibe uma mensagem de sucesso para um campo (método para validação server-side)
  mostrarSucesso: function (campoId, mensagem = "Parece bom!") {
    const input = document.getElementById(campoId);
    if (!input) return;

    // Marca o campo como válido no Bootstrap
    input.classList.remove("is-invalid"); // Remove classe de inválido se existir
    input.classList.add("is-valid");

    // Gera o ID para o feedback
    const feedbackId = `validation${
      campoId.charAt(0).toUpperCase() + campoId.slice(1)
    }ValidFeedback`;

    // Verificar se existe um feedback de validação do Bootstrap
    let feedbackElement = document.getElementById(feedbackId);

    if (!feedbackElement) {
      // Se não existir, criar um novo
      feedbackElement = document.createElement("div");
      feedbackElement.className = "valid-feedback";
      feedbackElement.id = feedbackId;
      input.parentNode.appendChild(feedbackElement);
    }

    // Atualizar a mensagem de feedback
    feedbackElement.textContent = mensagem;
  },

  // Valida um campo específico e exibe o feedback apropriado
  validarCampo: function (
    campo,
    validador,
    mensagemErro,
    mensagemSucesso = "Parece bom!"
  ) {
    const input = document.getElementById(campo);
    if (!input) return false;

    let valido = true;

    // Se for uma função, executa para validar
    if (typeof validador === "function") {
      valido = validador(input.value);
    }
    // Se for uma regex, testa o valor
    else if (validador instanceof RegExp) {
      valido = validador.test(input.value);
    }
    // Se for um valor booleano, usa diretamente
    else if (typeof validador === "boolean") {
      valido = validador;
    }

    // Exibe feedback adequado
    if (valido) {
      this.mostrarSucesso(campo, mensagemSucesso);
    } else {
      this.mostrarErro(campo, mensagemErro);
    }

    return valido;
  },

  // Foca no primeiro campo inválido de um formulário
  focarPrimeiroInvalido: function (formId) {
    const form = document.getElementById(formId);
    if (!form) return;

    const primeiroInvalido = form.querySelector(".is-invalid");
    if (primeiroInvalido) {
      primeiroInvalido.scrollIntoView({ behavior: "smooth", block: "center" });
      primeiroInvalido.focus();
    }
  },

  // Adiciona feedback para todos os campos obrigatórios
  adicionarFeedbackCamposObrigatorios: function (formId) {
    const form = document.getElementById(formId);
    if (!form) return;

    const camposObrigatorios = form.querySelectorAll("[required]");

    camposObrigatorios.forEach((campo) => {
      // Gera o ID para o feedback
      const feedbackId = `validation${
        campo.id.charAt(0).toUpperCase() + campo.id.slice(1)
      }Feedback`;

      // Verifica se já existe um feedback para este campo
      let feedbackElement = document.getElementById(feedbackId);

      // Se não existir, criar um novo
      if (!feedbackElement) {
        feedbackElement = document.createElement("div");
        feedbackElement.className = "invalid-feedback";
        feedbackElement.id = feedbackId;

        // Define a mensagem padrão com base no tipo de campo
        const labelElement = document.querySelector(`label[for="${campo.id}"]`);
        const labelText = labelElement
          ? labelElement.textContent.replace(" *", "")
          : "Este campo";

        if (campo.tagName.toLowerCase() === "select") {
          feedbackElement.textContent = `Por favor, selecione ${labelText.toLowerCase()}.`;
        } else {
          feedbackElement.textContent = `Por favor, preencha ${labelText.toLowerCase()}.`;
        }

        // Verifica se é um input-group
        const inputGroup = campo.closest(".input-group");
        if (inputGroup) {
          // Se for input group, adiciona a classe has-validation e adiciona o feedback após o grupo
          inputGroup.classList.add("has-validation");
          inputGroup.parentNode.appendChild(feedbackElement);
        } else {
          // Senão, adiciona após o input
          campo.parentNode.appendChild(feedbackElement);
        }

        // Adiciona o aria-describedby para acessibilidade
        campo.setAttribute("aria-describedby", feedbackId);
      }
    });
  },
};

export default UIValidacao;

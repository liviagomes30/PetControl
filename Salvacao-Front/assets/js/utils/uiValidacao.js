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

  limparErroCampo: function (campoId) {
    const campo = document.getElementById(campoId);
    if (!campo) return;

    campo.classList.remove("is-invalid");
    campo.classList.remove("is-valid"); // Remove também 'is-valid' para um estado neutro

    // ID de referência para acessibilidade e busca
    const feedbackIdAria = `validation${
      campoId.charAt(0).toUpperCase() + campoId.slice(1)
    }Feedback`;
    let feedbackElementBS = null;
    const inputGroup = campo.closest(".input-group");

    // Tenta encontrar o elemento de feedback do Bootstrap
    // 1. Para campos dentro de um input-group
    if (inputGroup) {
      const nextSibling = inputGroup.nextElementSibling;
      if (nextSibling) {
        // Prioriza elemento com ID específico ou um .invalid-feedback genérico
        if (
          nextSibling.id === feedbackIdAria &&
          nextSibling.classList.contains("invalid-feedback")
        ) {
          feedbackElementBS = nextSibling;
        } else if (nextSibling.classList.contains("invalid-feedback")) {
          // Pode ser um sem ID específico
          feedbackElementBS = nextSibling;
        }
      }
    }
    // 2. Para campos normais (não em input-group)
    else {
      const nextSibling = campo.nextElementSibling;
      if (nextSibling) {
        if (
          nextSibling.id === feedbackIdAria &&
          nextSibling.classList.contains("invalid-feedback")
        ) {
          feedbackElementBS = nextSibling;
        } else if (nextSibling.classList.contains("invalid-feedback")) {
          // Pode ser um sem ID específico
          feedbackElementBS = nextSibling;
        }
      }
    }

    // Tratar o elemento legado se ele existir e for o feedback do Bootstrap
    const erroIdLegado = `${campoId}Error`;
    const erroElementLegado = document.getElementById(erroIdLegado);

    if (
      erroElementLegado &&
      erroElementLegado.classList.contains("invalid-feedback")
    ) {
      // Se o elemento legado é também o de feedback do Bootstrap, use-o
      feedbackElementBS = erroElementLegado;
    }

    // Limpa o texto do elemento de feedback do Bootstrap encontrado/identificado
    if (feedbackElementBS) {
      feedbackElementBS.textContent = "";
      // A classe 'is-invalid' já foi removida do campo, o Bootstrap irá esconder o feedback.
    }

    // Limpa o elemento legado separadamente se ele não for o feedback do Bootstrap
    if (erroElementLegado && erroElementLegado !== feedbackElementBS) {
      erroElementLegado.textContent = "";
      erroElementLegado.style.display = "none";
    }
  },

  // Exibe uma mensagem de erro para um campo específico (método para validação server-side)
  mostrarErro: function (campoId, mensagem) {
    const input = document.getElementById(campoId);
    if (!input) return;

    // Marca o campo como inválido no Bootstrap
    input.classList.remove("is-valid"); // Remove classe de válido se existir
    input.classList.add("is-invalid");

    // Adiciona o aria-describedby para acessibilidade (mantido da versão original)
    const feedbackIdAria = `validation${
      campoId.charAt(0).toUpperCase() + campoId.slice(1)
    }Feedback`; // Usar um ID consistente para aria
    if (input.getAttribute("aria-describedby")) {
      if (!input.getAttribute("aria-describedby").includes(feedbackIdAria)) {
        input.setAttribute(
          "aria-describedby",
          input.getAttribute("aria-describedby") + " " + feedbackIdAria
        );
      }
    } else {
      input.setAttribute("aria-describedby", feedbackIdAria);
    }

    // Verificar se existe um elemento para exibir erro (modo legado)
    const erroIdLegado = `${campoId}Error`;
    const erroElementLegado = document.getElementById(erroIdLegado);
    let mensagemDefinidaPeloLegado = false;

    if (erroElementLegado) {
      erroElementLegado.textContent = mensagem;
      erroElementLegado.style.display = "block";
      // Se o elemento legado também for o feedback do Bootstrap, marcamos
      if (erroElementLegado.classList.contains("invalid-feedback")) {
        mensagemDefinidaPeloLegado = true;
      }
    }

    // Lógica para encontrar/criar o feedbackElement do Bootstrap
    let feedbackElementBS = null;
    const inputGroup = input.closest(".input-group");

    // Tenta encontrar um feedbackElement existente associado ao input ou input-group
    if (inputGroup) {
      // Para input-groups, o feedback geralmente fica FORA e DEPOIS do input-group.
      // Tenta encontrar por ID específico ou classe .invalid-feedback DEPOIS do input-group.
      const nextSibling = inputGroup.nextElementSibling;
      if (nextSibling) {
        if (
          nextSibling.id === feedbackIdAria &&
          nextSibling.classList.contains("invalid-feedback")
        ) {
          feedbackElementBS = nextSibling;
        } else if (
          nextSibling.classList.contains("invalid-feedback") &&
          !nextSibling.id
        ) {
          // Prioriza um sem ID se o ID específico não bate
          feedbackElementBS = nextSibling;
        } else if (nextSibling.classList.contains("invalid-feedback")) {
          // Ou qualquer invalid-feedback se não achou com ID ou sem ID específico
          feedbackElementBS = nextSibling;
        }
      }
    } else {
      // Para inputs normais (não em input-group)
      // Tenta encontrar por ID específico ou classe .invalid-feedback como irmão posterior.
      const nextSibling = input.nextElementSibling;
      if (nextSibling) {
        if (
          nextSibling.id === feedbackIdAria &&
          nextSibling.classList.contains("invalid-feedback")
        ) {
          feedbackElementBS = nextSibling;
        } else if (
          nextSibling.classList.contains("invalid-feedback") &&
          !nextSibling.id
        ) {
          feedbackElementBS = nextSibling;
        } else if (nextSibling.classList.contains("invalid-feedback")) {
          feedbackElementBS = nextSibling;
        }
      }
    }

    // Se o elemento legado já é o feedback do Bootstrap, usa ele.
    if (
      erroElementLegado &&
      erroElementLegado.classList.contains("invalid-feedback")
    ) {
      feedbackElementBS = erroElementLegado;
    }

    // Se NENHUM feedbackElementBS existente foi encontrado, criar um novo.
    if (!feedbackElementBS) {
      feedbackElementBS = document.createElement("div");
      feedbackElementBS.className = "invalid-feedback";
      feedbackElementBS.id = feedbackIdAria; // Atribui o ID para acessibilidade e referência

      if (inputGroup) {
        inputGroup.classList.add("has-validation");
        inputGroup.parentNode.insertBefore(
          feedbackElementBS,
          inputGroup.nextSibling
        );
      } else {
        // Garante que não vai inserir se o elemento legado (sem classe invalid-feedback) já estiver lá
        if (
          !(erroElementLegado && input.nextElementSibling === erroElementLegado)
        ) {
          input.parentNode.insertBefore(feedbackElementBS, input.nextSibling);
        } else if (!erroElementLegado) {
          // Se não há elemento legado, insere normalmente
          input.parentNode.insertBefore(feedbackElementBS, input.nextSibling);
        }
      }
    }

    // Atualizar a mensagem do feedbackElementBS
    // Só define o texto se não foi definido pelo legado OU se o legado não é o mesmo elemento
    if (
      feedbackElementBS &&
      (!mensagemDefinidaPeloLegado || erroElementLegado !== feedbackElementBS)
    ) {
      feedbackElementBS.textContent = mensagem;
    }

    // Garante classes corretas no elemento de feedback do Bootstrap
    if (feedbackElementBS) {
      feedbackElementBS.classList.add("invalid-feedback");
      feedbackElementBS.classList.remove("valid-feedback");
    }
  },

  // Exibe uma mensagem de sucesso para um campo (método para validação server-side)
  mostrarSucesso: function (campoId, mensagem = "Parece bom!") {
    const input = document.getElementById(campoId);
    if (!input) return;

    // Marca o campo como válido no Bootstrap
    input.classList.remove("is-invalid"); // Remove classe de inválido se existir
    input.classList.add("is-valid");

    // Gera o ID para o feedback de sucesso
    const feedbackId = `validation${
      campoId.charAt(0).toUpperCase() + campoId.slice(1)
    }ValidFeedback`;

    let feedbackElement = null;
    const inputGroup = input.closest(".input-group");

    if (inputGroup) {
      if (
        inputGroup.nextElementSibling &&
        (inputGroup.nextElementSibling.id === feedbackId ||
          inputGroup.nextElementSibling.classList.contains("valid-feedback"))
      ) {
        feedbackElement = inputGroup.nextElementSibling;
      }
    } else {
      if (
        input.nextElementSibling &&
        (input.nextElementSibling.id === feedbackId ||
          input.nextElementSibling.classList.contains("valid-feedback"))
      ) {
        feedbackElement = input.nextElementSibling;
      }
    }

    if (!feedbackElement) {
      feedbackElement = document.createElement("div");
      feedbackElement.id = feedbackId;
      if (inputGroup) {
        inputGroup.classList.add("has-validation");
        inputGroup.parentNode.insertBefore(
          feedbackElement,
          inputGroup.nextSibling
        );
      } else {
        input.parentNode.insertBefore(feedbackElement, input.nextSibling);
      }
    }

    feedbackElement.className = "valid-feedback"; // Garante a classe correta
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

    if (typeof validador === "function") {
      valido = validador(input.value);
    } else if (validador instanceof RegExp) {
      valido = validador.test(input.value);
    } else if (typeof validador === "boolean") {
      valido = validador;
    }

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

  // Adiciona feedback para todos os campos obrigatórios (esta função pode precisar de revisão se usada extensivamente,
  // para garantir que não crie divs redundantes se `mostrarErro` já os manipula bem)
  adicionarFeedbackCamposObrigatorios: function (formId) {
    const form = document.getElementById(formId);
    if (!form) return;

    const camposObrigatorios = form.querySelectorAll("[required]");

    camposObrigatorios.forEach((campo) => {
      const feedbackId = `validation${
        campo.id.charAt(0).toUpperCase() + campo.id.slice(1)
      }Feedback`; // ID para acessibilidade e referência

      let feedbackElement = null;
      const inputGroup = campo.closest(".input-group");

      // Tenta encontrar um feedback já existente (irmão do input ou do input-group)
      if (inputGroup) {
        if (
          inputGroup.nextElementSibling &&
          inputGroup.nextElementSibling.classList.contains("invalid-feedback")
        ) {
          feedbackElement = inputGroup.nextElementSibling;
        }
      } else {
        if (
          campo.nextElementSibling &&
          campo.nextElementSibling.classList.contains("invalid-feedback")
        ) {
          feedbackElement = campo.nextElementSibling;
        }
      }

      if (!feedbackElement) {
        feedbackElement = document.createElement("div");
        feedbackElement.className = "invalid-feedback";
        // Não necessariamente atribuir o ID feedbackId aqui, pois o HTML pode já ter um feedback genérico
        // que foi pego acima. Se não foi pego, este novo div será o feedback.
        // Se o HTML já tiver um div.invalid-feedback sem ID, esta função não deve criar outro,
        // mas sim garantir que ele exista. A lógica de `mostrarErro` é mais crítica para a exibição.

        // Para esta função, se o div de feedback NÃO EXISTE DE TODO, nós o criamos.
        // Se ele existe (mesmo sem ID), não criamos outro.
        let deveCriar = true;
        if (inputGroup) {
          if (
            inputGroup.nextElementSibling &&
            inputGroup.nextElementSibling.classList.contains("invalid-feedback")
          )
            deveCriar = false;
        } else {
          if (
            campo.nextElementSibling &&
            campo.nextElementSibling.classList.contains("invalid-feedback")
          )
            deveCriar = false;
        }

        if (deveCriar) {
          feedbackElement.id = feedbackId; // Dê o ID se for criar
          if (inputGroup) {
            inputGroup.classList.add("has-validation");
            inputGroup.parentNode.appendChild(feedbackElement);
          } else {
            campo.parentNode.appendChild(feedbackElement);
          }
        } else if (feedbackElement && !feedbackElement.id) {
          // Se encontrou um genérico, pode dar o ID para aria
          feedbackElement.id = feedbackId;
        }
      }

      // Preenche a mensagem padrão se o feedbackElement (existente ou novo) estiver vazio
      if (feedbackElement && feedbackElement.textContent === "") {
        const labelElement = document.querySelector(`label[for="${campo.id}"]`);
        const labelText = labelElement
          ? labelElement.textContent.replace(" *", "").trim()
          : "Este campo";

        if (campo.tagName.toLowerCase() === "select") {
          feedbackElement.textContent = `Por favor, selecione ${labelText.toLowerCase()}.`;
        } else {
          feedbackElement.textContent = `Por favor, preencha ${labelText.toLowerCase()}.`;
        }
      }

      // Adiciona o aria-describedby para acessibilidade
      if (feedbackElement && feedbackElement.id) {
        // Só adiciona aria se o feedback tiver ID
        if (campo.getAttribute("aria-describedby")) {
          if (
            !campo.getAttribute("aria-describedby").includes(feedbackElement.id)
          ) {
            campo.setAttribute(
              "aria-describedby",
              campo.getAttribute("aria-describedby") + " " + feedbackElement.id
            );
          }
        } else {
          campo.setAttribute("aria-describedby", feedbackElement.id);
        }
      }
    });
  },
};

export default UIValidacao;

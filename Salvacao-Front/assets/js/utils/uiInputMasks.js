// uiInputMasks.js

const UIInputMasks = {
  inicializar: function () {
    this.aplicarMascaras();
  },

  aplicarMascaras: function () {
    const camposComMascara = document.querySelectorAll("[data-mask]");

    camposComMascara.forEach((campo) => {
      const tipoMascara = campo.getAttribute("data-mask");

      campo.setAttribute("maxlength", this.getMaxLengthForMask(tipoMascara));

      campo.addEventListener("input", (e) => {
        const valorAtual = e.target.value;
        const valorFormatado = this.aplicarMascara(
          valorAtual,
          tipoMascara,
          false
        ); // Adicione false para isBlur

        if (tipoMascara === "money") {
          const valorNumerico = this.extrairNumero(valorAtual);
          if (valorNumerico !== null) {
            e.target.dataset.valor = valorNumerico;
          }
        }

        e.target.value = valorFormatado;
      });

      campo.addEventListener("focus", function (e) {
        e.target.select();
      });

      if (["money", "number"].includes(tipoMascara)) {
        campo.addEventListener("blur", (e) => {
          if (tipoMascara === "money" && e.target.value) {
            const valor = e.target.dataset.valor
              ? parseFloat(e.target.dataset.valor)
              : 0;
            e.target.value = this.formatarValorMonetario(valor);
          }
        });
      }

      if (campo.value) {
        if (tipoMascara === "money") {
          campo.value = this.formatarValorMonetario(campo.value);
        } else {
          campo.value = this.aplicarMascara(campo.value, tipoMascara);
        }
      }
    });

    this.aplicarLimitadorTexto();
  },

  aplicarMascara: function (valor, tipo) {
    switch (tipo) {
      case "cpf":
        return this.cpfMask(valor);
      case "cnpj":
        return this.cnpjMask(valor);
      case "phone":
        return this.phoneMask(valor);
      case "cep":
        return this.cepMask(valor);
      case "pis":
        return this.pisMask(valor);
      case "date":
        return this.dateMask(valor);
      case "money":
        return this.moneyMask(valor);
      case "number":
        return this.numberMask(valor);
      default:
        return valor;
    }
  },

  getMaxLengthForMask: function (tipo) {
    const maxLengths = {
      cpf: 14, // 999.999.999-99
      cnpj: 18, // 99.999.999/9999-99
      phone: 15, // (99) 99999-9999
      cep: 9, // 99999-999
      pis: 14, // 999.99999.99-9
      date: 10, // 99/99/9999
      money: 20, // Valor grande suficiente
      number: 10, // Valor padrão
    };

    return maxLengths[tipo] || 50;
  },

  cpfMask: function (value) {
    return value
      .replace(/\D/g, "")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d{1,2})/, "$1-$2")
      .replace(/(-\d{2})\d+?$/, "$1");
  },

  dateMask: function (value) {
    return value
      .replace(/\D/g, "")
      .replace(/(\d{2})(\d)/, "$1/$2")
      .replace(/(\d{2})(\d)/, "$1/$2")
      .replace(/(\/\d{4})\d+?$/, "$1");
  },

  cnpjMask: function (value) {
    return value
      .replace(/\D/g, "")
      .replace(/(\d{2})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d)/, "$1/$2")
      .replace(/(\d{4})(\d)/, "$1-$2")
      .replace(/(-\d{2})\d+?$/, "$1");
  },

  phoneMask: function (value) {
    return value
      .replace(/\D/g, "")
      .replace(/(\d{2})(\d)/, "($1) $2")
      .replace(/(\d{4})(\d)/, "$1-$2")
      .replace(/(\d{4})-(\d)(\d{4})/, "$1$2-$3")
      .replace(/(-\d{4})\d+?$/, "$1");
  },

  cepMask: function (value) {
    return value
      .replace(/\D/g, "")
      .replace(/(\d{5})(\d)/, "$1-$2")
      .replace(/(-\d{3})\d+?$/, "$1");
  },

  pisMask: function (value) {
    return value
      .replace(/\D/g, "")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{5})(\d)/, "$1.$2")
      .replace(/(\d{5}\.)(\d{2})(\d)/, "$1$2-$3")
      .replace(/(-\d{1})\d+?$/, "$1");
  },

  moneyMask: function (value, isBlur = false) {
    let valorLimpo = value.replace(/[^\d]/g, ""); // Remove tudo exceto dígitos

    // Se estiver em blur ou for vazio, formata como monetário
    if (isBlur || !valorLimpo) {
      valorLimpo = valorLimpo.replace(/^0+/, "") || "0"; // Remove zeros à esquerda
      const valorNumerico = parseFloat(valorLimpo) / 100; // Converte para decimal
      return this.formatarValorMonetario(valorNumerico);
    }

    // Durante a digitação, mostra apenas os números
    return valorLimpo.replace(/^0+/, "") || "0";
  },

  numberMask: function (value) {
    let valorLimpo = value.replace(/\D/g, "");

    valorLimpo = valorLimpo.replace(/^0+/, "");

    return valorLimpo;
  },

  formatarValorMonetario: function (valor) {
    if (valor === "" || valor === null || valor === undefined) return "";

    const numero = typeof valor === "string" ? parseFloat(valor) : valor;

    if (isNaN(numero)) return "";

    return numero.toLocaleString("pt-BR", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });
  },

  extrairNumero: function (texto) {
    if (!texto) return null;

    const numeroTexto = texto.replace(/[^\d.,]/g, "").replace(",", ".");

    const numero = parseFloat(numeroTexto);
    return !isNaN(numero) ? numero : null;
  },

  aplicarLimitadorTexto: function (seletor = "[data-max-length]") {
    const camposTexto = document.querySelectorAll(seletor);

    camposTexto.forEach((campo) => {
      const maxLength = campo.dataset.maxLength;
      if (!maxLength) return;

      // Verificar se já existe um contador para este campo
      let contador = campo.nextElementSibling;
      if (!contador || !contador.classList.contains("text-muted")) {
        if (campo.dataset.showCounter === "true") {
          contador = document.createElement("small");
          contador.className = "text-muted d-block text-end";
          contador.textContent = `0/${maxLength}`;
          campo.parentNode.insertBefore(contador, campo.nextSibling);
        }
      }

      // Restante do código...
    });
  },

  obterValorNumerico: function (campo) {
    if (!campo) return null;

    if (campo.dataset.valor !== undefined) {
      return campo.dataset.valor ? parseFloat(campo.dataset.valor) : null;
    }

    return this.extrairNumero(campo.value);
  },
};

export default UIInputMasks;

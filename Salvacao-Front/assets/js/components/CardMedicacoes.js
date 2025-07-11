// assets/js/components/CardMedicacoes.js

class CardMedicacoes {
  constructor(containerId, compromissos = []) {
    this.containerId = containerId;
    this.compromissos = compromissos;
  }

  render() {
    const container = document.getElementById(this.containerId);
    if (!container) return;

    let compromissosHTML = "";

    if (!this.compromissos || this.compromissos.length === 0) {
      compromissosHTML = `<tr><td colspan="4" class="text-center">Nenhum compromisso de medicação encontrado.</td></tr>`;
    } else {
      this.compromissos.forEach((comp) => {
        const statusClass =
          comp.status.toLowerCase() === "planejado"
            ? "text-primary"
            : "text-success";
        const statusText =
          comp.status.toLowerCase() === "planejado" ? "Planejado" : "Aplicado";

        // **AQUI ESTÁ A CORREÇÃO:** Usamos a nova função universal.
        const dataFormatada = this.formatarDataUniversal(comp.dataHora);

        compromissosHTML += `
          <tr>
            <td>${dataFormatada}</td>
            <td>${comp.animalNome || "N/D"}</td>
            <td>${comp.medicamentoNome || "N/D"}</td>
            <td>
              <strong class="${statusClass}">${statusText}</strong>
            </td>
          </tr>
        `;
      });
    }

    const cardHTML = `
      <div class="card mt-4" id="card-medicacoes">
        <div class="card-header">Medicações (Aplicadas e Planejadas)</div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>Data/Hora</th>
                  <th>Animal</th>
                  <th>Medicamento</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                ${compromissosHTML}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    `;

    container.innerHTML = cardHTML;
  }

  formatarDataUniversal(dataOrigem) {
    if (!dataOrigem) {
      return "Data não fornecida";
    }

    let ano,
      mes,
      dia,
      hora = "00",
      minuto = "00";

    // Cenário 1: A data vem como um array (ex: [2024, 7, 15, 9, 30])
    if (Array.isArray(dataOrigem)) {
      [ano, mes, dia, hora = "00", minuto = "00"] = dataOrigem;
    }
    // Cenário 2: A data vem como uma string (ex: "2024-07-15T09:30:00")
    else if (typeof dataOrigem === "string") {
      const partes = dataOrigem.split("T");
      if (partes.length === 2) {
        const dataPartes = partes[0].split("-");
        const horaPartes = partes[1].split(":");
        if (dataPartes.length === 3) [ano, mes, dia] = dataPartes;
        if (horaPartes.length >= 2) [hora, minuto] = horaPartes;
      } else {
        const dataPartes = dataOrigem.split("-");
        if (dataPartes.length === 3) [ano, mes, dia] = dataPartes;
      }
    }
    // Cenário 3: A data vem como um objeto (formato comum de serialização)
    else if (
      typeof dataOrigem === "object" &&
      dataOrigem.year &&
      dataOrigem.monthValue
    ) {
      ano = dataOrigem.year;
      mes = dataOrigem.monthValue;
      dia = dataOrigem.dayOfMonth;
      hora = dataOrigem.hour;
      minuto = dataOrigem.minute;
    }

    // Se, após todas as tentativas, o ano não for válido, retorna erro.
    if (!ano) {
      console.error(
        "Formato de data desconhecido recebido do backend:",
        dataOrigem
      );
      return "Formato de data inválido";
    }

    // Função para adicionar um zero à esquerda se o número for menor que 10
    const pad = (num) => num.toString().padStart(2, "0");

    return `${pad(dia)}/${pad(mes)}/${ano} ${pad(hora)}:${pad(minuto)}`;
  }
}

export default CardMedicacoes;

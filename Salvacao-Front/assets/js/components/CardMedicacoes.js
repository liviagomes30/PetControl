// assets/js/components/CardMedicacoes.js

class CardMedicacoes {
  constructor(containerId, compromissos = [], animaisMap = new Map()) {
    this.containerId = containerId;
    this.compromissos = compromissos;
    this.animaisMap = animaisMap;
  }

  render() {
    const container = document.getElementById(this.containerId);
    if (!container) return;

    let compromissosHTML = "";

    if (!this.compromissos || this.compromissos.length === 0) {
      compromissosHTML = `<tr><td colspan="5" class="text-center">Nenhum compromisso de medicação encontrado.</td></tr>`;
    } else {
      this.compromissos.forEach((comp) => {
        const statusClass =
          comp.status.toLowerCase() === "planejado"
            ? "text-primary"
            : "text-success";
        const statusText =
          comp.status.toLowerCase() === "planejado" ? "Planejado" : "Aplicado";

        const dataFormatada = this.formatarDataUniversal(comp.dataHora);

        let acoesHTML = "";
        const statusLower = comp.status.toLowerCase();

        if (statusLower === "planejado") {
          const animalEntry = [...this.animaisMap.entries()].find(
            ([id, nome]) => nome === comp.animalNome
          );
          const animalId = animalEntry ? animalEntry[0] : null;

          if (animalId) {
            // --- INÍCIO DA MODIFICAÇÃO ---
            // Adicionado o compromissoId (que é o posologiaId do DTO) à URL.
            const url = `pages/medicamentos/efetuarMedicacao.html?animalId=${animalId}&compromissoId=${comp.posologiaId}`;
            // --- FIM DA MODIFICAÇÃO ---

            acoesHTML = `
              <a href="${url}" class="btn btn-sm btn-primary" title="Efetuar Medicação">
                <i class="bi bi-check-circle me-1"></i> Efetuar
              </a>
            `;
          }
        } else if (statusLower === "aplicado") {
          acoesHTML = `
            <a href="pages/medicamentos/historicoMedicacoes.html" class="btn btn-sm btn-secondary" title="Ver Histórico de Medicações">
              <i class="bi bi-clock-history me-1"></i> Histórico
            </a>
          `;
        }

        compromissosHTML += `
          <tr>
            <td>${dataFormatada}</td>
            <td>${comp.animalNome || "N/D"}</td>
            <td>${comp.medicamentoNome || "N/D"}</td>
            <td>
              <strong class="${statusClass}">${statusText}</strong>
            </td>
            <td>${acoesHTML}</td>
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
                  <th>Ações</th>
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
    if (!dataOrigem) return "Data não fornecida";
    let ano,
      mes,
      dia,
      hora = "00",
      minuto = "00";
    if (Array.isArray(dataOrigem)) {
      [ano, mes, dia, hora = "00", minuto = "00"] = dataOrigem;
    } else if (typeof dataOrigem === "string") {
      const partes = dataOrigem.split("T");
      if (partes.length === 2) {
        const dataPartes = partes[0].split("-");
        const horaPartes = partes[1].split(":");
        if (dataPartes.length === 3) [ano, mes, dia] = dataPartes;
        if (horaPartes.length >= 2) [hora, minuto] = horaPartes;
      }
    } else if (
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
    if (!ano) {
      console.error("Formato de data desconhecido:", dataOrigem);
      return "Formato de data inválido";
    }
    const pad = (num) => String(num).padStart(2, "0");
    return `${pad(dia)}/${pad(mes)}/${ano} ${pad(hora)}:${pad(minuto)}`;
  }
}

export default CardMedicacoes;

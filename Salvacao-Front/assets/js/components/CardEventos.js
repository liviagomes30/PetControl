// CardEventos.js
import EventoService from "../services/EventoService.js";

class CardEventos {
  // Adicionamos o 'onStatusChangeCallback' ao construtor
  constructor(containerId, eventos = [], onStatusChangeCallback) {
    this.containerId = containerId;
    this.eventos = eventos;
    this.eventoService = new EventoService();
    this.onStatusChangeCallback = onStatusChangeCallback; // Armazenamos a função de callback
    this.statusOptions = [
      "Planejado",
      "Em andamento",
      "Concluído",
      "Cancelado",
      "Adiado",
    ];
  }

  render() {
    const container = document.getElementById(this.containerId);
    if (!container) return;

    let eventosHTML = "";

    if (this.eventos.length === 0) {
      eventosHTML = `<tr><td colspan="7" class="text-center">Nenhum evento ativo encontrado.</td></tr>`;
    } else {
      this.eventos.forEach((evento) => {
        const statusDropdown = this.criarDropdownStatus(evento);
        eventosHTML += `
          <tr data-event-id="${evento.idEvento}">
            <td>${evento.data}</td>
            <td>${evento.animal}</td>
            <td>${evento.tipo}</td>
            <td>${evento.descricao}</td>
            <td>${statusDropdown}</td>
            <td>
              <button class="btn btn-sm btn-outline-primary" onclick="window.location.href='pages/eventos/editarEvento.html?id=${evento.idEvento}'" title="Editar Evento">
                <i class="bi bi-pencil"></i>
              </button>
            </td>
          </tr>
        `;
      });
    }

    const cardHTML = `
      <div class="card" id="card-eventos">
        <div class="card-header">Eventos Ativos</div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>Data</th>
                  <th>Animal</th>
                  <th>Tipo</th>
                  <th>Descrição</th>
                  <th>Status</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                ${eventosHTML}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    `;

    container.innerHTML = cardHTML;
    this.setupEventListeners();
  }

  criarDropdownStatus(evento) {
    const selectId = `status-select-${evento.idEvento}`;
    let optionsHTML = this.statusOptions
      .map(
        (option) =>
          `<option value="${option}" ${
            evento.status.toLowerCase() === option.toLowerCase()
              ? "selected"
              : ""
          }>${option}</option>`
      )
      .join("");

    return `
      <select class="form-select form-select-sm status-dropdown" id="${selectId}" data-event-id="${evento.idEvento}">
        ${optionsHTML}
      </select>
    `;
  }

  setupEventListeners() {
    const dropdowns = document.querySelectorAll(".status-dropdown");
    dropdowns.forEach((dropdown) => {
      dropdown.addEventListener("change", async (e) => {
        const eventoId = e.target.dataset.eventId;
        const novoStatus = e.target.value;
        const linhaEvento = document.querySelector(
          `tr[data-event-id="${eventoId}"]`
        );

        // Adiciona um feedback visual de carregamento
        linhaEvento.style.opacity = "0.5";

        try {
          await this.eventoService.atualizarStatus(eventoId, novoStatus);

          // Se a atualização for bem-sucedida, chama o callback para recarregar a UI
          if (this.onStatusChangeCallback) {
            this.onStatusChangeCallback();
          }
        } catch (error) {
          alert(`Erro ao atualizar status: ${error.message}`);
          // Reverte a opacidade em caso de erro
          linhaEvento.style.opacity = "1";
        }
      });
    });
  }
}

export default CardEventos;

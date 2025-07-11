// CardEventosInativos.js
import EventoService from "../services/EventoService.js";

class CardEventosInativos {
  // Adicionamos o 'onStatusChangeCallback' para notificar o dashboard
  constructor(containerId, eventos = [], onStatusChangeCallback) {
    this.containerId = containerId;
    this.eventos = eventos;
    this.onStatusChangeCallback = onStatusChangeCallback; // Função para recarregar a UI
    this.eventoService = new EventoService();
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

    // Se não houver eventos, o card não será renderizado para economizar espaço.
    if (this.eventos.length === 0) {
      container.innerHTML = ""; // Limpa qualquer conteúdo anterior
      return;
    }

    this.eventos.forEach((evento) => {
      // Usaremos o dropdown de status aqui também
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

    const cardHTML = `
      <div class="card mt-4" id="card-eventos-inativos">
        <div class="card-header">Eventos Cancelados e Adiados</div>
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
    this.setupEventListeners(); // Precisamos chamar os listeners
  }

  // Método para criar o dropdown, igual ao do CardEventos
  criarDropdownStatus(evento) {
    const selectId = `status-select-inativo-${evento.idEvento}`;
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
      <select class="form-select form-select-sm status-dropdown-inativo" id="${selectId}" data-event-id="${evento.idEvento}">
        ${optionsHTML}
      </select>
    `;
  }

  // Método para escutar as mudanças, também similar ao do CardEventos
  setupEventListeners() {
    const dropdowns = document.querySelectorAll(".status-dropdown-inativo");
    dropdowns.forEach((dropdown) => {
      dropdown.addEventListener("change", async (e) => {
        const eventoId = e.target.dataset.eventId;
        const novoStatus = e.target.value;
        const linhaEvento = document.querySelector(
          `tr[data-event-id="${eventoId}"]`
        );

        linhaEvento.style.opacity = "0.5";

        try {
          await this.eventoService.atualizarStatus(eventoId, novoStatus);

          // Avisa o dashboard para recarregar tudo
          if (this.onStatusChangeCallback) {
            this.onStatusChangeCallback();
          }
        } catch (error) {
          alert(`Erro ao atualizar status: ${error.message}`);
          linhaEvento.style.opacity = "1";
        }
      });
    });
  }
}

export default CardEventosInativos;

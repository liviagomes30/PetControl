// assets/js/components/CardFiltrosEventos.js
class CardFiltrosEventos {
  constructor(containerId) {
    this.containerId = containerId;
  }

  render() {
    const container = document.getElementById(this.containerId);
    if (!container) return;

    const cardHTML = `
      <div class="filter-section mb-4 p-3 border rounded" id="filtros-eventos-container">
        <a class="text-decoration-none text-dark d-flex justify-content-between align-items-center"
           data-bs-toggle="collapse"
           href="#filterContentEventos"
           role="button"
           aria-expanded="true"
           aria-controls="filterContentEventos">
          <h6 class="mb-0"><i class="bi bi-filter me-2"></i>Filtros de Eventos</h6>
          <i class="bi bi-chevron-down filter-toggle-icon"></i>
        </a>
        <div class="collapse show mt-3" id="filterContentEventos">
          <form id="eventosFilterForm">
            <div class="row g-3 align-items-end">
                <div class="col-md-4">
                    <label for="filtroStatusEvento" class="form-label">Status</label>
                    <select class="form-select form-select-sm" id="filtroStatusEvento">
                        <option value="">Todos</option>
                        <option value="Planejado">Planejado</option>
                        <option value="Em andamento">Em andamento</option>
                        <option value="Concluído">Concluído</option>
                    </select>
                </div>
                <div class="col-md-4">
                  <label for="filtroDataEvento" class="form-label">Data</label>
                  <input type="date" class="form-control form-control-sm" id="filtroDataEvento" />
                </div>
                <div class="col-md-2">
                  <button type="button" class="btn btn-primary btn-sm w-100" id="btnFiltrarEventos">
                      <i class="bi bi-search"></i> Filtrar
                  </button>
                </div>
                 <div class="col-md-2">
                  <button type="button" class="btn btn-secondary btn-sm w-100" id="btnLimparFiltrosEventos">
                      <i class="bi bi-x-circle"></i> Limpar
                  </button>
                </div>
            </div>
          </form>
        </div>
      </div>
      <style>
        .filter-toggle-icon {
          transition: transform 0.3s ease;
        }
        [aria-expanded="true"] .filter-toggle-icon {
          transform: rotate(180deg);
        }
      </style>
    `;
    container.innerHTML = cardHTML;
  }
}
export default CardFiltrosEventos;

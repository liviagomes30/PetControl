// assets/js/components/CardFiltrosMedicacoes.js
class CardFiltrosMedicacoes {
  constructor(containerId) {
    this.containerId = containerId;
  }

  render() {
    const container = document.getElementById(this.containerId);
    if (!container) return;

    const cardHTML = `
      <div class="filter-section mb-4 p-3 border rounded" id="filtros-medicacoes-container">
        <a class="text-decoration-none text-dark d-flex justify-content-between align-items-center"
           data-bs-toggle="collapse"
           href="#filterContentMedicacoes"
           role="button"
           aria-expanded="true"
           aria-controls="filterContentMedicacoes">
            <h6 class="mb-0"><i class="bi bi-filter me-2"></i>Filtros de Medicações</h6>
            <i class="bi bi-chevron-down filter-toggle-icon"></i>
        </a>
        <div class="collapse show mt-3" id="filterContentMedicacoes">
          <form id="medicacoesFilterForm">
            <div class="row g-3 align-items-end">
              <div class="col-md-4">
                <label for="filtroDataInicioMedicacoes" class="form-label">Período</label>
                <div class="input-group input-group-sm">
                  <input type="date" class="form-control" id="filtroDataInicioMedicacoes" />
                  <span class="input-group-text">até</span>
                  <input type="date" class="form-control" id="filtroDataFimMedicacoes" />
                </div>
              </div>
              <div class="col-md-3">
                <label for="filtroStatusMedicacao" class="form-label">Status</label>
                <select class="form-select form-select-sm" id="filtroStatusMedicacao">
                  <option value="">Todos</option>
                  <option value="Planejado">Planejado</option>
                  <option value="Aplicado">Aplicado</option>
                </select>
              </div>
              <div class="col-md-3">
                  <label for="filtroAnimalMedicacao" class="form-label">Animal</label>
                  <select class="form-select form-select-sm" id="filtroAnimalMedicacao">
                      <option value="">Todos</option>
                      </select>
              </div>
              <div class="col-md-2">
                <button type="button" class="btn btn-primary btn-sm w-100" id="btnFiltrarMedicacoes">
                  <i class="bi bi-search"></i> Filtrar
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

export default CardFiltrosMedicacoes;

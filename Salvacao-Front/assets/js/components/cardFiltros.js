// cardFiltros.js
class CardFiltros {
  constructor(containerId) {
    this.containerId = containerId;
  }

  render() {
    const container = document.getElementById(this.containerId);
    if (!container) return;

    const cardHTML = `
      <div class="card mb-4" id="card-filtros">
        <div class="card-header d-flex justify-content-between align-items-center">
          <span>Filtros</span>
          <button class="btn btn-sm btn-outline-light" id="toggleFilterBtn">
            <i class="bi bi-chevron-up"></i>
          </button>
        </div>
        <div class="card-body" id="filterContent">
          <form id="compromissosFilterForm">
            <div class="row">
              <div class="col-md-4 mb-3">
                <label class="form-label">Período</label>
                <div class="input-group">
                  <input type="date" class="form-control" id="dataInicio" />
                  <span class="input-group-text">até</span>
                  <input type="date" class="form-control" id="dataFim" />
                </div>
              </div>

              <div class="col-md-3 mb-3">
                <label class="form-label">Tipo</label>
                <select class="form-select" id="tipoCompromisso">
                  <option value="">Todos</option>
                  <option value="medicacao">Medicação</option>
                  <option value="vacinacao">Vacinação</option>
                  <option value="consulta">Consulta</option>
                </select>
              </div>

              <div class="col-md-3 mb-3">
                <label class="form-label">Status</label>
                <select class="form-select" id="statusCompromisso">
                  <option value="">Todos</option>
                  <option value="pendente">Pendente</option>
                  <option value="concluido">Concluído</option>
                  <option value="cancelado">Cancelado</option>
                </select>
              </div>

              <div class="col-md-2 d-flex align-items-end mb-3">
                <button
                  type="button"
                  class="btn btn-primary w-100"
                  id="btnFiltrar"
                >
                  <i class="bi bi-search"></i> Filtrar
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    `;

    container.innerHTML += cardHTML;
    this.setupEventListeners();
    this.initializeDates();
  }

  setupEventListeners() {
    document
      .getElementById("toggleFilterBtn")
      .addEventListener("click", function () {
        const filterContent = document.getElementById("filterContent");
        const icon = this.querySelector("i");

        if (filterContent.style.display === "none") {
          filterContent.style.display = "block";
          icon.classList.remove("bi-chevron-down");
          icon.classList.add("bi-chevron-up");
        } else {
          filterContent.style.display = "none";
          icon.classList.remove("bi-chevron-up");
          icon.classList.add("bi-chevron-down");
        }
      });

    document
      .getElementById("btnFiltrar")
      .addEventListener("click", function () {
        alert(
          "Filtro aplicado! Em um ambiente real, isso buscaria dados do servidor."
        );
      });
  }

  initializeDates() {
    const today = new Date().toISOString().split("T")[0];
    document.getElementById("dataFim").value = today;

    const sevenDaysAgo = new Date();
    sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);
    document.getElementById("dataInicio").value = sevenDaysAgo
      .toISOString()
      .split("T")[0];
  }
}

export default CardFiltros;

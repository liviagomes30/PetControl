// cardCompromissosProximos.js
class CardCompromissosProximos {
  constructor(containerId, compromissos = []) {
    this.containerId = containerId;
    this.compromissos = compromissos;
  }

  render() {
    const container = document.getElementById(this.containerId);
    if (!container) return;

    let compromissosHTML = "";

    if (this.compromissos.length === 0) {
      compromissosHTML = `
        <tr>
          <td colspan="6" class="text-center">Nenhum compromisso próximo.</td>
        </tr>
      `;
    } else {
      this.compromissos.forEach((compromisso) => {
        const prioridadeClass = compromisso.prioridade
          ? `priority-${compromisso.prioridade}`
          : "";

        compromissosHTML += `
          <tr class="${prioridadeClass}">
            <td>${compromisso.data}</td>
            <td>${compromisso.animal}</td>
            <td>${compromisso.tipo}</td>
            <td>${compromisso.descricao}</td>
            <td>
              <span class="badge badge-status-${
                compromisso.status
              }">${this.capitalizeFirstLetter(compromisso.status)}</span>
            </td>
            <td>
              <button class="btn btn-sm btn-outline-primary">
                <i class="bi bi-pencil"></i>
              </button>
              <button class="btn btn-sm btn-outline-success">
                <i class="bi bi-check2"></i>
              </button>
            </td>
          </tr>
        `;
      });
    }

    const cardHTML = `
      <div class="card mb-4" id="card-compromissos-proximos">
        <div class="card-header">Compromissos Próximos</div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>Data/Hora</th>
                  <th>Animal</th>
                  <th>Tipo</th>
                  <th>Descrição</th>
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

    container.innerHTML += cardHTML;
    this.setupEventListeners();
  }

  setupEventListeners() {
    const editButtons = document.querySelectorAll(
      "#card-compromissos-proximos .btn-outline-primary"
    );
    const completeButtons = document.querySelectorAll(
      "#card-compromissos-proximos .btn-outline-success"
    );

    editButtons.forEach((button) => {
      button.addEventListener("click", (e) => {
        const row = e.target.closest("tr");
        const animal = row.cells[1].textContent;
        const tipo = row.cells[2].textContent;
        alert(`Editar ${tipo} para ${animal}`);
      });
    });

    completeButtons.forEach((button) => {
      button.addEventListener("click", (e) => {
        const row = e.target.closest("tr");
        const animal = row.cells[1].textContent;
        const tipo = row.cells[2].textContent;
        if (confirm(`Marcar ${tipo} para ${animal} como concluído?`)) {
          row.cells[4].innerHTML =
            '<span class="badge badge-status-concluido">Concluído</span>';
        }
      });
    });
  }

  capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
  }
}

export default CardCompromissosProximos;

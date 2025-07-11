// cardCompromissosRecentes.js
class CardCompromissosRecentes {
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
          <td colspan="6" class="text-center">Nenhum compromisso recente.</td>
        </tr>
      `;
    } else {
      this.compromissos.forEach((compromisso) => {
        compromissosHTML += `
          <tr>
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
              <button class="btn btn-sm btn-outline-primary view-details">
                <i class="bi bi-eye"></i>
              </button>
            </td>
          </tr>
        `;
      });
    }

    const cardHTML = `
      <div class="card" id="card-compromissos-recentes">
        <div class="card-header">Compromissos Recentes</div>
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
    const viewButtons = document.querySelectorAll(
      "#card-compromissos-recentes .view-details"
    );

    viewButtons.forEach((button) => {
      button.addEventListener("click", (e) => {
        const row = e.target.closest("tr");
        const animal = row.cells[1].textContent;
        const tipo = row.cells[2].textContent;
        const data = row.cells[0].textContent;
        alert(`Detalhes de ${tipo} para ${animal} em ${data}`);
      });
    });
  }

  capitalizeFirstLetter(string) {
    if (!string) return "";
    return string.charAt(0).toUpperCase() + string.slice(1);
  }
}

export default CardCompromissosRecentes;

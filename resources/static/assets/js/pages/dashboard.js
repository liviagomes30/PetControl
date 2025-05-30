// dashboard.js
import {
  CardFiltros,
  CardCompromissosProximos,
  CardCompromissosRecentes,
} from "../components/components-cards.js";

document.addEventListener("DOMContentLoaded", function () {
  inicializarDashboard();
});

function inicializarDashboard() {
  const container = document.getElementById("dashboard-container");

  if (!container) {
    console.error("Container do dashboard não encontrado!");
    return;
  }

  container.innerHTML = "";

  const filtros = new CardFiltros("dashboard-container");
  filtros.render();

  const compromissosProximos = new CardCompromissosProximos(
    "dashboard-container"
  );
  compromissosProximos.render();

  const compromissosRecentes = new CardCompromissosRecentes(
    "dashboard-container"
  );
  compromissosRecentes.render();

  console.log("Dashboard inicializado com sucesso!");
}

export { inicializarDashboard };

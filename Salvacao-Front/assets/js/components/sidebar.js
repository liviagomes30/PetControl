// sidebar.js
document.addEventListener("DOMContentLoaded", function () {
  if (
    !window.location.pathname.includes("login.html") &&
    !AuthService.isAuthenticated()
  ) {
    AuthService.requireAuth();
    return;
  }

  const bootstrapIconsLoaded = Array.from(
    document.querySelectorAll("link")
  ).some((link) => link.href.includes("bootstrap-icons"));

  if (!bootstrapIconsLoaded) {
    const iconLink = document.createElement("link");
    iconLink.rel = "stylesheet";
    iconLink.href =
      "https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css";
    document.head.appendChild(iconLink);
  }

  const currentUser = AuthService.getCurrentUser();
  const userName = currentUser
    ? currentUser.pessoa?.nome || currentUser.usuario?.login || "Usuário"
    : "Usuário";

  const sidebar = document.createElement("div");
  sidebar.className = "sidebar";

  sidebar.innerHTML = `
    <div class="sidebar-logo">
      PetControl
    </div>
    
    <!-- User Info Section -->
    <div class="sidebar-user">
      <div class="sidebar-user-avatar">
        <i class="bi bi-person-circle"></i>
      </div>
      <div class="sidebar-user-info">
        <span class="sidebar-user-name">${userName}</span>
        <small class="sidebar-user-role">Usuário</small>
      </div>
      <div class="sidebar-user-actions">
        <button class="btn btn-link btn-sm text-black" onclick="handleLogout()" title="Sair">
          <i class="bi bi-box-arrow-right"></i>
        </button>
      </div>
    </div>
    
    <ul class="sidebar-menu">
      <a href="${getBasePath()}index.html" class="sidebar-item" id="menu-home">
        <div class="sidebar-item-content">
          <i class="bi bi-house sidebar-item-icon"></i>
          <span class="sidebar-item-text">Home</span>
        </div>
        <i class="bi bi-chevron-right sidebar-item-arrow"></i>
      </a>

      <div class="sidebar-item" id="menu-animais" onclick="toggleSubmenu('animaisSubmenu')">
        <div class="sidebar-item-content">
          <i class="bi bi-palette sidebar-item-icon"></i>
          <span class="sidebar-item-text">Animais</span>
        </div>
        <i class="bi bi-chevron-down sidebar-item-arrow"></i>
      </div>
      <ul id="animaisSubmenu" class="sidebar-submenu">
        <a href="${getBasePath()}pages/animal/gerenciarAnimal.html" class="sidebar-submenu-item" id="submenu-animais-gerenciar">Gerenciar Animais</a>
        <a href="${getBasePath()}pages/eventos/listarEventos.html" class="sidebar-submenu-item" id="submenu-animais-evento">Gerenciar Evento</a>
      </ul>
      
      <div class="sidebar-item" id="menu-medicacao" onclick="toggleSubmenu('medicacaoSubmenu')">
        <div class="sidebar-item-content">
          <i class="bi bi-capsule sidebar-item-icon"></i>
          <span class="sidebar-item-text">Medicação</span>
        </div>
        <i class="bi bi-chevron-down sidebar-item-arrow"></i>
      </div>
      <ul id="medicacaoSubmenu" class="sidebar-submenu">
        <a href="${getBasePath()}pages/medicamentos/listarMedicamentos.html" class="sidebar-submenu-item" id="submenu-medicacao-gerenciar">Gerenciar Medicamentos</a>
        <a href="${getBasePath()}pages/medicamentos/efetuarMedicacao.html" class="sidebar-submenu-item" id="submenu-medicacao-efetuar">Efetuar Medicação</a>
        <a href="${getBasePath()}pages/medicamentos/historicoMedicacoes.html" class="sidebar-submenu-item" id="submenu-medicacao-historico">Histórico Medicações</a>
      </ul>
      
      <div class="sidebar-item" id="menu-receituario" onclick="toggleSubmenu('receituarioSubmenu')">
        <div class="sidebar-item-content">
          <i class="bi bi-clipboard-plus sidebar-item-icon"></i>
          <span class="sidebar-item-text">Receituários</span>
        </div>
        <i class="bi bi-chevron-down sidebar-item-arrow"></i>
      </div>
      <ul id="receituarioSubmenu" class="sidebar-submenu">
        <a href="${getBasePath()}pages/receituario/listarReceituario.html" class="sidebar-submenu-item" id="submenu-receituario-listar">Gerenciar Receituários</a>
        <a href="${getBasePath()}pages/receituario/cadastrarReceituario.html" class="sidebar-submenu-item" id="submenu-receituario-novo">Novo Receituário</a>
      </ul>
      
      <a href="${getBasePath()}pages/vacinacao/index.html" class="sidebar-item" id="menu-vacinacao">
        <div class="sidebar-item-content">
          <i class="bi bi-bandaid sidebar-item-icon"></i>
          <span class="sidebar-item-text">Vacinação</span>
        </div>
        <i class="bi bi-chevron-right sidebar-item-arrow"></i>
      </a>
      
      <div class="sidebar-item" id="menu-pessoas" onclick="toggleSubmenu('pessoasSubmenu')">
        <div class="sidebar-item-content">
          <i class="bi bi-people sidebar-item-icon"></i>
          <span class="sidebar-item-text">Pessoas</span>
        </div>
        <i class="bi bi-chevron-down sidebar-item-arrow"></i>
      </div>
      <ul id="pessoasSubmenu" class="sidebar-submenu">
        <a href="${getBasePath()}pages/pessoas/listarPessoa.html" class="sidebar-submenu-item" id="submenu-pessoas-adotante">Gerenciar Adotante</a>
        <a href="${getBasePath()}pages/doacao/listarDoacao.html" class="sidebar-submenu-item" id="submenu-pessoas-doacao">Gerenciar Adoação</a>
      </ul>
      
      <a href="${getBasePath()}pages/usuarios/index.html" class="sidebar-item" id="menu-usuarios">
        <div class="sidebar-item-content">
          <i class="bi bi-person-gear sidebar-item-icon"></i>
          <span class="sidebar-item-text">Usuários</span>
        </div>
        <i class="bi bi-chevron-right sidebar-item-arrow"></i>
      </a>
      
      <div class="sidebar-item" id="menu-produtos" onclick="toggleSubmenu('produtosSubmenu')">
        <div class="sidebar-item-content">
          <i class="bi bi-plus-circle sidebar-item-icon"></i>
          <span class="sidebar-item-text">Produtos</span>
        </div>
        <i class="bi bi-chevron-down sidebar-item-arrow"></i>
      </div>
      <ul id="produtosSubmenu" class="sidebar-submenu">
        <a href="${getBasePath()}pages/produto/listar.html" class="sidebar-submenu-item" id="submenu-produtos-listar">Lista de Produtos</a>
        <a href="${getBasePath()}pages/produto/novo.html" class="sidebar-submenu-item" id="submenu-produtos-novo">Novo Produto</a>
        <a href="${getBasePath()}pages/produto/tipo/listarTipos.html" class="sidebar-submenu-item" id="submenu-produtos-tipos">Tipos de Produto</a>
        <a href="${getBasePath()}pages/produto/unidade/listarUnidades.html" class="sidebar-submenu-item" id="submenu-produtos-unidades">Unidades de Medida</a>
        <a href="${getBasePath()}pages/produto/entrada/listarRegistros.html" class="sidebar-submenu-item" id="submenu-produtos-entrada">Entrada Produtos</a>
        <a href="${getBasePath()}pages/produto/saida/listarRegistros.html" class="sidebar-submenu-item" id="submenu-produtos-saida">Registrar Uso</a>
        </ul>
      
      <a href="${getBasePath()}pages/acerto-estoque/listarAcertosEstoque.html" class="sidebar-item" id="menu-estoque">
        <div class="sidebar-item-content">
          <i class="bi bi-box sidebar-item-icon"></i>
          <span class="sidebar-item-text">Estoque</span>
        </div>
        <i class="bi bi-chevron-right sidebar-item-arrow"></i>
      </a>
    </ul>
  `;

  document.body.prepend(sidebar);

  const mainContent =
    document.querySelector(".main-content") || document.createElement("div");
  if (!mainContent.classList.contains("main-content")) {
    mainContent.className = "main-content";
    while (document.body.childNodes.length > 1) {
      if (document.body.childNodes[1] !== sidebar) {
        mainContent.appendChild(document.body.childNodes[1]);
      } else {
        document.body.removeChild(document.body.childNodes[1]);
      }
    }
    document.body.appendChild(mainContent);
  }

  markActiveMenuItem();
});

function toggleSubmenu(submenuId) {
  const submenu = document.getElementById(submenuId);
  submenu.classList.toggle("open");

  // Alterna o ícone de seta
  const parentItem = submenu.previousElementSibling;
  const arrow = parentItem.querySelector(".sidebar-item-arrow");

  if (submenu.classList.contains("open")) {
    arrow.classList.remove("bi-chevron-down");
    arrow.classList.add("bi-chevron-up");
  } else {
    arrow.classList.remove("bi-chevron-up");
    arrow.classList.add("bi-chevron-down");
  }
}

function markActiveMenuItem() {
  const currentPath = window.location.pathname;

  document
    .querySelectorAll(".sidebar-item, .sidebar-submenu-item")
    .forEach((item) => {
      item.classList.remove("active");
    });

  if (currentPath.includes("index.html") || currentPath.endsWith("/")) {
    document.getElementById("menu-home").classList.add("active");
  } else if (currentPath.includes("/animal/")) {
    document.getElementById("menu-animais").classList.add("active");
  } else if (currentPath.includes("/medicamentos/")) {
    document.getElementById("menu-medicacao").classList.add("active");
    document.getElementById("medicacaoSubmenu").classList.add("open");

    if (currentPath.includes("listarMedicamentos.html")) {
      document
        .getElementById("submenu-medicacao-gerenciar")
        .classList.add("active");
    } else if (currentPath.includes("efetuarMedicacao.html")) {
      document
        .getElementById("submenu-medicacao-efetuar")
        .classList.add("active");
    } else if (currentPath.includes("historicoMedicacoes.html")) {
      document
        .getElementById("submenu-medicacao-historico")
        .classList.add("active");
    }

    const arrow = document.querySelector("#menu-medicacao .sidebar-item-arrow");
    arrow.classList.remove("bi-chevron-down");
    arrow.classList.add("bi-chevron-up");
  } else if (currentPath.includes("/receituario/")) {
    document.getElementById("menu-receituario").classList.add("active");
    document.getElementById("receituarioSubmenu").classList.add("open");

    if (currentPath.includes("listarReceituario.html")) {
      document
        .getElementById("submenu-receituario-listar")
        .classList.add("active");
    } else if (
      currentPath.includes("cadastrarReceituario.html") ||
      currentPath.includes("editarReceituario.html")
    ) {
      document
        .getElementById("submenu-receituario-novo")
        .classList.add("active");
    }

    const arrow = document.querySelector(
      "#menu-receituario .sidebar-item-arrow"
    );
    arrow.classList.remove("bi-chevron-down");
    arrow.classList.add("bi-chevron-up");
  } else if (currentPath.includes("/vacinacao/")) {
    document.getElementById("menu-vacinacao").classList.add("active");
  } else if (currentPath.includes("/pessoas/")) {
    document.getElementById("menu-pessoas").classList.add("active");
  } else if (currentPath.includes("/usuarios/")) {
    document.getElementById("menu-usuarios").classList.add("active");
  } else if (currentPath.includes("/produto/")) {
    document.getElementById("menu-produtos").classList.add("active");
    document.getElementById("produtosSubmenu").classList.add("open");

    if (currentPath.includes("/produto/listar.html")) {
      document
        .getElementById("submenu-produtos-listar")
        .classList.add("active");
    } else if (currentPath.includes("/produto/novo.html")) {
      document.getElementById("submenu-produtos-novo").classList.add("active");
    } else if (currentPath.includes("/produto/categorias.html")) {
      document
        .getElementById("submenu-produtos-categorias")
        .classList.add("active");
    } else if (currentPath.includes("/produto/tipo/")) {
      document.getElementById("submenu-produtos-tipos").classList.add("active");
    } else if (currentPath.includes("/produto/unidade/")) {
      document
        .getElementById("submenu-produtos-unidades")
        .classList.add("active");
    }

    const arrow = document.querySelector("#menu-produtos .sidebar-item-arrow");
    arrow.classList.remove("bi-chevron-down");
    arrow.classList.add("bi-chevron-up");
  } else if (currentPath.includes("")) {
    document.getElementById("menu-estoque").classList.add("active");
  }
}

function getBasePath() {
  const path = window.location.pathname;

  // Lógica ajustada para a nova estrutura de pastas
  if (path.includes("/pages/")) {
    const segments = path.split("/");
    const pagesIndex = segments.indexOf("pages");
    if (pagesIndex !== -1) {
      // Calcula quantos níveis precisamos subir para chegar à raiz
      // pages + mais subpastas
      const levelsUp = segments.length - pagesIndex - 1;
      return "../".repeat(levelsUp);
    }
    return "../../"; // Fallback padrão se a lógica acima falhar
  }

  return "";
}

function handleLogout() {
  AuthService.logout();
}

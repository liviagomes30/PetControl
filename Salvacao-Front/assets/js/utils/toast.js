const Toast = {
  show(message, type = "info", duration = 5000) {
    const existingToast = document.querySelector(".toast-custom");
    if (existingToast) {
      existingToast.remove();
    }

    let toastContainer = document.querySelector(".toast-container");
    if (!toastContainer) {
      toastContainer = document.createElement("div");
      toastContainer.className =
        "toast-container position-fixed top-0 end-0 p-3";
      toastContainer.style.zIndex = "9999";
      document.body.appendChild(toastContainer);
    }

    const toast = document.createElement("div");
    toast.className = `toast toast-custom align-items-center border-0 show`;
    toast.setAttribute("role", "alert");
    toast.setAttribute("aria-live", "assertive");
    toast.setAttribute("aria-atomic", "true");

    let bgClass = "bg-primary";
    let iconClass = "bi-info-circle";

    switch (type) {
      case "success":
        bgClass = "bg-success";
        iconClass = "bi-check-circle";
        break;
      case "error":
        bgClass = "bg-danger";
        iconClass = "bi-exclamation-circle";
        break;
      case "warning":
        bgClass = "bg-warning";
        iconClass = "bi-exclamation-triangle";
        break;
      default:
        bgClass = "bg-primary";
        iconClass = "bi-info-circle";
    }

    toast.classList.add(bgClass, "text-white");

    toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body d-flex align-items-center">
                    <i class="bi ${iconClass} me-2"></i>
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        `;

    toastContainer.appendChild(toast);

    setTimeout(() => {
      if (toast && toast.parentNode) {
        toast.remove();
      }
    }, duration);

    return toast;
  },

  success(message, duration = 5000) {
    return this.show(message, "success", duration);
  },

  error(message, duration = 7000) {
    return this.show(message, "error", duration);
  },

  warning(message, duration = 6000) {
    return this.show(message, "warning", duration);
  },

  info(message, duration = 5000) {
    return this.show(message, "info", duration);
  },
};

window.Toast = Toast;

const Loading = {
  show() {
    const existingLoading = document.querySelector(".loading-overlay");
    if (existingLoading) {
      existingLoading.remove();
    }

    const loadingOverlay = document.createElement("div");
    loadingOverlay.className = "loading-overlay";
    loadingOverlay.innerHTML = `
            <div class="loading-content">
                <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
                    <span class="visually-hidden">Carregando...</span>
                </div>
                <div class="loading-text mt-3">Carregando...</div>
            </div>
        `;

    loadingOverlay.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 9999;
            opacity: 0;
            transition: opacity 0.3s ease;
        `;

    const loadingContent = loadingOverlay.querySelector(".loading-content");
    if (loadingContent) {
      loadingContent.style.cssText = `
                text-align: center;
                color: white;
                font-size: 1.1rem;
            `;
    }

    document.body.appendChild(loadingOverlay);

    setTimeout(() => {
      loadingOverlay.style.opacity = "1";
    }, 10);

    return loadingOverlay;
  },

  hide() {
    const loadingOverlay = document.querySelector(".loading-overlay");
    if (loadingOverlay) {
      loadingOverlay.style.opacity = "0";
      setTimeout(() => {
        if (loadingOverlay.parentNode) {
          loadingOverlay.remove();
        }
      }, 300);
    }
  },

  showWithText(text) {
    const overlay = this.show();
    const textElement = overlay.querySelector(".loading-text");
    if (textElement) {
      textElement.textContent = text;
    }
    return overlay;
  },

  async wrap(asyncOperation, loadingText = "Carregando...") {
    this.showWithText(loadingText);
    try {
      const result = await asyncOperation();
      return result;
    } finally {
      this.hide();
    }
  },
};

window.Loading = Loading;

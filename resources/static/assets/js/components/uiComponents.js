// uiComponents.js
import ModalConfirmacao from "../utils/modalConfirmacao.js";
import UILoading from "../utils/uiLoading.js";
import UIToast from "../utils/uiToast.js";
import UIModalErro from "../utils/uiModalErro.js";
import UIValidacao from "../utils/uiValidacao.js";
import UIInputMasks from "../utils/uiInputMasks.js";
import ModalHelper from "../utils/modalHelper.js";

const UIComponents = {
  ModalConfirmacao,
  ModalHelper,
  Loading: UILoading,
  Toast: UIToast,
  ModalErro: UIModalErro,
  Validacao: UIValidacao,
  InputMasks: UIInputMasks,
};

export default UIComponents;

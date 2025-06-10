const MensagensPadroes = {
  SUCESSO: {
    CADASTRO: "Cadastro realizado com sucesso!",
    ATUALIZACAO: "Registro atualizado com sucesso!",
    EXCLUSAO: "Registro excluído com sucesso!",
    MEDICACAO: "Medicação registrada com sucesso!",
    VACINACAO: "Vacinação agendada com sucesso!",
    ADOCAO: "Adoção registrada com sucesso!",
    ENTRADA_ESTOQUE: "Entrada de produtos registrada com sucesso!",
    ACERTO_ESTOQUE: "Acerto de estoque realizado com sucesso!",
    ENTRADA_ANIMAL: "Entrada do animal registrada com sucesso!",
    RECEITUARIO: "Receituário registrado com sucesso!",
  },

  ERRO: {
    GERAL:
      "Ocorreu um erro ao processar sua solicitação. Tente novamente mais tarde.",
    CONEXAO:
      "Erro de conexão com o servidor. Verifique sua conexão com a internet.",
    CADASTRO:
      "Não foi possível realizar o cadastro. Verifique os dados e tente novamente.",
    ATUALIZACAO:
      "Não foi possível atualizar o registro. Verifique os dados e tente novamente.",
    EXCLUSAO:
      "Não foi possível excluir o registro. Tente novamente mais tarde.",
    CARREGAMENTO: "Erro ao carregar os dados. Tente atualizar a página.",
    ACESSO_NEGADO:
      "Acesso negado. Você não tem permissão para realizar esta operação.",
    ESTOQUE_INSUFICIENTE: "Estoque insuficiente para esta operação.",
    REGISTRO_DUPLICADO: "Já existe um registro com estas informações.",
  },

  ALERTA: {
    ESTOQUE_BAIXO:
      "Atenção: O estoque deste item está abaixo do mínimo recomendado.",
    CAMPOS_OBRIGATORIOS: "Atenção: Preencha todos os campos obrigatórios.",
    PERDA_DADOS: "Atenção: As alterações não salvas serão perdidas.",
    VACINACAO_PENDENTE: "Atenção: Este animal possui vacinações pendentes.",
    MEDICACAO_PENDENTE: "Atenção: Este animal possui medicações pendentes.",
    VENCIMENTO_PROXIMO:
      "Atenção: Este produto está próximo da data de vencimento.",
    VERSAO_DESATUALIZADA:
      "Atenção: Uma nova versão do sistema está disponível.",
  },

  CONFIRMACAO: {
    EXCLUSAO:
      "Tem certeza que deseja excluir este registro? Esta ação não poderá ser desfeita.",
    CANCELAMENTO:
      "Tem certeza que deseja cancelar esta operação? Os dados não serão salvos.",
    SAIDA_FORMULARIO:
      "Existem alterações não salvas. Deseja realmente sair desta página?",
    FINALIZACAO: "Tem certeza que deseja finalizar este processo?",
    ADOCAO:
      'Confirma a adoção deste animal? Esta ação alterará o status do animal para "Adotado".',
    MEDICACAO:
      "Confirma a administração deste medicamento? Esta ação irá atualizar o estoque.",
  },

  VALIDACAO: {
    CAMPO_OBRIGATORIO: "Este campo é obrigatório.",
    FORMATO_INVALIDO: "O formato informado é inválido.",
    VALOR_MINIMO: "O valor deve ser maior ou igual a {0}.",
    VALOR_MAXIMO: "O valor deve ser menor ou igual a {0}.",
    DATA_INVALIDA: "A data informada é inválida.",
    DATA_FUTURA: "A data não pode ser futura.",
    DATA_PASSADA: "A data não pode ser anterior a hoje.",
    EMAIL_INVALIDO: "O e-mail informado é inválido.",
    TELEFONE_INVALIDO: "O telefone informado é inválido.",
    CPF_INVALIDO: "O CPF informado é inválido.",
    TAMANHO_MINIMO: "Este campo deve ter pelo menos {0} caracteres.",
    TAMANHO_MAXIMO: "Este campo deve ter no máximo {0} caracteres.",
    SENHA_FRACA:
      "A senha deve conter letras maiúsculas, minúsculas, números e caracteres especiais.",
  },

  INFO: {
    SESSAO_EXPIRADA: "Sua sessão expirou. Faça login novamente para continuar.",
    SEM_RESULTADOS: "Nenhum resultado encontrado para os filtros aplicados.",
    DADOS_SALVOS: "Os dados foram salvos como rascunho.",
    OPERACAO_CANCELADA: "Operação cancelada pelo usuário.",
    PROXIMA_VACINACAO: "A próxima vacinação está agendada para {0}.",
    DADOS_IMPORTADOS: "Dados importados com sucesso.",
    PROCESSO_BACKGROUND:
      "O processo foi iniciado e continuará em segundo plano.",
  },

  ANIMAL: {
    ADOTADO: "Este animal já foi adotado.",
    VACINACAO_EM_DIA: "Animal com vacinação em dia.",
    HISTORICO_ATUALIZADO: "Histórico do animal atualizado com sucesso.",
    RECEITUARIO_ADICIONADO: "Receituário adicionado ao histórico do animal.",
    DESAPARECIDO: "Este animal está registrado como desaparecido.",
    RETORNADO: "Animal retornado da adoção.",
  },

  ESTOQUE: {
    PRODUTO_SEM_ESTOQUE: "Este produto não possui itens em estoque.",
    PRODUTO_INDISPONIVEL: "Este produto está indisponível no momento.",
    ABAIXO_MINIMO:
      "O estoque está abaixo do mínimo recomendado ({0} unidades).",
    ESTOQUE_ATUALIZADO: "Estoque atualizado com sucesso.",
    ALERTA_VENCIMENTO: "Este lote vencerá em {0} dias.",
    LOTE_VENCIDO: "Este lote está vencido desde {0}.",
    ESTOQUE_ZERADO: "O estoque deste produto foi zerado.",
  },

  MEDICACAO: {
    CONCLUIDA: "Medicação concluída com sucesso.",
    AGENDADA: "Medicação agendada para {0}.",
    CANCELADA: "Medicação cancelada com sucesso.",
    TRANSFERIDA: "Medicação transferida para {0}.",
    FALTANTE: "Faltam {0} doses para completar o tratamento.",
    EM_ANDAMENTO: "Tratamento em andamento ({0}% concluído).",
    INTERROMPIDA: "Tratamento interrompido. Motivo: {0}.",
  },

  VACINACAO: {
    CONCLUIDA: "Vacinação concluída com sucesso.",
    AGENDADA: "Vacinação agendada para {0}.",
    CANCELADA: "Agendamento de vacinação cancelado com sucesso.",
    TRANSFERIDA: "Vacinação transferida para {0}.",
    PROXIMA_DOSE: "Próxima dose agendada para {0}.",
    ULTIMA_DOSE: "Última dose aplicada em {0}.",
    COMPLETA: "Protocolo de vacinação completo.",
  },

  ADOCAO: {
    CONCLUIDA: "Processo de adoção concluído com sucesso.",
    EM_ANALISE: "Processo de adoção em análise.",
    REPROVADA: "Processo de adoção reprovado. Motivo: {0}.",
    CANCELADA: "Processo de adoção cancelado.",
    VISITA_AGENDADA: "Visita pós-adoção agendada para {0}.",
    VISITA_REALIZADA: "Visita pós-adoção realizada em {0}.",
    TERMO_GERADO: "Termo de adoção gerado com sucesso.",
  },

  formatar: function (mensagem, ...parametros) {
    if (!mensagem) return "";

    return parametros.reduce((msg, param, i) => {
      return msg.replace(new RegExp(`\\{${i}\\}`, "g"), param);
    }, mensagem);
  },
};

export default MensagensPadroes;

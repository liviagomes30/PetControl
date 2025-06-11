package salvacao.petcontrol.service;

import salvacao.petcontrol.dao.VacinacaoDAO;
import salvacao.petcontrol.dto.VacinacaoDTO;
import salvacao.petcontrol.model.Vacinacao;

import org.springframework.stereotype.Service; // Para que o Spring gerencie como um serviço

import java.util.List;
import java.util.stream.Collectors;

@Service // Indica que esta classe é um componente de serviço gerenciado pelo Spring
public class VacinacaoService {

    private final VacinacaoDAO vacinacaoDAO;

    // Instanciação direta do VacinacaoDAO.
    // Para melhor prática com Spring, VacinacaoDAO poderia ser um @Repository
    // e injetado aqui via construtor @Autowired.
    public VacinacaoService() {
        this.vacinacaoDAO = new VacinacaoDAO();
    }

    // Construtor alternativo para injeção de dependência se VacinacaoDAO fosse um Bean Spring:
    // import org.springframework.beans.factory.annotation.Autowired;
    // @Autowired
    // public VacinacaoService(VacinacaoDAO vacinacaoDAO) {
    //    this.vacinacaoDAO = vacinacaoDAO;
    // }

    /**
     * Registra uma nova vacinação.
     *
     * @param vacinacaoDTO O DTO contendo os dados da vacinação.
     * @return O VacinacaoDTO da vacinação registrada, incluindo seu ID.
     * @throws Exception Em caso de erro de validação ou falha no banco.
     */
    public VacinacaoDTO registrarVacinacao(VacinacaoDTO vacinacaoDTO) throws Exception {
        // Validações básicas
        if (vacinacaoDTO == null) {
            throw new IllegalArgumentException("Dados da vacinação não podem ser nulos.");
        }
        if (vacinacaoDTO.getIdVacina() == null || vacinacaoDTO.getIdVacina() <= 0) {
            throw new IllegalArgumentException("ID do tipo de vacina inválido ou não fornecido.");
        }
        if (vacinacaoDTO.getIdAnimal() == null || vacinacaoDTO.getIdAnimal() <= 0) {
            throw new IllegalArgumentException("ID do animal inválido ou não fornecido.");
        }
        if (vacinacaoDTO.getDataVacinacao() == null) {
            throw new IllegalArgumentException("Data da vacinação é obrigatória.");
        }
        if (vacinacaoDTO.getLocalVacinacao() == null || vacinacaoDTO.getLocalVacinacao().trim().isEmpty()) {
            throw new IllegalArgumentException("Local da vacinação é obrigatório.");
        }
        // Validação de datas (ex: validade não pode ser anterior à aplicação)
        if (vacinacaoDTO.getDataValidadeVacina() != null && vacinacaoDTO.getDataVacinacao().after(vacinacaoDTO.getDataValidadeVacina())) {
            throw new IllegalArgumentException("Data de validade da vacina não pode ser anterior à data da vacinação.");
        }


        // Mapeamento de DTO para Model
        Vacinacao vacinacaoModel = new Vacinacao();
        vacinacaoModel.setIdVacina(vacinacaoDTO.getIdVacina());
        vacinacaoModel.setIdAnimal(vacinacaoDTO.getIdAnimal());
        // Conversão de java.util.Date (do DTO) para java.sql.Date (para o Model/DAO)
        vacinacaoModel.setDataVacinacao(new java.sql.Date(vacinacaoDTO.getDataVacinacao().getTime()));
        vacinacaoModel.setLocalVacinacao(vacinacaoDTO.getLocalVacinacao());

        if (vacinacaoDTO.getLoteVacina() != null && !vacinacaoDTO.getLoteVacina().trim().isEmpty()) {
            vacinacaoModel.setLoteVacina(vacinacaoDTO.getLoteVacina().trim());
        }
        if (vacinacaoDTO.getDataValidadeVacina() != null) {
            vacinacaoModel.setDataValidadeVacina(new java.sql.Date(vacinacaoDTO.getDataValidadeVacina().getTime()));
        }
        if (vacinacaoDTO.getLaboratorioVacina() != null && !vacinacaoDTO.getLaboratorioVacina().trim().isEmpty()) {
            vacinacaoModel.setLaboratorioVacina(vacinacaoDTO.getLaboratorioVacina().trim());
        }

        Vacinacao vacinacaoSalva = vacinacaoDAO.cadastrar(vacinacaoModel);

        if (vacinacaoSalva == null || vacinacaoSalva.getIdVacinacao() == null) {
            throw new Exception("Falha ao registrar a vacinação no banco de dados.");
        }

        // Mapeamento de Model para DTO para o retorno
        return modelToDTO(vacinacaoSalva);
    }

    /**
     * Busca um registro de vacinação pelo seu ID.
     *
     * @param id O ID da vacinação.
     * @return O VacinacaoDTO encontrado, ou null se não existir.
     */
    public VacinacaoDTO buscarVacinacaoPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da vacinação inválido.");
        }
        Vacinacao vacinacaoModel = vacinacaoDAO.buscarPorId(id);
        return vacinacaoModel != null ? modelToDTO(vacinacaoModel) : null;
    }

    /**
     * Lista todos os registros de vacinação.
     * @return Lista de VacinacaoDTOs.
     */
    public List<VacinacaoDTO> listarTodasVacinacoes() {
        List<Vacinacao> listaModel = vacinacaoDAO.listarTodas();
        return listaModel.stream()
                .map(this::modelToDTO) // Usando method reference para o mapeamento
                .collect(Collectors.toList());
    }

    /**
     * Lista todos os registros de vacinação para um animal específico.
     * @param idAnimal O ID do animal.
     * @return Lista de VacinacaoDTOs.
     */



    /**
     * Atualiza um registro de vacinação existente.
     *
     * @param id O ID da vacinação a ser atualizada.
     * @param vacinacaoDTO DTO com os novos dados.
     * @return VacinacaoDTO atualizado.
     * @throws Exception Se a vacinação não for encontrada ou ocorrer erro na atualização.
     */
    public VacinacaoDTO atualizarVacinacao(Integer id, VacinacaoDTO vacinacaoDTO) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da vacinação inválido para atualização.");
        }
        // Validações similares ao cadastrar
        if (vacinacaoDTO == null) throw new IllegalArgumentException("Dados da vacinação não podem ser nulos.");
        // ... (outras validações do DTO)

        Vacinacao vacinacaoExistente = vacinacaoDAO.buscarPorId(id);
        if (vacinacaoExistente == null) {
            throw new Exception("Registro de vacinação com ID " + id + " não encontrado para atualização.");
        }

        // Atualiza o modelo existente com os dados do DTO
        vacinacaoExistente.setIdVacina(vacinacaoDTO.getIdVacina());
        vacinacaoExistente.setIdAnimal(vacinacaoDTO.getIdAnimal());
        vacinacaoExistente.setDataVacinacao(new java.sql.Date(vacinacaoDTO.getDataVacinacao().getTime()));
        vacinacaoExistente.setLocalVacinacao(vacinacaoDTO.getLocalVacinacao());
        vacinacaoExistente.setLoteVacina(vacinacaoDTO.getLoteVacina() != null && !vacinacaoDTO.getLoteVacina().trim().isEmpty() ? vacinacaoDTO.getLoteVacina().trim() : null);
        vacinacaoExistente.setDataValidadeVacina(vacinacaoDTO.getDataValidadeVacina() != null ? new java.sql.Date(vacinacaoDTO.getDataValidadeVacina().getTime()) : null);
        vacinacaoExistente.setLaboratorioVacina(vacinacaoDTO.getLaboratorioVacina() != null && !vacinacaoDTO.getLaboratorioVacina().trim().isEmpty() ? vacinacaoDTO.getLaboratorioVacina().trim() : null);


        boolean atualizado = vacinacaoDAO.atualizar(vacinacaoExistente);
        if (!atualizado) {
            throw new Exception("Falha ao atualizar o registro de vacinação no banco de dados.");
        }
        return modelToDTO(vacinacaoExistente);
    }

    /**
     * Deleta um registro de vacinação pelo seu ID.
     *
     * @param id O ID da vacinação a ser deletada.
     * @throws Exception Se a vacinação não for encontrada ou ocorrer erro na deleção.
     */
    public void deletarVacinacao(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da vacinação inválido para exclusão.");
        }
        Vacinacao vacinacaoExistente = vacinacaoDAO.buscarPorId(id);
        if (vacinacaoExistente == null) {
            throw new Exception("Registro de vacinação com ID " + id + " não encontrado para exclusão.");
        }
        boolean deletado = vacinacaoDAO.deletar(id);
        if (!deletado) {
            throw new Exception("Falha ao deletar o registro de vacinação do banco de dados.");
        }
    }

    // Método utilitário para converter Model para DTO
    private VacinacaoDTO modelToDTO(Vacinacao vacinacao) {
        if (vacinacao == null) return null;
        return new VacinacaoDTO(
                vacinacao.getIdVacinacao(),
                vacinacao.getIdVacina(),
                vacinacao.getIdAnimal(),
                vacinacao.getDataVacinacao(), // java.sql.Date é subclasse de java.util.Date, então é compatível
                vacinacao.getLocalVacinacao(),
                vacinacao.getLoteVacina(),
                vacinacao.getDataValidadeVacina(),
                vacinacao.getLaboratorioVacina()
        );
        // Se o DTO tivesse campos como nomeAnimal, descricaoTipoVacina, aqui seria o lugar
        // para buscar esses dados adicionais e popular o DTO antes de retorná-lo.
    }
    public Vacinacao registrarVacinacao(Vacinacao vacinacao) throws Exception {
        if (vacinacao == null || vacinacao.getIdVacina() == null || vacinacao.getIdAnimal() == null || vacinacao.getDataVacinacao() == null || vacinacao.getLocalVacinacao() == null || vacinacao.getLocalVacinacao().trim().isEmpty()) {
            throw new IllegalArgumentException("Campos obrigatórios (vacina, animal, data, local) não podem ser nulos.");
        }
        return vacinacaoDAO.cadastrar(vacinacao);
    }

    public List<Vacinacao> listarVacinacoesPorAnimal(Integer idAnimal) {
        if (idAnimal == null || idAnimal <= 0) {
            throw new IllegalArgumentException("ID do animal inválido.");
        }
        return vacinacaoDAO.listarPorAnimal(idAnimal);
    }



}
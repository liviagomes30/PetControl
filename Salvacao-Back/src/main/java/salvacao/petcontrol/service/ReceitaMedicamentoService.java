package salvacao.petcontrol.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.dto.ReceitaMedicamentoDTO;
import salvacao.petcontrol.dto.ReceitaMedicamentoRequestDTO;
import salvacao.petcontrol.dto.PosologiaDTO;
import salvacao.petcontrol.model.ReceitaMedicamentoModel;
import salvacao.petcontrol.model.PosologiaModel;
import salvacao.petcontrol.util.ResultadoOperacao;

@Service
public class ReceitaMedicamentoService {

    @Autowired
    private ReceitaMedicamentoModel receitaModel;
    
    @Autowired
    private PosologiaModel posologiaModel;

    public ReceitaMedicamentoService() {
        if (receitaModel == null) {
            receitaModel = new ReceitaMedicamentoModel();
        }
        if (posologiaModel == null) {
            posologiaModel = new PosologiaModel();
        }
    }

    public ResultadoOperacao cadastrar(ReceitaMedicamentoRequestDTO request) throws Exception {
        if (request == null) {
            throw new Exception("Dados da receita não fornecidos");
        }

        if (request.getAnimal_idanimal() == null) {
            throw new Exception("Animal deve ser informado");
        }

        if (request.getMedico() == null || request.getMedico().trim().isEmpty()) {
            throw new Exception("Médico veterinário deve ser informado");
        }

        if (request.getPosologias() == null || request.getPosologias().isEmpty()) {
            throw new Exception("Pelo menos um medicamento deve ser prescrito");
        }

        ResultadoOperacao resultado = new ResultadoOperacao();
        
        try {
            SingletonDB.getConexao().getConnection().setAutoCommit(false);

            ReceitaMedicamentoModel receita = new ReceitaMedicamentoModel();
            receita.setData(request.getData());
            receita.setMedico(request.getMedico());
            receita.setClinica(request.getClinica());
            receita.setAnimal_idanimal(request.getAnimal_idanimal());

            boolean receitaCriada = receitaModel.getReceitaDAO().inserir(receita);
            if (!receitaCriada) {
                throw new Exception("Falha ao criar a receita médica");
            }

            Integer receitaId = receitaModel.getReceitaDAO().getUltimoId();
            if (receitaId == null) {
                throw new Exception("Não foi possível obter o ID da receita criada");
            }

            for (ReceitaMedicamentoRequestDTO.PosologiaRequestDTO posReq : request.getPosologias()) {
                if (posReq.getMedicamento_idproduto() == null) {
                    throw new Exception("Medicamento deve ser informado na posologia");
                }

                PosologiaModel posologia = new PosologiaModel();
                posologia.setDose(posReq.getDose());
                posologia.setQuantidadedias(posReq.getQuantidadedias());
                posologia.setIntervalohoras(posReq.getIntervalohoras());
                posologia.setMedicamento_idproduto(posReq.getMedicamento_idproduto());
                posologia.setReceitamedicamento_idreceita(receitaId);

                boolean posologiaCriada = posologiaModel.getPosologiaDAO().inserir(posologia);
                if (!posologiaCriada) {
                    throw new Exception("Falha ao criar posologia para medicamento ID: " + posReq.getMedicamento_idproduto());
                }
            }

            SingletonDB.getConexao().getConnection().commit();
            
            resultado.setOperacao("cadastrar");
            resultado.setSucesso(true);
            resultado.setMensagem("Receituário registrado com sucesso!");
            
            return resultado;

        } catch (Exception e) {
            try {
                SingletonDB.getConexao().getConnection().rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw e;
        } finally {
            try {
                SingletonDB.getConexao().getConnection().setAutoCommit(true);
            } catch (SQLException finalEx) {
                finalEx.printStackTrace();
            }
        }
    }

    public boolean alterar(Integer id, ReceitaMedicamentoRequestDTO request) throws Exception {
        ReceitaMedicamentoDTO existente = receitaModel.getReceitaDAO().findReceitaCompleta(id);
        if (existente == null) {
            throw new Exception("Receita não encontrada");
        }

        if (request.getAnimal_idanimal() == null) {
            throw new Exception("Animal deve ser informado");
        }

        if (request.getMedico() == null || request.getMedico().trim().isEmpty()) {
            throw new Exception("Médico veterinário deve ser informado");
        }

        try {
            SingletonDB.getConexao().getConnection().setAutoCommit(false);

            ReceitaMedicamentoModel receita = new ReceitaMedicamentoModel();
            receita.setIdreceita(id);
            receita.setData(request.getData());
            receita.setMedico(request.getMedico());
            receita.setClinica(request.getClinica());
            receita.setAnimal_idanimal(request.getAnimal_idanimal());

            boolean receitaAtualizada = receitaModel.getReceitaDAO().alterar(receita);
            if (!receitaAtualizada) {
                throw new Exception("Falha ao atualizar a receita médica");
            }

            List<PosologiaDTO> posologiasAntigas = posologiaModel.getPosologiaDAO().listarPorReceita(id);
            for (PosologiaDTO posAntiga : posologiasAntigas) {
                if (posologiaModel.getPosologiaDAO().posologiaPodeSerExcluida(
                        posAntiga.getMedicamento_idproduto(), 
                        posAntiga.getReceitamedicamento_idreceita())) {
                    posologiaModel.getPosologiaDAO().apagar(
                        posAntiga.getMedicamento_idproduto(), 
                        posAntiga.getReceitamedicamento_idreceita());
                }
            }

            if (request.getPosologias() != null) {
                for (ReceitaMedicamentoRequestDTO.PosologiaRequestDTO posReq : request.getPosologias()) {
                    if (posReq.getMedicamento_idproduto() == null) {
                        throw new Exception("Medicamento deve ser informado na posologia");
                    }

                    PosologiaModel posologia = new PosologiaModel();
                    posologia.setDose(posReq.getDose());
                    posologia.setQuantidadedias(posReq.getQuantidadedias());
                    posologia.setIntervalohoras(posReq.getIntervalohoras());
                    posologia.setMedicamento_idproduto(posReq.getMedicamento_idproduto());
                    posologia.setReceitamedicamento_idreceita(id);

                    boolean posologiaCriada = posologiaModel.getPosologiaDAO().inserir(posologia);
                    if (!posologiaCriada) {
                        throw new Exception("Falha ao criar posologia para medicamento ID: " + posReq.getMedicamento_idproduto());
                    }
                }
            }

            SingletonDB.getConexao().getConnection().commit();
            return true;

        } catch (Exception e) {
            try {
                SingletonDB.getConexao().getConnection().rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw e;
        } finally {
            try {
                SingletonDB.getConexao().getConnection().setAutoCommit(true);
            } catch (SQLException finalEx) {
                finalEx.printStackTrace();
            }
        }
    }

    public ResultadoOperacao apagarReceita(Integer id) throws Exception {
        ReceitaMedicamentoDTO existente = receitaModel.getReceitaDAO().findReceitaCompleta(id);
        if (existente == null) {
            throw new Exception("Receita não encontrada");
        }

        ResultadoOperacao resultado = new ResultadoOperacao();

        try {
            boolean podeExcluir = receitaModel.getReceitaDAO().receitaPodeSerExcluida(id);

            if (podeExcluir) {
                SingletonDB.getConexao().getConnection().setAutoCommit(false);

                posologiaModel.getPosologiaDAO().apagarPorReceita(id);

                boolean sucesso = receitaModel.getReceitaDAO().apagar(id);
                
                if (sucesso) {
                    SingletonDB.getConexao().getConnection().commit();
                    resultado.setOperacao("excluido");
                    resultado.setSucesso(true);
                    resultado.setMensagem("Receita excluída com sucesso");
                } else {
                    SingletonDB.getConexao().getConnection().rollback();
                    resultado.setOperacao("erro");
                    resultado.setSucesso(false);
                    resultado.setMensagem("Falha ao excluir a receita");
                }
            } else {
                resultado.setOperacao("bloqueado");
                resultado.setSucesso(false);
                resultado.setMensagem("Esta receita não pode ser excluída pois está sendo utilizada no sistema (possui medicações registradas).");
            }

            return resultado;

        } catch (Exception e) {
            try {
                SingletonDB.getConexao().getConnection().rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw new Exception("Erro ao processar a exclusão: " + e.getMessage());
        } finally {
            try {
                SingletonDB.getConexao().getConnection().setAutoCommit(true);
            } catch (SQLException finalEx) {
                finalEx.printStackTrace();
            }
        }
    }

    public ReceitaMedicamentoDTO buscarPorId(Integer id) throws Exception {
        return receitaModel.getReceitaDAO().findReceitaCompleta(id);
    }

    public List<ReceitaMedicamentoDTO> listarTodas() {
        return receitaModel.getReceitaDAO().listarTodas();
    }

    public List<ReceitaMedicamentoDTO> buscarPorAnimal(Integer animalId) {
        return receitaModel.getReceitaDAO().buscarPorAnimal(animalId);
    }

    public List<ReceitaMedicamentoDTO> buscarPorMedico(String medico) {
        return receitaModel.getReceitaDAO().buscarPorMedico(medico);
    }

    public List<ReceitaMedicamentoDTO> buscarPorData(java.time.LocalDate data) {
        return receitaModel.getReceitaDAO().buscarPorData(data);
    }

    public List<PosologiaDTO> buscarPosologiasPorReceita(Integer receitaId) {
        return posologiaModel.getPosologiaDAO().listarPorReceita(receitaId);
    }

    public List<PosologiaDTO> buscarPosologiasPorMedicamento(Integer medicamentoId) {
        return posologiaModel.getPosologiaDAO().listarPorMedicamento(medicamentoId);
    }


    public ReceitaMedicamentoModel getId(Integer id) {
        return receitaMedicamentoModel.getRmDAO().getId(id);
    }

    public List<ReceitaMedicamentoModel> getAll() {
        return receitaMedicamentoModel.getRmDAO().getAll();
    }

    public List<ReceitaMedicamentoModel> getReceitasByAnimal(Integer animalId) {
        return receitaMedicamentoModel.getRmDAO().getReceitasByAnimal(animalId);
    }

    public List<ReceitaMedicamentoModel> searchReceitas(String searchTerm) {
        return receitaMedicamentoModel.getRmDAO().searchReceitas(searchTerm);
    }

    public ReceitaMedicamentoModel gravar(ReceitaMedicamentoModel receita) throws Exception {
        if (receita.getAnimal_idanimal() == null) {
            throw new Exception("ID do animal é obrigatório para a receita.");
        }
        if (receita.getMedico() == null || receita.getMedico().trim().isEmpty()) {
            throw new Exception("Nome do médico é obrigatório.");
        }
        if (receita.getClinica() == null || receita.getClinica().trim().isEmpty()) {
            throw new Exception("Nome da clínica é obrigatório.");
        }
        if (receita.getData() == null) {
            receita.setData(LocalDate.now());
        }

        // Validate existence of related Animal
        if (animalModel.getAnimalDAO().getId(receita.getAnimal_idanimal()) == null) {
            throw new Exception("Animal não encontrado.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            ReceitaMedicamentoModel novaReceita = receitaMedicamentoModel.getRmDAO().gravar(receita, conn);

            conn.commit();
            return novaReceita;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao gravar receita de medicamento: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public boolean alterar(ReceitaMedicamentoModel receita) throws Exception {
        if (receita.getIdreceita() == null) {
            throw new Exception("ID da receita é obrigatório para alteração.");
        }
        if (receita.getAnimal_idanimal() == null) {
            throw new Exception("ID do animal é obrigatório para a receita.");
        }
        if (receita.getMedico() == null || receita.getMedico().trim().isEmpty()) {
            throw new Exception("Nome do médico é obrigatório.");
        }
        if (receita.getClinica() == null || receita.getClinica().trim().isEmpty()) {
            throw new Exception("Nome da clínica é obrigatório.");
        }
        if (receita.getData() == null) {
            receita.setData(LocalDate.now());
        }

        if (receitaMedicamentoModel.getRmDAO().getId(receita.getIdreceita()) == null) {
            throw new Exception("Receita de medicamento não encontrada para alteração.");
        }

        if (animalModel.getAnimalDAO().getId(receita.getAnimal_idanimal()) == null) {
            throw new Exception("Animal não encontrado.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean atualizado = receitaMedicamentoModel.getRmDAO().alterar(receita, conn);

            if (atualizado) {
                conn.commit();
            } else {
                conn.rollback();
            }
            return atualizado;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao alterar receita de medicamento: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public boolean apagar(Integer id) throws Exception {
        if (id == null) {
            throw new Exception("ID da receita é obrigatório para exclusão.");
        }
        if (receitaMedicamentoModel.getRmDAO().getId(id) == null) {
            throw new Exception("Receita de medicamento não encontrada para exclusão.");
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean deletado = receitaMedicamentoModel.getRmDAO().apagar(id, conn);

            if (deletado) {
                conn.commit();
            } else {
                conn.rollback();
            }
            return deletado;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao excluir receita de medicamento: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public List<PosologiaDTO> buscarPosologiasPorReceita(Integer receitaId) {
        return posologiaModel.getPosDAO().listarPorReceita(receitaId);
    }
}
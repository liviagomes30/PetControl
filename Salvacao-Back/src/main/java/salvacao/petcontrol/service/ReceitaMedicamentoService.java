package salvacao.petcontrol.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.dto.ReceitaMedicamentoDTO;
import salvacao.petcontrol.dto.ReceitaMedicamentoRequestDTO;
import salvacao.petcontrol.dto.PosologiaDTO;
import salvacao.petcontrol.model.ReceitaMedicamentoModel;
import salvacao.petcontrol.model.PosologiaModel;
import salvacao.petcontrol.model.AnimalModel;
import salvacao.petcontrol.util.ResultadoOperacao;

@Service
public class ReceitaMedicamentoService {

    @Autowired
    private ReceitaMedicamentoModel receitaModel;
    
    @Autowired
    private PosologiaModel posologiaModel;

    @Autowired
    private AnimalModel animalModel;

    public ReceitaMedicamentoService() {
        if (receitaModel == null) {
            receitaModel = new ReceitaMedicamentoModel();
        }
        if (posologiaModel == null) {
            posologiaModel = new PosologiaModel();
        }
        if (animalModel == null) {
            animalModel = new AnimalModel();
        }
    }

    // ==================== CRUD OPERATIONS ====================

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

        if (animalModel.getAnimalDAO().getId(request.getAnimal_idanimal()) == null) {
            throw new Exception("Animal não encontrado");
        }

        ResultadoOperacao resultado = new ResultadoOperacao();
        
        try {
            SingletonDB.getConexao().getConnection().setAutoCommit(false);

            ReceitaMedicamentoModel receita = new ReceitaMedicamentoModel();
            receita.setData(request.getData());
            receita.setMedico(request.getMedico());
            receita.setClinica(request.getClinica());
            receita.setAnimal_idanimal(request.getAnimal_idanimal());

            boolean receitaCriada = receitaModel.getRmDAO().inserir(receita);
            if (!receitaCriada) {
                throw new Exception("Falha ao criar a receita médica");
            }

            Integer receitaId = receitaModel.getRmDAO().getUltimoId();
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
                posologia.setFrequencia_diaria(posReq.getFrequencia_diaria());
                posologia.setMedicamento_idproduto(posReq.getMedicamento_idproduto());
                posologia.setReceitamedicamento_idreceita(receitaId);

                boolean posologiaCriada = posologiaModel.getPosDAO().inserir(posologia);
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
        ReceitaMedicamentoDTO existente = receitaModel.getRmDAO().findReceitaCompleta(id);
        if (existente == null) {
            throw new Exception("Receita não encontrada");
        }

        if (request.getAnimal_idanimal() == null) {
            throw new Exception("Animal deve ser informado");
        }

        if (request.getMedico() == null || request.getMedico().trim().isEmpty()) {
            throw new Exception("Médico veterinário deve ser informado");
        }

        if (animalModel.getAnimalDAO().getId(request.getAnimal_idanimal()) == null) {
            throw new Exception("Animal não encontrado");
        }

        try {
            SingletonDB.getConexao().getConnection().setAutoCommit(false);

            ReceitaMedicamentoModel receita = new ReceitaMedicamentoModel();
            receita.setIdreceita(id);
            receita.setData(request.getData());
            receita.setMedico(request.getMedico());
            receita.setClinica(request.getClinica());
            receita.setAnimal_idanimal(request.getAnimal_idanimal());

            boolean receitaAtualizada = receitaModel.getRmDAO().alterar(receita);
            if (!receitaAtualizada) {
                throw new Exception("Falha ao atualizar a receita médica");
            }

            List<PosologiaDTO> posologiasAntigas = posologiaModel.getPosDAO().listarPorReceita(id);
            for (PosologiaDTO posAntiga : posologiasAntigas) {
                if (posologiaModel.getPosDAO().posologiaPodeSerExcluida(
                        posAntiga.getMedicamento_idproduto(), 
                        posAntiga.getReceitamedicamento_idreceita())) {
                    posologiaModel.getPosDAO().apagar(
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
                    posologia.setFrequencia_diaria(posReq.getFrequencia_diaria());
                    posologia.setMedicamento_idproduto(posReq.getMedicamento_idproduto());
                    posologia.setReceitamedicamento_idreceita(id);

                    boolean posologiaCriada = posologiaModel.getPosDAO().inserir(posologia);
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
        ReceitaMedicamentoDTO existente = receitaModel.getRmDAO().findReceitaCompleta(id);
        if (existente == null) {
            throw new Exception("Receita não encontrada");
        }

        ResultadoOperacao resultado = new ResultadoOperacao();

        try {
            boolean podeExcluir = receitaModel.getRmDAO().receitaPodeSerExcluida(id);

            if (podeExcluir) {
                SingletonDB.getConexao().getConnection().setAutoCommit(false);

                posologiaModel.getPosDAO().apagarPorReceita(id);

                boolean sucesso = receitaModel.getRmDAO().apagar(id);
                
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

    // ==================== READ OPERATIONS ====================

    public ReceitaMedicamentoDTO buscarPorId(Integer id) throws Exception {
        return receitaModel.getRmDAO().findReceitaCompleta(id);
    }

    public List<ReceitaMedicamentoDTO> listarTodas() {
        return receitaModel.getRmDAO().listarTodas();
    }

    public List<ReceitaMedicamentoDTO> buscarPorAnimal(Integer animalId) {
        return receitaModel.getRmDAO().buscarPorAnimal(animalId);
    }

    public List<ReceitaMedicamentoDTO> buscarPorMedico(String medico) {
        return receitaModel.getRmDAO().buscarPorMedico(medico);
    }

    public List<ReceitaMedicamentoDTO> buscarPorData(java.time.LocalDate data) {
        return receitaModel.getRmDAO().buscarPorData(data);
    }

    public List<PosologiaDTO> buscarPosologiasPorReceita(Integer receitaId) {
        return posologiaModel.getPosDAO().listarPorReceita(receitaId);
    }

    public List<PosologiaDTO> buscarPosologiasPorMedicamento(Integer medicamentoId) {
        return posologiaModel.getPosDAO().listarPorMedicamento(medicamentoId);
    }
}

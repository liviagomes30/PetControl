package salvacao.petcontrol.dal;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.model.AnimalModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

@Repository
public class AnimalDAO {

    public AnimalModel gravar(AnimalModel animal){
        String sql = "INSERT INTO animal(\n" +
                "  nome, especie, datanascimento, raca, porte, sexo, status, dataresgate, foto, castrado, cor\n" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            if(animal.getDatanascimento() != null)
                stmt.setDate(3, java.sql.Date.valueOf(animal.getDatanascimento()));
            else
                stmt.setNull(3, java.sql.Types.DATE);
            stmt.setString(4, animal.getRaca());
            stmt.setString(5, animal.getPorte());
            stmt.setString(6, animal.getSexo());
            stmt.setString(7, animal.getStatus());

            // Se dataresgate puder ser nulo, trate-o aqui:
            if (animal.getDataresgate() != null) {
                stmt.setDate(8, java.sql.Date.valueOf(animal.getDataresgate()));
            } else {
                stmt.setNull(8, java.sql.Types.DATE);
            }

            stmt.setString(9, animal.getFoto());
            stmt.setBoolean(10, animal.isCastrado());
            stmt.setString(11, animal.getCor());

            int linhasMod = stmt.executeUpdate();
            if (linhasMod > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    animal.setId(rs.getInt(1));
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar animal: " + e.getMessage(), e);
        }
        return animal;
    }

    public boolean alterar(AnimalModel animal){
        String sql = "UPDATE animal SET nome=?, especie=?, datanascimento=?, raca=?, porte=?, sexo=?, status=?, dataresgate=?, foto=?, castrado=?, cor=? WHERE idanimal=?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            if(animal.getDatanascimento() != null)
                stmt.setDate(3, java.sql.Date.valueOf(animal.getDatanascimento()));
            else
                stmt.setNull(3, java.sql.Types.DATE);
            stmt.setString(4, animal.getRaca());
            stmt.setString(5, animal.getPorte());
            stmt.setString(6, animal.getSexo());
            stmt.setString(7, animal.getStatus());
            if (animal.getDataresgate() != null) {
                stmt.setDate(8, java.sql.Date.valueOf(animal.getDataresgate()));
            } else {
                stmt.setNull(8, java.sql.Types.DATE);
            }
            stmt.setString(9, animal.getFoto());
            stmt.setBoolean(10, animal.isCastrado());
            stmt.setString(11, animal.getCor());
            stmt.setLong(12, animal.getId());

            int linhasMod = stmt.executeUpdate();

            if (linhasMod == 0) {
                throw new RuntimeException("Nenhum animal foi atualizado.");
            }
            else
                return true;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar animal: " + e.getMessage(), e);
        }
    }

    public boolean buscarAdocaoPorAnimal(int idAnimal) {
        String sql = "SELECT * FROM adocao WHERE idanimal = ?";
        boolean flag = false;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean buscarAgendamentoVacinacaoPorAnimal(int idAnimal) {
        String sql = "SELECT * FROM agendavacinacao WHERE animal_idanimal = ?";
        boolean flag = false;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean buscarEventoPorAnimal(int idAnimal) {
        String sql = "SELECT * FROM evento WHERE animal_idanimal = ?";
        boolean flag = false;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean buscarHistoricoPorAnimal(int idAnimal) {
        String sql = "SELECT * FROM historico WHERE animal_idanimal = ?";
        boolean flag = false;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean buscarMedicacaoPorAnimal(int idAnimal) {
        String sql = "SELECT * FROM medicacao WHERE idanimal = ?";
        boolean flag = false;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean buscarReceitaMedicamentoPorAnimal(int idAnimal) {
        String sql = "SELECT * FROM receitamedicamento WHERE animal_idanimal = ?";
        boolean flag = false;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean buscarVacinacaoPorAnimal(int idAnimal) {
        String sql = "SELECT * FROM vacinacao WHERE idanimal = ?";
        boolean flag = false;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, idAnimal);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }


    public boolean apagar(AnimalModel animalModel){
        String sql = "DELETE FROM animal WHERE idanimal = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, animalModel.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public AnimalModel getId(Integer id){
        String sql = "SELECT * FROM animal WHERE idanimal = ?";
        AnimalModel animal = null;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                Date dataNascimentoSQL = resultset.getDate("datanascimento");
                LocalDate dataNascimento = (dataNascimentoSQL != null) ? dataNascimentoSQL.toLocalDate() : null;

                Date dataResgateSQL = resultset.getDate("dataresgate");
                LocalDate dataResgate = (dataResgateSQL != null) ? dataResgateSQL.toLocalDate() : null;

                animal = new AnimalModel(
                        resultset.getInt("idanimal"),
                        resultset.getString("nome"),
                        resultset.getString("especie"),
                        dataNascimento,
                        resultset.getString("raca"),
                        resultset.getString("porte"),
                        resultset.getString("sexo"),
                        resultset.getString("status"),
                        dataResgate,
                        resultset.getString("foto"),
                        resultset.getBoolean("castrado"),
                        resultset.getString("cor")
                );
            } else {
                System.out.println("Nenhum animal encontrado com ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return animal;
    }

    public List<AnimalModel> getNome(String filtro){
        List<AnimalModel> animalModelList = new ArrayList<>();
        String sql = "SELECT * FROM animal WHERE nome ILIKE ?";
        AnimalModel animalModel = null;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql))
        {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet resultset = stmt.executeQuery();

            while(resultset.next()){
                Date dataNascimentoSQL = resultset.getDate("datanascimento");
                LocalDate dataNascimento = (dataNascimentoSQL != null) ? dataNascimentoSQL.toLocalDate() : null;

                Date dataResgateSQL = resultset.getDate("dataresgate");
                LocalDate dataResgate = (dataResgateSQL != null) ? dataResgateSQL.toLocalDate() : null;

                animalModel = new AnimalModel(resultset.getInt("idanimal"),
                        resultset.getString("nome"),
                        resultset.getString("especie"),
                        dataNascimento,
                        resultset.getString("raca"),
                        resultset.getString("porte"),
                        resultset.getString("sexo"),
                        resultset.getString("status"),
                        dataResgate,
                        resultset.getString("foto"),
                        resultset.getBoolean("castrado"),
                        resultset.getString("cor"));
                animalModelList.add(animalModel);
            }
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return animalModelList;
    }

    public List<AnimalModel> getEspecie(String filtro){
        List<AnimalModel> animalModelList = new ArrayList<>();
        String sql = "SELECT * FROM animal WHERE especie ILIKE ?";
        AnimalModel animalModel = null;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet resultset = stmt.executeQuery();
            while(resultset.next()){
                Date dataNascimentoSQL = resultset.getDate("datanascimento");
                LocalDate dataNascimento = (dataNascimentoSQL != null) ? dataNascimentoSQL.toLocalDate() : null;

                Date dataResgateSQL = resultset.getDate("dataresgate");
                LocalDate dataResgate = (dataResgateSQL != null) ? dataResgateSQL.toLocalDate() : null;

                animalModel = new AnimalModel(resultset.getInt("idanimal"),
                        resultset.getString("nome"),
                        resultset.getString("especie"),
                        dataNascimento,
                        resultset.getString("raca"),
                        resultset.getString("porte"),
                        resultset.getString("sexo"),
                        resultset.getString("status"),
                        dataResgate,
                        resultset.getString("foto"),
                        resultset.getBoolean("castrado"),
                        resultset.getString("cor"));
                animalModelList.add(animalModel);
            }
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return animalModelList;
    }

    public List<AnimalModel> getRaca(String filtro){
        List<AnimalModel> animalModelList = new ArrayList<>();
        String sql = "SELECT * FROM animal WHERE raca ILIKE ?";
        AnimalModel animalModel = null;

        try (PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet resultset = stmt.executeQuery();
            while(resultset.next()){
                Date dataNascimentoSQL = resultset.getDate("datanascimento");
                LocalDate dataNascimento = (dataNascimentoSQL != null) ? dataNascimentoSQL.toLocalDate() : null;

                Date dataResgateSQL = resultset.getDate("dataresgate");
                LocalDate dataResgate = (dataResgateSQL != null) ? dataResgateSQL.toLocalDate() : null;

                animalModel = new AnimalModel(resultset.getInt("idanimal"),
                        resultset.getString("nome"),
                        resultset.getString("especie"),
                        dataNascimento,
                        resultset.getString("raca"),
                        resultset.getString("porte"),
                        resultset.getString("sexo"),
                        resultset.getString("status"),
                        dataResgate,
                        resultset.getString("foto"),
                        resultset.getBoolean("castrado"),
                        resultset.getString("cor"));
                animalModelList.add(animalModel);
            }
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return animalModelList;
    }

    public List<AnimalModel> getAll(){
        List<AnimalModel> animalModelList = new ArrayList<>();
        String sql = "SELECT * FROM animal";
        AnimalModel animalModel = null;

        try {
            ResultSet resultset = SingletonDB.getConexao().consultar(sql);
            while(resultset.next()){
                Date dataNascimentoSQL = resultset.getDate("datanascimento");
                LocalDate dataNascimento = (dataNascimentoSQL != null) ? dataNascimentoSQL.toLocalDate() : null;

                Date dataResgateSQL = resultset.getDate("dataresgate");
                LocalDate dataResgate = (dataResgateSQL != null) ? dataResgateSQL.toLocalDate() : null;

                animalModel = new AnimalModel(resultset.getInt("idanimal"),
                        resultset.getString("nome"),
                        resultset.getString("especie"),
                        dataNascimento,
                        resultset.getString("raca"),
                        resultset.getString("porte"),
                        resultset.getString("sexo"),
                        resultset.getString("status"),
                        dataResgate,
                        resultset.getString("foto"),
                        resultset.getBoolean("castrado"),
                        resultset.getString("cor"));
                animalModelList.add(animalModel);
            }
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return animalModelList;
    }
}

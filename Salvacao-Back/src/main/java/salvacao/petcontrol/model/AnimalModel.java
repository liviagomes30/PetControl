package salvacao.petcontrol.model;

import java.time.LocalDate;

public class AnimalModel {
    private Integer id;
    private String nome;
    private String especie;
    private LocalDate datanascimento;
    private String raca;
    private String porte;
    private String sexo;
    private String status;
    private LocalDate dataresgate;
    private String foto;
    private boolean castrado;
    private String cor;


    public AnimalModel(Integer id, String nome, String especie, LocalDate datanascimento, String raca, String porte, String sexo, String status, LocalDate dataresgate, String foto, boolean castrado, String cor) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.datanascimento = datanascimento;
        this.raca = raca;
        this.porte = porte;
        this.sexo = sexo;
        this.status = status;
        this.dataresgate = dataresgate;
        this.foto = foto;
        this.castrado = castrado;
        this.cor = cor;
    }

    public AnimalModel(String nome, String especie, LocalDate datanascimento, String raca, String porte, String sexo, String status, LocalDate dataresgate, String foto, boolean castrado, String cor) {
        this.nome = nome;
        this.especie = especie;
        this.datanascimento = datanascimento;
        this.raca = raca;
        this.porte = porte;
        this.sexo = sexo;
        this.status = status;
        this.dataresgate = dataresgate;
        this.foto = foto;
        this.castrado = castrado;
        this.cor = cor;
    }

    public AnimalModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public LocalDate getDatanascimento() {
        return datanascimento;
    }

    public void setDatanascimento(LocalDate datanascimento) {
        this.datanascimento = datanascimento;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDataresgate() {
        return dataresgate;
    }

    public void setDataresgate(LocalDate dataresgate) {
        this.dataresgate = dataresgate;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public boolean isCastrado() {
        return castrado;
    }

    public void setCastrado(boolean castrado) {
        this.castrado = castrado;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }
}

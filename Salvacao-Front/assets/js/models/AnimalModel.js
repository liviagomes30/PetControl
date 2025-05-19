class AnimalModel {
    constructor(data = {}) {
        this.id = data.id || null;
        this.nome = data.nome || "";
        this.especie = data.especie || "";
        this.datanascimento = data.datanascimento || null;
        this.raca = data.raca || "";
        this.porte = data.porte || "";
        this.sexo = data.sexo || "";
        this.status = data.status || "";
        this.dataresgate = data.dataresgate || null;
        this.foto = data.foto || "";
        this.castrado = data.castrado ?? false;
        this.cor = data.cor || "";
    }

}
export default AnimalModel;
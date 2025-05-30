package salvacao.petcontrol.util;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validation {

    public static boolean isCpfValido(String cpf)
    {
        cpf = cpf.replaceAll("[^\\d]", "");

        if(cpf.length() != 11 || cpf.matches("(\\d)\\1{10}"))
            return false;

        int soma = 0;
        for (int i = 0; i < 9; i++)
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);

        int resto = (soma * 10) % 11;
        if(resto == 10 || resto == 11)
            resto = 0;

        if(resto != Character.getNumericValue(cpf.charAt(9)))
            return false;

        soma = 0;
        for(int i = 0; i < 10; i++)
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);

        resto = (soma * 10) % 11;
        if(resto == 10 || resto == 11)
            resto = 0;

        return resto == Character.getNumericValue(cpf.charAt(10));
    }

    public static boolean isDataAdocaoValida(LocalDate dataAdocao) {
        if (dataAdocao == null) {
            return false;
        }

        // Verifica se a data não está no futuro
        return !dataAdocao.isAfter(LocalDate.now());
    }

    public static boolean isDataEventoValida(LocalDate dataEvento) {
        if (dataEvento == null) {
            return false;
        }

        // Permite eventos para hoje ou no futuro (diferente da adoção)
        return !dataEvento.isBefore(LocalDate.now());
    }

    public static boolean isAnimalValidoParaEvento(int idAnimal) {
        // Validação simples - se for informado, deve ser maior que 0
        // Se for 0, significa que o evento não é específico de um animal
        return idAnimal >= 0;
    }

    public static boolean isAnimalDisponivel(int idAnimal) {
        // Aqui você poderia adicionar uma verificação no banco para confirmar
        // se o animal está com status "Disponível"
        return idAnimal > 0;
    }

    public static boolean isAdotanteValido(int idAdotante) {
        // Aqui você poderia adicionar uma verificação no banco para confirmar
        // se o adotante existe
        return idAdotante > 0;
    }

    public static boolean isStatusAcompanhamentoValido(String status) {
        if (status == null || status.trim().isEmpty()) {
            return true; // Campo opcional
        }

        // Lista de status válidos
        String[] statusValidos = {"Pendente", "Em andamento", "Concluído", "Cancelado"};

        for (String s : statusValidos) {
            if (s.equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }


}
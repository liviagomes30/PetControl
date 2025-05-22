package salvacao.petcontrol.util;

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
}
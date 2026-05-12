package modelo;

/**
 * Centraliza todas as regras de validação do sistema.
 * Garante conformidade com a LGPD — sem dados sensíveis.
 */
public class Validacao {

    private static final int NOME_MIN  = 2;
    private static final int NOME_MAX  = 60;
    private static final int IDADE_MIN = 1;
    private static final int IDADE_MAX = 120;
    public static final String[] FAIXAS_ETARIAS_VALIDAS = {
            "Até 12 anos",
            "13 a 17 anos",
            "18 a 24 anos",
            "25 a 34 anos",
            "35 a 49 anos",
            "50 anos ou mais"
    };

    // ── Nome ───────────────────────────────────────────────────────────────

    public boolean validarNome(String nome) {
        if (nome == null || nome.isBlank()) return false;
        String t = nome.trim();
        if (t.length() < NOME_MIN || t.length() > NOME_MAX) return false;
        return t.matches("[\\p{L} ]+");
    }

    public String mensagemErroNome(String nome) {
        if (nome == null || nome.isBlank())      return "O nome n\u00e3o pode estar vazio.";
        String t = nome.trim();
        if (t.length() < NOME_MIN)               return "O nome deve ter pelo menos " + NOME_MIN + " letras.";
        if (t.length() > NOME_MAX)               return "O nome n\u00e3o pode ultrapassar " + NOME_MAX + " caracteres.";
        if (!t.matches("[\\p{L} ]+"))            return "Use apenas letras e espa\u00e7os.";
        return "";
    }

    public boolean validarSobrenome(String sobrenome) {
        return validarNome(sobrenome);
    }

    public String mensagemErroSobrenome(String sobrenome) {
        return mensagemErroNome(sobrenome);
    }

    // ── Idade ──────────────────────────────────────────────────────────────

    public boolean validarIdadeTexto(String texto) {
        if (texto == null || texto.isBlank()) return false;
        try {
            return validarIdade(Integer.parseInt(texto.trim()));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validarIdade(int idade) {
        return idade >= IDADE_MIN && idade <= IDADE_MAX;
    }

    public int converterIdade(String texto) {
        return Integer.parseInt(texto.trim());
    }

    public String mensagemErroIdade(String texto) {
        if (texto == null || texto.isBlank()) return "A idade n\u00e3o pode estar vazia.";
        try {
            int v = Integer.parseInt(texto.trim());
            if (v < IDADE_MIN) return "Idade m\u00ednima: " + IDADE_MIN + " ano.";
            if (v > IDADE_MAX) return "Idade m\u00e1xima: " + IDADE_MAX + " anos.";
            return "";
        } catch (NumberFormatException e) {
            return "Digite apenas n\u00fameros para a idade.";
        }
    }

    // ── Faixa etária ───────────────────────────────────────────────────────

    public boolean validarFaixaEtaria(String faixaEtaria) {
        if (faixaEtaria == null || faixaEtaria.isBlank()) return false;
        String valor = faixaEtaria.trim();
        for (String faixa : FAIXAS_ETARIAS_VALIDAS) {
            if (faixa.equals(valor)) return true;
        }
        return false;
    }

    public String mensagemErroFaixaEtaria(String faixaEtaria) {
        if (faixaEtaria == null || faixaEtaria.isBlank()) return "Selecione uma faixa etária.";
        return validarFaixaEtaria(faixaEtaria) ? "" : "Selecione uma faixa etária válida.";
    }

    // ── Questionário ───────────────────────────────────────────────────────

    public boolean validarQuestionarioCompleto(int[] respostas) {
        if (respostas == null) return false;
        for (int r : respostas) if (r < 0) return false;
        return true;
    }

    public boolean validarResposta(int opcao, int totalOpcoes) {
        return opcao >= 0 && opcao < totalOpcoes;
    }

    // ── Satisfação ─────────────────────────────────────────────────────────

    public boolean validarSatisfacao(int estrelas) {
        return estrelas >= 0 && estrelas <= 5;
    }

    // ── Sanitização (LGPD) ─────────────────────────────────────────────────

    public String sanitizarNome(String nome) {
        if (nome == null) return "";
        return nome.trim()
                .replaceAll("[^\\p{L} ]", "")
                .replaceAll("\\s{2,}", " ");
    }
}

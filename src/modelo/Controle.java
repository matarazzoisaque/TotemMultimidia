package modelo;

import apresentacao.*;
import javax.swing.*;
import java.util.Arrays;

/**
 * Controlador central — interliga frontend e backend,
 * gerencia o fluxo de telas e a passagem de dados.
 */
public class Controle extends absPropriedades {

    private final JFrame framePai;
    private final Validacao validacao;

    public Controle() {
        framePai = new JFrame();
        framePai.setUndecorated(true);
        framePai.setExtendedState(JFrame.MAXIMIZED_BOTH);
        framePai.setVisible(true);
        framePai.toFront();
        framePai.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        validacao = new Validacao();
        // absPropriedades chama Executar() automaticamente no construtor
    }

    // ── Ponto de entrada ───────────────────────────────────────────────────
    @Override
    public void Executar() {
        SwingUtilities.invokeLater(this::exibirTelaInicial);
    }

    // ── Navegação ──────────────────────────────────────────────────────────
    public void exibirTelaInicial()   { new fmrInicio(framePai, this).setVisible(true); }
    public void exibirCadastro()      { new fmrCadastroVisitante(framePai, this).setVisible(true); }
    public void exibirQuestionario()  { new fmrQuestionario(framePai, this).setVisible(true); }
    public void exibirSatisfacao()    { new fmrSatisfacao(framePai, this).setVisible(true); }
    public void exibirAdministracao() { new fmrAdministracao(framePai, this).setVisible(true); }

    public void exibirObra(int indice) {
        obraAtual = indice;
        new fmrObra(framePai, this, indice).setVisible(true);
    }

    // ── Lógica de fluxo ───────────────────────────────────────────────────

    public void proximaEtapaAposObra(int obraIdx) {
        int prox = obraIdx + 1;
        if (prox < titulosObras.length) exibirObra(prox);
        else exibirQuestionario();
    }

    public void aposQuestionario() {
        historicoPontuacoes.add(calcularPontuacao());
        exibirSatisfacao();
    }

    public void finalizarVisita(int notaFinal) {
        registrarSatisfacao(notaFinal);
        historicoNomes.add(nomeVisitante);
        historicoIdades.add(idadeVisitante);
        historicoSatisfacoes.add(notaFinal);
        reiniciarSessao();
        exibirTelaInicial();
    }

    private void reiniciarSessao() {
        nomeVisitante     = "";
        idadeVisitante    = 0;
        obraAtual         = 0;
        etapaAtual        = 0;
        notaSatisfacao    = -1;
        for (int i = 0; i < respostasVisitante.length; i++) respostasVisitante[i] = -1;
    }

    // ── Validação ──────────────────────────────────────────────────────────
    @Override
    public boolean validarVisitante(String nome, String idade) {
        return validacao.validarNome(nome) && validacao.validarIdadeTexto(idade);
    }

    public String erroNome(String nome)       { return validacao.mensagemErroNome(nome); }
    public String erroIdade(String idade)     { return validacao.mensagemErroIdade(idade); }

    public boolean salvarDadosVisitante(String nome, String idadeTexto) {
        if (!validarVisitante(nome, idadeTexto)) return false;
        this.nomeVisitante  = validacao.sanitizarNome(nome);
        this.idadeVisitante = validacao.converterIdade(idadeTexto);
        return true;
    }

    public boolean autenticarAdministracao(char[] senha) {
        char[] senhaCorreta = {'m', 'u', 'l', 't', 'i', 'm', 'i', 'd', 'i', 'a'};
        boolean autenticado = senha != null && Arrays.equals(senha, senhaCorreta);
        Arrays.fill(senhaCorreta, '\0');
        return autenticado;
    }

    public int getTotalAvaliacoes() {
        return historicoSatisfacoes.size();
    }

    public int getQuantidadeAvaliacoesPorNota(int nota) {
        int total = 0;
        for (int avaliacao : historicoSatisfacoes) {
            if (avaliacao == nota) total++;
        }
        return total;
    }

    public double getMediaAvaliacoes() {
        if (historicoSatisfacoes.isEmpty()) return 0;
        int soma = 0;
        for (int avaliacao : historicoSatisfacoes) soma += avaliacao;
        return soma / (double) historicoSatisfacoes.size();
    }

    public int getTotalAvaliacoesPositivas() {
        int total = 0;
        for (int avaliacao : historicoSatisfacoes) {
            if (avaliacao >= 4) total++;
        }
        return total;
    }

    public double getMediaPontuacaoHistorica() {
        if (historicoPontuacoes.isEmpty()) return 0;
        int soma = 0;
        for (int pontuacao : historicoPontuacoes) soma += pontuacao;
        return soma / (double) historicoPontuacoes.size();
    }

    // ── Getters auxiliares para as telas ──────────────────────────────────
    public JFrame    getFramePai()              { return framePai; }
    public String    getTituloObra(int i)       { return titulosObras[i]; }
    public String    getDescricaoObra(int i)    { return descricoesObras[i]; }
    public boolean   deveExibirModelo3D(int i)  { return exibirModelo3D[i]; }
    public String    getPergunta(int i)         { return perguntas[i]; }
    public String[]  getOpcoesPergunta(int i)   { return opcoes[i]; }
    public int       getTotalObras()            { return titulosObras.length; }
    public int       getTotalPerguntas()        { return perguntas.length; }
}

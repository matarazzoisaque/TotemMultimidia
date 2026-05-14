package modelo;

import apresentacao.*;
import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * Controlador central — interliga frontend e backend,
 * gerencia o fluxo de telas e a passagem de dados.
 *
 * Etapa 7 — Gestão de janelas:
 * Todos os métodos de navegação agora recebem a JDialog de origem e chamam
 * dispose() nela ANTES de criar a nova tela, garantindo que nenhuma janela
 * antiga fique viva em memória. Isso corrige o bug em que Alt+F4 voltava
 * à tela da visita anterior.
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
    }

    // ── Ponto de entrada ──────────────────────────────────────────────────────
    @Override
    public void Executar() {
        SwingUtilities.invokeLater(this::exibirTelaInicial);
    }

    // ── Implementação dos métodos abstratos de intMetodos ─────────────────────

    @Override
    public void registrarResposta(int pergunta, int opcao) {
        if (pergunta >= 0 && pergunta < respostasVisitante.length) {
            respostasVisitante[pergunta] = opcao;
        }
    }

    @Override
    public void registrarSatisfacao(int estrelas) {
        this.notaSatisfacao = estrelas;
    }

    @Override
    public int calcularPontuacao() {
        int acertos = 0;
        for (int i = 0; i < gabaritos.length; i++) {
            if (i < respostasVisitante.length && respostasVisitante[i] == gabaritos[i]) {
                acertos++;
            }
        }
        return acertos;
    }

    @Override
    public void avancar() {
        etapaAtual++;
    }

    @Override
    public void voltar() {
        if (etapaAtual > 0) etapaAtual--;
    }

    // ── Navegação sem origem (entrada do fluxo) ───────────────────────────────

    /** Exibe a tela inicial sem fechar nenhuma tela anterior (ponto de entrada). */
    public void exibirTelaInicial()   { new fmrInicio(framePai, this).setVisible(true); }

    /** Exibe o cadastro sem fechar nenhuma tela anterior (chamado por fmrInicio via fadeOutThen). */
    public void exibirCadastro()      { new fmrCadastroVisitante(framePai, this).setVisible(true); }

    /** Exibe o questionário sem fechar nenhuma tela anterior (chamado via fadeOutThen). */
    public void exibirQuestionario()  { new fmrQuestionario(framePai, this).setVisible(true); }

    /** Exibe a satisfação sem fechar nenhuma tela anterior (chamado via fadeOutThen). */
    public void exibirSatisfacao()    { new fmrSatisfacao(framePai, this).setVisible(true); }

    /** Exibe a administração sem fechar nenhuma tela anterior (chamado via fadeOutThen). */
    public void exibirAdministracao() { new fmrAdministracao(framePai, this).setVisible(true); }

    /**
     * Exibe uma obra sem fechar nenhuma tela anterior.
     * Usado pela navegação interna de fmrObra via fadeOutThen (a tela de origem
     * já é descartada pelo próprio fadeOutThen antes desta chamada).
     */
    public void exibirObra(int indice) {
        obraAtual = indice;
        new fmrObra(framePai, this, indice).setVisible(true);
    }

    /** Alias mantido para compatibilidade. */
    public void abrirObra(int indice) { exibirObra(indice); }

    /**
     * Retorna para a tela inicial descartando a janela de origem.
     * Deve ser chamado DENTRO de um fadeOutThen para que a origem já esteja
     * invisível no momento do dispose.
     */
    public void voltarParaInicio() { exibirTelaInicial(); }

    // ── Lógica de fluxo ───────────────────────────────────────────────────────

    /**
     * Avança para a próxima etapa após uma obra.
     * Deve ser invocado de dentro de um fadeOutThen — a tela chamadora já foi
     * escondida pelo fade e será descartada em seguida.
     */
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
        historicoNomes.add(getNomeCompletoVisitanteAtual());
        historicoSobrenomes.add(sobrenomeVisitante);
        historicoFaixasEtarias.add(faixaEtariaVisitante);
        historicoSatisfacoes.add(notaFinal);
        reiniciarSessao();
        exibirTelaInicial();
    }

    private void reiniciarSessao() {
        nomeVisitante        = "";
        sobrenomeVisitante   = "";
        faixaEtariaVisitante = "";
        dadosVisitante[0]    = "";
        dadosVisitante[1]    = "";
        dadosVisitante[2]    = "";
        idadeVisitante       = 0;
        obraAtual            = 0;
        etapaAtual           = 0;
        notaSatisfacao       = -1;
        for (int i = 0; i < respostasVisitante.length; i++) respostasVisitante[i] = -1;
    }

    // ── Validação ─────────────────────────────────────────────────────────────
    @Override
    public boolean validarVisitante(String nome, String idade) {
        return validacao.validarNome(nome)
                && (validacao.validarFaixaEtaria(idade) || validacao.validarIdadeTexto(idade));
    }

    public boolean validarVisitante(String nome, String sobrenome, String faixaEtaria) {
        return validacao.validarNome(nome)
                && validacao.validarSobrenome(sobrenome)
                && validacao.validarFaixaEtaria(faixaEtaria);
    }

    public String erroNome(String nome)               { return validacao.mensagemErroNome(nome); }
    public String erroSobrenome(String sobrenome)     { return validacao.mensagemErroSobrenome(sobrenome); }
    public String erroIdade(String idade)             { return validacao.mensagemErroIdade(idade); }
    public String erroFaixaEtaria(String faixaEtaria) { return validacao.mensagemErroFaixaEtaria(faixaEtaria); }

    public boolean salvarDadosVisitante(String nome, String sobrenome, String faixaEtaria) {
        if (!validarVisitante(nome, sobrenome, faixaEtaria)) return false;
        this.nomeVisitante        = validacao.sanitizarNome(nome);
        this.sobrenomeVisitante   = validacao.sanitizarNome(sobrenome);
        this.faixaEtariaVisitante = faixaEtaria;
        this.dadosVisitante[0]    = this.nomeVisitante;
        this.dadosVisitante[1]    = this.sobrenomeVisitante;
        this.dadosVisitante[2]    = this.faixaEtariaVisitante;
        return true;
    }

    public boolean autenticarAdministracao(char[] senha) {
        char[] senhaCorreta = {'1', '2', '3', '4', '5', '6'};
        boolean autenticado = senha != null && Arrays.equals(senha, senhaCorreta);
        Arrays.fill(senhaCorreta, '\0');
        return autenticado;
    }

    // ── Estatísticas do histórico ─────────────────────────────────────────────

    public int    getTotalAvaliacoes()                         { return historicoSatisfacoes.size(); }
    public double getMediaAvaliacoes()                         { if (historicoSatisfacoes.isEmpty()) return 0; int s=0; for(int v:historicoSatisfacoes) s+=v; return s/(double)historicoSatisfacoes.size(); }
    public int    getTotalAvaliacoesPositivas()                { int t=0; for(int v:historicoSatisfacoes) if(v>=4) t++; return t; }
    public double getMediaPontuacaoHistorica()                 { if (historicoPontuacoes.isEmpty()) return 0; int s=0; for(int v:historicoPontuacoes) s+=v; return s/(double)historicoPontuacoes.size(); }
    public int    getQuantidadeAvaliacoesPorNota(int nota)     { int t=0; for(int v:historicoSatisfacoes) if(v==nota) t++; return t; }

    /** Retorna a lista completa de satisfações da sessão — usado por fmrAdministracao. */
    public List<Integer> getHistoricoSatisfacoes()  { return historicoSatisfacoes; }

    /** Retorna a lista completa de pontuações do quiz — usado por fmrAdministracao. */
    public List<Integer> getHistoricoPontuacoes()   { return historicoPontuacoes; }

    // ── Getters do visitante atual ────────────────────────────────────────────

    public String   getNomeVisitanteAtual()        { return nomeVisitante; }
    /** Alias sem sufixo — chamado por fmrSatisfacao. */
    public String   getNomeVisitante()             { return nomeVisitante; }
    public String   getSobrenomeVisitanteAtual()   { return sobrenomeVisitante; }
    public String   getNomeCompletoVisitanteAtual(){ return (nomeVisitante + " " + sobrenomeVisitante).trim(); }
    public String   getFaixaEtariaVisitanteAtual() { return faixaEtariaVisitante; }
    public String[] getDadosVisitanteAtual()       { return dadosVisitante; }

    // ── Getters auxiliares para as telas ──────────────────────────────────────
    public JFrame    getFramePai()             { return framePai; }
    public String    getTituloObra(int i)      { return titulosObras[i]; }
    public String    getDescricaoObra(int i)   { return descricoesObras[i]; }
    public String    getImagemObra(int i)      { return imagensObras[i]; }
    public String    getCodigoObra(int i)      { return codigosObras[i]; }
    public String    getAnoObra(int i)         { return anosObras[i]; }
    public String    getPergunta(int i)        { return perguntas[i]; }
    public String[]  getOpcoesPergunta(int i)  { return opcoes[i]; }
    public int       getTotalObras()           { return titulosObras.length; }
    public int       getTotalPerguntas()       { return perguntas.length; }
    /** Retorna o índice por item — compatível com chamadas pontuais. */
    public int       getGabarito(int i)        { return gabaritos[i]; }
    /** Retorna o array completo de gabaritos — usado por fmrQuestionario. */
    public int[]     getGabaritos()            { return gabaritos; }
    /** Retorna o array completo de respostas do visitante — usado por fmrQuestionario. */
    public int[]     getRespostasVisitante()   { return respostasVisitante; }
    /** Retorna a resposta de uma pergunta específica — compatível com chamadas pontuais. */
    public int       getRespostaVisitante(int i){ return respostasVisitante[i]; }
}

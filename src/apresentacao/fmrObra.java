package apresentacao;

import modelo.Controle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.net.URL;

/**
 * Tela de obra com composicao editorial:
 * imagem real da obra/missao na esquerda e descricao na direita.
 *
 * Alteracoes Etapa 4:
 * - Titulo movido para dentro do JScrollPane (desce junto com o texto)
 * - Visualizador fullscreen ao clicar na imagem (4.3)
 * - Botao Voltar adicionado na faixa de acao (4.4)
 */
public class fmrObra extends JDialog {

    private static final int ESPACO_CODIGO_ANO = 5;
    private static final Color COR_FAIXA_ACAO = new Color(7, 5, 7);

    private final Controle controle;
    private final int indice;

    public fmrObra(JFrame pai, Controle controle, int indice) {
        super(pai, true);
        this.controle = controle;
        this.indice = indice;
        EstiloBase.configurarDialogFullscreen(this);
        construirInterface();
    }

    private void construirInterface() {
        JPanel fundo = EstiloBase.criarPainelFundo(220L + (indice * 31L));
        Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();

        int margem = Math.max(30, EstiloBase.escalar(42, tela));
        int topo = Math.max(26, EstiloBase.escalar(38, tela));
        int gap = Math.max(24, EstiloBase.escalar(34, tela));
        int conteudoY = topo + Math.max(76, EstiloBase.escalar(86, tela));
        int conteudoH = tela.height - conteudoY - Math.max(54, EstiloBase.escalar(68, tela));
        int arteW = Math.min(560, (int) (tela.width * 0.34));
        int painelW = tela.width - (margem * 2) - arteW - gap;
        int arteH = conteudoH;

        JLabel lblColecao = EstiloBase.criarTag("Cole\u00e7\u00e3o Marte");
        lblColecao.setBounds(margem, topo, Math.max(142, EstiloBase.escalar(150, tela)), Math.max(32, EstiloBase.escalar(34, tela)));
        fundo.add(lblColecao);

        JLabel lblEtapa = EstiloBase.criarLabel(
                String.format("OBRA %02d  DE  %02d", indice + 1, controle.getTotalObras()),
                EstiloBase.FONTE_LABEL.deriveFont(13f),
                EstiloBase.COR_TEXTO_SECUNDARIO
        );
        lblEtapa.setHorizontalAlignment(SwingConstants.RIGHT);
        int etapaW = Math.max(160, EstiloBase.escalar(180, tela));
        lblEtapa.setBounds(tela.width - margem - etapaW, topo + 2, etapaW, Math.max(26, EstiloBase.escalar(28, tela)));
        fundo.add(lblEtapa);

        JPanel barraProgress = criarBarraProgresso(tela.width - (margem * 2));
        barraProgress.setBounds(margem, topo + Math.max(40, EstiloBase.escalar(46, tela)), tela.width - (margem * 2), Math.max(14, EstiloBase.escalar(18, tela)));
        fundo.add(barraProgress);

        // ── Card da imagem ─────────────────────────────────────────────────────

        JPanel cardArte = EstiloBase.criarCard();
        cardArte.setLayout(null);
        cardArte.setBounds(margem, conteudoY, arteW, arteH);
        fundo.add(cardArte);

        String imageObra = controle.getImagemObra(indice);
        int artePadding = Math.max(18, EstiloBase.escalar(20, tela));
        int seloX = Math.max(20, EstiloBase.escalar(24, tela));
        int seloY = arteH - Math.max(100, EstiloBase.escalar(100, tela));
        int seloW = Math.max(92, EstiloBase.escalar(92, tela));
        int seloH = Math.max(32, EstiloBase.escalar(32, tela));

        // Painel da imagem com clique para abrir visualizador fullscreen (4.3)
        JPanel painelImagem = criarPainelImagemObra(imageObra);
        painelImagem.setBounds(
                artePadding,
                artePadding,
                arteW - (artePadding * 2),
                Math.max(120, seloY - artePadding - Math.max(14, EstiloBase.escalar(14, tela)))
        );
        painelImagem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        painelImagem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirVisualizadorFullscreen(imageObra);
            }
        });
        cardArte.add(painelImagem);

        JLabel lblCodigo = EstiloBase.criarTag(controle.getCodigoObra(indice));
        lblCodigo.setBounds(seloX, seloY, seloW, seloH);
        cardArte.add(lblCodigo);

        JLabel lblAno = EstiloBase.criarLabel(
                controle.getAnoObra(indice),
                EstiloBase.fonteResponsiva(34f, tela),
                EstiloBase.COR_TEXTO_PRIMARIO
        );
        lblAno.setHorizontalAlignment(SwingConstants.LEFT);
        int anoY = seloY + seloH + ESPACO_CODIGO_ANO;
        int anoH = Math.max(32, EstiloBase.escalar(34, tela));
        lblAno.setBounds(seloX, anoY, Math.max(180, EstiloBase.escalar(180, tela)), anoH);
        cardArte.add(lblAno);

        JLabel lblLegenda = EstiloBase.criarLabel(
                "Imagem da obra / miss\u00e3o",
                EstiloBase.FONTE_PEQUENA,
                EstiloBase.COR_TEXTO_FRACO
        );
        lblLegenda.setHorizontalAlignment(SwingConstants.LEFT);
        lblLegenda.setBounds(seloX, anoY + anoH, arteW - (seloX * 2), Math.max(18, EstiloBase.escalar(20, tela)));
        cardArte.add(lblLegenda);

        // ── Card de informacoes ───────────────────────────────────────────────
        // Titulo agora esta DENTRO do scroll para descer junto com o texto (4)

        JPanel cardInfo = EstiloBase.criarCard();
        cardInfo.setLayout(null);
        cardInfo.setBounds(margem + arteW + gap, conteudoY, painelW, conteudoH);
        fundo.add(cardInfo);

        int infoPad = Math.max(24, EstiloBase.escalar(30, tela));
        int infoTagY = Math.max(22, EstiloBase.escalar(26, tela));
        int infoTagH = Math.max(32, EstiloBase.escalar(34, tela));

        JLabel lblTema = EstiloBase.criarTag("Detalhes da obra");
        lblTema.setBounds(infoPad, infoTagY, Math.max(150, EstiloBase.escalar(160, tela)), infoTagH);
        cardInfo.add(lblTema);

        // Chips de ano e tipo ficam fora do scroll (referencia visual fixa)
        int chipY = infoTagY + infoTagH + Math.max(14, EstiloBase.escalar(14, tela));
        int chipH = Math.max(32, EstiloBase.escalar(32, tela));
        int chipAnoW = Math.max(138, EstiloBase.escalar(150, tela));
        int chipTipoW = Math.max(176, EstiloBase.escalar(176, tela));

        JLabel lblChipAno = EstiloBase.criarTag("ANO " + controle.getAnoObra(indice));
        lblChipAno.setBounds(infoPad, chipY, chipAnoW, chipH);
        cardInfo.add(lblChipAno);

        JLabel lblChipTipo = EstiloBase.criarTag("TEXTO CURATORIAL");
        lblChipTipo.setBounds(infoPad + chipAnoW + Math.max(10, EstiloBase.escalar(10, tela)), chipY, chipTipoW, chipH);
        cardInfo.add(lblChipTipo);

        // ── Conteudo scrollavel: TITULO + subtitulo + texto descritivo ────────
        // Titulo e subtitulo agora ficam dentro de um painel que entra no scroll

        JPanel painelConteudo = new JPanel();
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
        painelConteudo.setOpaque(false);
        painelConteudo.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        // Titulo dentro do scroll
        JTextArea lblTitulo = EstiloBase.criarTextoQuebravel(
                controle.getTituloObra(indice),
                EstiloBase.fonteResponsiva(34f, tela),
                EstiloBase.COR_TEXTO_PRIMARIO
        );
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitulo.setMaximumSize(new Dimension(painelW - (infoPad * 2), Integer.MAX_VALUE));
        painelConteudo.add(lblTitulo);
        painelConteudo.add(Box.createVerticalStrut(Math.max(10, EstiloBase.escalar(10, tela))));

        // Subtitulo dentro do scroll
        JTextArea lblSub = EstiloBase.criarTextoQuebravel(
                "Conhe\u00e7a a hist\u00f3ria, os objetivos, os desafios e os impactos desta miss\u00e3o "
                        + "na explora\u00e7\u00e3o rob\u00f3tica de Marte.",
                EstiloBase.fonteResponsiva(18f, tela),
                EstiloBase.COR_TEXTO_SECUNDARIO
        );
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSub.setMaximumSize(new Dimension(painelW - (infoPad * 2), Integer.MAX_VALUE));
        painelConteudo.add(lblSub);
        painelConteudo.add(Box.createVerticalStrut(Math.max(14, EstiloBase.escalar(16, tela))));

        // Texto descritivo dentro do scroll
        JTextArea txtDesc = new JTextArea(controle.getDescricaoObra(indice));
        txtDesc.setFont(EstiloBase.fonteResponsiva(18f, tela));
        txtDesc.setForeground(EstiloBase.COR_TEXTO_SECUNDARIO);
        txtDesc.setBackground(new Color(0, 0, 0, 0));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        txtDesc.setOpaque(false);
        txtDesc.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        txtDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtDesc.setMaximumSize(new Dimension(painelW - (infoPad * 2), Integer.MAX_VALUE));
        painelConteudo.add(txtDesc);

        JScrollPane scroll = EstiloBase.criarScrollPane(painelConteudo);
        int faixaH = Math.max(86, EstiloBase.escalar(102, tela));
        int scrollY = chipY + chipH + Math.max(20, EstiloBase.escalar(24, tela));
        int barraAcaoY = conteudoH - faixaH - Math.max(30, EstiloBase.escalar(46, tela));
        int scrollH = Math.max(1, barraAcaoY - scrollY - Math.max(18, EstiloBase.escalar(24, tela)));
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBounds(infoPad, scrollY, painelW - (infoPad * 2), scrollH);
        cardInfo.add(scroll);

        // ── Faixa de acao com botoes Voltar e Proximo ────────────────────────

        JPanel faixaAcao = criarFaixaAcao();
        faixaAcao.setBounds(infoPad, barraAcaoY, painelW - (infoPad * 2), faixaH);
        cardInfo.add(faixaAcao);

        int acaoPadding = Math.max(18, EstiloBase.escalar(22, tela));
        int botaoH = Math.max(46, EstiloBase.escalar(50, tela));
        int botaoY = Math.max(24, (faixaAcao.getHeight() - botaoH) / 2);
        int larguraBotaoVoltar = Math.max(140, EstiloBase.escalar(150, tela));
        int larguraProximoDesejada = indice == controle.getTotalObras() - 1 ? 252 : 228;
        int espaco = Math.max(10, EstiloBase.escalar(12, tela));
        int larguraProximo = Math.min(
                Math.max(190, EstiloBase.escalar(larguraProximoDesejada, tela)),
                faixaAcao.getWidth() - (acaoPadding * 2) - larguraBotaoVoltar - espaco
        );

        // Botao Voltar (4.4) — na primeira obra volta ao inicio/menu
        JButton btnVoltar = criarBotaoAcaoObra("\u2190 Voltar", false);
        btnVoltar.setFont(EstiloBase.fonteResponsiva(17f, tela));
        btnVoltar.setBounds(acaoPadding, botaoY, larguraBotaoVoltar, botaoH);
        btnVoltar.addActionListener(e -> {
            btnVoltar.setEnabled(false);
            dispose();
            if (indice == 0) {
                // Primeira obra: volta para a tela inicial/menu
                controle.voltarParaInicio();
            } else {
                // Demais obras: abre a obra anterior
                controle.abrirObra(indice - 1);
            }
        });
        faixaAcao.add(btnVoltar);

        // Botao Proximo
        JButton btnProximo = criarBotaoAcaoObra(
                indice == controle.getTotalObras() - 1
                        ? "Ir para o question\u00e1rio"
                        : "Pr\u00f3xima obra",
                true
        );
        btnProximo.setFont(EstiloBase.fonteResponsiva(18f, tela));
        btnProximo.setBounds(faixaAcao.getWidth() - acaoPadding - larguraProximo, botaoY, larguraProximo, botaoH);
        btnProximo.addActionListener(e -> {
            btnProximo.setEnabled(false);
            controle.proximaEtapaAposObra(indice);
            dispose();
        });
        faixaAcao.add(btnProximo);

        setContentPane(fundo);
    }

    // ── Visualizador fullscreen (4.3) ─────────────────────────────────────────

    private void abrirVisualizadorFullscreen(String caminhoImagem) {
        Image imagem = carregarImagem(caminhoImagem);

        JDialog viewer = new JDialog(this, true);
        viewer.setUndecorated(true);
        Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
        viewer.setSize(tela);
        viewer.setLocationRelativeTo(null);

        JPanel overlay = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setColor(new Color(0, 0, 0, 230));
                g2.fillRect(0, 0, getWidth(), getHeight());
                if (imagem != null) {
                    int imgW = imagem.getWidth(null);
                    int imgH = imagem.getHeight(null);
                    if (imgW > 0 && imgH > 0) {
                        double escala = Math.min(
                                (double)(getWidth() - 100) / imgW,
                                (double)(getHeight() - 100) / imgH
                        );
                        int novaLargura = (int)(imgW * escala);
                        int novaAltura  = (int)(imgH * escala);
                        int x = (getWidth() - novaLargura) / 2;
                        int y = (getHeight() - novaAltura) / 2;
                        g2.drawImage(imagem, x, y, novaLargura, novaAltura, null);
                    }
                }
                g2.dispose();
            }
        };
        overlay.setBackground(Color.BLACK);

        // Instrucao de fechamento
        JLabel lblFechar = new JLabel("\u00d7  Clique em qualquer lugar para fechar");
        lblFechar.setFont(EstiloBase.FONTE_LABEL.deriveFont(Font.BOLD, 16f));
        lblFechar.setForeground(new Color(255, 255, 255, 180));
        lblFechar.setBounds(0, 18, tela.width, 30);
        lblFechar.setHorizontalAlignment(SwingConstants.CENTER);
        overlay.add(lblFechar);

        // Clique em qualquer lugar fecha o visualizador
        overlay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                viewer.dispose();
            }
        });

        viewer.setContentPane(overlay);
        viewer.setVisible(true);
    }

    // ── Painel de imagem ──────────────────────────────────────────────────────

    private JPanel criarPainelImagemObra(String caminhoImagem) {
        return new JPanel(null) {
            private Image imagem;

            {
                imagem = carregarImagem(caminhoImagem);
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                Shape forma = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 28, 28);
                g2.setClip(forma);

                if (imagem != null) {
                    desenharImagemCover(g2, imagem, getWidth(), getHeight());
                } else {
                    desenharFallbackImagem(g2);
                }

                g2.setClip(null);
                g2.setColor(new Color(255, 255, 255, 26));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 28, 28);

                g2.dispose();
            }
        };
    }

    private Image carregarImagem(String caminhoImagem) {
        if (caminhoImagem == null || caminhoImagem.isBlank()) {
            return null;
        }

        try {
            URL url = getClass().getResource(caminhoImagem);
            if (url != null) {
                return new ImageIcon(url).getImage();
            }

            File arquivo = new File("src" + caminhoImagem);
            if (arquivo.exists()) {
                return new ImageIcon(arquivo.getAbsolutePath()).getImage();
            }

            File arquivoDireto = new File(caminhoImagem);
            if (arquivoDireto.exists()) {
                return new ImageIcon(arquivoDireto.getAbsolutePath()).getImage();
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    private void desenharImagemCover(Graphics2D g2, Image imagem, int larguraPainel, int alturaPainel) {
        int larguraImagem = imagem.getWidth(null);
        int alturaImagem = imagem.getHeight(null);

        if (larguraImagem <= 0 || alturaImagem <= 0) {
            desenharFallbackImagem(g2);
            return;
        }

        double escala = Math.max(
                (double) larguraPainel / larguraImagem,
                (double) alturaPainel / alturaImagem
        );

        int novaLargura = (int) Math.ceil(larguraImagem * escala);
        int novaAltura = (int) Math.ceil(alturaImagem * escala);

        int x = (larguraPainel - novaLargura) / 2;
        int y = (alturaPainel - novaAltura) / 2;

        g2.drawImage(imagem, x, y, novaLargura, novaAltura, null);

        GradientPaint sombra = new GradientPaint(
                0, 0, new Color(0, 0, 0, 20),
                0, alturaPainel, new Color(0, 0, 0, 150)
        );
        g2.setPaint(sombra);
        g2.fillRect(0, 0, larguraPainel, alturaPainel);
    }

    private void desenharFallbackImagem(Graphics2D g2) {
        GradientPaint fundo = new GradientPaint(
                0, 0, new Color(255, 98, 36, 54),
                getWidth(), getHeight(), new Color(233, 32, 97, 42)
        );
        g2.setPaint(fundo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);

        g2.setColor(new Color(255, 255, 255, 26));
        for (int x = 24; x < getWidth(); x += 26) {
            g2.drawLine(x, 0, x, getHeight());
        }

        for (int y = 24; y < getHeight(); y += 26) {
            g2.drawLine(0, y, getWidth(), y);
        }

        JLabelHelper.drawCenteredText(
                g2,
                "Imagem n\u00e3o encontrada",
                getWidth(),
                getHeight(),
                EstiloBase.fontePoppins(24f),
                new Color(255, 255, 255, 190)
        );
    }

    private JPanel criarFaixaAcao() {
        JPanel faixa = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                ativarQualidadeLocal(g2);
                g2.setColor(COR_FAIXA_ACAO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.setColor(new Color(255, 255, 255, 18));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 28, 28);
                g2.dispose();
            }
        };
        faixa.setOpaque(false);
        return faixa;
    }

    private JButton criarBotaoAcaoObra(String texto, boolean primario) {
        JButton botao = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                ativarQualidadeLocal(g2);

                g2.setColor(COR_FAIXA_ACAO);
                g2.fillRect(0, 0, getWidth(), getHeight());

                boolean hover = getModel().isRollover() || getModel().isPressed();
                int arco = 28;

                if (primario) {
                    Color inicio = hover ? EstiloBase.COR_DESTAQUE_HOVER : EstiloBase.COR_DESTAQUE;
                    Color fim = hover ? new Color(255, 204, 28) : EstiloBase.COR_DESTAQUE_2;
                    g2.setPaint(new GradientPaint(0, 0, inicio, getWidth(), getHeight(), fim));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), arco, arco);
                    g2.setColor(new Color(255, 255, 255, 58));
                } else {
                    g2.setColor(hover ? new Color(30, 23, 25) : new Color(16, 12, 14));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), arco, arco);
                    g2.setPaint(new GradientPaint(
                            0, 0, hover ? EstiloBase.COR_DESTAQUE : EstiloBase.COR_CARD_BORDA,
                            getWidth(), getHeight(), hover ? EstiloBase.COR_DESTAQUE_2 : EstiloBase.COR_CARD_GLOW
                    ));
                }

                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arco, arco);

                Font fonte = ajustarFonteAoBotao(g2, getFont(), texto, getWidth() - 26);
                g2.setFont(fonte);
                g2.setColor(primario || hover ? EstiloBase.COR_TEXTO_PRIMARIO : EstiloBase.COR_TEXTO_SECUNDARIO);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(texto)) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(texto, tx, ty);

                g2.dispose();
            }
        };
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setOpaque(true);
        botao.setBackground(COR_FAIXA_ACAO);
        botao.setBorder(BorderFactory.createEmptyBorder());
        botao.setMargin(new Insets(0, 0, 0, 0));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private Font ajustarFonteAoBotao(Graphics2D g2, Font fonteBase, String texto, int larguraMaxima) {
        Font fonte = fonteBase;
        FontMetrics fm = g2.getFontMetrics(fonte);
        while (fm.stringWidth(texto) > larguraMaxima && fonte.getSize2D() > 12f) {
            fonte = fonte.deriveFont(fonte.getSize2D() - 1f);
            fm = g2.getFontMetrics(fonte);
        }
        return fonte;
    }

    private void ativarQualidadeLocal(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private JPanel criarBarraProgresso(int largura) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int total = controle.getTotalObras();
                int gap = 8;
                int segW = (largura - ((total - 1) * gap)) / total;

                for (int i = 0; i < total; i++) {
                    int x = i * (segW + gap);

                    g2.setColor(new Color(255, 255, 255, 18));
                    g2.fillRoundRect(x, 4, segW, 10, 10, 10);

                    if (i <= indice) {
                        GradientPaint gp = new GradientPaint(
                                x, 0, EstiloBase.COR_DESTAQUE,
                                x + segW, 14, EstiloBase.COR_DESTAQUE_2
                        );
                        g2.setPaint(gp);
                        g2.fillRoundRect(x, 4, segW, 10, 10, 10);
                    }
                }

                g2.dispose();
            }
        };
    }

    /**
     * Helper interno para desenhar texto centralizado no fallback.
     */
    private static class JLabelHelper {
        static void drawCenteredText(
                Graphics2D g2,
                String texto,
                int largura,
                int altura,
                Font fonte,
                Color cor
        ) {
            Font fonteAnterior = g2.getFont();
            Color corAnterior = g2.getColor();

            g2.setFont(fonte);
            g2.setColor(cor);

            FontMetrics fm = g2.getFontMetrics();
            int x = (largura - fm.stringWidth(texto)) / 2;
            int y = (altura - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(texto, x, y);

            g2.setFont(fonteAnterior);
            g2.setColor(corAnterior);
        }
    }
}

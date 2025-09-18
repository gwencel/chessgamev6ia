package view;

import controller.Game;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.board.Position;
import model.pieces.Piece;

public class ChessGUI extends JFrame {
    private Game game;
    private JPanel boardPanel;
    private JButton[][] squares;
    private Map<String, ImageIcon> pieceIcons;

    // Novos componentes
    private JTextArea moveHistory;
    private JLabel turnLabel;
    private JLabel statusLabel;
    private JLabel timerLabel;
    
    // Sistema de cronômetro
    private Timer gameTimer;
    private long gameStartTime;
    private long elapsedTime = 0;
    private boolean isGameRunning = false;
    
       private boolean isDarkTheme = true; // Controla o tema atual
    
    /**
     * Definições de cores para o tema escuro.
     */
    private final Color DARK_LIGHT_SQUARE = new Color(240, 217, 181); // Bege claro para casas claras
    private final Color DARK_DARK_SQUARE = new Color(181, 136, 99);   // Marrom para casas escuras
    private final Color DARK_BACKGROUND = new Color(49, 46, 43);      // Fundo geral escuro
    private final Color DARK_TEXT = new Color(235, 235, 235);         // Cor do texto para tema escuro
    private final Color DARK_PANEL = new Color(60, 60, 60);           // Cor de painéis para tema escuro
    
    /**
     * Definições de cores para o tema claro.
     */
    private final Color LIGHT_LIGHT_SQUARE = new Color(255, 248, 220); // Bege muito claro para casas claras
    private final Color LIGHT_DARK_SQUARE = new Color(139, 69, 19);     // Marrom mais escuro para casas escuras
    private final Color LIGHT_BACKGROUND = new Color(245, 245, 245);    // Fundo geral claro
    private final Color LIGHT_TEXT = new Color(50, 50, 50);             // Cor do texto para tema claro
    private final Color LIGHT_PANEL = new Color(230, 230, 230);         // Cor de painéis para tema claro
    
    
      /**
     * Definições de cores para o tema claro.
     */
    private Color LIGHT_SQUARE;
    private Color DARK_SQUARE;
    private Color BACKGROUND_COLOR;
    private Color TEXT_COLOR;
    private Color PANEL_COLOR;
    
    // Cores de destaque (independentes do tema)
    private final Color HIGHLIGHT_SELECTED = new Color(106, 168, 79, 200); // Verde transparente
    private final Color HIGHLIGHT_MOVE = new Color(106, 140, 210, 160);    // Azul transparente
    private final Color HIGHLIGHT_CAPTURE = new Color(220, 53, 69, 180);   // Vermelho para capturas
    
    private final Font CHESS_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Font HISTORY_FONT = new Font("Consolas", Font.PLAIN, 14);

    public ChessGUI() {
        game = new Game();
        updateThemeColors(); // Inicializar cores do tema
        initializeGUI();
        loadPieceIcons();
        updateBoardDisplay();
    }
    
    /**
     * Atualiza as variáveis de cor dinâmicas com base no tema atual (escuro ou claro).
     * Chamado na inicialização e ao alternar o tema.
     */
    private void updateThemeColors() {
        if (isDarkTheme) {
            LIGHT_SQUARE = DARK_LIGHT_SQUARE;
            DARK_SQUARE = DARK_DARK_SQUARE;
            BACKGROUND_COLOR = DARK_BACKGROUND;
            TEXT_COLOR = DARK_TEXT;
            PANEL_COLOR = DARK_PANEL;
        } else {
            LIGHT_SQUARE = LIGHT_LIGHT_SQUARE;
            DARK_SQUARE = LIGHT_DARK_SQUARE;
            BACKGROUND_COLOR = LIGHT_BACKGROUND;
            TEXT_COLOR = LIGHT_TEXT;
            PANEL_COLOR = LIGHT_PANEL;
        }
    }

    private void initializeGUI() {
        setTitle("♔ Xadrez Royal - Jogo de Xadrez em Java ♛");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 750); // Aumentei um pouco mais para acomodar melhor os elementos
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        // Painel superior com informações do turno e botão de tema
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        
        timerLabel = new JLabel("00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timerLabel.setForeground(TEXT_COLOR);
        timerLabel.setPreferredSize(new Dimension(140, 40));
        timerLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
            BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));
        topPanel.add(timerLabel, BorderLayout.WEST);
        
        
        turnLabel = new JLabel("VEZ DAS BRANCAS", SwingConstants.CENTER);
        turnLabel.setFont(CHESS_FONT);
        turnLabel.setForeground(TEXT_COLOR);
        turnLabel.setPreferredSize(new Dimension(600, 40));
        turnLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
            BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));
        topPanel.add(turnLabel, BorderLayout.CENTER);
        
        // --- Botão para alternar entre o tema claro e escuro ---
        JButton themeToggleButton = new JButton(isDarkTheme ? "TEMA CLARO" : "TEMA ESCURO");
        themeToggleButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        themeToggleButton.setBackground(isDarkTheme ? new Color(70, 70, 70) : new Color(200, 200, 200));
        themeToggleButton.setForeground(isDarkTheme ? Color.WHITE : Color.BLACK);
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        themeToggleButton.setPreferredSize(new Dimension(140, 40));
        
        // Adicionar efeitos de hover ao botão de tema
        themeToggleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                themeToggleButton.setBackground(isDarkTheme ? new Color(90, 90, 90) : new Color(180, 180, 180));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                themeToggleButton.setBackground(isDarkTheme ? new Color(70, 70, 70) : new Color(200, 200, 200));
            }
        });
        
        themeToggleButton.addActionListener(e -> toggleTheme());
        topPanel.add(themeToggleButton, BorderLayout.EAST);
  
        add(topPanel, BorderLayout.NORTH);

        // Painel central com o tabuleiro
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerPanel.setBackground(BACKGROUND_COLOR);
        
        // Painel do tabuleiro com borda
        boardPanel = new JPanel(new GridLayout(8, 8, 0, 0));
        boardPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 3));
        
        // Adicionar coordenadas ao redor do tabuleiro
        JPanel boardWithCoords = new JPanel(new BorderLayout(5, 5));
        boardWithCoords.setBackground(BACKGROUND_COLOR);
        
        // Criar as casas do tabuleiro
        squares = new JButton[8][8];
        boolean isWhite = true;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = new JButton();
                squares[row][col].setPreferredSize(new Dimension(75, 75));
                squares[row][col].setBackground(isWhite ? LIGHT_SQUARE : DARK_SQUARE);
                squares[row][col].setBorderPainted(false);
                squares[row][col].setFocusPainted(false);
                
                // --- A cor da casa muda ligeiramente ao passar o mouse, exceto quando uma peça está selecionada ---
                final Color originalColor = squares[row][col].getBackground();
                final int r = row;
                final int c = col;
                squares[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        // Aplica efeito de hover apenas se nenhuma peça estiver selecionada ou o jogo não estiver rodando
                        if (!isGameRunning || game.getSelectedPiece() == null) {
                            JButton button = (JButton) e.getSource();
                            Color hoverColor = new Color(
                                Math.min(255, originalColor.getRed() + 20),
                                Math.min(255, originalColor.getGreen() + 20),
                                Math.min(255, originalColor.getBlue() + 20)
                            );
                            button.setBackground(hoverColor);
                        }
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        // Restaura a cor original da casa ao remover o mouse, exceto se uma peça estiver selecionada
                        if (!isGameRunning || game.getSelectedPiece() == null) {
                            JButton button = (JButton) e.getSource();
                            button.setBackground(originalColor);
                        }
                    }
                    
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleSquareClick(r, c);
                    }
                });
                

                boardPanel.add(squares[row][col]);
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
        
        // Adicionar coordenadas
        JPanel colCoords = new JPanel(new GridLayout(1, 8));
        colCoords.setBackground(BACKGROUND_COLOR);
        for (int col = 0; col < 8; col++) {
            JLabel colLabel = new JLabel(String.valueOf((char)('a' + col)), SwingConstants.CENTER);
            colLabel.setForeground(TEXT_COLOR);
            colCoords.add(colLabel);
        }
        
        JPanel rowCoords = new JPanel(new GridLayout(8, 1));
        rowCoords.setBackground(BACKGROUND_COLOR);
        for (int row = 0; row < 8; row++) {
            JLabel rowLabel = new JLabel(String.valueOf(8 - row), SwingConstants.CENTER);
            rowLabel.setForeground(TEXT_COLOR);
            rowCoords.add(rowLabel);
        }
        
        boardWithCoords.add(boardPanel, BorderLayout.CENTER);
        boardWithCoords.add(colCoords, BorderLayout.SOUTH);
        boardWithCoords.add(rowCoords, BorderLayout.WEST);
        
        centerPanel.add(boardWithCoords);
        add(centerPanel, BorderLayout.CENTER);

        // Painel lateral para histórico e botão reiniciar
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout(0, 10));
        sidePanel.setPreferredSize(new Dimension(220, 600));
        sidePanel.setBackground(BACKGROUND_COLOR);
        sidePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        // Título do histórico
        JLabel historyTitle = new JLabel("HISTÓRICO DE JOGADAS", SwingConstants.CENTER);
        historyTitle.setForeground(TEXT_COLOR);
        historyTitle.setFont(CHESS_FONT);
        historyTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        sidePanel.add(historyTitle, BorderLayout.NORTH);

        // Área de histórico
        moveHistory = new JTextArea();
        moveHistory.setEditable(false);
        moveHistory.setFont(HISTORY_FONT);
        moveHistory.setBackground(PANEL_COLOR);
        moveHistory.setForeground(TEXT_COLOR);
        moveHistory.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(moveHistory);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        sidePanel.add(scrollPane, BorderLayout.CENTER);

        // Painel inferior com botões
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Status do jogo
        statusLabel = new JLabel("Jogo em andamento", SwingConstants.CENTER);
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        buttonPanel.add(statusLabel);
        
        // --- Botão para ativar/desativar IA ---
        JButton aiButton = new JButton("ATIVAR IA");
        aiButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        aiButton.setBackground(new Color(106, 140, 210));
        aiButton.setForeground(Color.WHITE);
        aiButton.setFocusPainted(false);
        aiButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Adicionar efeitos de hover ao botão de IA
        aiButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                aiButton.setBackground(new Color(126, 160, 230)); // Cor mais clara ao passar o mouse
                aiButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursor de mão
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                aiButton.setBackground(new Color(106, 140, 210)); // Retorna à cor original
                aiButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // Cursor padrão
            }
        });
        
        aiButton.addActionListener(e -> {
            game.toggleAIMode();
            aiButton.setText(game.isAIMode() ? "DESATIVAR IA" : "ATIVAR IA");
            updateBoardDisplay();
        });
        buttonPanel.add(aiButton);
        
        // --- Botão para reiniciar o jogo ---
        JButton restartButton = new JButton("REINICIAR JOGO");
        restartButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        restartButton.setBackground(new Color(181, 136, 99));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Adicionar efeitos de hover ao botão reiniciar
        restartButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                restartButton.setBackground(new Color(201, 156, 119)); // Cor mais clara ao passar o mouse
                restartButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursor de mão
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                restartButton.setBackground(new Color(181, 136, 99)); // Retorna à cor original
                restartButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // Cursor padrão
            }
        });
        
        restartButton.addActionListener(e -> restartGame());
        buttonPanel.add(restartButton);
        // --- FIM: MELHORIA DE FRONTEND - BOTÃO REINICIAR ---
        
        sidePanel.add(buttonPanel, BorderLayout.SOUTH);
        add(sidePanel, BorderLayout.EAST);

        setLocationRelativeTo(null);
        setVisible(true);
        
        // Inicializar cronômetro
        initializeTimer();
    }
    
    /**
     * Inicializa o objeto Timer para o cronômetro do jogo.
     * O timer é configurado para disparar a cada segundo e atualizar a exibição.
     */
    private void initializeTimer() {
        gameTimer = new Timer(1000, e -> updateTimerDisplay());
        gameTimer.setRepeats(true);
    }
    
    /**
     * Inicia o cronômetro do jogo.
     * Registra o tempo de início e começa a contagem, se o jogo não estiver rodando.
     */
    private void startTimer() {
        if (!isGameRunning) {
            gameStartTime = System.currentTimeMillis() - elapsedTime;
            isGameRunning = true;
            gameTimer.start();
        }
    }
    
    /**
     * Para o cronômetro do jogo.
     * Salva o tempo decorrido e interrompe o timer.
     */
    private void stopTimer() {
        if (isGameRunning) {
            isGameRunning = false;
            gameTimer.stop();
            elapsedTime = System.currentTimeMillis() - gameStartTime;
        }
    }
    
    /**
     * Reseta o cronômetro para zero.
     * Para o timer, zera o tempo decorrido e atualiza a exibição.
     */
    private void resetTimer() {
        stopTimer();
        elapsedTime = 0;
        updateTimerDisplay();
    }
    
    /**
     * Atualiza o texto do JLabel do cronômetro com o tempo decorrido formatado.
     */
    private void updateTimerDisplay() {
        if (isGameRunning) {
            elapsedTime = System.currentTimeMillis() - gameStartTime;
        }
        
        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        
        String timeText = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText(timeText);
    }

    private void loadPieceIcons() {
        pieceIcons = new HashMap<>();

        // Símbolos das peças (k=rei, q=rainha, r=torre, b=bispo, n=cavalo, p=peão)
        String[] symbols = {"k", "q", "r", "b", "n", "p"};
        String[] colors = {"w", "b"}; // w = branco, b = preto

        for (String color : colors) {
            for (String sym : symbols) {
                String key = color + sym;
                String resourcePath = "resources/" + key + ".png";
                
                try {
                    // Tenta carregar o recurso usando ClassLoader 
                    java.net.URL imageURL = getClass().getClassLoader().getResource(resourcePath);
                    ImageIcon icon;
                    
                    if (imageURL != null) {
                        icon = new ImageIcon(imageURL);
                        System.out.println("✓ Carregado via ClassLoader: " + resourcePath);
                    } else {
                        // Fallback: tenta carregar via caminho relativo 
                        String fallbackPath = "../resources/" + key + ".png";
                        icon = new ImageIcon(fallbackPath);
                        
                        if (icon.getIconWidth() <= 0) {
                            // Segundo fallback: tenta carregar via caminho absoluto
                            String absolutePath = System.getProperty("user.dir") + "/resources/" + key + ".png";
                            icon = new ImageIcon(absolutePath);
                            
                            if (icon.getIconWidth() <= 0) {
                                System.out.println("⚠️ Não foi possível carregar: " + key + ".png");
                                icon = new ImageIcon(); // Ícone vazio como fallback
                            } else {
                                System.out.println("✓ Carregado via caminho absoluto: " + absolutePath);
                            }
                        } else {
                            System.out.println("✓ Carregado via caminho relativo: " + fallbackPath);
                        }
                    }
                    
                    // Redimensiona a imagem se ela foi carregada com sucesso
                    if (icon.getIconWidth() > 0) {
                        Image scaled = icon.getImage().getScaledInstance(65, 65, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaled);
                    }
                    
                    pieceIcons.put(key, icon);
                    
                } catch (Exception e) {
                    System.out.println("❌ Erro ao carregar " + key + ".png: " + e.getMessage());
                    pieceIcons.put(key, new ImageIcon()); // Ícone vazio como fallback em caso de erro
                }
            }
        }
    }

    private void updateBoardDisplay() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Restaurar cores originais do tabuleiro
                boolean isWhite = (row + col) % 2 == 0;
                squares[row][col].setBackground(isWhite ? LIGHT_SQUARE : DARK_SQUARE);
                
                Piece piece = game.getBoard().getPieceAt(new Position(row, col));
                if (piece == null) {
                    squares[row][col].setIcon(null);
                } else {
                    String key = (piece.isWhite() ? "w" : "b") + piece.getSymbol();
                    squares[row][col].setIcon(pieceIcons.get(key));
                }
            }
        }
        // Atualizar turno
        String turnText = game.isWhiteTurn() ? "VEZ DAS BRANCAS" : "VEZ DAS PRETAS";
        turnLabel.setText(turnText);
        
        // Atualizar status
        if (game.isGameOver()) {
            String winner = game.isWhiteTurn() ? "PRETAS" : "BRANCAS";
            statusLabel.setText(winner + " VENCERAM!");
        } else {
            statusLabel.setText("Jogo em andamento");
        }
    }

    private void handleSquareClick(int row, int col) {
        // Bloquear movimentos se for a vez da IA
        if (game.isAIMode() && game.isAIWhite() == game.isWhiteTurn()) {
            return;
        }
        
        Position position = new Position(row, col);

        if (game.getSelectedPiece() == null) {
            // Primeira seleção: escolher uma peça
            game.selectPiece(position);

            // Destacar a peça selecionada
            if (game.getSelectedPiece() != null) {
                squares[row][col].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_SELECTED, 3));
                
                // Efeito de brilho pulsante para a peça selecionada
                Timer pulseTimer = new Timer(500, null);
                pulseTimer.addActionListener(pulseEvent -> {
                    if (game.getSelectedPiece() != null && 
                        game.getSelectedPiece().getPosition().getRow() == row &&
                        game.getSelectedPiece().getPosition().getColumn() == col) {
                        
                        Color currentBg = squares[row][col].getBackground();
                        boolean isLight = (row + col) % 2 == 0;
                        Color baseBg = isLight ? LIGHT_SQUARE : DARK_SQUARE;
                        
                        // Alternar entre cor base e cor destacada
                        if (currentBg.equals(baseBg)) {
                            squares[row][col].setBackground(new Color(
                                Math.min(255, baseBg.getRed() + 30),
                                Math.min(255, baseBg.getGreen() + 30),
                                Math.min(255, baseBg.getBlue() + 30)
                            ));
                        } else {
                            squares[row][col].setBackground(baseBg);
                        }
                    } else {
                        pulseTimer.stop();
                    }
                });
                pulseTimer.start();

                // Destacar movimentos possíveis
                for (Position pos : game.getSelectedPiece().getPossibleMoves()) {
                    boolean isCapture = game.getBoard().getPieceAt(pos) != null;
                    Color borderColor = isCapture ? HIGHLIGHT_CAPTURE : HIGHLIGHT_MOVE;
                    
                    squares[pos.getRow()][pos.getColumn()].setBorder(
                            BorderFactory.createLineBorder(borderColor, 4));
                    
                    // Criar indicadores visuais diferentes para movimentos e capturas
                    if (isCapture) {
                        // Para capturas: criar um X vermelho sobre a peça inimiga
                        JPanel capturePanel = new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                Graphics2D g2d = (Graphics2D) g;
                                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                g2d.setColor(HIGHLIGHT_CAPTURE);
                                g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                                
                                int size = Math.min(getWidth(), getHeight());
                                int margin = size / 4;
                                
                                // Desenhar X
                                g2d.drawLine(margin, margin, size - margin, size - margin);
                                g2d.drawLine(size - margin, margin, margin, size - margin);
                                
                                // Adicionar um círculo ao redor
                                g2d.setStroke(new BasicStroke(3));
                                g2d.drawOval(margin/2, margin/2, size - margin, size - margin);
                            }
                        };
                        capturePanel.setOpaque(false);
                        squares[pos.getRow()][pos.getColumn()].setLayout(new BorderLayout());
                        squares[pos.getRow()][pos.getColumn()].add(capturePanel, BorderLayout.CENTER);
                    } else {
                        // Para movimentos normais: círculo azul maior e mais visível
                        JPanel movePanel = new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                Graphics2D g2d = (Graphics2D) g;
                                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                
                                // Círculo preenchido
                                g2d.setColor(new Color(HIGHLIGHT_MOVE.getRed(), HIGHLIGHT_MOVE.getGreen(), 
                                                      HIGHLIGHT_MOVE.getBlue(), 200));
                                int circleSize = 24;
                                g2d.fillOval(getWidth()/2 - circleSize/2, getHeight()/2 - circleSize/2, 
                                           circleSize, circleSize);
                                
                                // Borda do círculo
                                g2d.setColor(HIGHLIGHT_MOVE);
                                g2d.setStroke(new BasicStroke(2));
                                g2d.drawOval(getWidth()/2 - circleSize/2, getHeight()/2 - circleSize/2, 
                                           circleSize, circleSize);
                            }
                        };
                        movePanel.setOpaque(false);
                        squares[pos.getRow()][pos.getColumn()].setLayout(new BorderLayout());
                        squares[pos.getRow()][pos.getColumn()].add(movePanel, BorderLayout.CENTER);
                    }
                }
            }
        } else {
            // Segunda seleção: mover a peça ou selecionar outra
            Piece selectedPiece = game.getSelectedPiece();

            // Limpar todos os destaques
            clearHighlights();

            if (selectedPiece.getPosition().equals(position)) {
                // Clicou na mesma peça, deselecionar
                game.selectPiece(null);
            } else if (game.getBoard().getPieceAt(position) != null &&
                    game.getBoard().getPieceAt(position).isWhite() == game.isWhiteTurn()) {
                // Clicou em outra peça do mesmo lado, selecionar essa nova peça
                game.selectPiece(position);
                squares[row][col].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_SELECTED, 3));
                
                // Efeito de brilho para a peça selecionada
                squares[row][col].setBackground(new Color(
                    squares[row][col].getBackground().getRed() + 15,
                    squares[row][col].getBackground().getGreen() + 15,
                    squares[row][col].getBackground().getBlue() + 15
                ));

                // Destacar movimentos possíveis da nova peça
                for (Position pos : game.getSelectedPiece().getPossibleMoves()) {
                    boolean isCapture = game.getBoard().getPieceAt(pos) != null;
                    Color borderColor = isCapture ? HIGHLIGHT_CAPTURE : HIGHLIGHT_MOVE;
                    
                    squares[pos.getRow()][pos.getColumn()].setBorder(
                            BorderFactory.createLineBorder(borderColor, 4));
                    
                    // Criar indicadores visuais diferentes para movimentos e capturas
                    if (isCapture) {
                        // Para capturas: criar um X vermelho sobre a peça inimiga
                        JPanel capturePanel = new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                Graphics2D g2d = (Graphics2D) g;
                                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                g2d.setColor(HIGHLIGHT_CAPTURE);
                                g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                                
                                int size = Math.min(getWidth(), getHeight());
                                int margin = size / 4;
                                
                                // Desenhar X
                                g2d.drawLine(margin, margin, size - margin, size - margin);
                                g2d.drawLine(size - margin, margin, margin, size - margin);
                                
                                // Adicionar um círculo ao redor
                                g2d.setStroke(new BasicStroke(3));
                                g2d.drawOval(margin/2, margin/2, size - margin, size - margin);
                            }
                        };
                        capturePanel.setOpaque(false);
                        squares[pos.getRow()][pos.getColumn()].setLayout(new BorderLayout());
                        squares[pos.getRow()][pos.getColumn()].add(capturePanel, BorderLayout.CENTER);
                    } else {
                        // Para movimentos normais: círculo azul maior e mais visível
                        JPanel movePanel = new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                Graphics2D g2d = (Graphics2D) g;
                                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                
                                // Círculo preenchido
                                g2d.setColor(new Color(HIGHLIGHT_MOVE.getRed(), HIGHLIGHT_MOVE.getGreen(), 
                                                      HIGHLIGHT_MOVE.getBlue(), 200));
                                int circleSize = 24;
                                g2d.fillOval(getWidth()/2 - circleSize/2, getHeight()/2 - circleSize/2, 
                                           circleSize, circleSize);
                                
                                // Borda do círculo
                                g2d.setColor(HIGHLIGHT_MOVE);
                                g2d.setStroke(new BasicStroke(2));
                                g2d.drawOval(getWidth()/2 - circleSize/2, getHeight()/2 - circleSize/2, 
                                           circleSize, circleSize);
                            }
                        };
                        movePanel.setOpaque(false);
                        squares[pos.getRow()][pos.getColumn()].setLayout(new BorderLayout());
                        squares[pos.getRow()][pos.getColumn()].add(movePanel, BorderLayout.CENTER);
                    }
                }
            }
    
            else {
                // Tentar mover a peça
                boolean moveSuccessful = game.movePiece(position);
                if (moveSuccessful) {
                    // Iniciar cronômetro no primeiro movimento
                    if (!isGameRunning && moveHistory.getText().isEmpty()) {
                        startTimer();
                    }
                    
                    // Efeito visual de movimento bem-sucedido
                    squares[row][col].setBackground(new Color(106, 168, 79, 100));
                    Timer flashTimer = new Timer(200, null);
                    flashTimer.addActionListener(flashEvent -> {
                        boolean isLight = (row + col) % 2 == 0;
                        squares[row][col].setBackground(isLight ? LIGHT_SQUARE : DARK_SQUARE);
                        flashTimer.stop();
                    });
                    flashTimer.start();
                    
                    updateBoardDisplay();

                    // Adicionar ao histórico com formatação melhorada
                    String pieceColor = selectedPiece.isWhite() ? "Branca" : "Preta";
                    String moveText = String.format("%s %s → %s\n", 
                                                   pieceColor, 
                                                   selectedPiece.getName(), 
                                                   getChessNotation(position));
                    moveHistory.append(moveText);
                    
                    // Rolar para o final do histórico
                    moveHistory.setCaretPosition(moveHistory.getDocument().getLength());

                    // Verificar fim de jogo
                    if (game.isGameOver()) {
                        stopTimer(); // Parar cronômetro quando o jogo termina
                        String winner = game.isWhiteTurn() ? "Pretas" : "Brancas";
                        
                        // Mostrar tempo final no diálogo
                        long seconds = elapsedTime / 1000;
                        long minutes = seconds / 60;
                        seconds = seconds % 60;
                        String finalTime = String.format("%02d:%02d", minutes, seconds);
                        
                        JOptionPane.showMessageDialog(this,
                                winner + " vencem!\nTempo de jogo: " + finalTime,
                                "Fim de Jogo",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
    }
    
    // Método auxiliar para converter posição em notação de xadrez (ex: e4)
    private String getChessNotation(Position position) {
        char column = (char) ('a' + position.getColumn());
        int row = 8 - position.getRow();
        return column + "" + row;
    }

    private void clearHighlights() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                squares[r][c].setBorder(null);
                
                // Remover os painéis de círculos
                squares[r][c].removeAll();
                
                // Restaurar cores originais
                boolean isWhite = (r + c) % 2 == 0;
                squares[r][c].setBackground(isWhite ? LIGHT_SQUARE : DARK_SQUARE);
                
                // Restaurar ícones
                Piece piece = game.getBoard().getPieceAt(new Position(r, c));
                if (piece != null) {
                    String key = (piece.isWhite() ? "w" : "b") + piece.getSymbol();
                    squares[r][c].setIcon(pieceIcons.get(key));
                }
            }
        }
    }

    private void restartGame() {
        game = new Game();
        moveHistory.setText("");
        resetTimer(); // Reiniciar cronômetro
        updateBoardDisplay();
        
        // Resetar o botão de IA
        Component[] components = ((JPanel) ((JPanel) getContentPane().getComponent(2)).getComponent(1)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton && ((JButton) comp).getText().contains("IA")) {
                ((JButton) comp).setText("ATIVAR IA");
                break;
            }
        }
    }
    
       private void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        updateThemeColors(); // Atualiza as variáveis de cor
        updateThemeDisplay(); // Aplica as novas cores aos componentes
        
        // Atualizar o texto e as cores do próprio botão de tema
        JButton themeToggleButton = (JButton) ((JPanel) getContentPane().getComponent(0)).getComponent(2); // Acessa o botão de tema
        themeToggleButton.setText(isDarkTheme ? "TEMA CLARO" : "TEMA ESCURO");
        themeToggleButton.setBackground(isDarkTheme ? new Color(70, 70, 70) : new Color(200, 200, 200));
        themeToggleButton.setForeground(isDarkTheme ? Color.WHITE : Color.BLACK);
    }
    
    private void updateThemeDisplay() {
        // Atualizar fundo principal da janela
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Atualizar todos os painéis recursivamente
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                updatePanelTheme((JPanel) comp);
            }
        }
        
        // Atualizar cores da área de histórico de jogadas
        moveHistory.setBackground(PANEL_COLOR);
        moveHistory.setForeground(TEXT_COLOR);
        
        // Atualizar cores dos rótulos de texto
        turnLabel.setForeground(TEXT_COLOR);
        statusLabel.setForeground(TEXT_COLOR);
        timerLabel.setForeground(TEXT_COLOR);
        
        // Forçar a atualização visual do tabuleiro para aplicar as novas cores das casas
        updateBoardDisplay();
        
        // Repintar a interface para garantir que todas as mudanças sejam visíveis
        repaint();
    }
    
    
    private void updatePanelTheme(JPanel panel) {
        panel.setBackground(BACKGROUND_COLOR);
        
        // Atualizar componentes filhos recursivamente
        Component[] children = panel.getComponents();
        for (Component child : children) {
            if (child instanceof JPanel) {
                updatePanelTheme((JPanel) child); // Chamada recursiva para sub-painéis
            } else if (child instanceof JLabel) {
                ((JLabel) child).setForeground(TEXT_COLOR);
            } else if (child instanceof JButton) {
                JButton button = (JButton) child;
                // O botão de tema tem tratamento especial para manter suas cores e texto
                if (!button.getText().contains("TEMA")) {
                    button.setBackground(isDarkTheme ? new Color(181, 136, 99) : new Color(139, 69, 19));
                    button.setForeground(Color.WHITE);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGUI::new);
    }
}
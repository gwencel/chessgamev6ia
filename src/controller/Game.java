package controller;

import javax.swing.JOptionPane;
import model.board.Board;
import model.board.Position;
import model.pieces.*;
import model.board.Move;
import controller.AIPlayer;

/**
 * A classe Game gerencia a lógica principal do jogo de xadrez, incluindo o estado do tabuleiro,
 * turnos, seleção e movimento de peças, e condições de fim de jogo (xeque, xeque-mate, empate).
 */
public class Game {
    private Board board;
    private boolean isWhiteTurn;
    private boolean isGameOver;
    private Piece selectedPiece;
    private AIPlayer aiPlayer;
    private boolean aiMode;
    private boolean isPlayerWhite;

    /**
     * Construtor da classe Game.
     * Inicializa um novo tabuleiro, define o turno inicial como branco e configura as peças.
     */
    public Game() {
        board = new Board();
        isWhiteTurn = true;
        isGameOver = false;
        setupPieces();
        this.aiMode = false;
        this.isPlayerWhite = true; // Por padrão, o jogador é branco
        this.aiPlayer = new AIPlayer(board, !isPlayerWhite); // A IA joga com a cor oposta ao jogador
    }

    /**
     * Configura as peças iniciais no tabuleiro para um jogo de xadrez padrão.
     * Coloca todas as peças brancas e pretas em suas posições iniciais.
     */
    private void setupPieces() {
        // Peças brancas
        board.placePiece(new Rook(board, true), new Position(7, 0));
        board.placePiece(new Knight(board, true), new Position(7, 1));
        board.placePiece(new Bishop(board, true), new Position(7, 2));
        board.placePiece(new Queen(board, true), new Position(7, 3));
        board.placePiece(new King(board, true), new Position(7, 4));
        board.placePiece(new Bishop(board, true), new Position(7, 5));
        board.placePiece(new Knight(board, true), new Position(7, 6));
        board.placePiece(new Rook(board, true), new Position(7, 7));
        for (int col = 0; col < 8; col++) {
            board.placePiece(new Pawn(board, true), new Position(6, col));
        }

        // Peças pretas
        board.placePiece(new Rook(board, false), new Position(0, 0));
        board.placePiece(new Knight(board, false), new Position(0, 1));
        board.placePiece(new Bishop(board, false), new Position(0, 2));
        board.placePiece(new Queen(board, false), new Position(0, 3));
        board.placePiece(new King(board, false), new Position(0, 4));
        board.placePiece(new Bishop(board, false), new Position(0, 5));
        board.placePiece(new Knight(board, false), new Position(0, 6));
        board.placePiece(new Rook(board, false), new Position(0, 7));
        for (int col = 0; col < 8; col++) {
            board.placePiece(new Pawn(board, false), new Position(1, col));
        }
    }

    public Board getBoard() {
        return board;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    /**
     * Seleciona uma peça no tabuleiro.
     * Uma peça só pode ser selecionada se pertencer ao jogador do turno atual.
     * @param position A posição da peça a ser selecionada.
     */
    public void selectPiece(Position position) {
        Piece piece = board.getPieceAt(position);
        if (piece != null && piece.isWhite() == isWhiteTurn) {
            selectedPiece = piece;
        }
    }

    /**
     * Limpa a seleção da peça atual.
     */
    public void clearSelection() {
        selectedPiece = null;
    }

    /**
     * Tenta mover a peça selecionada para a posição de destino.
     * Realiza validações de movimento, verifica xeque e lida com condições especiais como roque.
     * @param destination A posição para onde a peça selecionada deve ser movida.
     * @return true se o movimento foi bem-sucedido, false caso contrário.
     */
    public boolean movePiece(Position destination) {
        // Verifica se há uma peça selecionada e se o jogo não terminou
        if (selectedPiece == null || isGameOver) {
            return false;
        }

        // Verifica se o movimento é válido para a peça selecionada
        if (!selectedPiece.canMoveTo(destination)) {
            return false;
        }

        // Verifica se o movimento causa xeque no próprio rei
        if (moveCausesCheck(selectedPiece, destination)) {
            return false;
        }

        Position originalPosition = selectedPiece.getPosition();
        Piece capturedPiece = board.getPieceAt(destination);

        // --- Lógica para o Roque ---
        // Se a peça selecionada é um Rei e o movimento é de duas casas na horizontal, é um roque.
        if (selectedPiece instanceof King && Math.abs(originalPosition.getColumn() - destination.getColumn()) == 2) {
            int rookOriginalCol;
            int rookDestinationCol;
            // Determina qual torre está envolvida no roque (pequeno ou grande)
            if (destination.getColumn() > originalPosition.getColumn()) { // Roque pequeno (lado do rei)
                rookOriginalCol = 7;
                rookDestinationCol = originalPosition.getColumn() + 1;
            } else { // Roque grande (lado da rainha)
                rookOriginalCol = 0;
                rookDestinationCol = originalPosition.getColumn() - 1;
            }
            // Move a torre para sua nova posição no roque
            Piece rook = board.getPieceAt(new Position(originalPosition.getRow(), rookOriginalCol));
            board.removePiece(new Position(originalPosition.getRow(), rookOriginalCol));
            board.placePiece(rook, new Position(originalPosition.getRow(), rookDestinationCol));
            rook.setHasMoved(true); // Marca a torre como movida para evitar roques futuros
        }

        // Remove a peça da posição original e a coloca na posição de destino
        board.removePiece(originalPosition);
        board.placePiece(selectedPiece, destination);
        selectedPiece.setHasMoved(true); // Marca a peça como movida

        // Verifica e aplica condições especiais (ex: promoção de peão)
        checkSpecialConditions(selectedPiece, destination);

        // Troca o turno para o próximo jogador
        isWhiteTurn = !isWhiteTurn;

        if (aiMode && aiPlayer.isAIWhite() == isWhiteTurn) {
            makeAIMove();
        }

        // Verifica o status do jogo após o movimento (xeque, xeque-mate, empate)
        checkGameStatus();

        // Limpa a seleção da peça após o movimento
        selectedPiece = null;
        return true;
    }

    /**
     * Simula um movimento de peça para verificar se ele colocaria o próprio rei em xeque.
     * Isso é crucial para validar movimentos e evitar auto-xeque.
     * @param piece A peça que seria movida.
     * @param destination A posição para onde a peça seria movida.
     * @return true se o movimento simulado resultar em xeque para o próprio rei, false caso contrário.
     */
    private boolean moveCausesCheck(Piece piece, Position destination) {
        Position from = piece.getPosition();
        Piece captured = board.getPieceAt(destination);

        // Salva o estado original da peça e da peça capturada (se houver) para restaurar depois
        boolean originalHasMoved = piece.hasMoved();
        boolean capturedOriginalHasMoved = (captured != null) ? captured.hasMoved() : false;

        // Simula o movimento: remove a peça da origem e a coloca no destino
        board.removePiece(from);
        if (captured != null) board.removePiece(destination);
        board.placePiece(piece, destination);

        boolean inCheck;
        try {
            // Encontra a posição do rei do jogador atual
            Position myKing = findKingPosition(piece.isWhite());
            if (myKing == null) {
                // Se o rei não for encontrado (situação inesperada), assume que está em xeque
                return true;
            }
            // Verifica se o rei está sob ataque após o movimento simulado
            inCheck = isSquareAttacked(myKing, !piece.isWhite());
        } finally {
            // Restaura o tabuleiro ao estado original, desfazendo o movimento simulado
            board.removePiece(destination);
            board.placePiece(piece, from);
            piece.setHasMoved(originalHasMoved);
            if (captured != null) board.placePiece(captured, destination);
            if (captured != null) captured.setHasMoved(capturedOriginalHasMoved);
        }

        return inCheck;
    }

    /**
     * Encontra a posição do rei de uma determinada cor no tabuleiro.
     * @param whiteKing true para o rei branco, false para o rei preto.
     * @return A posição do rei, ou null se não for encontrado (situação inesperada).
     */
    private Position findKingPosition(boolean whiteKing) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Position p = new Position(r, c);
                Piece piece = board.getPieceAt(p);
                if (piece instanceof King && piece.isWhite() == whiteKing) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Verifica se uma determinada casa está sendo atacada por peças de uma cor específica.
     * @param square A posição da casa a ser verificada.
     * @param byWhite true para verificar ataques de peças brancas, false para peças pretas.
     * @return true se a casa estiver sendo atacada, false caso contrário.
     */
    private boolean isSquareAttacked(Position square, boolean byWhite) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Position from = new Position(r, c);
                Piece p = board.getPieceAt(from);
                // Ignora casas vazias ou peças da mesma cor
                if (p == null || p.isWhite() != byWhite) continue;
                // Ignora a própria casa se for a peça que está sendo verificada
                if (from.equals(square)) continue;
                // Verifica se a peça pode mover para a casa alvo (indicando um ataque)
                if (p.canMoveTo(square)) return true;
            }
        }
        return false;
    }

    /**
     * Verifica e aplica condições especiais após um movimento, como promoção de peão.
     * @param piece A peça que acabou de ser movida.
     * @param destination A posição final da peça.
     */
    private void checkSpecialConditions(Piece piece, Position destination) {
        // --- Promoção de Peão ---
        // Se um peão alcança a última fileira, ele é promovido.
        if (piece instanceof Pawn) {
            if ((piece.isWhite() && destination.getRow() == 0) || // Peão branco na última fileira
                (!piece.isWhite() && destination.getRow() == 7)) { // Peão preto na última fileira

                // Opções de promoção para o usuário
                String[] options = {"Rainha", "Torre", "Bispo", "Cavalo"};
                int choice = JOptionPane.showOptionDialog(null,
                        "Escolha uma peça para promoção:",
                        "Promoção de Peão",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);

                Piece newPiece; // A nova peça promovida
                switch (choice) {
                    case 1: // Torre
                        newPiece = new Rook(board, piece.isWhite());
                        break;
                    case 2: // Bispo
                        newPiece = new Bishop(board, piece.isWhite());
                        break;
                    case 3: // Cavalo
                        newPiece = new Knight(board, piece.isWhite());
                        break;
                    default: // Padrão: Rainha
                        newPiece = new Queen(board, piece.isWhite());
                        break;
                }

                // Remove o peão e coloca a nova peça promovida no tabuleiro
                board.removePiece(destination);
                board.placePiece(newPiece, destination);
            }
        }
    }

    /**
     * Verifica o status atual do jogo: xeque, xeque-mate ou empate.
     * Atualiza a flag `isGameOver` e exibe mensagens ao usuário conforme o resultado.
     */
    private void checkGameStatus() {
        Position kingPos = findKingPosition(isWhiteTurn);
        if (kingPos == null) return; // Rei não encontrado, situação inesperada

        boolean inCheck = isSquareAttacked(kingPos, !isWhiteTurn); // Verifica se o rei do turno atual está em xeque

        boolean hasMove = false;
        // Itera sobre todas as peças do jogador do turno atual para verificar se há movimentos legais
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPieceAt(new Position(r, c));
                if (p != null && p.isWhite() == isWhiteTurn) {
                    for (Position move : p.getPossibleMoves()) {
                        // Se encontrar um movimento que não deixa o próprio rei em xeque, há movimentos possíveis
                        if (!moveCausesCheck(p, move)) {
                            hasMove = true;
                            break;
                        }
                    }
                    if (hasMove) break;
                }
            }
            if (hasMove) break;
        }

        // --- Condições de Fim de Jogo ---
        if (inCheck && !hasMove) {
            isGameOver = true;
            JOptionPane.showMessageDialog(null,
                    (isWhiteTurn ? "Brancas" : "Pretas") + " estão em XEQUE-MATE!");
        } else if (!inCheck && !hasMove) {
            isGameOver = true;
            JOptionPane.showMessageDialog(null, "EMPATE por afogamento!");
        } else if (inCheck) {
            JOptionPane.showMessageDialog(null,
                    (isWhiteTurn ? "Brancas" : "Pretas") + " estão em XEQUE!");
        }
    }

    public void toggleAIMode() {
        this.aiMode = !this.aiMode;
        if (this.aiMode) {
            JOptionPane.showMessageDialog(null, "Modo IA ativado! A IA joga como " + (aiPlayer.isAIWhite() ? "Brancas" : "Pretas") + ".");
            if (aiPlayer.isAIWhite() == isWhiteTurn) {
                makeAIMove();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Modo IA desativado!");
        }
    }

    public boolean isAIMode() {
        return aiMode;
    }

    public boolean isAIWhite() {
        return aiPlayer.isAIWhite();
    }

    public void makeAIMove() {
        if (isGameOver || !aiMode || aiPlayer.isAIWhite() != isWhiteTurn) {
            return;
        }

        Move aiMove = aiPlayer.makeMove();
        if (aiMove != null) {
            selectedPiece = board.getPieceAt(aiMove.getFrom());
            movePiece(aiMove.getTo());
            clearSelection();
        }
    }
}


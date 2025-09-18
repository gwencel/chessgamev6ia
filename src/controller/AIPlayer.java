package controller;

import model.board.Board;
import model.board.Move;
import model.board.Position;
import model.pieces.Piece;
import model.pieces.King;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIPlayer {
    private Board board;
    private boolean isAIWhite;
    private Random random;

    public AIPlayer(Board board, boolean isAIWhite) {
        this.board = board;
        this.isAIWhite = isAIWhite;
        this.random = new Random();
    }

    public boolean isAIWhite() {
        return isAIWhite;
    }

    public Move makeMove() {
        List<Move> possibleMoves = new ArrayList<>();

        // Coleta todos os movimentos possíveis para as peças da IA
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Position from = new Position(r, c);
                Piece piece = board.getPieceAt(from);

                if (piece != null && piece.isWhite() == isAIWhite) {
                    for (Position to : piece.getPossibleMoves()) {
                        // Verifica se o movimento é válido e não coloca o próprio rei em xeque
                        if (piece.canMoveTo(to) && !simulatedMoveCausesCheck(piece, from, to)) {
                            possibleMoves.add(new Move(from, to));
                        }
                    }
                }
            }
        }

        if (possibleMoves.isEmpty()) {
            return null; // Sem movimentos possíveis
        }

        // Por enquanto, a IA escolhe um movimento aleatório
        return possibleMoves.get(random.nextInt(possibleMoves.size()));
    }

    // Simula um movimento para verificar se ele causa xeque no próprio rei
    private boolean simulatedMoveCausesCheck(Piece piece, Position from, Position to) {
        Piece captured = board.getPieceAt(to);

        boolean originalHasMoved = piece.hasMoved();
        boolean capturedOriginalHasMoved = (captured != null) ? captured.hasMoved() : false;

        // Faz o movimento simulado
        board.removePiece(from);
        if (captured != null) board.removePiece(to);
        board.placePiece(piece, to);

        boolean inCheck;
        try {
            Position myKing = findKingPosition(isAIWhite);
            if (myKing == null) {
                return true; // Rei não encontrado, algo está errado
            }
            inCheck = isSquareAttacked(myKing, !isAIWhite);
        } finally {
            // Desfaz o movimento simulado
            board.removePiece(to);
            board.placePiece(piece, from);
            piece.setHasMoved(originalHasMoved);
            if (captured != null) board.placePiece(captured, to);
            if (captured != null) captured.setHasMoved(capturedOriginalHasMoved);
        }
        return inCheck;
    }

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

    private boolean isSquareAttacked(Position square, boolean byWhite) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Position from = new Position(r, c);
                Piece p = board.getPieceAt(from);
                if (p == null || p.isWhite() != byWhite) continue;
                if (from.equals(square)) continue;
                if (p.canMoveTo(square)) return true;
            }
        }
        return false;
    }
}



package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MoveCalculatorPawn extends MoveCalculator {
    public MoveCalculatorPawn(ChessBoard board) {
        super(board);
    }

    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessPosition startPos) {
        Collection<ChessMove> potentialMoves = new ArrayList<>();
        int advanceY = (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1);
        int promotionY = (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 8: 1);
        int firstMoveY = (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 2: 7);

        // if first move and nothing in front of it, jump advanceY twice
        if (startPos.getRow() == firstMoveY && board.getPiece(new ChessPosition(startPos.getRow() + advanceY, startPos.getColumn())) == null &&
                board.getPiece(new ChessPosition(startPos.getRow() + 2 * advanceY, startPos.getColumn())) == null) {

            potentialMoves.add(new ChessMove(startPos,
                    new ChessPosition(startPos.getRow() + 2 * advanceY, startPos.getColumn()),
                    null));
        }

        // normal move forward
        if (!outOfBounds(startPos.getRow() + advanceY, startPos.getColumn()) &&
                board.getPiece(new ChessPosition(startPos.getRow() + advanceY, startPos.getColumn())) == null) {
            if (startPos.getRow() + advanceY == promotionY) {
                potentialMoves.addAll(addPromoteMoves(startPos, new ChessPosition(startPos.getRow() + advanceY, startPos.getColumn())));
            }
            else {
                potentialMoves.add(new ChessMove(startPos, new ChessPosition(startPos.getRow() + advanceY, startPos.getColumn()), null));
            }
        }

        // left Diagonal Capture
        if (!outOfBounds(startPos.getRow() + advanceY, startPos.getColumn() - 1) &&
                board.getPiece(new ChessPosition(startPos.getRow() + advanceY, startPos.getColumn() - 1)) != null &&
                board.getPiece(new ChessPosition(startPos.getRow() + advanceY, startPos.getColumn() - 1)).getTeamColor() != piece.getTeamColor()) {
            if (startPos.getRow() + advanceY == promotionY) {
                potentialMoves.addAll(addPromoteMoves(startPos, new ChessPosition(startPos.getRow() + advanceY, startPos.getColumn() - 1)));
            }
            else {
                potentialMoves.add(new ChessMove(startPos, new ChessPosition(startPos.getRow() + advanceY, startPos.getColumn() - 1), null));
            }

        }

        // right diagonal capture
        if (!outOfBounds(startPos.getRow() + advanceY, startPos.getColumn() + 1) &&
                board.getPiece(new ChessPosition(startPos.getRow() + advanceY, startPos.getColumn() + 1)) != null &&
                board.getPiece(new ChessPosition(startPos.getRow() + advanceY, startPos.getColumn() + 1)).getTeamColor() != piece.getTeamColor()) {
            if (startPos.getRow() + advanceY == promotionY) {
                potentialMoves.addAll(addPromoteMoves(startPos, new ChessPosition(startPos.getRow() + advanceY, startPos.getColumn() + 1)));
            }
            else {
                potentialMoves.add(new ChessMove(startPos, new ChessPosition(startPos.getRow() + advanceY, startPos.getColumn() + 1), null));
            }
        }
        return potentialMoves;
    }

    // helper to add all the promotion moves
    private Collection<ChessMove> addPromoteMoves(ChessPosition startPos, ChessPosition position) {
        Collection<ChessMove> promoteMoves = new ArrayList<>();
        promoteMoves.add(new ChessMove(startPos, position, ChessPiece.PieceType.QUEEN));
        promoteMoves.add(new ChessMove(startPos, position, ChessPiece.PieceType.ROOK));
        promoteMoves.add(new ChessMove(startPos, position, ChessPiece.PieceType.BISHOP));
        promoteMoves.add(new ChessMove(startPos, position, ChessPiece.PieceType.KNIGHT));
        return promoteMoves;
    }
}

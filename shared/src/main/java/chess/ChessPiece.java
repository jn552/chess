package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        // logic for bishops
        if (piece.getPieceType() == PieceType.ROOK) {
            MoveCalculatorRook rook_calc = new MoveCalculatorRook(board);
            return rook_calc.get_moves(piece, myPosition);
        }

        // Bishop logic
        if (piece.getPieceType() == PieceType.BISHOP){
            MoveCalculatorBishop bishop_calc = new MoveCalculatorBishop(board);
            return bishop_calc.get_moves(piece, myPosition);
        }

        //QUeen logic
        if (piece.getPieceType() == PieceType.QUEEN) {
            MoveCalculatorQueen queen_calc = new MoveCalculatorQueen(board);
            return queen_calc.get_moves(piece, myPosition);
        }
        return List.of();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type &&
                pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 71 * hash + type.hashCode();
        hash = 71 * hash + pieceColor.hashCode();
        return hash;
    }
}

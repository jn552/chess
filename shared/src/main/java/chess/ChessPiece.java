package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    public boolean moved = false;

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
            MoveCalculatorRook rookCalc = new MoveCalculatorRook(board);
            return rookCalc.getMoves(piece, myPosition);
        }

        // Bishop logic
        else if (piece.getPieceType() == PieceType.BISHOP){
            MoveCalculatorBishop bishopCalc = new MoveCalculatorBishop(board);
            return bishopCalc.getMoves(piece, myPosition);
        }

        //Queen logic
        else if (piece.getPieceType() == PieceType.QUEEN) {
            MoveCalculatorQueen queenCalc = new MoveCalculatorQueen(board);
            return queenCalc.getMoves(piece, myPosition);
        }

        // King logic
        else if (piece.getPieceType() == PieceType.KING) {
            MoveCalculatorKing kingCalc = new MoveCalculatorKing(board);
            return kingCalc.getMoves(piece, myPosition);
        }

        // Pawn logic
        else if (piece.getPieceType() == PieceType.PAWN) {
            MoveCalculatorPawn pawnCalc = new MoveCalculatorPawn(board);
            return pawnCalc.getMoves(piece, myPosition);
        }

        // only piece left is knight so knight logic
        else {
            MoveCalculatorKnight knightCalc = new MoveCalculatorKnight(board);
            return knightCalc.getMoves(piece, myPosition);
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
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

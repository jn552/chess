package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    public ChessPiece[][] squares = new ChessPiece[8][8];  //creates empty 8x8 board but index starts at 0, so 0-7

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // getting pawns in
        for (int i = 0;  i <=7; i++) {
            squares[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            squares[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }

        ChessGame.TeamColor[] colors = {ChessGame.TeamColor.BLACK, ChessGame.TeamColor.WHITE};
        for (ChessGame.TeamColor color : colors) {
            int row = (color == ChessGame.TeamColor.WHITE ? 0 : 7);
            squares[row][0] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
            squares[row][1] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            squares[row][2] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            squares[row][3] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
            squares[row][4] = new ChessPiece(color, ChessPiece.PieceType.KING);
            squares[row][5] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            squares[row][6] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            squares[row][7] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(this.squares, that.squares);
    }

    @Override
    public int hashCode() {
        int hash = 31;
        hash = 71 * hash + Arrays.deepHashCode(squares);
        return hash;
    }

    @Override
    public ChessBoard clone() {
        ChessBoard clone = new ChessBoard();
        clone.squares = new ChessPiece[8][8];

        //Making a deep copy
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.squares[i][j] != null) {
                    ChessPiece piece = this.squares[i][j];
                    clone.squares[i][j] = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
                }
            }
        }

        return clone;
    }
}

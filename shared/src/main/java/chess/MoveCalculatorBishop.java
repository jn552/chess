package chess;

import java.util.Collection;

public class MoveCalculatorBishop extends MoveCalculator {
    public MoveCalculatorBishop (ChessBoard board) {
        super(board);
    }

    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessPosition startPos){
        int[][] bishopDirections = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        return slideHelper(piece, startPos, bishopDirections);
    }
}

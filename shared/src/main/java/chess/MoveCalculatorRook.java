package chess;

import java.util.Collection;

public class MoveCalculatorRook extends MoveCalculator {
    public MoveCalculatorRook(ChessBoard board){
        super(board);
    }
    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessPosition startPos){
        int[][] rookDirections = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        return slideHelper(piece, startPos, rookDirections);
    }
}

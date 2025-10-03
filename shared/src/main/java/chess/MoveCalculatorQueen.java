package chess;

import java.util.Collection;

public class MoveCalculatorQueen extends MoveCalculator {
    public MoveCalculatorQueen(ChessBoard board) {
        super(board);
    }

    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessPosition startPos){
        int[][] queenDirections = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        return slideHelper(piece, startPos, queenDirections);
    }

}

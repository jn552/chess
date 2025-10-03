package chess;

import java.util.Collection;

public class MoveCalculatorKing extends MoveCalculator {
    public MoveCalculatorKing(ChessBoard board) {
        super(board);
    }

    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessPosition startPos) {
        int[][] jumps = {{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
        return jumpHelper(piece, startPos, jumps);
    }
}

package chess;

import java.util.Collection;

public class MoveCalculatorKnight extends MoveCalculator {
    public MoveCalculatorKnight(ChessBoard board) {
        super(board);
    }

    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessPosition startPos) {
        int[][] jumps = {{-1, 2}, {1, 2}, {-2, 1}, {2, 1}, {-2, -1}, {2, -1}, {-1, -2}, {1, -2}};
        return jumpHelper(piece, startPos, jumps);
    }
}


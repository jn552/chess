package chess;

import java.util.Collection;

public class MoveCalculatorKnight extends MoveCalculator {
    public MoveCalculatorKnight(ChessBoard board) {
        super(board);
    }

    @Override
    public Collection<ChessMove> get_moves(ChessPiece piece, ChessPosition start_pos) {
        int[][] jumps = {{-1, 2}, {1, 2}, {-2, 1}, {2, 1}, {-2, -1}, {2, -1}, {-1, -2}, {1, -2}};
        return jump_helper(piece, start_pos, jumps);
    }
}


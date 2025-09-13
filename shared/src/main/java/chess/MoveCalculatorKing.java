package chess;

import java.util.Collection;

public class MoveCalculatorKing extends MoveCalculator {
    public MoveCalculatorKing(ChessBoard board) {
        super(board);
    }

    @Override
    public Collection<ChessMove> get_moves(ChessPiece piece, ChessPosition start_pos) {
        int[][] jumps = {{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
        return jump_helper(piece, start_pos, jumps);
    }
}

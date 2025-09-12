package chess;

import java.util.Collection;

public class MoveCalculatorBishop extends MoveCalculator {
    public MoveCalculatorBishop (ChessBoard board) {
        super(board);
    }

    @Override
    public Collection<ChessMove> get_moves(ChessPiece piece, ChessPosition start_pos){
        int[][] bishop_directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        return slide_helper(piece, start_pos, bishop_directions);
    }
}

package chess;

import java.util.Collection;

public class MoveCalculatorQueen extends MoveCalculator {
    public MoveCalculatorQueen(ChessBoard board) {
        super(board);
    }

    @Override
    public Collection<ChessMove> get_moves(ChessPiece piece, ChessPosition start_pos){
        int[][] queen_directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        return slide_helper(piece, start_pos, queen_directions);
    }

}

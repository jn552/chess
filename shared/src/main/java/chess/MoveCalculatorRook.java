package chess;

import java.util.Collection;

public class MoveCalculatorRook extends MoveCalculator {
    public MoveCalculatorRook(ChessBoard board){
        super(board);
    }
    @Override
    public Collection<ChessMove> get_moves(ChessPiece piece, ChessPosition start_pos){
        int[][] rook_directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        return slide_helper(piece, start_pos, rook_directions);
    }
}

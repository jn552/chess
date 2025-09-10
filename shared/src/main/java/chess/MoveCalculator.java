package chess;

import java.util.ArrayList;
import java.util.List;

public abstract class MoveCalculator {
    protected ChessBoard board;

    public MoveCalculator(ChessBoard board) {
        this.board = board;
    }

    // my abstract function for each subclass to implement
    public abstract List<ChessPosition> get_moves(ChessPiece piece);

    // helper for sliding queens, rooks, bishops
    public List<ChessPosition> slide_helper(ChessPiece piece, ChessPosition start_pos, int[][] direction_vects) {
        List<ChessPosition> possible_moves = new ArrayList<>();

        // loop through all direction vectors and append scalar multiples until obstacle
        for (int[] vect : direction_vects) {

            int x_comp = vect[0];
            int y_comp = vect[1];
            int scalar = 1;

            while (true) {

                int x_tracker = start_pos.getRow() + scalar * x_comp - 1;  // tracking where we slide to
                int y_tracker = start_pos.getColumn() + scalar * y_comp - 1;
                ChessPosition potential_move = new ChessPosition(x_tracker, y_tracker);

                // break if blocked or out of bounds and move onto next vector direction
                if (x_tracker > 7 || y_tracker > 7) {
                    break;
                }

                if (board.getPiece(potential_move) == null) {
                    possible_moves.add(potential_move);
                }

                else {
                    if (board.getPiece(potential_move).getTeamColor() != piece.getTeamColor()) {
                        possible_moves.add(potential_move);
                    }
                    break;
                }

            }
        }
        return possible_moves;

    }

}

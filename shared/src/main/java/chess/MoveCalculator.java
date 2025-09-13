package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class MoveCalculator {
    protected ChessBoard board;

    public MoveCalculator(ChessBoard board) {
        this.board = board;
    }

    // my abstract function for each subclass to implement
    public abstract Collection<ChessMove> get_moves(ChessPiece piece, ChessPosition start_pos);

    // helper for checking out of bounds
    public Boolean out_of_bounds(int x, int y){
        return x < 1 || x > 8 || y < 1 || y > 8;
    }

    // helper for jumping pieces
    public Collection<ChessMove> jump_helper(ChessPiece piece, ChessPosition start_pos, int[][] jumps) {
        Collection<ChessMove> possible_moves = new ArrayList<>();

        // loop through jump locations
        for (int[] jump : jumps) {
            int x_tracker = start_pos.getRow() + jump[0];
            int y_tracker = start_pos.getColumn() + jump[1];
            if (out_of_bounds(x_tracker, y_tracker)) continue;
            ChessPosition potential_pos = new ChessPosition(x_tracker, y_tracker);
            if (board.getPiece(potential_pos) == null) {
                possible_moves.add(new ChessMove(start_pos, potential_pos, null));
            }
            else {
                if (board.getPiece(potential_pos).getTeamColor() != piece.getTeamColor()) {
                    possible_moves.add(new ChessMove(start_pos, potential_pos, null));
                }
            }
        }
        return possible_moves;
    }

    // helper for sliding queens, rooks, bishops
    public Collection<ChessMove> slide_helper(ChessPiece piece, ChessPosition start_pos, int[][] direction_vects) {
        Collection<ChessMove> possible_moves = new ArrayList<>();

        // loop through all direction vectors and append scalar multiples until obstacle
        for (int[] vect : direction_vects) {
            int x_comp = vect[0];
            int y_comp = vect[1];
            int scalar = 1;

            while (true) {
                int x_tracker = start_pos.getRow() + scalar * x_comp;  // tracking where we slide to
                int y_tracker = start_pos.getColumn() + scalar * y_comp;

                // break if blocked or out of bounds and move onto next vector direction
                if (out_of_bounds(x_tracker, y_tracker)) {
                    break;
                }

                ChessPosition potential_pos = new ChessPosition(x_tracker, y_tracker);
                ChessMove potential_move = new ChessMove(start_pos, potential_pos, null);

                if (board.getPiece(potential_pos) == null) {
                    possible_moves.add(potential_move);
                }
                else {
                    if (board.getPiece(potential_pos).getTeamColor() != piece.getTeamColor()) {
                        possible_moves.add(potential_move);
                    }
                    break;
                }
                scalar += 1;
            }
        }
        return possible_moves;

    }

}

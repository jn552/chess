package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MoveCalclatorPawn extends MoveCalculator {
    public MoveCalclatorPawn(ChessBoard board) {
        super(board);
    }

    @Override
    public Collection<ChessMove> get_moves(ChessPiece piece, ChessPosition start_pos) {
        int[][] jumps = {};
        Collection<ChessMove> potential_moves = new ArrayList<>();
        // Work flow
        // if first move, maybe check if its in start position, since pawns can't move backwards, they can only be at starting pos once
        // check if there are ENEMIES on their diagonals if there are, add those possible moves
        // check if can move forward

        // first move check: if in start psoition (differnet for blacks and whites)
        // eating check: if there are enemies on the front diagonals no need for bounds check
        // forward check: if space infront is null move, maybe do bounds check just in case not sure how promotions work
        return potential_moves;
    }
}

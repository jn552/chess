package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MoveCalclatorPawn extends MoveCalculator {
    public MoveCalclatorPawn(ChessBoard board) {
        super(board);
    }

    @Override
    public Collection<ChessMove> get_moves(ChessPiece piece, ChessPosition start_pos) {
        Collection<ChessMove> potential_moves = new ArrayList<>();
        // Work flow
        // if first move, maybe check if its in start position, since pawns can't move backwards, they can only be at starting pos once
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (start_pos.getRow() == 2) potential_moves.add(new ChessMove(start_pos, new ChessPosition(4, start_pos.getColumn()), null));

            if (!out_of_bounds(start_pos.getRow() + 1, start_pos.getColumn()) &&
                    board.getPiece(new ChessPosition(start_pos.getRow() + 1, start_pos.getColumn())) == null) {
                potential_moves.add(new ChessMove(start_pos, new ChessPosition(start_pos.getRow() + 1, start_pos.getColumn()), null));
            }

        }
        else {

        }
        // check if there are ENEMIES on their diagonals if there are, add those possible moves
        // check if can move forward

        // first move check: if in start psoition (differnet for blacks and whites)
        // eating check: if there are enemies on the front diagonals no need for bounds check
        // forward check: if space infront is null move, maybe do bounds check just in case not sure how promotions work
        return potential_moves;
    }
}

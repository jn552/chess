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
        int advance_y = (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1);
        int promotion_y = (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 8: 1);
        int first_move_y = (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 2: 7);

        // if first move and nothing in front of it, jump advance_y twice
        if (start_pos.getRow() == first_move_y && board.getPiece(new ChessPosition(start_pos.getRow() + advance_y, start_pos.getColumn())) == null &&
                board.getPiece(new ChessPosition(start_pos.getRow() + 2 * advance_y, start_pos.getColumn())) == null) {

            potential_moves.add(new ChessMove(start_pos,
                    new ChessPosition(start_pos.getRow() + 2 * advance_y, start_pos.getColumn()),
                    null));
        }

        // normal move forward
        if (!out_of_bounds(start_pos.getRow() + advance_y, start_pos.getColumn()) &&
                board.getPiece(new ChessPosition(start_pos.getRow() + advance_y, start_pos.getColumn())) == null) {
            if (start_pos.getRow() + advance_y == promotion_y) {
                potential_moves.addAll(add_promote_moves(start_pos, new ChessPosition(start_pos.getRow() + advance_y, start_pos.getColumn())));
            }
            else {
                potential_moves.add(new ChessMove(start_pos, new ChessPosition(start_pos.getRow() + advance_y, start_pos.getColumn()), null));
            }
        }

        // left Diagonal Capture
        if (!out_of_bounds(start_pos.getRow() + advance_y, start_pos.getColumn() - 1) &&
                board.getPiece(new ChessPosition(start_pos.getRow() + advance_y, start_pos.getColumn() - 1)) != null &&
                board.getPiece(new ChessPosition(start_pos.getRow() + advance_y, start_pos.getColumn() - 1)).getTeamColor() != piece.getTeamColor()) {
            if (start_pos.getRow() + advance_y == promotion_y) {
                potential_moves.addAll(add_promote_moves(start_pos, new ChessPosition(start_pos.getRow() + advance_y, start_pos.getColumn() - 1)));
            }
            else {
                potential_moves.add(new ChessMove(start_pos, new ChessPosition(start_pos.getRow() + advance_y, start_pos.getColumn() - 1), null));
            }

        }

        // right diagonal capture
        if (!out_of_bounds(start_pos.getRow() + advance_y, start_pos.getColumn() + 1) &&
                board.getPiece(new ChessPosition(start_pos.getRow() + advance_y, start_pos.getColumn() + 1)) != null &&
                board.getPiece(new ChessPosition(start_pos.getRow() + advance_y, start_pos.getColumn() + 1)).getTeamColor() != piece.getTeamColor()) {
            if (start_pos.getRow() + advance_y == promotion_y) {
                potential_moves.addAll(add_promote_moves(start_pos, new ChessPosition(start_pos.getRow() + advance_y, start_pos.getColumn() + 1)));
            }
            else {
                potential_moves.add(new ChessMove(start_pos, new ChessPosition(start_pos.getRow() + advance_y, start_pos.getColumn() + 1), null));
            }
        }
        return potential_moves;
    }

    // helper to add all the promotion moves
    private Collection<ChessMove> add_promote_moves(ChessPosition start_pos, ChessPosition position) {
        Collection<ChessMove> promote_moves = new ArrayList<>();
        promote_moves.add(new ChessMove(start_pos, position, ChessPiece.PieceType.QUEEN));
        promote_moves.add(new ChessMove(start_pos, position, ChessPiece.PieceType.ROOK));
        promote_moves.add(new ChessMove(start_pos, position, ChessPiece.PieceType.BISHOP));
        promote_moves.add(new ChessMove(start_pos, position, ChessPiece.PieceType.KNIGHT));
        return promote_moves;
    }
}

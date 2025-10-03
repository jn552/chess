package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class MoveCalculator {
    protected ChessBoard board;

    public MoveCalculator(ChessBoard board) {
        this.board = board;
    }

    // my abstract function for each subclass to implement
    public abstract Collection<ChessMove> getMoves(ChessPiece piece, ChessPosition startPos);

    // helper for checking out of bounds
    public Boolean outOfBounds(int x, int y){
        return x < 1 || x > 8 || y < 1 || y > 8;
    }

    // helper for jumping pieces
    public Collection<ChessMove> jumpHelper(ChessPiece piece, ChessPosition startPos, int[][] jumps) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        // loop through jump locations
        for (int[] jump : jumps) {
            int xTracker = startPos.getRow() + jump[0];
            int yTracker = startPos.getColumn() + jump[1];
            if (outOfBounds(xTracker, yTracker)) {continue;}
            ChessPosition potentialPos = new ChessPosition(xTracker, yTracker);
            if (board.getPiece(potentialPos) == null) {
                possibleMoves.add(new ChessMove(startPos, potentialPos, null));
            }
            else {
                if (board.getPiece(potentialPos).getTeamColor() != piece.getTeamColor()) {
                    possibleMoves.add(new ChessMove(startPos, potentialPos, null));
                }
            }
        }
        return possibleMoves;
    }

    // helper for sliding queens, rooks, bishops
    public Collection<ChessMove> slideHelper(ChessPiece piece, ChessPosition startPos, int[][] directionVects) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        // loop through all direction vectors and append scalar multiples until obstacle
        for (int[] vect : directionVects) {
            int xComp = vect[0];
            int yComp = vect[1];
            int scalar = 1;

            while (true) {
                int xTracker = startPos.getRow() + scalar * xComp;  // tracking where we slide to
                int yTracker = startPos.getColumn() + scalar * yComp;

                // break if blocked or out of bounds and move onto next vector direction
                if (outOfBounds(xTracker, yTracker)) {
                    break;
                }

                ChessPosition potentialPos = new ChessPosition(xTracker, yTracker);
                ChessMove potentialMove = new ChessMove(startPos, potentialPos, null);

                if (board.getPiece(potentialPos) == null) {
                    possibleMoves.add(potentialMove);
                }
                else {
                    if (board.getPiece(potentialPos).getTeamColor() != piece.getTeamColor()) {
                        possibleMoves.add(potentialMove);
                    }
                    break;
                }
                scalar += 1;
            }
        }
        return possibleMoves;

    }

}

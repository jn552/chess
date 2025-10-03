package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard gameBoard;
    TeamColor teamTurn = TeamColor.WHITE;

    public ChessGame() {
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // init new list to append valid moves to
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = gameBoard.getPiece(startPosition);  // get piece
        if (piece == null) return null;  // return null if no piece

        // get raw moves
        Collection<ChessMove> potential_moves = piece.pieceMoves(gameBoard, startPosition);

        // remove invalid moves
        for (ChessMove move : potential_moves) {
            if (isValid(move, gameBoard)) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    public boolean isValid(ChessMove move, ChessBoard board) {
        // makes temporary chess game and executes move
        ChessBoard potentialBoard = gameBoard.clone();
        ChessGame tempGame = new ChessGame();
        tempGame.setBoard(potentialBoard);
        ChessPiece piece = tempGame.gameBoard.getPiece(move.getStartPosition());

        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(teamTurn, move.getPromotionPiece());
        }
        tempGame.gameBoard.squares[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1] = piece;
        tempGame.gameBoard.squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = null;

        // throw exception if move leaves you in check
        return !tempGame.isInCheck(piece.getTeamColor());
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition endPos = move.getEndPosition();
        ChessPosition startPos = move.getStartPosition();
        int startRow = startPos.getRow();
        int startCol = startPos.getColumn();
        ChessPiece piece = gameBoard.getPiece(startPos);
        int endRow = endPos.getRow();
        int endCol = endPos.getColumn();

        // if move has no piece on it
        if (piece == null) throw new InvalidMoveException();

        // if not your turn
        if (piece.getTeamColor() != teamTurn) throw new InvalidMoveException();

        // if not a valid move for the specific piece it is
        Collection<ChessMove> validMoves = piece.pieceMoves(gameBoard, startPos);
        if (!validMoves.contains(move)) throw new InvalidMoveException();

        // check to see if move leaves you in check, if it does kill the move
        if (!isValid(move, gameBoard)) throw new InvalidMoveException();

        // by now, should be safe to make move on the real board
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(teamTurn, move.getPromotionPiece());
        }
        gameBoard.squares[startRow - 1][startCol - 1].moved = true;
        gameBoard.squares[endRow - 1][endCol - 1] = piece;
        gameBoard.squares[startRow - 1][startCol - 1] = null;

        TeamColor nextTurnColor = (teamTurn == TeamColor.WHITE ? TeamColor.BLACK: TeamColor.WHITE);
        setTeamTurn(nextTurnColor);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        // finding king by looping through board
        ChessPosition kingPos = null;  // init
        outerLoop:
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = gameBoard.squares[i][j];
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    kingPos = new ChessPosition(i + 1, j + 1);
                    break outerLoop;
                }
            }
        }

        // checking all opponent's possible moves
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = gameBoard.squares[i][j];

                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> potentialMoves = piece.pieceMoves(gameBoard, new ChessPosition(i + 1, j + 1));
                    for (ChessMove move: potentialMoves) {
                        if (move.getEndPosition().equals(kingPos)) return true;
                    }
                }
            }
        }

        return false;  // return not in check if above return statement is never run
    }

    public boolean validMovesLeft(TeamColor teamColor) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = gameBoard.squares[i][j];
                if (piece == null || piece.getTeamColor() != teamColor) continue;
                Collection<ChessMove> validMoves = validMoves(new ChessPosition(i + 1, j + 1));
                if (!validMoves.isEmpty()) return false;
            }
        }
        return true;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        // checking to see if in check to begin with
        if (!isInCheck(teamColor)) return false;

        // checking to see if there are any valid moves left
        return validMovesLeft(teamColor);

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        // if you are not in checkmate, and you have no legal moves then you are in checkmate

        // checking to see if you are in check, if you are then not in stalemate
        if (isInCheck(teamColor)) return false;

        // checking to see if there are any valid moves left
        return validMovesLeft(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame that = (ChessGame) o;
        return gameBoard.equals(that.gameBoard) &&
                teamTurn == that.teamTurn;
    }

    @Override
    public int hashCode(){
        int hash = 17;
        hash = 71 * hash + (gameBoard == null ? 0: gameBoard.hashCode());
        hash = 71 * hash + teamTurn.hashCode();
        return hash;
    }
}


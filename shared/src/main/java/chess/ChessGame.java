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

    ChessBoard game_board;
    TeamColor team_turn = TeamColor.WHITE;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team_turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        team_turn = team;
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
        Collection<ChessMove> valid_moves = new ArrayList<>();
        ChessPiece piece = game_board.getPiece(startPosition);  // get piece
        if (piece == null) return null;  // return null if no piece

        // get raw moves
        Collection<ChessMove> potential_moves = piece.pieceMoves(game_board, startPosition);

        // remove invalid moves
        for (ChessMove move : potential_moves) {
            try {
                is_valid(move, game_board);
                valid_moves.add(move);
            } catch (InvalidMoveException e) {
                continue;
            }
        }

        return valid_moves;
    }

    public void is_valid(ChessMove move, ChessBoard board) throws InvalidMoveException {
        // makes temporary chess game and executes move
        ChessBoard potential_board = game_board.clone();
        ChessGame temp_game = new ChessGame();
        temp_game.setBoard(potential_board);
        ChessPiece piece = board.getPiece(move.getStartPosition());


        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(team_turn, move.getPromotionPiece());
        }
        temp_game.game_board.squares[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1] = piece;
        temp_game.game_board.squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = null;

        // throw exception if move leaves you in check
        if (temp_game.isInCheck(team_turn)) throw new InvalidMoveException();
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition end_pos = move.getEndPosition();
        ChessPosition start_pos = move.getStartPosition();
        int start_row = start_pos.getRow();
        int start_col = start_pos.getColumn();
        ChessPiece piece = game_board.getPiece(start_pos);
        int end_row = end_pos.getRow();
        int end_col = end_pos.getColumn();
        ChessPiece end_piece = game_board.getPiece(end_pos);

        // if move has no piece on it
        if (piece == null) throw new InvalidMoveException();

        // if not your turn
        if (piece.getTeamColor() != team_turn) throw new InvalidMoveException();

        // if moving capture piece is on same team
        if (end_piece != null && end_piece.getTeamColor() == team_turn) throw new InvalidMoveException();

        // check to see if move leaves you in check, if it does kill the move
        is_valid(move, game_board);

        // by now, should be safe to make move on the real board
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(team_turn, move.getPromotionPiece());
        }
        game_board.squares[end_row - 1][end_col - 1] = piece;
        game_board.squares[start_row - 1][start_col - 1] = null;
        TeamColor next_turn_color = (team_turn == TeamColor.WHITE ? TeamColor.BLACK: TeamColor.WHITE);
        setTeamTurn(next_turn_color);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        // finding king by looping through board
        ChessPosition king_pos = null;  // init
        outerLoop:
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = game_board.squares[i][j];
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    king_pos = new ChessPosition(i + 1, j + 1);
                    break outerLoop;
                }
            }
        }

        // checking all opponent's possible moves
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = game_board.squares[i][j];

                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> potential_moves = piece.pieceMoves(game_board, new ChessPosition(i + 1, j + 1));
                    for (ChessMove move: potential_moves) {
                        if (move.getEndPosition().equals(king_pos)) return true;
                    }
                }
            }
        }

        return false;  // return not in check if above return statement is never run
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");

        // get all legal moves, make and copy a new chess board after executing the move; if still in check for all of them, you are in check mate
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
        // if you are not in checkmate and you have no legal moves then you are in checkmate
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        game_board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return game_board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame that = (ChessGame) o;
        return game_board.equals(that.game_board) &&
                team_turn == that.team_turn;
    }

    @Override
    public int hashCode(){
        int hash = 17;
        hash = 71 * hash + game_board.hashCode();
        hash = 71 * hash + team_turn.hashCode();
        return hash;
    }
}

package ui.helpers;

import chess.*;
import exception.ResponseException;
import model.GameData;

import java.util.Collection;
import java.util.Map;

public class BoardPrinter {

    public static String printGame(ChessBoard chessBoard, int gameID, Collection<GameData> gameList, String perspective, boolean highlight, Integer row, Integer col) throws ResponseException {
        String highlightColor = "104";

        Map<ChessPiece.PieceType, String> pieceToString = Map.ofEntries(
                Map.entry(ChessPiece.PieceType.KING, "Ki"),
                Map.entry(ChessPiece.PieceType.QUEEN, "Qu"),
                Map.entry(ChessPiece.PieceType.ROOK, "Ro"),
                Map.entry(ChessPiece.PieceType.BISHOP, "Bi"),
                Map.entry(ChessPiece.PieceType.KNIGHT, "Kn"),
                Map.entry(ChessPiece.PieceType.PAWN, "Pa")
        );

        Map<ChessGame.TeamColor, String> colorToString = Map.ofEntries(
                Map.entry(ChessGame.TeamColor.WHITE, "20"),
                Map.entry(ChessGame.TeamColor.BLACK, "30"));

        // getting chessgame's board
        ChessBoard board = null;
        if (chessBoard == null) {
            for (GameData gameData: gameList) {
                if (gameData.gameID() == gameID) {
                    board = gameData.game().getBoard();
                }
            }
        }
        else {
            board = chessBoard;
        }

        String vertEndRowsBlack = "\u001B[102;102;1m' '\u001B[0m" +
                "\u001B[30;102;1m h  \u001B[0m" +
                "\u001B[30;102;1m g  \u001B[0m" +
                "\u001B[30;102;1m f  \u001B[0m" +
                "\u001B[30;102;1m e  \u001B[0m" +
                "\u001B[30;102;1m d  \u001B[0m" +
                "\u001B[30;102;1m c  \u001B[0m" +
                "\u001B[30;102;1m b  \u001B[0m" +
                "\u001B[30;102;1m a  \u001B[0m" +
                "\u001B[102;102;1m' '\u001B[0m" +
                "\n";

        String vertEndRowsWhite = "\u001B[102;102;1m' '\u001B[0m" +
                "\u001B[30;102;1m a  \u001B[0m" +
                "\u001B[30;102;1m b  \u001B[0m" +
                "\u001B[30;102;1m c  \u001B[0m" +
                "\u001B[30;102;1m d  \u001B[0m" +
                "\u001B[30;102;1m e  \u001B[0m" +
                "\u001B[30;102;1m f  \u001B[0m" +
                "\u001B[30;102;1m g  \u001B[0m" +
                "\u001B[30;102;1m h  \u001B[0m" +
                "\u001B[102;102;1m' '\u001B[0m" +
                "\n";

        StringBuilder chessBoardString = new StringBuilder();

        // adding 1st row accordig to black or white perspective
        if (perspective.equals("black")) {
            chessBoardString.append(vertEndRowsBlack);
        }
        else {
            chessBoardString.append(vertEndRowsWhite);
        }

        for (int i=0; i<8; i++) {
            // reverse i if building other perspective
            int newI = (perspective.equals("black")) ? i : (7 - i);

            // building rows one by one
            StringBuilder rowString = new StringBuilder();
            String rowEndSquare = String.format("\u001B[30;102;1m %s \u001B[0m", Integer.toString(newI + 1));

            rowString.append(rowEndSquare);
            for (int j=0; j<8; j++) {

                // reverse j if building from other persepctive
                int newJ = (perspective.equals("white")) ? j : 8 - (j + 1);
                String backColor = ((newI + newJ) % 2 == 1) ? "102" : "42";

                // highlight logic
                if (highlight) {
                    backColor = (checkValidMoves(chessBoard,row, col, newI, newJ)) ? highlightColor: backColor;
                }

                if (board.squares[newI][newJ] != null) {
                    rowString.append(String.format("\u001B[%s;%s;1m %s \u001B[0m",
                            colorToString.get(board.squares[newI][newJ].getTeamColor()),
                            backColor,
                            pieceToString.get(board.squares[newI][newJ].getPieceType())));
                }
                else {
                    rowString.append(String.format("\u001B[%s;%s;1m %s \u001B[0m",
                            backColor,
                            backColor,
                            "  "));
                }
            }
            rowString.append(rowEndSquare).append("\n");

            chessBoardString.append(rowString);
        }

        if (perspective.equals("black")) {
            chessBoardString.append(vertEndRowsBlack);
        }
        else {
            chessBoardString.append(vertEndRowsWhite);
        }

        return chessBoardString.toString();

    }

    private static boolean checkValidMoves(ChessBoard board, int row, int col, int newI, int newJ) throws ResponseException {
        ChessPiece piece;

        if (board.squares[row-1][col-1] == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "There is no piece at that position");
        }
        piece = board.squares[row-1][col-1];
        System.out.println(row);
        System.out.println(col);
        Collection<ChessMove> moves = piece.pieceMoves(board, new ChessPosition(row, col));
        for (ChessMove move: moves) {
            int endRow = move.getEndPosition().getRow();
            int endCol = move.getEndPosition().getColumn();
            if (endRow==newI + 1 && endCol==newJ + 1) {
                return true;
            }
        }
        return false;
    }
}

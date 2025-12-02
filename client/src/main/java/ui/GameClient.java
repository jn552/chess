package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import exception.ResponseException;
import model.*;
import ui.helpers.BoardPrinter;

import java.util.*;

public class GameClient {
    public final String serverUrl;
    private final ServerFacade server;
    private final AuthData userAuthData;
    private final Integer gameID;
    private final String playerColor;

    public GameClient(String serverUrl, AuthData authData, Integer gameID, String playerColor) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
        this.userAuthData = authData;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "highlight" -> highlight();
                default -> help();
            };
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String redraw() throws ResponseException {
        ChessBoard board = getUpdatedBoard();

        System.out.print(BoardPrinter.printGame(board, gameID, null, playerColor));
        return "board redrawn";
    }


    public String leave() throws ResponseException {
        //TODO disconnnect the session
        return "";
    }

    public String move(String... params) throws ResponseException {
        //TODO replace with move logic
        //checking only a gameID was passed in
        if (params.length == 1) {
            String gameID = params[0];
            int intGameID = 0;

            // checking if ID is actually an integer
            try {
                intGameID = Integer.parseInt(gameID);
            }

            catch (Exception e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Expected as an integer: <ID>" );
            }
        }
        return "";

    }

    public String resign() throws ResponseException {
        //TODO resign logic
        //server.logout(getAuthData().authToken());
        return "";
    }

    public String highlight(String... params) throws ResponseException {
        //TODO highlight logic
        return "";
    }

    public AuthData getAuthData(){
        return this.userAuthData;
    }

    public String help() {
        return """
                    redraw - redraw the chessboard.
                    leave <name> - leave the chess game.
                    move <start_pos> <end_pos> - moves the piece from start_pos to end_pos if legal. 
                    resign - forfeit the game.
                    highlight <pos> - highlight possible moves for the piece at specified postion, pos.
                    help - get list of commands
                   """;
    }

    private ChessBoard getUpdatedBoard() throws ResponseException {
        // updating local game Storage of the game
        Collection<GameData> gameList = server.listGames(getAuthData().authToken()).games();
        for (GameData gameData: gameList) {
            if (gameData.gameID() == gameID) {
                return gameData.game().getBoard();
            }
        }
        return null;
    }

}

package ui;

import chess.*;
import exception.ResponseException;
import model.*;
import ui.helpers.BoardPrinter;
import ui.websocket.ConsoleMessageHandler;
import ui.websocket.WebSocketFacade;

import java.util.*;

public class GameClient {
    public final String serverUrl;
    private final ServerFacade server;
    private final AuthData userAuthData;
    private final Integer gameID;
    private final String playerColor;
    private WebSocketFacade webSocketFacade;
    private ConsoleMessageHandler consoleMessageHandler;

    public GameClient(String serverUrl, AuthData authData, Integer gameID, String playerColor)
            throws ResponseException {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
        this.userAuthData = authData;
        this.gameID = gameID;
        this.playerColor = playerColor;
        consoleMessageHandler = new ConsoleMessageHandler(playerColor);
        webSocketFacade = new WebSocketFacade(serverUrl, consoleMessageHandler);
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
                case "highlight" -> highlight(params);
                default -> help();
            };
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String redraw() throws ResponseException {
        ChessBoard board = getUpdatedBoard();

        System.out.print(BoardPrinter.printGame(board, gameID, null,
                playerColor, false, null, null));
        return "board redrawn";
    }


    public String leave() throws ResponseException {

        webSocketFacade.leave(userAuthData.authToken(), gameID);
        return "left game";
    }

    public String move(String... params) throws ResponseException {

        //checking only a gameID was passed in
        String startPos = "";
        String endPos = "";
        int startRow = 9;
        int startCol = 9;
        int endRow = 9;  // standin values to be overwritten below
        int endCol = 9;

        if (params.length == 2) {
            startPos = params[0];
            endPos = params[1];

            // checking if Position strings are valid
            try {
                startRow = Character.getNumericValue(parsePosString(startPos).charAt(1));
                startCol = parsePosString(startPos).charAt(0) - 96;  // zero for col since its A5 not 5A
                endRow = Character.getNumericValue(parsePosString(endPos).charAt(1));
                endCol = parsePosString(endPos).charAt(0) - 96;  // ascii for 'a' is 97, so I can just subtract 97

                webSocketFacade.makeMove(userAuthData.authToken(), gameID,
                        new ChessMove(new ChessPosition(startRow, startCol),
                                new ChessPosition(endRow, endCol), null));
            }

            catch (Exception e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Positions must be entered " +
                        "as a letter (a-h) followed by a number (1-8) with no spaces");
            }
        }
        return "";
    }

    public String resign() throws ResponseException {
        // add a confirmation check
        Scanner scanner = new Scanner(System.in);
        String line = "";
        while (true) {
            System.out.println("Are you sure? Enter Y/N");
            line = scanner.nextLine();
            if (line.equals("N") || line.equals("n")) {
                System.out.println("Did not resign; continue playing");
                return "";
            }
            else if (line.equals("Y") || line.equals("y")) {
                break;
            }
        }
        webSocketFacade.resign(userAuthData.authToken(), gameID);
        return "resigned";
    }

    public String highlight(String... params) throws ResponseException {

        ChessBoard board = getUpdatedBoard();
        String pos = "";
        int row = 9; // standins
        int col = 9;

        if (params.length == 1) {
            pos = params[0];

            // checking if Position strings are valid
            try {
                row = Character.getNumericValue(parsePosString(pos).charAt(1));
                col = parsePosString(pos).charAt(0) - 96;  // zero for col since its A5 not 5A
                System.out.println(BoardPrinter.printGame(board, gameID, null,
                        playerColor, true, row, col));

            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                throw new ResponseException(ResponseException.Code.ClientError, "Positions must be entered as a " +
                        "letter (a-h) followed by a number (1-8) with no spaces");
            }
        }

        else {
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: highlight <pos> , " +
                    "where pos is letter (a-h) folowed by a number (1-8)");
        }
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

    private String parsePosString(String pos) throws ResponseException {
        try {
            char row = pos.charAt(1);
            char col = Character.toLowerCase(pos.charAt(0));  // need to turn this into a number

            // checking valid inputs
            if (!(row=='1' || row=='2' || row=='3' || row=='4' || row=='5' || row=='6' || row=='7' || row=='8') ||
                    !(col=='a'|| col=='b'|| col=='c'|| col=='d'|| col=='e'|| col=='f'|| col=='g'|| col=='h')) {
                throw new ResponseException(ResponseException.Code.ClientError, "Positions must be entered as a " +
                        "letter followed by a number with no spaces");
            }

            char[] parsedPos = {col, row};
            return new String(parsedPos);
        }

        catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ClientError, "Positions must be entered as a letter " +
                    "followed by a number with no spaces");
        }

    }

    public void connect() throws ResponseException {
        webSocketFacade.enterGame(userAuthData.authToken(), gameID);
    }

}

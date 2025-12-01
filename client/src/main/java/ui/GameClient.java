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

    // for observe (just for now while no live updates)
    private List<GameData> gameList = new ArrayList<>();

    public GameClient(String serverUrl, AuthData authData) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
        this.userAuthData = authData;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> create(params);
                case "leave" -> list();
                case "move" -> join(params);
                case "resign" -> observe(params);
                case "highlight" -> logout();
                default -> help();
            };
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String create(String...params) throws ResponseException {
        //checking to make sure a username, password, and email only were sent in
        if (params.length == 1) {
            String gameName = params[0];

            CreateGameResponse createGameResponse = server.createGame(new CreateGameData(gameName), getAuthData().authToken());
            gameList.add(new GameData(createGameResponse.gameID(), null, null, null, new ChessGame()));
            return String.format("Created game with name %s and ID %s. ", gameName, createGameResponse.gameID());
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameName>");
    }

    public String list() throws ResponseException {
        GameListData gameList = server.listGames(getAuthData().authToken());
        return printGameList(gameList);
    }

    public String join(String... params) throws ResponseException {
        // update game list HERE also update it in create. gameList = server.listGames(getAuthData().authToken()) this
        // code return a GameListData, but I need it to just be a list

        //checking to make sure a username and password only were sent in
        if (params.length == 2) {
            String gameID = params[0];
            String color = params[1].toLowerCase();
            int intGameID = 0;


            // checking if use ractualy entererd white or black
            if (!color.equals("white") && !color.equals("black")) {
                throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID> <WHITE|BLACK>");
            }

            // convert color to white or black type
            ChessGame.TeamColor teamColor = (color.equals("white")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            // checking if the user actually entered a string that can be converted to an integer
            try {
                intGameID = Integer.parseInt(gameID);
                int actGameID = gameList.get(intGameID- 1).gameID();
            }

            catch (Exception e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Game ID doesn't exist. must be an integer");
            }

            // checking game existence
            if (!gameExists(intGameID)) {
                throw new ResponseException(ResponseException.Code.ClientError, "Game ID doesn't exist");
            }

            server.joinGame(new JoinRequestData(teamColor, intGameID), getAuthData().authToken());
            return String.format("Joined game %s as team %s. \n" + BoardPrinter.printGame(null, intGameID, gameList, color), gameID, color);
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID> <WHITE|BLACK>");
    }

    public String observe(String... params) throws ResponseException {
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

            // if game exists check
            if (!gameExists(intGameID)) {
                throw new ResponseException(ResponseException.Code.ClientError, "Game ID doesn't exist." );
            }

            return BoardPrinter.printGame(null, intGameID, gameList, "black");
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID>");
    }

    public String logout() throws ResponseException {
        server.logout(getAuthData().authToken());
        return "You have successfully logged out \n";
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

    private boolean gameExists(int id) {
        for (GameData game: gameList) {
            if (game.gameID() == id) {
                return true;
            }
        }
        return false;
    }

    public String printGameList(GameListData gameList){
        if (gameList.games().isEmpty()) {
            return "There are currently no games. \n";
        }

        StringBuilder list = new StringBuilder();
        list.append("Here are all the Games:\n");

        // loping through each game and adding a newline to the string builder
        int counter = 1;
        for (GameData game: gameList.games()) {
            String name = game.gameName();
            String whiteName = (game.whiteUsername() != null) ? game.whiteUsername() : "empty";
            String blackName = (game.blackUsername() != null) ? game.blackUsername() : "empty";
            list.append(String.format("Game#: %s, Game name: %s, White username: %s, Black username: %s\n", counter, name, whiteName, blackName));
            counter += 1;
        }
        // to make output more pretty lol
        list.append("\n");

        this.gameList.clear();
        this.gameList.addAll(gameList.games());

        return list.toString();
    }
}

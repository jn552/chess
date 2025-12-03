package ui;



import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import exception.ResponseException;
import model.*;
import ui.helpers.BoardPrinter;

import java.util.*;

public class PostLoginClient {
    public final String serverUrl;
    private final ServerFacade server;
    private final AuthData userAuthData;

    // for observe (just for now while no live updates)
    private List<GameData> gameList = new ArrayList<>();

    public PostLoginClient(String serverUrl, AuthData authData) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
        this.userAuthData = authData;
    }

    public EvalResponse eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                default -> help();
            };
        }
        catch (ResponseException ex) {
            return new EvalResponse(ex.getMessage(), null, null);
        }
    }

    public EvalResponse create(String...params) throws ResponseException {
        //checking to make sure a username, password, and email only were sent in
        if (params.length == 1) {
            String gameName = params[0];

            CreateGameResponse createGameResponse = server.createGame(new CreateGameData(gameName), getAuthData().authToken());
            gameList.add(new GameData(createGameResponse.gameID(), null, null, null, new ChessGame()));
            System.out.println(String.format("Created game with name %s. ", gameName));
            return new EvalResponse("", createGameResponse.gameID(), null);
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameName>");
    }

    public EvalResponse list() throws ResponseException {
        GameListData gameList = server.listGames(getAuthData().authToken());
        System.out.println(printGameList(gameList));
        return new EvalResponse("", null, null);
    }

    public EvalResponse join(String... params) throws ResponseException {
        // update game list field HERE also update it in create. gameList = server.listGames(getAuthData().authToken()) this; DONE
        gameList = new ArrayList<>(server.listGames(getAuthData().authToken()).games());

        //checking to make sure a username and password only were sent in
        if (params.length == 2) {
            String gameID = params[0];
            String color = params[1].toLowerCase();
            int intGameID = 0;
            int actGameID = -1;

            // checking if use ractualy entererd white or black
            if (!color.equals("white") && !color.equals("black")) {
                throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID> <WHITE|BLACK>");
            }

            // convert color to white or black type
            ChessGame.TeamColor teamColor = (color.equals("white")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            // checking if the user actually entered a string that can be converted to an integer
            try {
                intGameID = Integer.parseInt(gameID);
                actGameID = gameList.get(intGameID - 1).gameID();
            }

            catch (Exception e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Game ID doesn't exist. must be an integer");
            }

            // checking game existence
            if (!gameExists(intGameID)) {
                throw new ResponseException(ResponseException.Code.ClientError, "Game ID doesn't exist");
            }

            server.joinGame(new JoinRequestData(teamColor, intGameID), getAuthData().authToken());
            System.out.println(BoardPrinter.printGame(null, intGameID, gameList, color, false, null, null));
            System.out.println("Joined Game");
            return new EvalResponse("", actGameID, color);  //TODO mayber return the gameID in the string then parse it back to pass into the gamerepl
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID> <WHITE|BLACK>");
    }

    public EvalResponse observe(String... params) throws ResponseException {
        //checking only a gameID was passed in
        gameList = new ArrayList<>(server.listGames(getAuthData().authToken()).games());

        if (params.length == 1) {
            String gameID = params[0];
            int intGameID = 0;
            int actGameID = -1;
            // checking if ID is actually an integer
            try {
                intGameID = Integer.parseInt(gameID);
            }

            catch (Exception e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Expected as an integer: <ID>" );
            }

            actGameID = gameList.get(intGameID - 1).gameID();
            System.out.println(actGameID);

            // if game exists check
            if (!gameExists(actGameID)) {
                throw new ResponseException(ResponseException.Code.ClientError, "Game ID doesn't exist." );
            }

            System.out.println(BoardPrinter.printGame(null, intGameID, gameList, "black", false, null, null));
            System.out.println("Observing Game");
            return new EvalResponse("", actGameID, "black");
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID>");
    }

    public EvalResponse logout() throws ResponseException {
        server.logout(getAuthData().authToken());
        System.out.println("You have successfully logged out \n");
        return new EvalResponse("You have successfully logged out \n", null, null);
    }


    public AuthData getAuthData(){
        return this.userAuthData;
    }

    public EvalResponse help() {
        System.out.println("""
                    logout - logs you out of the server
                    create <name> - create a game
                    list - get  list of current games
                    join <ID> <white|black> - join game with specified ID as specified color
                    observe <ID> - observe the game with specified ID
                    help - get list of commands
                   """);
        return new EvalResponse("", null, null);
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

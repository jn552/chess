package ui;



import chess.ChessGame;
import model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PostLoginClient {
    public final String serverUrl;
    private final ServerFacade server;
    private final AuthData userAuthData;

    // for observe (just for now while no live updates)
    private Collection<GameData> gameList = new ArrayList<>();

    public PostLoginClient(String serverUrl, AuthData authData) {
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
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
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
            }

            catch (Exception e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Game ID doesn't exist. must be an integer");
            }

            // checking game existence
            if (!gameExists(intGameID)) {
                throw new ResponseException(ResponseException.Code.ClientError, "Game ID doesn't exist");
            }

            server.joinGame(new JoinRequestData(teamColor, intGameID), getAuthData().authToken());
            return String.format("Joined game %s as team %s.", gameID, color);
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

            return printGame(intGameID);
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID>");
    }

    public String logout() throws ResponseException {
        server.logout(getAuthData().authToken());
        System.out.println("You have successfully logged out \n");
        return "quit\n";
    }


    public AuthData getAuthData(){
        return this.userAuthData;
    }

    public String help() {
        return """
                    logout - logs you out of the server
                    create <name> - create a game
                    list - get  list of current games
                    join <ID> <white|black> - join game with specified ID as specified color
                    observe <ID> - observe the game with specified ID
                    help - get list of commands
                   """;
    }

    private boolean gameExists(int ID) {
        for (GameData game: gameList) {
            if (game.gameID() == ID) {
                return true;
            }
        }
        return false;
    }

    public String printGame(int gameID) {
        return """
               how do i pritn the game
               """;
    }

    public String printGameList(GameListData gameList){
        if (gameList.games().isEmpty()) {
            return "There are currently no games. \n";
        }

        StringBuilder list = new StringBuilder();
        list.append("Here are all the Games:\n");

        // loping through each game and adding a newline to the string builder
        for (GameData game: gameList.games()) {
            int numID = game.gameID();
            String name = game.gameName();
            String whiteName = (game.whiteUsername() != null) ? game.whiteUsername() : "empty";
            String blackName = (game.blackUsername() != null) ? game.blackUsername() : "empty";
            list.append(String.format("GameID: %s, Game name: %s, White username: %s, Black username: %s\n", numID, name, whiteName, blackName));
        }
        // to make output more pretty lol
        list.append("\n");

        return list.toString();
    }
}

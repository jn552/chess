package ui;



import chess.ChessGame;
import model.*;

import java.util.Arrays;

public class PostLoginClient {
    public final String serverUrl;
    private final ServerFacade server;
    private AuthData userAuthData;

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
                case "quit" -> "quit";
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
            return String.format("Created game with name %s and ID %s. ", gameName, createGameResponse.gameID());
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password> <email>");
    }

    public String list() throws ResponseException {

        GameListData gameList = server.listGames(getAuthData().authToken());
        // MIGHT HAVE TO DESERIALIZE HERE
        return String.format("Logging in as %s. ", gameList);
    }

    public void join(String... params) throws ResponseException {
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

            // checking if the user actually entered a sstring that can be converted to an integer
            try {
                intGameID = Integer.parseInt(gameID);
            }

            catch (Exception e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID> <WHITE|BLACK>");
            }

            server.join(new JoinRequestData(color, intGameID), getAuthData().authToken());
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
    }

    public String observe(String... params) throws ResponseException {
        //checking to make sure a username and password only were sent in
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];

            userAuthData = server.login(new LoginData(username, password));
            return String.format("Logging in as %s. ", username);
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
    }

    public String logout(String... params) throws ResponseException {
        //checking to make sure a username and password only were sent in
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];

            userAuthData = server.login(new LoginData(username, password));
            return String.format("Logging in as %s. ", username);
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
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

}

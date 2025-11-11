package ui;



import model.AuthData;
import model.LoginData;
import model.UserData;

import java.net.http.WebSocket;
import java.util.Arrays;

public class PreLoginClient {
    public final String serverUrl;
    private final ServerFacade server;
    private AuthData userAuthData = null;

    public PreLoginClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String...params) throws ResponseException {
        //checking to make sure a username, password, and email only were sent in
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            userAuthData = server.register(new UserData(username, password, email));
            return String.format("Registered %s with email %s. ", username, email);
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password> <email>");
    }

    public String login(String... params) throws ResponseException {
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
                register <username> <password> <email> - to register an account
                login <username> <password> to login to an account and play
                quit - exit program
                help - get list of commands
               """;
    }

}

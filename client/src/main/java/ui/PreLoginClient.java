package ui;



import model.LoginData;

import java.net.http.WebSocket;
import java.util.Arrays;

public class PreLoginClient {
    public final String serverUrl;
    private final ServerFacade server;

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
                case "help" -> help();
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
        return "";
    }

    public String login(String... params) throws ResponseException {
        //checking to make sure a username and password only were sent in
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];

            server.login(new LoginData(username, password));
            return String.format("Logging in as %s. ", username);
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
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

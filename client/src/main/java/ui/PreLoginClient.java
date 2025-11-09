package ui;

import com.sun.nio.sctp.NotificationHandler;

import java.net.http.WebSocket;
import java.util.Arrays;

public class PreLoginClient {
    public final String serverUrl;
    private final NotificationHandler notificationHandler;

    public PreLoginClient(String serverUrl, NotificationHandler notificationHandler) {
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
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

    public String login(String... params) throws ResponseException {
        if (params.length >= 1) {
            state = State.SIGNEDIN;
            username = String.join(" ", params);
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            return String.format("Loggin in as %s. ", username);
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
    }


}

package ui;

import com.sun.nio.sctp.NotificationHandler;

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
        catch (ResponseExeption ex) {
            return ex.getMessage();
        }
    }


}

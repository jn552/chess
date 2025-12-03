package ui;

import exception.ResponseException;
import model.AuthData;

import java.util.Scanner;

public class GameRepl {
    private final GameClient client;
    private final Integer gameID;
    private final String playerColor;
    private final AuthData authData;

    public GameRepl(String serverUrl, AuthData authData, Integer gameID, String playerColor) throws ResponseException {
        this.client = new GameClient(serverUrl, authData, gameID, playerColor);
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.authData = authData;

    }

    public void run() throws ResponseException {
        // opening messages
        client.connect();
        printPrompt();
        System.out.println(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        // main loop
        while (!result.equals("left game")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            }
            // resign should ask the player if they are sure and they enter Y or N
            catch (Throwable e) {
                System.out.println(e.toString());
            }
        }
    }

    private void printPrompt(){
        System.out.println("\nYou are in a game. Type 'help' to get started.");
    }

}

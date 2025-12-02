package ui;

import model.AuthData;

import java.util.Scanner;

public class GameRepl {
    private final GameClient client;
    private final Integer gameID;

    public GameRepl(String serverUrl, AuthData authData, Integer gameID) {
        this.client = new GameClient(serverUrl, authData);
        this.gameID = gameID;
    }

    public void run() {
        // opening messages
        printPrompt();
        System.out.println(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        // main loop
        while (!result.equals("You have successfully logged out \n")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);

            }

            catch (Throwable e) {
                System.out.println(e.toString());
            }
        }
    }

    private void printPrompt(){
        System.out.println("\nYou are in a game. Type 'help' to get started.");
    }

}

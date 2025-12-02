package ui;

import model.AuthData;

import java.util.Scanner;


public class PostLoginRepl {
    private final PostLoginClient client;
    public boolean inGame = false;
    public GameRepl gameRepl;
    private String serverUrl;
    private AuthData authData;

    public PostLoginRepl(String serverUrl, AuthData authData) {
        client = new PostLoginClient(serverUrl, authData);
        this.serverUrl = serverUrl;
        this.authData = authData;
    }

    public void run() {

        // opening messages
        System.out.println("\n Welcome, you are now logged in");
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


                if (inGame) {
                    Integer gameID = 0;   //TODO standin, find a way to get the gameID
                    gameRepl = new GameRepl(serverUrl, authData, gameID);
                    gameRepl.run();
                    inGame = false;
                }
            }

            catch (Throwable e) {
                System.out.println(e.toString());
            }
        }
    }

    private void printPrompt(){
        System.out.println("\nYou are logged in. Type 'help' to get started.");
    }
}

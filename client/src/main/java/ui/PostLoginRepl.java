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
        EvalResponse result = new EvalResponse("", null, null);

        // main loop
        while (!result.message.equals("You have successfully logged out \n")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result.message);

                if (!(result.color==null) && !(result.gameID==null)) {
                    inGame = true;
                }

                if (inGame) {
                    //TODO standin, find a way to get the gameID, i think i did this
                    gameRepl = new GameRepl(serverUrl, authData, result.gameID, result.color);
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

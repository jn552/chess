package ui;

import model.AuthData;

import java.util.Scanner;


public class PostLoginRepl {
    private final PostLoginClient client;

    public PostLoginRepl(String serverUrl, AuthData authData) {
        client = new PostLoginClient(serverUrl, authData);
    }

    public void run() {

        // opening messages
        System.out.println("\n Welcome, you are now logged in");
        System.out.println(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        // main loop
        while (!result.equals("quit")) {
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
        System.out.println("\nYou are logged in. Type 'help' to get started.");
    }
}

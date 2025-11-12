package ui;

import model.AuthData;

import java.util.Scanner;

import static java.awt.Color.BLUE;

public class PostLoginRepl {
    private final PostLoginClient client;

    public PostLoginRepl(String serverUrl, AuthData authData) {
        client = new PostLoginClient(serverUrl, authData);
    }

    public void run() {

        // opening messages
        System.out.println("\uD83D\uDC36 Welcome, you are now logged in");
        System.out.println(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        // main loop
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(BLUE + result);
            }

            catch (Throwable e) {
                var msg = e.toString();
                System.out.println(msg);
            }
        }
    }

    private void printPrompt(){
        System.out.println("Welcome to CS 240 Chess. Type 'help' to get started.");
    }
}

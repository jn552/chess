package ui;

import model.AuthData;

import java.util.Scanner;


public class PreLoginRepl {
    private final PreLoginClient client;

    public PreLoginRepl(String serverUrl) {
        client = new PreLoginClient(serverUrl);
    }

    public AuthData run() {

        // opening messages
        System.out.println("\nWelcome to a CS240 Chess Server. Sign in to start.");
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
                var msg = e.toString();
                System.out.println(msg);
            }

            if (client.getAuthData() != null) {
                break;
            }
        }

        return client.getAuthData();
    }

    private void printPrompt(){
        System.out.println("\nWelcome to CS 240 Chess. Type 'help' to get started.");
    }
}

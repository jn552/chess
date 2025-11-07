package ui;

import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.NotificationHandler;

import java.util.Scanner;

import static java.awt.Color.BLUE;

public class PreLoginRepl implements NotificationHandler {
    private final PreLoginClient client;

    public PreLoginRepl(String serverUrl) {
        client = new PreLoginClient(serverUrl, this);
    }
    public void run() {

        // opening messages
        System.out.println("\uD83D\uDC36 Welcome to a CS240 Chess Server. Sign in to start.");
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
        System.out.println("PRINT PROMPT HERE");
    }
    @Override
    public HandlerResult handleNotification(Notification notification, Object attachment) {
        // INTELLIJ SAID TO DO THIS< NOT SURE WHAT TO DO WITH THIS YET
        return null;
    }
}

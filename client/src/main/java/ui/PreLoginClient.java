package ui;

import com.sun.nio.sctp.NotificationHandler;

public class PreLoginClient {
    public final String serverUrl;
    private final NotificationHandler notificationHandler;

    public PreLoginClient(String serverUrl, NotificationHandler notificationHandler) {
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;

    }


}

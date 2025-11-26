package websocket.messages;

public class NotificationMessage extends ServerMessage {
    public String message;
    public String playerName;
    public int gameID;  //actual gameID in the database

    public NotificationMessage(String message, String playerName, int gameID) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
        this.playerName = playerName;
        this.gameID = gameID;
    }

    public String getMessage() {
        return message;
    }
}

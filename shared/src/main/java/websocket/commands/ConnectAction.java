package websocket.commands;

public class ConnectAction extends UserGameCommand {

    public ConnectAction(String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID, null);

    }
}

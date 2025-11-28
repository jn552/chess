package websocket.commands;

public class ResignAction extends UserGameCommand {

    public ResignAction(String authToken, Integer gameID) {
        super(CommandType.RESIGN, authToken, gameID, null);

    }
}

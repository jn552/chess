package websocket.commands;

public class LeaveAction extends UserGameCommand {

    public LeaveAction(String authToken, Integer gameID) {
        super(CommandType.LEAVE, authToken, gameID, null);
    }
}

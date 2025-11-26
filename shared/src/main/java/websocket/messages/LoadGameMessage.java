package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
    public String message;
    public int gameID;
    public ChessGame chessGame;
    // may need to add more but work with this for now

    public LoadGameMessage(String message, int gameID, ChessGame chessGame) {
        super(ServerMessageType.LOAD_GAME);
        this.message = message;
        this.gameID = gameID;
        this.chessGame = chessGame;
    }

    public String getMessage() {
        return message;
    }
}

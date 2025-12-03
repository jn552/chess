package ui.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.Gson;
import model.GameData;
import ui.helpers.BoardPrinter;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


public class ConsoleMessageHandler implements NotificationHandler {
    private final String playerColor;

    public ConsoleMessageHandler(String playerColor) {
        // playerColor used to determine perspective of board, defualts to white
        this.playerColor = playerColor;
        if (playerColor == null) {
            playerColor = "white";
        }
    }

    @Override
    public void notify(ServerMessage notification) {
        // loadgame message, prints the board

        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            LoadGameMessage loadGameMessage = (LoadGameMessage) notification;
            ChessGame chessGame = loadGameMessage.getChessGame();
            System.out.println(BoardPrinter.printGame(chessGame.getBoard(), loadGameMessage.gameID, null, playerColor));
        }

        else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            NotificationMessage notificationMessage = (NotificationMessage) notification;
            System.out.println(notificationMessage.getMessage());
        }

        else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            ErrorMessage errorMessage = (ErrorMessage) notification;
            System.out.println(errorMessage.getMessage());
        }
    }
}



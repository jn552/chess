package ui.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import model.GameData;
import ui.helpers.BoardPrinter;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.awt.*;
import java.util.Collection;
import java.util.Map;

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
        if (notification instanceof LoadGameMessage loadGameMessage) {
            ChessGame chessGame = loadGameMessage.getChessGame();
            System.out.println(BoardPrinter.printGame(chessGame.getBoard(), loadGameMessage.gameID, null, playerColor));
        }

        else if (notification instanceof NotificationMessage note) {
            System.out.println(note.getMessage());
        }

        else if (notification instanceof ErrorMessage errorNote) {
            System.out.println(errorNote.getMessage());
        }
    }
}



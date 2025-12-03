package ui.websocket;

import exception.ResponseException;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage notification) throws ResponseException;

}

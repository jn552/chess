package ui.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import websocket.commands.ConnectAction;
import websocket.commands.LeaveAction;
import websocket.commands.MakeMoveAction;
import websocket.commands.ResignAction;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    ConsoleMessageHandler messageHandler;

    public WebSocketFacade(String url, ConsoleMessageHandler messageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.messageHandler = messageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);

                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                        serverMessage = new Gson().fromJson(message, LoadGameMessage.class);
                    }
                    else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                        serverMessage = new Gson().fromJson(message, NotificationMessage.class);
                    }
                    else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                        serverMessage = new Gson().fromJson(message, ErrorMessage.class);
                    }

                    messageHandler.notify(serverMessage);
                }
            });
        }
        catch (DeploymentException | IOException | URISyntaxException e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void enterGame(String authToken, Integer gameID) throws ResponseException {
        try {
            var action = new ConnectAction(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        }
        catch (IOException e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove chessMove) throws ResponseException {
        try {
            var action = new MakeMoveAction(authToken, gameID, chessMove);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        }
        catch (IOException e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID) throws ResponseException {
        try {
            var action = new ResignAction(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        }
        catch (IOException e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    public void leave(String authToken, Integer gameID) throws ResponseException {
        try {
            var action = new LeaveAction(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        }
        catch (IOException e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

}

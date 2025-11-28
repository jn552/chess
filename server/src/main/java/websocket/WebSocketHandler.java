package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAOInterface;
import dataaccess.DataAccessException;
import dataaccess.GameDAOInterface;
import io.javalin.websocket.WsCloseContext;
import exception.ResponseException;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connectHandler = new ConnectionManager();
    private final AuthDAOInterface authDao;
    private final GameDAOInterface gameDao;

    public WebSocketHandler(AuthDAOInterface authDao, GameDAOInterface gameDao) {
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws IOException {
        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            String username = authDao.find(action.getAuthToken()).username();
            switch (action.getCommandType()) {
                case CONNECT -> enter(username, ctx.session, action.getGameID());
                case MAKE_MOVE-> makeMove(username, ctx.session, action.getGameID(), action.getChessMove());
                case LEAVE-> exit(username, ctx.session, action.getGameID());
                case RESIGN-> resign(username, ctx.session, action.getGameID());
            }
        }

        catch (IOException ex) {
            ex.printStackTrace();
        }

        catch (DataAccessException e) {
            try {
                var errorMessage = new ErrorMessage(e.getMessage());
                ctx.session.getRemote().sendString(errorMessage.toString());
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void enter(String username, Session session, Integer gameID) throws IOException {
        connectHandler.add(session, gameID);
        var message = String.format("%s just entered the game", username);
        var notification = new NotificationMessage(message, username, gameID);
        connectHandler.broadcast(gameID, notification);
    }

    private void exit(String username, Session session, Integer gameID) throws IOException {
        var message = String.format("%s left the game", username);
        var notification = new NotificationMessage(message, username, gameID);
        connectHandler.broadcast(gameID, notification);
        connectHandler.remove(session);
    }

    private void makeMove(String username, Session session, Integer gameID, ChessMove chessMove) {
        try {
            // finidng game and then makign the move; if succeed broadcast to everyone move is made
            GameData gameData = gameDao.find(gameID);
            ChessGame chessGame = gameData.game();
            chessGame.makeMove(chessMove);

            //  update gamedao entry clear old one then sanve new one with same ID
            GameData newGameData = new GameData(gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    chessGame);
            gameDao.remove(gameData);
            gameDao.save(newGameData);

            // broadcast to everyone move was made
            var message = String.format("%s moved a piece", username);
            var notification = new NotificationMessage(message, username, gameID);
            connectHandler.broadcast(gameID, notification);

            // special state notifications (ie in check stalemate)
            if (chessGame.isInCheck(chessGame.getTeamTurn())) {
                var stateMessage = new NotificationMessage("INSERT PLAYER is in Check", username, gameID);
                connectHandler.broadcast(gameID, stateMessage);
            }

            else if (chessGame.isInCheckmate(chessGame.getTeamTurn())) {
                var stateMessage = new NotificationMessage("INSERT PLAYER is in Checkmate", username, gameID);
                connectHandler.broadcast(gameID, stateMessage);
            }

            else if (chessGame.isInStalemate(chessGame.getTeamTurn())) {
                var stateMessage = new NotificationMessage("Stalemate has occurred", username, gameID);
                connectHandler.broadcast(gameID, stateMessage);
            }

            // board game update mesage
            var boardMessage = new LoadGameMessage(gameID, chessGame);
            connectHandler.broadcast(gameID, boardMessage);
        }

        catch (IOException ex) {
            ex.printStackTrace();
        }

        catch (DataAccessException e) {
            try {
                var errorMessage = new ErrorMessage(e.getMessage());
                session.getRemote().sendString(errorMessage.toString());
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // just telling that one player that the move was invalid
        catch (InvalidMoveException e) {
            try {
                var errorMessage = new ErrorMessage("Invalid move");
                session.getRemote().sendString(errorMessage.toString());
            }
            catch (IOException error) {
                error.printStackTrace();
            }
        }
    }

    public void resign(String username, Session session, Integer gameID) throws IOException {
        var message = String.format("%s resigned", username);
        var notification = new NotificationMessage(message, username, gameID);
        connectHandler.broadcast(gameID, notification);
        // NEED TO ADD ATTRIBUTE TO CHESSGAME, like GAMEOVER defaulting to false, then can'
        // make move or resign if game is already over
        // do something similar to lines  130 to 131 to send error message
    }
}

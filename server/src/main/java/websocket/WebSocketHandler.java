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
    private final Gson gson = new Gson();

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
        System.out.println(ctx.message()); //leav eher
        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);

            AuthData authData = authDao.find(action.getAuthToken());

            // if authtoken is bad ie authData is null
            if (authData == null) {
                var errorMessage = new ErrorMessage("Invalid authToken");
                ctx.session.getRemote().sendString(gson.toJson(errorMessage));
                return;
            }

            String username = authData.username();

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
                ctx.session.getRemote().sendString(gson.toJson(errorMessage));
            }
            catch (IOException error) {
                error.printStackTrace();
            }
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void enter(String username, Session session, Integer gameID) throws IOException {
        connectHandler.add(session, gameID);
        try {
            GameData gameData = gameDao.find(gameID);

            // if bad gameID
            if (gameData == null) {
                var errorMessage = new ErrorMessage("Invalid gameID");
                session.getRemote().sendString(gson.toJson(errorMessage));
                return;
            }

            ChessGame chessGame = gameData.game();

            // sending load game message to the player who joined
            var loadGameMessage = new LoadGameMessage(gameID, chessGame);
            session.getRemote().sendString(gson.toJson(loadGameMessage));

            // notifying everyone else that the player joined
            var message = String.format("%s just entered the game", username);
            var notification = new NotificationMessage(message, username, gameID);
            connectHandler.broadcast(gameID, notification, session);
        }

        catch (DataAccessException e) {
            try {
                var errorMessage = new ErrorMessage(e.getMessage());
                session.getRemote().sendString(gson.toJson(errorMessage));
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void exit(String username, Session session, Integer gameID) throws IOException {
        var message = String.format("%s left the game", username);
        var notification = new NotificationMessage(message, username, gameID);
        connectHandler.broadcast(gameID, notification, session);
        connectHandler.remove(session);

        // removing the session's user from the gameData
        try {
            GameData gameData = gameDao.find(gameID);
            ChessGame.TeamColor playerColor = (username.equals(gameData.whiteUsername())) ? ChessGame.TeamColor.WHITE: ChessGame.TeamColor.BLACK;
            GameData newGameData = (playerColor == ChessGame.TeamColor.WHITE) ? new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.blackUsername(), gameData.game()) :
                    new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.blackUsername(), gameData.game());
            gameDao.remove(gameData);
            gameDao.save(newGameData);
        }

        catch (DataAccessException e) {
            try {
                var errorMessage = new ErrorMessage(e.getMessage());
                session.getRemote().sendString(gson.toJson(errorMessage));
                return;
            }
            catch (IOException err) {
                err.printStackTrace();
                return;
            }
        }
    }

    private void makeMove(String username, Session session, Integer gameID, ChessMove chessMove) {
        try {
            // finidng game and then makign the move; if succeed broadcast to everyone move is made
            GameData gameData = gameDao.find(gameID);
            ChessGame chessGame = gameData.game();

            // checking to see if its session's turn
            String turnName = (chessGame.getTeamTurn() == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
            if (!turnName.equals(username)) {
                var errorMessage = new ErrorMessage("Not your turn");
                session.getRemote().sendString(gson.toJson(errorMessage));
                return;
            }

            if (chessGame.gameOver) {
                var errorMessage = new ErrorMessage("Someone has resigned; the game is over ");
                session.getRemote().sendString(gson.toJson(errorMessage));
                return;
            }
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
            var message = String.format("%s moved a piece from %s to %s", username, chessMove.getStartPosition(), chessMove.getEndPosition());
            var notification = new NotificationMessage(message, username, gameID);
            connectHandler.broadcast(gameID, notification, session);

            String enemyName = (chessGame.getTeamTurn() == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
            // special state notifications (ie in check stalemate)

            if (chessGame.isInCheckmate(chessGame.getTeamTurn())) {
                var stateMessage = new NotificationMessage(String.format("%s is in Checkmate", enemyName), username, gameID);
                connectHandler.broadcast(gameID, stateMessage, null);
            }

            else if (chessGame.isInCheck(chessGame.getTeamTurn())) {
                var stateMessage = new NotificationMessage(String.format("%s is in Check", enemyName), username, gameID);
                connectHandler.broadcast(gameID, stateMessage, null);
            }
            // or maybe just say the color is in check

            else if (chessGame.isInStalemate(chessGame.getTeamTurn())) {
                var stateMessage = new NotificationMessage("Stalemate has occurred", username, gameID);
                connectHandler.broadcast(gameID, stateMessage, null);
            }

            // board game update mesage
            var boardMessage = new LoadGameMessage(gameID, chessGame);
            connectHandler.broadcast(gameID, boardMessage, null);
        }

        catch (IOException ex) {
            ex.printStackTrace();
        }

        catch (DataAccessException e) {
            try {
                var errorMessage = new ErrorMessage(e.getMessage());
                session.getRemote().sendString(gson.toJson(errorMessage));
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // just telling that one player that the move was invalid
        catch (InvalidMoveException e) {
            try {
                var errorMessage = new ErrorMessage("Invalid move");
                session.getRemote().sendString(gson.toJson(errorMessage));
            }
            catch (IOException error) {
                error.printStackTrace();
            }
        }
    }

    public void resign(String username, Session session, Integer gameID) throws IOException {

        GameData gameData = null;
        // checkign to see if you are NOT an observer
        try {
            gameData = gameDao.find(gameID);
            if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
                var errorMessage = new ErrorMessage("Observers cannot resign; leave instead if you want to exit.");
                session.getRemote().sendString(gson.toJson(errorMessage));
                return;
            }

            // checking to see if game is already over
            if (gameData.game().gameOver) {
                var errorMessage = new ErrorMessage("Game is done; cannot resign again.");
                session.getRemote().sendString(gson.toJson(errorMessage));
                return;
            }
        }

         catch (DataAccessException e) {
            try {
                var errorMessage = new ErrorMessage(e.getMessage());
                session.getRemote().sendString(gson.toJson(errorMessage));
                return;
            }
            catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
        }

        var message = String.format("%s resigned", username);
        var notification = new NotificationMessage(message, username, gameID);
        connectHandler.broadcast(gameID, notification, null);

        //  update gamedao entry clear old one then sanve new one with same ID
        ChessGame chessGame = gameData.game();
        chessGame.gameOver = true;
        GameData newGameData = new GameData(gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                chessGame);

        try {
            gameDao.remove(gameData);
            gameDao.save(newGameData);
        }

        catch (DataAccessException e) {
            try {
                var errorMessage = new ErrorMessage(e.getMessage());
                session.getRemote().sendString(gson.toJson(errorMessage));
                return;
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
        }


    }
}

package server;

import dataaccess.*;
import handler.*;
import io.javalin.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import websocket.WebSocketHandler;

public class Server {

    private final Javalin javalin;

    public Server() {

        UserDAOInterface userDao = new UserDAOMemory();
        AuthDAOInterface authDao = new AuthDAOMemory();
        GameDAOInterface gameDao = new GameDAOMemory();


        // instantiate DAOs
        try {
            userDao = new UserDAOSQL();
            authDao = new AuthDAOSQL();
            gameDao = new GameDAOSQL();
        }

        catch (DataAccessException e) {
            System.out.println("Error making SQL DAOs");
        }

        // making services (pass in DAOs into them)
        UserService userService = new UserService(userDao, authDao);
        GameService gameService = new GameService(gameDao, authDao);
        ClearService clearService = new ClearService(userDao, authDao, gameDao);

        // instantiate handlers (pass in services)
        RegisterHandler registerHandler = new RegisterHandler(userService);

        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(gameService);
        ListGameHandler listGameHandler = new ListGameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);
        CreateGameHandler createGameHandler = new CreateGameHandler(gameService);
        WebSocketHandler webSocketHandler = new WebSocketHandler(authDao, gameDao);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));


        // Register your endpoints and exception handlers here.
        javalin.post("/user", registerHandler);
        javalin.delete("/db", clearHandler);
        javalin.post("/session", loginHandler);
        javalin.delete("/session", logoutHandler);
        javalin.get("/game", listGameHandler);
        javalin.post("/game", createGameHandler);
        javalin.put("/game", joinGameHandler);
        javalin.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler);
            ws.onMessage(webSocketHandler);
            ws.onClose(webSocketHandler);
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

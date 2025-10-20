package server;

import dataaccess.*;
import handler.*;
import io.javalin.*;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {

        // instantiate DAOs
        UserDAOInterface userDao = new UserDAOMemory();
        AuthDAOInterface authDao = new AuthDAOMemory();
        GameDAOInterface gameDao = new GameDAOMemory();

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

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", registerHandler);
        javalin.delete("/db", clearHandler);
        javalin.post("/session", loginHandler);
        javalin.delete("/session", logoutHandler);
        javalin.get("/game", listGameHandler);
        javalin.post("/game", createGameHandler);
        javalin.put("/game", joinGameHandler);



    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

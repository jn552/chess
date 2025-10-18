package server;

import dataaccess.*;
import handler.RegisterHandler;
import io.javalin.*;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {

        // instantiate DAOs
        UserDAOInterface userDao = new UserDAOMemory();
        AuthDAOInterface authDao = new AuthDAOMemory();
        GameDAOInterface gameDao = new GameDAOMemory();

        // making services (pass in daos into them)
        UserService userService = new UserService(userDao, authDao);
        GameService gameService = new GameService();  //TODO pass in gameDao and authDao
        // TODO make clear service



        // instantiate handlers (pass in services)
        RegisterHandler registerHandler = new RegisterHandler(userService);



        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", registerHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

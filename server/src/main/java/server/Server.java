package server;

import dataaccess.*;
import handler.RegisterHandler;
import io.javalin.*;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final AuthDAOInterface authDao;
    private final UserDAOInterface userDao;
    private final GameDAOInterface gameDao;


    public Server() {

        // instantiate DAOs
        this.userDao = new UserDAOMemory();
        this.authDao = new AuthDAOMemory();
        this.gameDao = new GameDAOMemory();

        // making services (pass in daos into them)
        UserService userService = new UserService(userDao, authDao);
        GameService gameService = new GameService();  //TODO pass in gameDao and authDao
        // TODO make clear service



        // instantiate handlers (pass in services)
        RegisterHandler registerHandler = new RegisterHandler(userService);



        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", registerHandler);
        //ctx -> instanceofhandler  so replace this here with the instance o fhandler, look at handler slides for exampel
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

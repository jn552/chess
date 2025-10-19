package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.GameService;

public class CreateGameHandler implements Handler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService){
        this.gameService = gameService;
    }
    public void handle(Context context) {
        // try catch, parse json as 2 strings

    }

}
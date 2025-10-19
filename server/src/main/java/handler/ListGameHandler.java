package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.GameService;

public class ListGameHandler implements Handler {
    private final GameService gameService;

    public ListGameHandler(GameService gameService){
        this.gameService = gameService;
    }
    public void handle(Context context) {
        // try catch, parse json as 2 strings

    }

}

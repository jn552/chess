package handler;

import exception.BadRequestException;
import exception.UsernameTakenException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.UserData;
import service.GameService;
import service.UserService;

import java.util.Map;


public class JoinGameHandler implements Handler {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService){
        this.gameService = gameService;
    }
    public void handle(Context context) {
        // try catch, parse json as 2 strings

    }

}

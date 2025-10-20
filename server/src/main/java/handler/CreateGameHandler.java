package handler;

import com.google.gson.Gson;
import exception.BadRequestException;
import exception.NotAuthException;
import exception.UsernameTakenException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.CreateGameData;
import model.UserData;
import service.GameService;

import java.util.Map;

public class CreateGameHandler implements Handler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(GameService gameService){
        this.gameService = gameService;
    }
    public void handle(Context context) {
        try {
            String json = context.body();
            CreateGameData gameRequest = gson.fromJson(json, CreateGameData.class);
            int gameID = gameService.createGame(gameRequest);
            context.status(200);
            context.result(gson.toJson(gameID));
        }

        catch (BadRequestException error) {
            context.status(400);
            context.result(gson.toJson(Map.of("message", error.getMessage())));
        }

        catch (NotAuthException error){
            context.status(401);
            context.result(gson.toJson(Map.of("message", error.getMessage())));
        }

    }

}
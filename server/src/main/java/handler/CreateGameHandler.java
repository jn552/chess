package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.BadRequestException;
import exception.NotAuthException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.CreateGameData;
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
            String authToken = context.header("authorization");
            String json = context.body();
            CreateGameData gameRequest = gson.fromJson(json, CreateGameData.class);
            int gameID = gameService.createGame(gameRequest, authToken);
            context.status(200);
            context.result(gson.toJson(Map.of("gameID", gameID)));
        }

        catch (BadRequestException error) {
            context.status(400);
            context.result(gson.toJson(Map.of("message", error.getMessage())));
        }

        catch (NotAuthException error){
            context.status(401);
            context.result(gson.toJson(Map.of("message", error.getMessage())));
        }

        catch (DataAccessException error){
            context.status(500);
            context.result(gson.toJson(Map.of("message", error.getMessage())));

        }

    }

}
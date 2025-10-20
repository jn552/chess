package handler;

import com.google.gson.Gson;
import exception.BadRequestException;
import exception.NotAuthException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.GameData;
import model.LoginData;
import service.GameService;

import java.util.Collection;
import java.util.Map;

public class ListGameHandler implements Handler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListGameHandler(GameService gameService){
        this.gameService = gameService;
    }
    public void handle(Context context) {
        try {
            // parse json into login data thing
            String authToken = context.header("authorization");

            // check user existence and nullity
            Collection<GameData> gameList = gameService.listGames(authToken);
            context.status(200);
            context.result(gson.toJson(Map.of("games", gameList)));
        }

        catch (NotAuthException error){
            context.status(401);
            context.result(gson.toJson(Map.of("message", error.getMessage())));
        }

    }

}

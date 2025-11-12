package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.BadRequestException;
import exception.NotAuthException;
import exception.TakenException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.JoinRequestData;
import service.GameService;

import java.util.Map;


public class JoinGameHandler implements Handler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public JoinGameHandler(GameService gameService){
        this.gameService = gameService;
    }
    public void handle(Context context) {
        try {
            String authToken = context.header("authorization");
            String json = context.body();
            JoinRequestData joinRequest = gson.fromJson(json, JoinRequestData.class);
            gameService.joinGame(joinRequest, authToken);
            context.status(200);
            context.result();
        }

        catch (BadRequestException error) {
            context.status(400);
            context.result(gson.toJson(Map.of("message", error.getMessage(),
                    "status", "ClientError")));
        }

        catch (NotAuthException error){
            context.status(401);
            context.result(gson.toJson(Map.of("message", error.getMessage(),
                    "status", "ClientError")));
        }

        catch (TakenException error) {
            context.status(403);
            context.result(gson.toJson(Map.of("message", error.getMessage(),
                    "status", "ClientError")));
        }

        catch (DataAccessException error){
            context.status(500);
            context.result(gson.toJson(Map.of("message", error.getMessage(),
                    "status", "ServerError")));

        }

    }

}

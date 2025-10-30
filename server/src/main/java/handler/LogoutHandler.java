package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.BadRequestException;
import exception.NotAuthException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.LoginData;
import service.UserService;

import java.util.Map;

public class LogoutHandler implements Handler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LogoutHandler(UserService userService){
        this.userService = userService;
    }

    public void handle(Context context) {

        try {

            // parse json into authToken
            String authToken = context.header("authorization");

            // check authToken validity
            userService.validateAuth(authToken);

            // now that we validated, logout
            userService.removeAuth(authToken);
            context.status(200);
        }

        catch (BadRequestException error) {
            context.status(400);
            context.result(gson.toJson(Map.of("message", error.getMessage())));
        }

        catch (NotAuthException error){
            context.status(401);
            context.result(gson.toJson(Map.of("message", error.getMessage())));
        }

        catch (DataAccessException error) {
            context.status(500);
            context.result(gson.toJson(Map.of("message", error.getMessage())));
        }
    }

}
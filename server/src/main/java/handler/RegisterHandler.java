package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.BadRequestException;
import exception.TakenException;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import model.AuthData;
import model.UserData;
import service.UserService;

import java.util.Map;

public class RegisterHandler implements Handler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public RegisterHandler(UserService userService){
        this.userService = userService;
    }

    public void handle(Context context) {
        try {
            String json = context.body();
            UserData user = gson.fromJson(json, UserData.class);
            AuthData authData = userService.registerUser(user);
            context.status(200);
            context.result(gson.toJson(Map.of("username", authData.username(), "authToken", authData.authToken())));
        }

        catch (BadRequestException error) {
            context.status(400);
            context.result(gson.toJson(Map.of("message", error.getMessage())));
        }

        catch (TakenException error){
            context.status(403);
            context.result(gson.toJson(Map.of("message", error.getMessage())));
        }

        catch (DataAccessException error){
            context.status(500);
            context.result(gson.toJson(Map.of("message", error.getMessage())));
        }

    }

}

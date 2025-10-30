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

public class LoginHandler implements Handler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(UserService userService){
        this.userService = userService;
    }
    public void handle(Context context) {
        try {
            // parse json into login data thing
            String json = context.body();
            LoginData loginData = gson.fromJson(json, LoginData.class);

            // check user existence and nullity
            userService.isUser(loginData.username());

            // check password and make auth
            AuthData authData = userService.validatePassword(loginData);
            context.status(200);
            context.result(gson.toJson(authData));
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
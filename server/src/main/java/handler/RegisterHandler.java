package handler;

import exception.BadRequestException;
import exception.UsernameTakenException;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import model.AuthData;
import model.UserData;
import service.UserService;

import java.util.Map;

public class RegisterHandler implements Handler {
    private final UserService userService;

    public RegisterHandler(UserService userService){
        this.userService = userService;
    }
    public void handle(Context context) {
        try {
            UserData user = context.bodyAsClass(UserData.class);
            AuthData authData = userService.registerUser(user);
            context.status(200).json(authData);
        }

        catch (BadRequestException error) {
            context.status(400);
            context.json(Map.of("message", error.getMessage()));
            // context.json() or context.result() put  new Gson.tojson(Map.of("message", ex.getMessage") there is example n phase 3 github
        }

        catch (UsernameTakenException error){
            context.status(403);
            context.json(Map.of("message", error.getMessage()));
        }

    }

}

package handler;

import com.google.gson.Gson;
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
            context.result(gson.toJson(authData));
        }

        catch (BadRequestException error) {
            context.status(400);
            context.result(gson.toJson(Map.of("message", error.getMessage())));
            // context.json() or context.result() put  new Gson.tojson(Map.of("message", ex.getMessage") there is example n phase 3 github
        }

        catch (UsernameTakenException error){
            context.status(403);
            context.result(gson.toJson(Map.of("message", error.getMessage())));
        }

    }

}

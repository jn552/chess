package handler;
import exception.BadRequestException;
import exception.UsernameTakenException;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import model.AuthData;
import model.UserData;
import service.UserService;

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

        //TODO
        // catch specific exceptions and return appropriate response
        // username taken
        // badrequest, one or more userdata fields are null?
        catch (BadRequestException error) {
            context.status(400);
            // return message?
            // context.json() or context.result() put  new Gson.tojson(Map.of("message", ex.getMessage") there is example n phase 3 github
        }

        catch (UsernameTakenException error){
            context.status(403);
            //return message?
        }

    }

}

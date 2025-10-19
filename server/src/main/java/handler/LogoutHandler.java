package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.UserService;

public class LogoutHandler implements Handler {
    private final UserService userService;

    public LogoutHandler(UserService userService){
        this.userService = userService;
    }
    public void handle(Context context) {
        // try catch, parse json as 2 strings

    }

}
package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.ClearService;

public class ClearHandler implements Handler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService){
        this.clearService = clearService;
    }
    public void handle(Context context) {
        clearService.clearAll();
        context.status(200);
    }

}
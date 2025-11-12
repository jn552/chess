package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.ClearService;

import java.util.Map;

public class ClearHandler implements Handler {
    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService){
        this.clearService = clearService;
    }

    public void handle(Context context) throws DataAccessException {
        try {
            clearService.clearAll();
            context.status(200);
        }

        catch (DataAccessException error) {
            context.status(500);
            context.result(gson.toJson(Map.of("message", error.getMessage(),
                    "status", "ServerError")));
        }
    }

}
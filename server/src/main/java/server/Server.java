package server;

import handler.RegisterHandler;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", new RegisterHandler());
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    //public void createHandlers(Javalin javalin){
        //javalin.get("/hello", new HelloBYUHandler()); // example from leture video, implememt that Handler as lambda , method or class (here it is a class)
        // javalin has post, delete, put (https methods) so not all endpoints wil use get

        //handler example class in javalin overview 18:19 (non json handler) and 20:05 (json handler)
        // 22:01 for more complicated example
    //}
}

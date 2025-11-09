package ui;

import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public UserData register(UserData user) throws ResponseException {
        var request = buildRequest("POST", "/user", user);
        var response = sendRequest(request);
        return handleResponse(response, UserData.class);
    }

    public AuthData login(LoginData loginData) {
        //example usesed EndpointResult as a standin i think
    }

    public void logout(String authToken) {
        //request here is auth tok
    }

    public Collection<GameData> listGames(String authToken) {
        // request is auth token
    }

    public String createGame(CreateGameData createGameData) {
        //req is CreateGameDAta record class
    }

    public void joinGame(JoinRequestData joinRequestData) {

    }

    public void clear() {

    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));

        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        }
        else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException{
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getLocalizedMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException{
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body !=null) {
                throw ResponseException.fromJson(body);
            }
            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "failure: " + status);

        }
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

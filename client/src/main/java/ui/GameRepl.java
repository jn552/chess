package ui;

import model.AuthData;
import model.CreateGameData;
import model.CreateGameResponse;
import model.GameListData;

import java.util.Arrays;

public class GameRepl {
    public class PostLoginClient {
        public final String serverUrl;
        private final ServerFacade server;
        private AuthData userAuthData;

        public PostLoginClient(String serverUrl, AuthData authData) {
            this.serverUrl = serverUrl;
            this.server = new ServerFacade(serverUrl);
            this.userAuthData = authData;
        }

//        public String eval(String input) {
//            try {
//                var tokens = input.toLowerCase().split(" ");
//                var cmd = (tokens.length > 0) ? tokens[0] : "help";
//                var params = Arrays.copyOfRange(tokens, 1, tokens.length);
//                return switch (cmd) {
//                    case "create" -> create(params);
//                    case "list" -> list();
//                    case "join" -> join(params);
//                    case "observe" -> observe(params);
//                    case "quit" -> "quit";
//                    default -> help();
//                };
//            }
//            catch (ResponseException ex) {
//                return ex.getMessage();
//            }
//        }
//
//        public String create(String...params) throws ResponseException {
//            //checking to make sure a username, password, and email only were sent in
//            if (params.length == 1) {
//                String gameName = params[0];
//
//                CreateGameResponse createGameResponse = server.createGame(new CreateGameData(gameName), getAuthData().authToken());
//                return String.format("Created game with name %s and ID %s. ", gameName, createGameResponse.gameID());
//            }
//
//            // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
//            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password> <email>");
//        }
//
//        public String list() throws ResponseException {
//
//            GameListData gameList = server.listGames(getAuthData().authToken());
//            // MIGHT HAVE TO DESERIALIZE HERE
//            return String.format("Logging in as %s. ", gameList);
//            }
//        }
    }
}

package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ResponseException;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private static int portNum;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        portNum = port;
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void testRegisterValid() throws ResponseException, DataAccessException {
        ServerFacade testFacade = new ServerFacade("http://localhost:" + portNum);
        UserData user = new UserData("jeremy", "12345", "testEmail");

        AuthData returnedAuth = testFacade.register(user);
        assert returnedAuth.authToken() != null;
        assert returnedAuth.username().equals("jeremy");
    }

    @Test
    public void testRegisterInvalid() {
        ServerFacade testFacade = new ServerFacade("http://localhost:" + portNum);
        UserData user = new UserData("jeremy", "12345", "testEmail");
        UserData user2 = new UserData("jeremy", "5", "testEmail");

        // should throw an exception for duplicate usernames
        assertThrows(ResponseException.class, () -> {
            testFacade.register(user2);
            testFacade.register(user);
        });
    }

    @Test
    public void testLoginValid() throws ResponseException {
        ServerFacade testFacade = new ServerFacade("http://localhost:" + portNum);
        UserData user = new UserData("testLogin", "12345", "testEmail");
        testFacade.register(user);

        // should throw an exception for duplicate usernames
        assert testFacade.login(new LoginData("testLogin", "12345")).username().equals("testLogin");
    }

    @Test
    public void testLoginInvalid() {
        ServerFacade testFacade = new ServerFacade("http://localhost:" + portNum);

        // should throw an exception since user DNE
        assertThrows(ResponseException.class, () -> {
            testFacade.login(new LoginData("hegheghe", "rughrouguewg"));
        });
    }

    @Test
    public void testLogoutValid() throws ResponseException {
        ServerFacade testFacade = new ServerFacade("http://localhost:" + portNum);
        UserData user = new UserData("testLogout", "12345", "testEmail");
        AuthData authData = testFacade.register(user);

        assertDoesNotThrow(() -> {
            testFacade.logout(authData.authToken());
        });
    }

    @Test
    public void testLogoutInvalid() {
        ServerFacade testFacade = new ServerFacade("http://localhost:" + portNum);

        assertThrows(ResponseException.class, () -> {
            testFacade.logout("clearlynotanauthtoken");
        });
    }

    @Test
    public void testCreateValid() throws ResponseException {
        ServerFacade testFacade = new ServerFacade("http://localhost:" + portNum);
        UserData user = new UserData("testCreate", "12345", "email");
        AuthData authData = testFacade.register(user);
        CreateGameResponse gameData = testFacade.createGame(new CreateGameData("testGame"), authData.authToken());
        assert gameData.gameID() == 1;

    }

    @Test
    public void testCreateInvalid() {
        ServerFacade testFacade = new ServerFacade("http://localhost:" + portNum);

        // testing incorrect authtoken
        assertThrows(ResponseException.class, () -> {
            testFacade.createGame(new CreateGameData("testinggame"), "clearlynotright");
        });
    }

    @Test
    public void testJoinValid() throws ResponseException {
        ServerFacade testFacade = new ServerFacade("http://localhost:" + portNum);
        UserData user = new UserData("Join", "12345", "email");
        AuthData authData = testFacade.register(user);
        testFacade.createGame(new CreateGameData("testgameagain"), authData.authToken());

        // testing to see if joined succesfully which it should have
        assertDoesNotThrow(() -> {
            testFacade.joinGame(new JoinRequestData(ChessGame.TeamColor.BLACK, 1), authData.authToken());
        });

    }

    @Test
    public void testJoinInvalid() throws ResponseException {
        ServerFacade testFacade = new ServerFacade("http://localhost:" + portNum);
        UserData user = new UserData("testJoin", "12345", "email");
        AuthData authData = testFacade.register(user);

        // testing to see if did not join since gameID doesnt exist
        assertThrows(ResponseException.class, () -> {
            testFacade.joinGame(new JoinRequestData(ChessGame.TeamColor.WHITE, 100), authData.authToken());
        });
    }

    @Test
    public void testListValid() throws ResponseException {
        ServerFacade testFacade = new ServerFacade("http://localhost:" + portNum);
        UserData user = new UserData("list", "12345", "email");
        AuthData authData = testFacade.register(user);

        // seeing if listed correctly with no errors
        assertDoesNotThrow(() -> {
            testFacade.listGames(authData.authToken());
        });

    }

    @Test
    public void testListInvalid() throws ResponseException {
        ServerFacade testFacade = new ServerFacade("http://localhost:" + portNum);
        UserData user = new UserData("listinvalid", "12345", "email");
        AuthData authData = testFacade.register(user);

        // testing not auth exception
        assertThrows(ResponseException.class, () -> {
            testFacade.listGames("NOTAUTH");
        });

    }

}

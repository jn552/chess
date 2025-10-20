package service;

import chess.ChessGame;
import dataaccess.AuthDAOMemory;
import dataaccess.GameDAOMemory;
import dataaccess.UserDAOMemory;
import exception.BadRequestException;
import exception.NotAuthException;
import model.AuthData;
import model.CreateGameData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    // instantiate DAOs
    GameDAOMemory gameDao = new GameDAOMemory();
    AuthDAOMemory authDao = new AuthDAOMemory();

    // instantiate things to add to DAOs
    GameData game = new GameData(12, "testwhite", "testblack", "testgame", new ChessGame());
    UserData user = new UserData("jeremy", "12345", "jeremy@email.com");
    AuthData auth = new AuthData("jeremy", "34-53.6");

    // instantiate the game service
    GameService testGameService = new GameService(gameDao, authDao);

    @Test
    void createGameBadRequest() {
        assertThrows(BadRequestException.class, () -> {
            testGameService.createGame(new CreateGameData(null), "jeremy");
        });
    }

    @Test
    void createGameValid() throws BadRequestException {
        // adding necessary things to DAOs
        authDao.save(auth);
        testGameService.createGame(new CreateGameData(("testGame")), "34-53.6");
        assert(gameDao.find(1) != null);

    }

    @Test
    void joinGame() {
        // test where I try to join as white but white is in there already

        // test I actually joined a game (I am white, white is empty, so check that white username was updated)
    }

    @Test
    void listGames() {
        // test where list games when no games doesn't throw error

        // test list games works
    }
}
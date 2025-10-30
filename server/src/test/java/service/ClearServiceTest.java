package service;

import chess.ChessGame;
import dataaccess.AuthDAOMemory;
import dataaccess.DataAccessException;
import dataaccess.GameDAOMemory;
import dataaccess.UserDAOMemory;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    // instantiate some DAOs
    GameDAOMemory gameDao = new GameDAOMemory();
    AuthDAOMemory authDao = new AuthDAOMemory();
    UserDAOMemory userDao = new UserDAOMemory();

    // instantiate things to add to DAOs
    GameData game = new GameData(12, "testwhite", "testblack", "testgame", new ChessGame());
    UserData user = new UserData("jeremy", "12345", "jeremy@email.com");
    AuthData auth = new AuthData("jeremy", "34-53.6");

    // instantiate the clear service
    ClearService testClearService = new ClearService(userDao, authDao, gameDao);

    @Test
    void clearAll() throws DataAccessException {

        // add things to each DAO
        gameDao.save(game);
        try {
            userDao.save(user);
        } catch (dataaccess.DataAccessException e) {
            throw new RuntimeException(e);
        }
        authDao.save(auth);

        // make sure clear actually clears
        testClearService.clearAll();
        try {
            assert(userDao.find("jeremy") == null);
        } catch (dataaccess.DataAccessException e) {
            throw new RuntimeException(e);
        }
        assert(gameDao.find(12) == null);
        assert(authDao.find("34-53.6") == null);
    }

    @Test
    void clearAllNoError() {
        // testing clearing with nothing in the DAOs doesn't throw exception
        assertDoesNotThrow(() -> {
            testClearService.clearAll();
        });
    }


}
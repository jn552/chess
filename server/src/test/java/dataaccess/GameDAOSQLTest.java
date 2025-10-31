package dataaccess;

import chess.ChessGame;

import model.GameData;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOSQLTest {

    // instantiate DAO
    GameDAOMemory gameDao = new GameDAOMemory();

    // instantiate things to add to DAOs
    GameData gameData = new GameData(1, "jeremy", "bob", "testGame", new ChessGame());
    GameData gameData2 = new GameData(2, "jeremy2", "bob2", "testGame2", new ChessGame());

    @Test
    void find() {
        gameDao.save(gameData);
        assert gameDao.find(1).whiteUsername().equals("jeremy");
    }

    @Test
    void findNegative() {
        assert gameDao.find(345) == null;
    }

    @Test
    void save() {
        gameDao.save(gameData);
        assert gameDao.find(1) != null;
    }

    @Test
    void saveNegative() {
        gameDao.save(gameData);
        assert !gameDao.find(1).whiteUsername().equals("clearlnotthisagaini");
    }

    @Test
    void findAllGames() {
        gameDao.save(gameData);
        gameDao.save(gameData2);
        Collection<GameData> games = new ArrayList<>();
        games.add(gameData);
        games.add(gameData2);

        // checking both games are there
        assert games.equals(gameDao.findAllGames());
    }

    @Test
    void findAllGamesNegative() {
        // checking case where games list is empty
        Collection<GameData> games = new ArrayList<>();
        assert gameDao.findAllGames().equals(games);
    }

    @Test
    void clear() {
        gameDao.save(gameData);
        gameDao.save(gameData2);
        gameDao.clear();
        assert gameDao.find(1) == null;

    }

    @Test
    void clearNegative() {
        // making sure clearing an empty table will not throw an error
        assertDoesNotThrow(() -> {
            gameDao.clear();
        });
    }

    @Test
    void remove() {
        gameDao.save(gameData);
        gameDao.remove(gameData);
        assert gameDao.find(1) == null;
    }

    @Test
    void removeNegative() {
        // making sure removing something that's not there will not throw an error
        assertDoesNotThrow(() -> {
            gameDao.remove(gameData2);
        });
    }
}
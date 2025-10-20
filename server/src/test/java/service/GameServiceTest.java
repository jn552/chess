package service;

import chess.ChessGame;
import dataaccess.AuthDAOMemory;
import dataaccess.GameDAOMemory;
import dataaccess.UserDAOMemory;
import exception.BadRequestException;
import exception.NotAuthException;
import exception.TakenException;
import model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    // instantiate DAOs
    GameDAOMemory gameDao = new GameDAOMemory();
    AuthDAOMemory authDao = new AuthDAOMemory();

    // instantiate things to add to DAOs
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
    void joinGameInvalid() {
        // add game where white player is already in
        GameData game = new GameData(4, "white", null, "testGame", new ChessGame());
        authDao.save(auth);
        gameDao.save(game);

        // should reject request to join
        assertThrows(TakenException.class, () -> {
            testGameService.joinGame(new JoinRequestData(ChessGame.TeamColor.WHITE, 4),"34-53.6");
        });
    }

    @Test
    void joinGameValid() throws BadRequestException {
        // add game where white player is already in
        GameData game = new GameData(4, null, "black", "testGame", new ChessGame());
        authDao.save(auth);
        gameDao.save(game);

        // join
        testGameService.joinGame(new JoinRequestData(ChessGame.TeamColor.WHITE, 4),"34-53.6");

        // checking if white username, which I joined as was updated
        assert gameDao.find(4).whiteUsername().equals("jeremy");
    }

    @Test
    void listGamesNotAuth() {
        // test where list games when no games doesn't throw error
        assertThrows(NotAuthException.class, () -> {
            testGameService.listGames("fakeAuth");
        });
    }

    @Test
    void listGamesValid() {
        authDao.save(auth);

        // add a bunch of games
        GameData game1 = new GameData(4, "white", null, "testGame", new ChessGame());
        GameData game2 = new GameData(5, "jeremy", "bob", "testGame1", new ChessGame());
        GameData game3 = new GameData(6, "bob", "harry", "testGame2", new ChessGame());
        GameData game4 = new GameData(7, null, null, "testGame7", new ChessGame());
        gameDao.save(game1);
        gameDao.save(game2);
        gameDao.save(game3);
        gameDao.save(game4);

        // getting actual list of games that should list
        Collection<GameData> realGames = new ArrayList<>();
        realGames.add(game1);
        realGames.add(game2);
        realGames.add(game3);
        realGames.add(game4);

        assert testGameService.listGames("34-53.6").equals(realGames);
    }
}
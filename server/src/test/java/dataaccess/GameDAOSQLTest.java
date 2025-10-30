package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
    }

    @Test
    void save() {
        gameDao.save(gameData);

        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM `game` WHERE gameID=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setInt(1, 1);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String whiteName = rs.getString("whiteUsername");
                    assert whiteName.equals("jeremy");
                }
            }
        }

        catch (Exception e) {
            System.out.println("Internal Error");
        }
    }

    @Test
    void saveNegative() {
    }

    @Test
    void findAllGames() {
    }

    @Test
    void findAllGamesNegative() {
    }

    @Test
    void clear() {
    }

    @Test
    void clearNegative() {
    }

    @Test
    void remove() {
    }

    @Test
    void removeNegative() {
    }

}
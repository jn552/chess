package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

public class GameDAOSQL implements GameDAOInterface{

    public GameDAOSQL() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS game (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256),
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            )
            """};
        DAOHelper.configureDatabase(createStatements);
    }

    @Override
    public GameData find(Integer gameID) throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM `game` WHERE gameID=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setInt(1, gameID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int gameIDNum = rs.getInt("gameID");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");
                    String json = rs.getString("json");
                    ChessGame chessGame = new Gson().fromJson(json, ChessGame.class);
                    return new GameData(gameIDNum, whiteUsername, blackUsername, gameName, chessGame);
                }
            }
        }

        catch (Exception e) {
            throw new DataAccessException("Error: error finding game");
        }
        return null;
    }

    @Override
    public void save(GameData gameData) throws DataAccessException{
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?, ?)";
        String json = new Gson().toJson(gameData.game());
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameData.gameID());
            ps.setString(2, gameData.whiteUsername());
            ps.setString(3, gameData.blackUsername());
            ps.setString(4, gameData.gameName());
            ps.setString(5, json);
            ps.executeUpdate();
        }

        catch (Exception e) {
            throw new DataAccessException("Error: error saving GameData");
        }

    }

    @Override
    public Collection<GameData> findAllGames() throws DataAccessException{

        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM `game`";
        Collection<GameData> gameList = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
                 try (ResultSet rs = ps.executeQuery()) {
                     while (rs.next()) {
                         int gameIDNum = rs.getInt("gameID");
                         String whiteUsername = rs.getString("whiteUsername");
                         String blackUsername = rs.getString("blackUsername");
                         String gameName = rs.getString("gameName");
                         String json = rs.getString("json");
                         ChessGame chessGame = new Gson().fromJson(json, ChessGame.class);
                         gameList.add(new GameData(gameIDNum, whiteUsername, blackUsername, gameName, chessGame));
                     }
                 }
        }

        catch (Exception e) {
            throw new DataAccessException("Error: error finding all games");
        }

        return gameList;
    }

    @Override
    public void clear() throws DataAccessException{
        var statement = "TRUNCATE `game`";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        }

        catch (Exception e) {
            throw new DataAccessException("Error: error clearing game dao");
        }
    }

    public void remove(GameData gameData) throws DataAccessException{
        var statement = "DELETE FROM `game` WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameData.gameID());
            ps.executeUpdate();
        }

        catch (Exception e){
            throw new DataAccessException("Error: error finding user");
        }
    }
}

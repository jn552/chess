package dataaccess;

import com.google.gson.Gson;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

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
    public GameData find(Integer gameID) {
        return new GameData(0, null, null, null, null);
    }

    @Override
    public void save(GameData gameData){
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?)";
        String json = new Gson().toJson(gameData.game());
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, gameData.whiteUsername());
            ps.setString(2, gameData.blackUsername());
            ps.setString(3, gameData.gameName());
            ps.setString(4, json);
            ps.executeUpdate();
        }

        catch (Exception e) {
            System.out.println("Error when saving gameDATA");
        }

    }

    @Override
    public Collection<GameData> findAllGames(){
        Collection<GameData> gameList = new ArrayList<>();
        return gameList;
    }

    @Override
    public void clear(){

    }

    public void remove(GameData gameData){

    }
}

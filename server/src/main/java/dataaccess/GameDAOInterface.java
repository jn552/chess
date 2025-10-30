package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAOInterface {

    GameData find(Integer gameID) throws DataAccessException;
    void save(GameData gameData) throws DataAccessException;
    Collection<GameData> findAllGames() throws DataAccessException;
    void clear() throws DataAccessException;
    void remove(GameData gameData) throws DataAccessException;
}

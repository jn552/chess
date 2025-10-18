package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAOInterface {

    void save(GameData gameData);
    GameData find(String gameID);
    Collection<GameData> findAllGames();
}

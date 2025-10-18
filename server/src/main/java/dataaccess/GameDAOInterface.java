package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAOInterface {

    GameData find(Integer gameID);
    void save(GameData gameData);
    Collection<GameData> findAllGames();
    void clear();
}

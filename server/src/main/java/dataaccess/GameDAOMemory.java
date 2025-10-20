package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameDAOMemory implements GameDAOInterface{

    private final Map<Integer, GameData> gameHash = new HashMap<>();

    @Override
    public GameData find(Integer gameID){
        return gameHash.get(gameID);
    }

    @ Override
    public void save(GameData game){
        gameHash.put(game.gameID(), game);
    }

    @ Override
    public Collection<GameData> findAllGames(){
        return new ArrayList<>(gameHash.values());
    }

    @ Override
    public void clear(){
        gameHash.clear();
    }

    @ Override
    public void remove(GameData gameData){
        gameHash.remove(gameData.gameID());
    }
}

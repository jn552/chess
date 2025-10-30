package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameDAOSQL implements GameDAOInterface{

    @Override
    public GameData find(Integer gameID) {
        return new GameData(0, null, null, null, null);
    }

    @Override
    public void save(GameData gameData){

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

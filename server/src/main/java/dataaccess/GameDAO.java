package dataaccess;

import model.GameData;

import java.util.Collection;

public class GameDAO {
    public GameData find(String gameID){
        //TODO
        // find the ret of game data by game id
        return null;  //return null means user DNE
    }

    public void save(GameData game){
        //TODO
        // save a game's data oacked in record class (GameData) to the db?
    }

    public Collection<GameData> findAllGames(){
        //TODO
        // return list of all games
        return null;
    }
}

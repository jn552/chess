package service;

import chess.ChessGame;
import dataaccess.AuthDAOInterface;
import dataaccess.GameDAOInterface;
import dataaccess.UserDAOInterface;
import exception.BadRequestException;
import exception.NotAuthException;
import model.CreateGameData;
import model.GameData;

public class GameService {
    private final GameDAOInterface gameDao;
    private final AuthDAOInterface authDao;
    private static int gameIDGenerator = 0;

    public GameService(GameDAOInterface gameDao, AuthDAOInterface authDao){
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public void validateAuth(String authToken) {
        // assumes authToken isn't null
        if (authDao.find(authToken) == null) {
            throw new NotAuthException("Error: unauthorized");
        }
    }

    public void saveGame(GameData gameData) {
        gameDao.save(gameData);
    }

    //TODO
    // go to sequence digagram and immplement all the methods
    public int createGame(CreateGameData gameRequest) throws BadRequestException {

        // unpacking info from request
        String authToken = gameRequest.authToken();
        String gameName = gameRequest.gameName();

        // checking nullity (if authToken is null it means not authorized)
        if (gameName == null) {
            throw new BadRequestException("Error: bad request");
        }

        // checking authToken
        validateAuth(authToken);

        // generate gameID and make gameData object
        int gameID = gameIDGenerator++;
        GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());

        // add gameData to dao
        saveGame(gameData);

        return gameID;
    }

}

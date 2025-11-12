package service;

import chess.ChessGame;
import dataaccess.AuthDAOInterface;
import dataaccess.DataAccessException;
import dataaccess.GameDAOInterface;
import exception.BadRequestException;
import exception.NotAuthException;
import exception.TakenException;
import model.CreateGameData;
import model.GameData;
import model.JoinRequestData;

import java.util.Collection;

public class GameService {
    private final GameDAOInterface gameDao;
    private final AuthDAOInterface authDao;
    private static int gameIDGenerator = 1;

    public GameService(GameDAOInterface gameDao, AuthDAOInterface authDao){
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    private void validateAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new NotAuthException("Error: unauthorized");
        }
        if (authDao.find(authToken) == null) {
            throw new NotAuthException("Error: unauthorized");
        }
    }

    private void saveGame(GameData gameData) throws DataAccessException {
        gameDao.save(gameData);
    }

    public int createGame(CreateGameData createGameData, String authToken) throws BadRequestException, DataAccessException {


        // checking nullity (if authToken is null it means not authorized)
        if (createGameData.gameName() == null) {
            throw new BadRequestException("Error: bad request (no game name given");
        }

        // checking authToken
        validateAuth(authToken);

        // generate gameID and make gameData object
        int gameID = gameIDGenerator;
        GameData gameData = new GameData(gameID, null, null, createGameData.gameName(), new ChessGame());
        gameIDGenerator += 1;

        // add gameData to dao
        saveGame(gameData);

        return gameID;
    }

    private void ableToJoin(ChessGame.TeamColor teamColor, GameData gameData) {
        if (teamColor == ChessGame.TeamColor.BLACK) {
            if (gameData.blackUsername() != null){
                throw new TakenException("Error: Team Color is already taken");
            }
        }
        else{
            if (gameData.whiteUsername() != null) {
                throw new TakenException("Error: Team Color is already taken");
            }
        }
    }

    public void joinGame(JoinRequestData joinRequestData, String authToken) throws BadRequestException, DataAccessException {
        //unpack info from request
        int gameID = joinRequestData.gameID();
        ChessGame.TeamColor teamColor = joinRequestData.playerColor();

        // null field check
        if (teamColor == null) {
            throw new BadRequestException("Error: bad request (no team color given)");
        }

        // validate Auth
        validateAuth(authToken);

        // check to see if gameID exists
        if (gameDao.find(gameID) == null) {
            throw new BadRequestException("Error: bad request (gameID does not exist)");
        }
        GameData gameData = gameDao.find(gameID);

        // check if you can join game
        ableToJoin(teamColor, gameData);

        // update gameData; clear the old one add new updated one
        String username = authDao.find(authToken).username();
        GameData newGameData;
        if (teamColor == ChessGame.TeamColor.BLACK) {
            newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
        }
        else {
            newGameData = new GameData(gameData.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.game());
        }
        gameDao.remove(new GameData(newGameData.gameID(), null, null, null, null));
        gameDao.save(newGameData);
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        //validate auth
        validateAuth(authToken);
        return gameDao.findAllGames();
    }

}

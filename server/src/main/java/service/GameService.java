package service;

import chess.ChessGame;
import dataaccess.AuthDAOInterface;
import dataaccess.GameDAOInterface;
import exception.BadRequestException;
import exception.NotAuthException;
import exception.TakenException;
import model.CreateGameData;
import model.GameData;
import model.JoinRequestData;

public class GameService {
    private final GameDAOInterface gameDao;
    private final AuthDAOInterface authDao;
    private static int gameIDGenerator = 1;

    public GameService(GameDAOInterface gameDao, AuthDAOInterface authDao){
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public void validateAuth(String authToken) {
        if (authToken == null) {
            throw new NotAuthException("Error: unauthorized");
        }
        if (authDao.find(authToken) == null) {
            throw new NotAuthException("Error: unauthorized");
        }
    }

    public void saveGame(GameData gameData) {
        gameDao.save(gameData);
    }

    public int createGame(CreateGameData createGameData, String authToken) throws BadRequestException {


        // checking nullity (if authToken is null it means not authorized)
        if (createGameData.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }

        // checking authToken
        validateAuth(authToken);

        // generate gameID and make gameData object
        int gameID = gameIDGenerator++;
        GameData gameData = new GameData(gameID, null, null, createGameData.gameName(), new ChessGame());

        // add gameData to dao
        saveGame(gameData);

        return gameID;
    }

    public void ableToJoin(ChessGame.TeamColor teamColor, GameData gameData) {
        if (teamColor == ChessGame.TeamColor.BLACK) {
            if (gameData.blackUsername() != null){
                throw new TakenException("Error: already taken");
            }
        }
        else{
            if (gameData.whiteUsername() != null) {
                throw new TakenException("Error: already taken");
            }
        }
    }

    public void joinGame(JoinRequestData joinRequestData, String authToken) throws BadRequestException {
        //unpack info from request
        int gameID = joinRequestData.gameID();
        ChessGame.TeamColor teamColor = joinRequestData.playerColor();

        // null field check
        if (teamColor == null) {
            throw new BadRequestException("Error: bad request");
        }

        // validate Auth
        validateAuth(authToken);

        // check to see if gameID exists
        if (gameDao.find(gameID) == null) {
            throw new BadRequestException("Error: bad request");
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
        gameDao.save(newGameData);
    }

}

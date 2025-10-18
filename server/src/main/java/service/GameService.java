package service;

import dataaccess.AuthDAOInterface;
import dataaccess.GameDAOInterface;
import dataaccess.UserDAOInterface;

public class GameService {
    private final GameDAOInterface gameDao;
    private final AuthDAOInterface authDao;

    public GameService(GameDAOInterface gameDao, AuthDAOInterface authDao){
        this.gameDao = gameDao;
        this.authDao = authDao;
    }
}

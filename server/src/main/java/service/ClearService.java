package service;

import dataaccess.AuthDAOInterface;
import dataaccess.DataAccessException;
import dataaccess.GameDAOInterface;
import dataaccess.UserDAOInterface;

public class ClearService {
    private final UserDAOInterface userDao;
    private final AuthDAOInterface authDao;
    private final GameDAOInterface gameDao;

    public ClearService(UserDAOInterface userDao, AuthDAOInterface authDao, GameDAOInterface gameDao){
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    private void clearAuth() throws DataAccessException{
        authDao.clear();
    }

    private void clearGame() throws DataAccessException{
        gameDao.clear();
    }

    private void clearUser() throws DataAccessException {
        userDao.clear();
    }

    public void clearAll() throws DataAccessException {
        clearAuth();
        clearGame();
        clearUser();
    }
}

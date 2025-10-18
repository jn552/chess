package service;

import dataaccess.AuthDAOInterface;
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

    public void clearAuth() {

    }

    public void clearGame(){

    }

    public void clearUser(){

    }

    public void clearAll(){

    }

}

package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.BadRequestException;
import exception.UsernameTakenException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDao = new UserDAO();
    private final AuthDAO authDao = new AuthDAO();

    public AuthData registerUser(UserData user) throws BadRequestException {

        // check if any of UserData fields are null
        if (user.username() == null || user.password() == null || user.email() == null){
            throw new BadRequestException("Error: bad request");
        }
        // check if username is taken
        if (userDao.find(user.username()) != null) {
            throw new UsernameTakenException("Error: already taken");
        }

        // if no errors then make auth token and save info
        String authToken = makeAuthToken();
        AuthData authData = new AuthData(authToken, user.username());
        userDao.save(user);
        authDao.save(authData);

        return authData;
    }

    public UserData getUserData(String username){
        return userDao.find(username);
    }

    public static String makeAuthToken(){
        return UUID.randomUUID().toString();
    }


}

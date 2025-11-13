package service;

import dataaccess.AuthDAOInterface;
import dataaccess.DataAccessException;
import dataaccess.UserDAOInterface;
import exception.BadRequestException;
import exception.NotAuthException;
import exception.TakenException;
import model.AuthData;
import model.LoginData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {

    private final UserDAOInterface userDao;
    private final AuthDAOInterface authDao;

    public UserService(UserDAOInterface userDao, AuthDAOInterface authDao){
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public AuthData registerUser(UserData user) throws BadRequestException, DataAccessException {

        // check if any of UserData fields are null
        if (user.username() == null || user.password() == null || user.email() == null){
            throw new BadRequestException("Error: bad request (null field)");
        }
        // check if username is taken

        if (userDao.find(user.username()) != null) {
            throw new TakenException("Error: username is already taken");
        }


        // if no errors then make auth token and save info
        String authToken = makeAuthToken();
        AuthData authData = new AuthData(user.username(), authToken);

        userDao.save(user);
        authDao.save(authData);

        return authData;
    }

    private static String makeAuthToken(){
        return UUID.randomUUID().toString();
    }

    public void isUser(String username) throws BadRequestException, DataAccessException {
        if (username == null) {
            throw new BadRequestException("Error: bad request (no username given)");
        }

        if (userDao.find(username) == null){
            throw new NotAuthException("Error: Username or password is incorrect)");
        }

    }

    public AuthData validatePassword(LoginData loginData) throws BadRequestException, DataAccessException {

        // unpack info
        String username = loginData.username();
        String password = loginData.password();

        // check if any of LoginData fields are null
        if (username == null || password == null) {
            throw new BadRequestException("Error: bad request (no username or password given)");
        }

        // get real password
        String realPass = null;

        realPass = userDao.find(username).password();  // this is the hashed password

        // check if passwords match
        if (!BCrypt.checkpw(password, realPass)) {
            throw new NotAuthException("Error: Failed to Login (incorrect username or password)");
        }

        // make new authData and save it
        AuthData authData = new AuthData(username, makeAuthToken());

        authDao.save(authData);

        return authData;
    }

    public void validateAuth(String authToken) throws BadRequestException, DataAccessException{

        // check if authToken is null
        if (authToken == null) {
            throw new BadRequestException("Error: bad request");
        }

        // check if data returned is null, ie unauthorized
        if (authDao.find(authToken) == null) {
            throw new NotAuthException("Error: unauthorized");
        }
    }

    public void removeAuth(String authToken) throws DataAccessException{
        authDao.remove(authToken);
    }
}

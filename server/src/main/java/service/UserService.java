package service;

import dataaccess.UserDAO;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDao = new UserDAO();

    public void registerUser(String username, String password, String email){

        // check if username is taken
        if (userDao.find(username) != null) {
            //TODO
            // raise error "username already taken"
        }

        // if not taken then save
        userDao.save(new UserData(username, password, email));

    }

    public UserData getUserData(String username){
        return userDao.find(username);
    }

    public static String makeAuthToken(){
        return UUID.randomUUID().toString();
    }


}

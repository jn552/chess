package dataaccess;

import model.AuthData;

public class AuthDAOMemory implements AuthDAOInterface{

    @ Override
    public AuthData find(String authToken){
        //TODO
        // find the ret of auth data from the auth token
        return null;  //return null means not authorized
    }

    @ Override
    public void save(AuthData authData){
        //TODO
        // save auth data for a single person in record class (Authdata) to the db?
    }
}

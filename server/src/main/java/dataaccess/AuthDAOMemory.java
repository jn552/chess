package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class AuthDAOMemory implements AuthDAOInterface{

    private final Map<String, AuthData> authHash = new HashMap<>();

    @ Override
    public AuthData find(String authToken){
        return authHash.get(authToken);  //return null means not authorized
    }

    @ Override
    public void save(AuthData authData){
        authHash.put(authData.authToken(), authData);
    }

    @ Override
    public void clear(){
        authHash.clear();
    }

    @ Override
    public void remove(String authToken){
        authHash.remove(authToken);
    }
}

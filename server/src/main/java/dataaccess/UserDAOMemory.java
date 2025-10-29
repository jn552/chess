package dataaccess;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class UserDAOMemory implements UserDAOInterface {

    private final Map<String, UserData> userHash = new HashMap<>();

    @ Override
    public UserData find(String username){
        return userHash.get(username);  //return null means user DNE
    }

    @ Override

    public void save(UserData userData) throws DataAccessException {
        userHash.put(userData.username(), userData);
    }

    @ Override
    public void clear(){
        userHash.clear();
    }
}

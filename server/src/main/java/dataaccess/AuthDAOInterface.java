package dataaccess;

import model.AuthData;

public interface AuthDAOInterface {
    void save(AuthData authData);
    AuthData find(String authToken);
}

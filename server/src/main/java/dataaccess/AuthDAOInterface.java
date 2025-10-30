package dataaccess;

import model.AuthData;

public interface AuthDAOInterface {
    void save(AuthData authData) throws DataAccessException;
    AuthData find(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
    void remove(String authToken) throws DataAccessException;
}

package dataaccess;

import model.UserData;

public interface UserDAOInterface {
    void save(UserData user) throws DataAccessException;
    UserData find(String username) throws DataAccessException;
    void clear();
}

package dataaccess;

import model.UserData;

public interface UserDAOInterface {
    void save(UserData user);
    UserData find(String username);
    void clear();
}

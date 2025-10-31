package dataaccess;


import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOSQLTest {

    // instantiate DAO
    UserDAOSQL userDao = new UserDAOSQL();

    // instantiate things to add to DAOs
    UserData userData = new UserData("jeremy", "hashedpasswork", "email");
    UserData userData2 = new UserData("jeremy22", "he", "email2");

    UserDAOSQLTest() throws DataAccessException {
    }

    @Test
    void save() throws DataAccessException {
        userDao.clear();
        userDao.save(userData);
        assert userDao.find("jeremy") != null;
    }

    @Test
    void find() throws DataAccessException {
        userDao.clear();
        userDao.save(userData);
        assert userDao.find("jeremy").email().equals("email");

    }

    @Test
    void clear() throws DataAccessException {
        userDao.save(new UserData("hoehgohes", "ebieue", "efee"));
        userDao.clear();
        assert userDao.find("hoehgohes") == null;
    }

    @Test
    void saveNegative() {

        assertThrows(DataAccessException.class, () -> {
            // no duplicates
            userDao.clear();
            userDao.save(userData2);
            userDao.save(userData2);
        });
    }

    @Test
    void findNegative() {
        assertDoesNotThrow(() -> {
            // makes sure that finding something non-existent doesn't break system
            userDao.clear();
            userDao.find("ntoinuserclearly");
        });
    }

    @Test
    void clearNegative() {
        // testing clearing nothing doesn't throw an error
        assertDoesNotThrow(() -> {
            userDao.clear();
            userDao.clear();
        });
    }
}
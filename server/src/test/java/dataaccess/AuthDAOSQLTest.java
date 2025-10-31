package dataaccess;

import model.AuthData;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOSQLTest {

    // instantiate DAO
    AuthDAOSQL authDao = new AuthDAOSQL();

    // instantiate things to add to DAOs
    AuthData auth2 = new AuthData("jeremy2", "hello");

    AuthDAOSQLTest() throws DataAccessException {
    }

    @Test
    void find() throws DataAccessException {
        authDao.clear();
        authDao.save(auth2);
        assert authDao.find("hello").equals(auth2);
    }

    @Test
    void findNegative() throws DataAccessException {
        authDao.clear();
        authDao.save(auth2);
        // making sure its not finding somethign thats not supposed to be there
        assert authDao.find("notanauthtoken") == null;
    }

    @Test
    void save() throws DataAccessException {
        authDao.clear();
        authDao.save(auth2);
        assert authDao.find("hello").username().equals("jeremy2");
    }

    @Test
    void saveNegative() {
        assertThrows(DataAccessException.class, () -> {
            // no duplicates
            authDao.clear();
            authDao.save(auth2);
            authDao.save(auth2);
        });
    }

    @Test
    void clear() throws DataAccessException {
        authDao.clear();
        authDao.save(auth2);
        authDao.clear();
        assert authDao.find("hello") == null;
    }

    @Test
    void clearNegative() throws DataAccessException {
        authDao.clear();
        authDao.save(auth2);
        authDao.clear();

        // making sure clearing an empty table will not throw an error
        assertDoesNotThrow(() -> {
            authDao.clear();
        });

    }

    @Test
    void remove() throws DataAccessException {
        authDao.clear();
        authDao.save(auth2);
        authDao.remove("hello");
        assert authDao.find("hello") == null;
    }

    @Test
    void removeNegative() {
        // making sure clearing an empty table will not throw an error
        assertDoesNotThrow(() -> {
            authDao.remove("hello");  // removing something thats not there doesn't throw error
        });
    }

}
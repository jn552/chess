package dataaccess;
import model.AuthData;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOSQLTest {

    // instantiate DAO
    AuthDAOMemory authDao = new AuthDAOMemory();

    // instantiate things to add to DAOs
    AuthData auth = new AuthData("jeremy", "34-53.6");
    AuthData auth2 = new AuthData("jeremy2", "hello");

    @Test
    void find() {
        authDao.save(auth2);
        assert authDao.find("hello").equals(auth2);
    }

    @Test
    void findNegative() {
        authDao.save(auth2);
        assert authDao.find("notanauthtoken") == null;
    }

    @Test
    void save() {
        authDao.save(auth2);

        var statement = "SELECT username, authToken FROM `auth` WHERE authToken=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setInt(1, 3);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("username");
                    String authenticationToken = rs.getString("authToken");

                    assert name.equals("jeremy2");
                    assert authenticationToken.equals("hello");
                }
            }
        }

        catch (Exception e) {
            System.out.println("Internal Error");
        }

    }

    @Test
    void saveNegative() {
        authDao.save(auth2);

        var statement = "SELECT username, authToken FROM `auth` WHERE authToken=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setString(1, "hello");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("username");
                    String authenticationToken = rs.getString("authToken");

                    assert !name.equals("cclearlynotthis");
                    assert authenticationToken.equals("random");

                }
            }
        }

        catch (Exception e) {
            System.out.println("Internal Error");
        }
    }

    @Test
    void clear() {
        authDao.save(auth2);
        authDao.clear();
        assert authDao.find("hello") == null;
    }

    @Test
    void clearNegative() {
        authDao.save(auth2);
        authDao.clear();

        // making sure clearing an empty table will not throw an error
        assertDoesNotThrow(() -> {
            authDao.clear();
        });

    }

    @Test
    void remove() {
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
package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class AuthDAOSQL implements AuthDAOInterface{

    public AuthDAOSQL() throws DataAccessException{
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  auth (
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            )
            """
        };

        DAOHelper.configureDatabase(createStatements);
    }

    @ Override
    public AuthData find(String authToken) throws DataAccessException{
        var statement = "SELECT username, authToken FROM `auth` WHERE authToken=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setString(1, authToken);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("username");
                    String authenticationToken = rs.getString("authToken");

                    return new AuthData(name, authenticationToken);
                }
            }
        }

        catch (Exception e) {
            throw new DataAccessException("Error: error finding authdata");
        }

        return null;
    }

    @ Override
    public void save(AuthData authData) throws DataAccessException{
        var statement = "INSERT INTO `auth` (username, authToken) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setString(1, authData.username());
            ps.setString(2, authData.authToken());
            ps.executeUpdate();
        }

        catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @ Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE `auth`";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        }

        catch (Exception e) {
            throw new DataAccessException("Error: error clearing authDao");
        }
    }

    @ Override
    public void remove(String authToken) throws DataAccessException{
        var statement = "DELETE FROM `auth` WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            ps.executeUpdate();
        }

        catch (Exception e){
            throw new DataAccessException("Error: error removing auth data");
        }
    }

}

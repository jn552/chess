package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


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
    public AuthData find(String authToken){
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
            System.out.println("Error with finding auhData");
        }

        return null;
    }

    @ Override
    public void save(AuthData authData){
        var statement = "INSERT INTO `auth` (username, authToken) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setString(1, authData.username());
            ps.setString(2, authData.authToken());
            ps.executeUpdate();
        }

        catch (Exception e) {
            System.out.println("Error with saving auhData");
        }
    }

    @ Override
    public void clear(){

    }

    @ Override
    public void remove(String authToken){

    }

}

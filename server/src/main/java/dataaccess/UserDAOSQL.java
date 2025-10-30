package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserDAOSQL implements UserDAOInterface{

    public UserDAOSQL() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            )
            """};
        DAOHelper.configureDatabase(createStatements);
    }

    @Override
    public void save(UserData user) throws DataAccessException{
        var statement = "INSERT INTO `user` (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setString(1, user.username());
            ps.setString(2, hashedPassword);
            ps.setString(3, user.email());
            ps.executeUpdate();
        }

        catch (Exception e) {
            throw new DataAccessException("Error: error saving user");
        }
    }

    @Override
    public UserData find (String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM `user` WHERE username=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("username");
                    String password = rs.getString("password");
                    String email = rs.getString("email");

                    return new UserData(name, password, email);
                }
            }
        }

        catch (Exception e) {
            throw new DataAccessException("Error: error finding user");
        }

        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE `user`";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        }

        catch (Exception e) {
            throw new DataAccessException("Error: error finding user");
        }
    }
}

package service;

import chess.ChessGame;
import dataaccess.AuthDAOMemory;
import dataaccess.GameDAOMemory;
import dataaccess.UserDAOMemory;
import exception.BadRequestException;
import exception.NotAuthException;
import exception.TakenException;
import model.AuthData;
import model.GameData;
import model.LoginData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    // instantiate some DAOs
    AuthDAOMemory authDao = new AuthDAOMemory();
    UserDAOMemory userDao = new UserDAOMemory();

    // instantiate things to add to DAOs
    GameData game = new GameData(12, "testwhite", "testblack", "testgame", new ChessGame());
    UserData user = new UserData("jeremy", "12345", "jeremy@email.com");
    AuthData auth = new AuthData("jeremy", "34-53.6");

    // instantiate the clear service
    UserService testUserService = new UserService(userDao, authDao);

    @Test
    void registerUser() throws BadRequestException {
        userDao.save(user);

        // test registering a taken username
        assertThrows(TakenException.class, () -> {
            testUserService.registerUser(user);
        });

        // add a different username
        UserData user2 = new UserData("jeremy2", "12345", "jer@email.com");
        testUserService.registerUser(user2);
        assert userDao.find("jeremy2") == user2;
    }

    @Test
    void isUser() {
        userDao.save(user);

        // test shouldn't throw any error since "jeremy" is a user
        assertDoesNotThrow(()-> {
            testUserService.isUser("jeremy");
        });

        // test case where user DNE and throws authError since the username is incorrect
        assertThrows(NotAuthException.class, () -> {
           testUserService.isUser("bob");
        });
    }

    @Test
    void validatePassword() {
        // test to see it catches an incorrect password
        userDao.save(user);
        assertThrows(NotAuthException.class, () -> {
            testUserService.validatePassword(new LoginData("jeremy", "99999"));
        });

        // test to see if it recognizes correct password
        assertDoesNotThrow(() ->{
            testUserService.validatePassword(new LoginData("jeremy", "12345"));
        });

    }

    @Test
    void validateAuth() {
        // testing a valid authToken
        authDao.save(auth);
        assertDoesNotThrow(() -> {
            testUserService.validateAuth("34-53.6");
        });

        //testing invalid authToken
        assertThrows(NotAuthException.class, () -> {
            testUserService.validateAuth("nottherighttoken");
        });
    }

    @Test
    void removeAuth() {
        authDao.save(auth);

        // testing removing when user DNE
        assertDoesNotThrow(() -> {
            testUserService.removeAuth("thisAuthTokenDNE");
        });

        // testing if user's auth was removed properly
        testUserService.removeAuth("34-53.6");
        assert authDao.find("34-53.6") == null;

    }
}
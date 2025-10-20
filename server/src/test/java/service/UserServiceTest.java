package service;

import chess.ChessGame;
import dataaccess.AuthDAOMemory;
import dataaccess.GameDAOMemory;
import dataaccess.UserDAOMemory;
import model.AuthData;
import model.GameData;
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
    void registerUser() {
    }

    @Test
    void isUser() {
    }

    @Test
    void validatePassword() {
    }

    @Test
    void validateAuth() {
    }

    @Test
    void removeAuth() {
    }
}
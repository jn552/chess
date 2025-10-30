package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class AuthDAOSQL implements AuthDAOInterface{

    public AuthDAOSQL() throws DataAccessException{
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            )
            """
        };

        DAOHelper.configureDatabase(createStatements);
    }

    @ Override
    public AuthData find(String authToken){
        return new AuthData(null, null);
    }

    @ Override
    public void save(AuthData authData){


    }

    @ Override
    public void clear(){

    }

    @ Override
    public void remove(String authToken){

    }

}

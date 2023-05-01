package com.geekbraines.chat_server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DAO {

    private Connection connection;

    public DAO(Connection connection) {
        this.connection = connection;
    }

    public boolean checkLoginPassword(String login, String pass) throws SQLException{
      String request = "SELECT id FROM user WHERE login = ? AND PASSWORD = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(request);
        preparedStatement.setString(1,login);
        preparedStatement.setString(2,pass);
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean result = resultSet.next();
        preparedStatement.close();
        return  result;
    }

}

package com.geekbraines.chat_server;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class SQLHandler {

    private static Connection connection;
    private static Statement stmt;


    public static Statement getStmt() {
        return stmt;
    }


    public static void connect() {
        try{
            Class.forName("org.sqlite.JDBC");
            connection= DriverManager.getConnection("jdbc:sqlite:dbUsers.db");
            stmt = connection.createStatement();
        } catch (SQLException e) {
            log.debug("Error connect to SQL Db.Error: {} ", e);
        } catch (ClassNotFoundException e) {
            log.debug("Error with sql Driver.Error: {} ", e);
        }
    }

    public static void disconnect(){
        try{
            stmt.close();
        } catch (SQLException e) {
            log.debug("Error disconnect from SQL DB. Error: {}", e);
        }
        try{
            connection.close();
        } catch (SQLException e) {
            log.debug("Error close connection to SQL DB. Error: {}", e);
        }
    }

    public static void addUser(String user, String password, String folder) {
        try{
            stmt.executeUpdate(
                    String.format("INSERT into users (name,password,folder) VALUES (%s, %s, '%s');"
                            , user, password, folder)
            );

        } catch (SQLException e) {
            log.debug("Error  add User. Error: {}", e);
        }
    }

    public static String getFolder(String user, String password) {
        try{
            ResultSet rs = stmt.executeQuery(String.format("SELECT folder FROM users WHERE name = '%s' AND password = '%s';",
                    user, password));
            if (!rs.next()){
                return null;
            }
            return rs.getString(1);
        } catch (SQLException e) {
            log.debug("Error get folder from BD. Error: {} ", e);
        }
            return null;
        }
    }





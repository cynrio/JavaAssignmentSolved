package com.maybank.customerApp.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hello-db";
        String user = "root";
        String password = "password123";
        return DriverManager.getConnection(url, user, password);
    }
}
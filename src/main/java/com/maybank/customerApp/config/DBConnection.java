package com.maybank.customerApp.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private final Connection connection;

    private DBConnection() throws SQLException {
        try {
            String url = "jdbc:mysql://localhost:3306/maybank-db";
            String user = "root";
            String password = "password123";
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new SQLException("Error connecting to the database", e);
        }
    }

    public static DBConnection getInstance() throws SQLException {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}

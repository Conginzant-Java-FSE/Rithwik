package org.example.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Singleton class for managing database connections
public class DBConnection {
    private static Connection connection;

    private DBConnection() {
    }

    private static Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/insta";
        String user = "root";
        String password = "K7#mR2!px9vL";
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException se) {
            System.out.println(se);
        }
        return connection;
    }

    public static Connection getInstance() {
        return getConnection();
    }

    public static void main(String[] args) throws Exception {
        Connection con1 = DBConnection.getConnection();
        Connection con2 = DBConnection.getConnection();
        System.out.println(con1 == con2);
    }
}
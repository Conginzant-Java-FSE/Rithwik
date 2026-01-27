package com.revpay.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Database configuration and connection manager
public class DatabaseConfig {

    private static final Logger logger = LogManager.getLogger(DatabaseConfig.class);
    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;
    private static Connection connection;
    private static boolean isInitialized = false;

    private DatabaseConfig() {
    }

    // Initialize database configuration from properties file
    public static void initialize() {
        if (isInitialized) {
            return;
        }
        try {
            Properties props = new Properties();
            InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties");
            if (input == null) {
                logger.error("Unable to find db.properties file");
                throw new RuntimeException("Database configuration file not found");
            }
            props.load(input);
            input.close();
            dbUrl = props.getProperty("db.url");
            dbUsername = props.getProperty("db.username");
            dbPassword = props.getProperty("db.password");
            // Load the MySQL driver
            String driver = props.getProperty("db.driver");
            Class.forName(driver);
            isInitialized = true;
            logger.info("Database configuration initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database configuration: " + e.getMessage());
            throw new RuntimeException("Database configuration failed", e);
        }
    }

    // Get a database connection (reuses existing connection if valid)
    public static Connection getConnection() throws SQLException {
        if (!isInitialized) {
            initialize();
        }
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                logger.info("Database connection established");
            }
            return connection;
        } catch (SQLException e) {
            logger.error("Failed to get database connection: " + e.getMessage());
            throw e;
        }
    }

    // Close a database connection safely

    // Close the persistent database connection
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    logger.info("Database connection closed");
                }
            } catch (SQLException e) {
                logger.error("Error closing database connection: " + e.getMessage());
            }
        }
    }

    // Helper to close resources (ResultSets, Statements) without closing the shared
    // connection
    public static void closeResources(Connection conn, java.sql.Statement stmt, java.sql.ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            // Do NOT close the connection here as it is shared
        } catch (Exception e) {
            logger.error("Error closing resources: " + e.getMessage());
        }
    }

    // Test database connection
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn.isValid(5);

        } catch (SQLException e) {
            logger.error("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}

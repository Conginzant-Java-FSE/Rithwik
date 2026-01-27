package com.revpay.ui;

import com.revpay.model.User;
import com.revpay.config.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Main console UI controller.
 * Manages the overall application flow.
 */
public class ConsoleUI {

    private static final Logger logger = LogManager.getLogger(ConsoleUI.class);

    private AuthMenu authMenu;
    private PersonalMenu personalMenu;
    private BusinessMenu businessMenu;

    public ConsoleUI() {
        this.authMenu = new AuthMenu();
        this.personalMenu = new PersonalMenu();
        this.businessMenu = new BusinessMenu();
    }

    /**
     * Start the console application.
     */
    public void start() {
        printWelcomeBanner();

        // Test database connection
        if (!testDatabaseConnection()) {
            System.out.println("\n[ERROR] Could not connect to database.");
            System.out.println("Please make sure MySQL is running and the database is set up.");
            System.out.println("Run the sql/schema.sql script to create the database.");
            return;
        }

        logger.info("RevPay application started");

        // Main application loop
        while (true) {
            try {
                // Show auth menu and get logged-in user
                User user = authMenu.showAuthMenu();

                if (user == null) {
                    // User chose to exit
                    printGoodbyeBanner();
                    break;
                }

                // Route to appropriate menu based on account type
                if (user.isBusinessAccount()) {
                    businessMenu.showMenu(user);
                } else {
                    personalMenu.showMenu(user);
                }

            } catch (Exception e) {
                logger.error("Application error: " + e.getMessage(), e);
                System.out.println("\n[ERROR] An unexpected error occurred. Please try again.");
            }
        }

        logger.info("RevPay application terminated");
    }

    /**
     * Test database connection at startup.
     */
    private boolean testDatabaseConnection() {
        try {
            System.out.println("\nConnecting to database...");
            DatabaseConfig.initialize();
            boolean connected = DatabaseConfig.testConnection();

            if (connected) {
                System.out.println("[OK] Database connected successfully!");
            }

            return connected;

        } catch (Exception e) {
            logger.error("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    /*
     Print welcome banner.
     */
    private void printWelcomeBanner() {
        System.out.println("Welcome to RevPay");
    }

    /*
     Print goodbye banner.
     */
    private void printGoodbyeBanner() {
        System.out.println();
        System.out.println("Thank you for using RevPay!");
        System.out.println();
    }
}

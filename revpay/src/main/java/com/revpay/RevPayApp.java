package com.revpay;

import com.revpay.ui.ConsoleUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// RevPay Application - Main Entry Point
// A console-based application for secure payments
public class RevPayApp {

    private static final Logger logger = LogManager.getLogger(RevPayApp.class);

    // Main method - Entry point of the application
    public static void main(String[] args) {
        try {
            logger.info("RevPay Application starting...");
            // Initialize and start the console UI
            ConsoleUI consoleUI = new ConsoleUI();
            consoleUI.start();
        } catch (Exception e) {
            logger.fatal("Fatal error starting application: " + e.getMessage(), e);
            System.out.println("\n[FATAL ERROR] Application failed to start: " + e.getMessage());
            System.out.println("Please check the logs for more details.");
            System.exit(1);
        }
    }
}

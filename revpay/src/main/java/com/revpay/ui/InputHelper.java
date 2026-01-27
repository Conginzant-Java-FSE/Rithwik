package com.revpay.ui;

import java.util.Scanner;

/**
 * Helper class for console input operations.
 * Provides methods to read different types of input with validation.
 */
public class InputHelper {

    private static Scanner scanner = new Scanner(System.in);

    /**
     * Read a string input from user.
     * 
     * @param prompt The prompt to display
     * @return The user's input
     */
    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Read a non-empty string from user.
     * Keeps prompting until user enters something.
     * 
     * @param prompt The prompt to display
     * @return Non-empty input string
     */
    public static String readRequiredString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("This field is required. Please enter a value.");
            }
        } while (input.isEmpty());
        return input;
    }

    /**
     * Read an integer from user.
     * 
     * @param prompt The prompt to display
     * @return The integer value
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid integer.");
            }
        }
    }

    /**
     * Read an integer within a range.
     * 
     * @param prompt The prompt to display
     * @param min    Minimum value (inclusive)
     * @param max    Maximum value (inclusive)
     * @return The integer value
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Please enter a number between " + min + " and " + max);
        }
    }

    /**
     * Read a double (for money amounts).
     * 
     * @param prompt The prompt to display
     * @return The double value
     */
    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a valid number.");
            }
        }
    }

    /**
     * Read a positive amount (for money).
     * 
     * @param prompt The prompt to display
     * @return The positive amount
     */
    public static double readPositiveAmount(String prompt) {
        while (true) {
            double amount = readDouble(prompt);
            if (amount > 0) {
                return amount;
            }
            System.out.println("Amount must be greater than zero.");
        }
    }

    /**
     * Read a yes/no confirmation.
     * 
     * @param prompt The prompt to display
     * @return true for yes, false for no
     */
    public static boolean readConfirmation(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println("Please enter 'y' for yes or 'n' for no.");
        }
    }

    /**
     * Read a password (hidden input simulation - shows asterisks).
     * Note: Real password hiding requires console-specific code.
     * 
     * @param prompt The prompt to display
     * @return The password
     */
    public static String readPassword(String prompt) {
        // In a real console app, you'd use Console.readPassword()
        // For simplicity, we just read normally here
        return readRequiredString(prompt);
    }

    /**
     * Read a PIN (4-6 digits).
     * 
     * @param prompt The prompt to display
     * @return The PIN
     */
    public static String readPin(String prompt) {
        while (true) {
            System.out.print(prompt);
            String pin = scanner.nextLine().trim();

            if (pin.length() >= 4 && pin.length() <= 6 && pin.matches("\\d+")) {
                return pin;
            }
            System.out.println("PIN must be 4-6 digits.");
        }
    }

    /**
     * Display a menu and get user choice.
     * 
     * @param title   Menu title
     * @param options Array of menu options
     * @return The selected option number (1-based)
     */
    public static int showMenu(String title, String[] options) {
        System.out.println("\n" + title);

        for (int i = 0; i < options.length; i++) {
            System.out.println("  " + (i + 1) + ". " + options[i]);
        }

        System.out.println("-".repeat(50));

        return readInt("Enter your choice: ", 1, options.length);
    }

    /**
     * Display a line separator.
     */
    public static void printLine() {
        System.out.println("-".repeat(50));
    }

    /**
     * Display a header.
     */
    public static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  " + title);
        System.out.println("=".repeat(50));
    }

    /**
     * Display a success message.
     */
    public static void printSuccess(String message) {
        System.out.println("\n[SUCCESS] " + message);
    }

    /**
     * Display an error message.
     */
    public static void printError(String message) {
        System.out.println("\n[ERROR] " + message);
    }

    /**
     * Display a warning message.
     */
    public static void printWarning(String message) {
        System.out.println("\n[WARNING] " + message);
    }

    /**
     * Display an info message.
     */
    public static void printInfo(String message) {
        System.out.println("\n[INFO] " + message);
    }

    /**
     * Wait for user to press Enter.
     */
    public static void waitForEnter() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Clear the screen (works on most terminals).
     */
    public static void clearScreen() {
        // Print multiple newlines as a simple clear
        System.out.println("\n".repeat(5));
    }

    /**
     * Format currency for display.
     */
    public static String formatCurrency(double amount) {
        return String.format("$%,.2f", amount);
    }
}

package com.revpay.ui;

import com.revpay.model.User;
import com.revpay.security.SessionManager;
import com.revpay.service.AuthService;
import com.revpay.util.ValidationUtils;

/**
 * Authentication menu for login and registration.
 */
public class AuthMenu {

    private AuthService authService;
    private SessionManager sessionManager;

    public AuthMenu() {
        this.authService = new AuthService();
        this.sessionManager = SessionManager.getInstance();
    }

    /**
     * Show the main authentication menu.
     */
    public User showAuthMenu() {
        while (true) {
            String[] options = {
                    "Login",
                    "Register Personal Account",
                    "Register Business Account",
                    "Forgot Password",
                    "Exit"
            };

            int choice = InputHelper.showMenu("Welcome to RevPay", options);

            switch (choice) {
                case 1:
                    User user = handleLogin();
                    if (user != null)
                        return user;
                    break;
                case 2:
                    user = handlePersonalRegistration();
                    if (user != null)
                        return user;
                    break;
                case 3:
                    user = handleBusinessRegistration();
                    if (user != null)
                        return user;
                    break;
                case 4:
                    handleForgotPassword();
                    break;
                case 5:
                    return null;
            }
        }
    }

    /**
     * Handle user login.
     */
    private User handleLogin() {
        InputHelper.printHeader("Login to RevPay");

        String identifier = InputHelper.readRequiredString("Email or Phone: ");
        String password = InputHelper.readPassword("Password: ");

        User user = authService.login(identifier, password);

        if (user != null) {
            InputHelper.printSuccess("Welcome back, " + user.getFullName() + "!");
            return user;
        } else {
            InputHelper.printError("Login failed. Invalid credentials.");
            return null;
        }
    }

    /**
     * Handle personal account registration.
     */
    private User handlePersonalRegistration() {
        InputHelper.printHeader("Personal Account Registration");

        // Get user details
        String fullName = InputHelper.readRequiredString("Full Name: ");

        String email;
        do {
            email = InputHelper.readRequiredString("Email: ");
            if (!ValidationUtils.isValidEmail(email)) {
                InputHelper.printError("Invalid email format. Please try again.");
            } else if (authService.isEmailTaken(email)) {
                InputHelper.printError("Email is already registered. Please use a different email.");
                email = null;
            }
        } while (email == null || !ValidationUtils.isValidEmail(email));

        String phone;
        do {
            phone = InputHelper.readRequiredString("Phone Number (10-15 digits): ");
            phone = ValidationUtils.formatPhone(phone);
            if (!ValidationUtils.isValidPhone(phone)) {
                InputHelper.printError("Invalid phone format. Please enter 10-15 digits.");
            } else if (authService.isPhoneTaken(phone)) {
                InputHelper.printError("Phone is already registered. Please use a different number.");
                phone = null;
            }
        } while (phone == null || !ValidationUtils.isValidPhone(phone));

        String password;
        do {
            password = InputHelper.readPassword("Password (min 8 chars, letters & numbers): ");
            if (!ValidationUtils.isValidPassword(password)) {
                InputHelper.printError("Password must be at least 8 characters with letters and numbers.");
            }
        } while (!ValidationUtils.isValidPassword(password));

        String confirmPassword = InputHelper.readPassword("Confirm Password: ");
        if (!password.equals(confirmPassword)) {
            InputHelper.printError("Passwords do not match.");
            return null;
        }

        // Security question
        InputHelper.printInfo("Set up a security question for password recovery:");
        String[] questions = {
                "What is your pet's name?",
                "What is your mother's maiden name?",
                "What city were you born in?",
                "What was your first school?"
        };

        System.out.println("\nSelect a security question:");
        for (int i = 0; i < questions.length; i++) {
            System.out.println("  " + (i + 1) + ". " + questions[i]);
        }
        int qChoice = InputHelper.readInt("Choose question: ", 1, questions.length);
        String securityQuestion = questions[qChoice - 1];
        String securityAnswer = InputHelper.readRequiredString("Your Answer: ");

        // Register user
        User user = authService.registerPersonalAccount(
                fullName, email, phone, password,
                securityQuestion, securityAnswer);

        if (user != null) {
            InputHelper.printSuccess("Account created successfully! Welcome to RevPay, " + fullName + "!");

            // Auto-login
            authService.login(email, password);
            return user;
        } else {
            InputHelper.printError("Registration failed. Please try again.");
            return null;
        }
    }

    /**
     * Handle business account registration.
     */
    private User handleBusinessRegistration() {
        InputHelper.printHeader("Business Account Registration");

        InputHelper.printInfo("First, let's set up your personal details...");

        // Get personal details
        String fullName = InputHelper.readRequiredString("Your Full Name: ");

        String email;
        do {
            email = InputHelper.readRequiredString("Email: ");
            if (!ValidationUtils.isValidEmail(email)) {
                InputHelper.printError("Invalid email format.");
            } else if (authService.isEmailTaken(email)) {
                InputHelper.printError("Email is already registered.");
                email = null;
            }
        } while (email == null || !ValidationUtils.isValidEmail(email));

        String phone;
        do {
            phone = InputHelper.readRequiredString("Phone Number: ");
            phone = ValidationUtils.formatPhone(phone);
            if (!ValidationUtils.isValidPhone(phone)) {
                InputHelper.printError("Invalid phone format.");
            } else if (authService.isPhoneTaken(phone)) {
                InputHelper.printError("Phone is already registered.");
                phone = null;
            }
        } while (phone == null || !ValidationUtils.isValidPhone(phone));

        String password;
        do {
            password = InputHelper.readPassword("Password: ");
        } while (!ValidationUtils.isValidPassword(password));

        String confirmPassword = InputHelper.readPassword("Confirm Password: ");
        if (!password.equals(confirmPassword)) {
            InputHelper.printError("Passwords do not match.");
            return null;
        }

        // Security question
        String securityQuestion = "What is your pet's name?";
        String securityAnswer = InputHelper.readRequiredString("Security Q - " + securityQuestion + ": ");

        // Get business details
        InputHelper.printInfo("\nNow, let's set up your business details...");

        String businessName = InputHelper.readRequiredString("Business Name: ");

        String[] businessTypes = { "Retail", "Restaurant", "Services", "Technology", "Healthcare", "Other" };
        System.out.println("\nBusiness Type:");
        for (int i = 0; i < businessTypes.length; i++) {
            System.out.println("  " + (i + 1) + ". " + businessTypes[i]);
        }
        int typeChoice = InputHelper.readInt("Choose type: ", 1, businessTypes.length);
        String businessType = businessTypes[typeChoice - 1];

        String taxId = InputHelper.readRequiredString("Tax ID / EIN: ");
        String address = InputHelper.readRequiredString("Business Address: ");

        // Register business account
        User user = authService.registerBusinessAccount(
                fullName, email, phone, password,
                securityQuestion, securityAnswer,
                businessName, businessType, taxId, address);

        if (user != null) {
            InputHelper.printSuccess("Business account created! Welcome to RevPay, " + businessName + "!");
            authService.login(email, password);
            return user;
        } else {
            InputHelper.printError("Registration failed. Please try again.");
            return null;
        }
    }

    /**
     * Handle forgot password flow.
     */
    private void handleForgotPassword() {
        InputHelper.printHeader("Password Recovery");

        String identifier = InputHelper.readRequiredString("Enter your Email or Phone: ");

        String question = authService.getSecurityQuestion(identifier);

        if (question == null) {
            InputHelper.printError("Account not found.");
            return;
        }

        System.out.println("\nAnswer your security question:");
        System.out.println("Q: " + question);
        String answer = InputHelper.readRequiredString("Your Answer: ");

        String newPassword;
        do {
            newPassword = InputHelper.readPassword("New Password: ");
        } while (!ValidationUtils.isValidPassword(newPassword));

        String confirmPassword = InputHelper.readPassword("Confirm Password: ");
        if (!newPassword.equals(confirmPassword)) {
            InputHelper.printError("Passwords do not match.");
            return;
        }

        boolean recovered = authService.recoverPassword(identifier, answer, newPassword);

        if (recovered) {
            InputHelper.printSuccess("Password reset successfully! You can now login with your new password.");
        } else {
            InputHelper.printError("Password recovery failed. Please check your answer and try again.");
        }
    }
}

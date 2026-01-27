package com.revpay.service;

import com.revpay.dao.BusinessAccountDAO;
import com.revpay.dao.UserDAO;
import com.revpay.model.BusinessAccount;
import com.revpay.model.User;
import com.revpay.security.PasswordUtils;
import com.revpay.security.SessionManager;
import com.revpay.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Authentication service for user registration, login, and security
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);
    private UserDAO userDAO;
    private BusinessAccountDAO businessAccountDAO;
    private SessionManager sessionManager;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.businessAccountDAO = new BusinessAccountDAO();
        this.sessionManager = SessionManager.getInstance();
    }

    // Register a new personal account
    public User registerPersonalAccount(String fullName, String email, String phone, String password,
            String securityQuestion, String securityAnswer) {
        try {
            // Check if email already exists
            if (userDAO.emailExists(email)) {
                logger.warn("Registration failed: Email already exists - " + email);
                System.out.println("Registration failed: Email already exists - " + email);
                return null;
            }
            // Check if phone already exists
            if (userDAO.phoneExists(phone)) {
                logger.warn("Registration failed: Phone already exists - " + phone);
                System.out.println("Registration failed: Phone already exists - " + phone);
                return null;
            }
            // Create user
            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPasswordHash(PasswordUtils.hashPassword(password));
            user.setSecurityQuestion(securityQuestion);
            user.setSecurityAnswer(securityAnswer);
            user.setAccountType(Constants.ACCOUNT_TYPE_PERSONAL);
            user.setWalletBalance(0.0);
            // Save to database
            User createdUser = userDAO.createUser(user);
            if (createdUser != null) {
                logger.info("Personal account registered: " + email);
                System.out.println("Welcome to RevPay! Personal account registered: " + email);
            }
            return createdUser;
        } catch (Exception e) {
            logger.error("Error registering personal account: " + e.getMessage());
            System.err.println("Error registering personal account: " + e.getMessage());
            throw e;
        }
    }

    // Register a new business account
    public User registerBusinessAccount(String fullName, String email, String phone, String password,
            String securityQuestion, String securityAnswer, String businessName, String businessType, String taxId,
            String address) {
        try {
            // Check if email/phone exists
            if (userDAO.emailExists(email)) {
                logger.warn("Registration failed: Email already exists - " + email);
                System.out.println("Registration failed: Email already exists - " + email);
                return null;
            }
            if (userDAO.phoneExists(phone)) {
                logger.warn("Registration failed: Phone already exists - " + phone);
                System.out.println("Registration failed: Phone already exists - " + phone);
                return null;
            }
            // Create user
            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPasswordHash(PasswordUtils.hashPassword(password));
            user.setSecurityQuestion(securityQuestion);
            user.setSecurityAnswer(securityAnswer);
            user.setAccountType(Constants.ACCOUNT_TYPE_BUSINESS);
            user.setWalletBalance(0.0);
            // Save user
            User createdUser = userDAO.createUser(user);
            if (createdUser != null) {
                // Create business account
                BusinessAccount businessAccount = new BusinessAccount();
                businessAccount.setUserId(createdUser.getUserId());
                businessAccount.setBusinessName(businessName);
                businessAccount.setBusinessType(businessType);
                businessAccount.setTaxId(taxId);
                businessAccount.setAddress(address);
                businessAccount.setVerified(false);
                businessAccountDAO.createBusinessAccount(businessAccount);
                logger.info("Business account registered: " + email);
                System.out.println("Business account registered: " + email + " (" + businessName + ")");
            }
            return createdUser;
        } catch (Exception e) {
            logger.error("Error registering business account: " + e.getMessage());
            System.err.println("Error registering business account: " + e.getMessage());
            throw e;
        }
    }

    // Login with email/phone and password
    public User login(String identifier, String password) {
        try {
            User user = userDAO.findByIdentifier(identifier);
            if (user == null) {
                logger.warn("Login failed: User not found - " + identifier);
                System.out.println("Login failed: User not found - " + identifier);
                return null;
            }

            // Check if account is locked
            if (user.isLockoutActive()) {
                logger.warn("Login failed: Account locked for " + identifier);
                System.out.println("Login failed: Account is temporarily locked. Try again later.");
                return null;
            }

            // Verify password
            if (PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
                // Successful login
                // Reset failed attempts if any
                if (user.getFailedLoginAttempts() > 0) {
                    userDAO.unlockAccount(user.getUserId());
                }

                userDAO.updateLastLogin(user.getUserId());
                sessionManager.startSession(user);
                logger.info("User logged in: " + identifier);
                System.out.println("User logged in: " + identifier);
                return user;
            } else {
                // Handle failed attempt
                int attempts = user.getFailedLoginAttempts() + 1;
                userDAO.updateFailedAttempts(user.getUserId(), attempts);
                logger.warn("Login failed: Invalid password for " + identifier + ". Attempt " + attempts + "/"
                        + Constants.MAX_FAILED_ATTEMPTS);

                if (attempts >= Constants.MAX_FAILED_ATTEMPTS) {
                    java.time.LocalDateTime unlockTime = java.time.LocalDateTime.now()
                            .plusMinutes(Constants.LOCKOUT_DURATION_MINUTES);
                    userDAO.lockAccount(user.getUserId(), unlockTime);
                    logger.warn("Account locked for user: " + identifier);
                    System.out.println("Login failed: Account locked due to too many failed attempts.");
                } else {
                    System.out.println("Login failed: Invalid password");
                }
                return null;
            }
        } catch (Exception e) {
            logger.error("Error during login: " + e.getMessage());
            System.err.println("Error during login: " + e.getMessage());
            throw e;
        }
    }

    // Logout current user
    public void logout() {
        sessionManager.endSession();
        logger.info("User logged out");
        System.out.println("User logged out");
    }

    // Change password with verification
    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                logger.warn("Change password failed: User not found - " + userId);
                return false;
            }
            // Verify current password
            if (!PasswordUtils.verifyPassword(currentPassword, user.getPasswordHash())) {
                logger.warn("Change password failed: Invalid current password for user " + userId);
                System.out.println("Password change failed: Invalid current password");
                return false;
            }
            // Update password
            user.setPasswordHash(PasswordUtils.hashPassword(newPassword));
            userDAO.updateUser(user);
            logger.info("Password changed successfully for user: " + userId);
            System.out.println("Password changed successfully");
            return true;
        } catch (Exception e) {
            logger.error("Error changing password: " + e.getMessage());
            System.err.println("Error changing password: " + e.getMessage());
            throw e;
        }
    }

    // Recover password using security question
    public boolean recoverPassword(String identifier, String answer, String newPassword) {
        try {
            User user = userDAO.findByIdentifier(identifier);
            if (user == null) {
                logger.warn("Password recovery failed: User not found - " + identifier);
                System.out.println("Password recovery failed: User not found");
                return false;
            }
            // Verify security answer (case-insensitive)
            if (!user.getSecurityAnswer().equalsIgnoreCase(answer.trim())) {
                logger.warn("Password recovery failed: Invalid security answer for " + identifier);
                System.out.println("Password recovery failed: Invalid security answer");
                return false;
            }
            // Update password
            user.setPasswordHash(PasswordUtils.hashPassword(newPassword));
            userDAO.updateUser(user);
            logger.info("Password recovered for user: " + identifier);
            System.out.println("Password recovered successfully");
            return true;
        } catch (Exception e) {
            logger.error("Error recovering password: " + e.getMessage());
            System.err.println("Error recovering password: " + e.getMessage());
            throw e;
        }
    }

    // Get security question for password recovery
    public String getSecurityQuestion(String identifier) {
        User user = userDAO.findByIdentifier(identifier);
        if (user == null) {
            return null;
        }
        return user.getSecurityQuestion();
    }

    // Check if email exists
    public boolean isEmailTaken(String email) {
        return userDAO.emailExists(email);
    }

    // Check if phone exists
    public boolean isPhoneTaken(String phone) {
        return userDAO.phoneExists(phone);
    }

    // Get user by ID
    public User getUserById(int userId) {
        return userDAO.findById(userId);
    }

    // Get business account for a user
    public BusinessAccount getBusinessAccount(int userId) {
        return businessAccountDAO.findByUserId(userId);
    }

    // Set Transaction PIN
    public boolean setTransactionPin(int userId, String pin) {
        try {
            String pinHash = PasswordUtils.hashPin(pin);
            return userDAO.updateTransactionPin(userId, pinHash);
        } catch (Exception e) {
            logger.error("Error setting PIN: " + e.getMessage());
            return false;
        }
    }

    // Verify Transaction PIN
    public boolean verifyTransactionPin(int userId, String pin) {
        User user = userDAO.findById(userId);
        if (user == null || user.getTransactionPinHash() == null) {
            return false;
        }
        return PasswordUtils.verifyPin(pin, user.getTransactionPinHash());
    }
}

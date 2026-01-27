package com.revpay.security;

import com.revpay.model.User;
import java.time.LocalDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Simple session manager for tracking logged-in user with timeout
public class SessionManager {

    private static final Logger logger = LogManager.getLogger(SessionManager.class);
    private static final int SESSION_TIMEOUT_MINUTES = 30; // 30 minutes inactivity timeout
    private User currentUser;
    private LocalDateTime lastActivityTime;
    private static SessionManager instance;

    private SessionManager() {
    }

    // Get the singleton instance
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Start a new session for a user
    public void startSession(User user) {
        this.currentUser = user;
        this.lastActivityTime = LocalDateTime.now();
        logger.info("Session started for user: " + user.getEmail());
        System.out.println("Session started for: " + user.getEmail());
    }

    // End the current session
    public void endSession() {
        if (currentUser != null) {
            logger.info("Session ended for user: " + currentUser.getEmail());
            System.out.println("Session ended for: " + currentUser.getEmail());
        }
        this.currentUser = null;
        this.lastActivityTime = null;
    }

    // Check if session is active and not timed out
    public boolean isSessionActive() {
        if (currentUser == null) {
            return false;
        }
        // Check for timeout
        if (checkTimeout()) {
            logger.warn("Session timed out for user: " + currentUser.getEmail());
            System.out.println("[SESSION] Your session has expired due to inactivity.");
            endSession();
            return false;
        }
        return true;
    }

    // Check if session has timed out
    public boolean checkTimeout() {
        if (lastActivityTime == null) {
            return true;
        }
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(SESSION_TIMEOUT_MINUTES);
        return lastActivityTime.isBefore(timeoutThreshold);
    }

    // Update activity time to prevent timeout
    public void updateActivity() {
        this.lastActivityTime = LocalDateTime.now();
        logger.debug("Activity updated for session");
    }

    // Get the current logged-in user
    public User getCurrentUser() {
        return currentUser;
    }

    // Update the current user's data
    public void updateCurrentUser(User user) {
        if (currentUser != null && currentUser.getUserId() == user.getUserId()) {
            this.currentUser = user;
            logger.debug("Current user data updated");
        }
    }

    // Check if the current user is a business account
    public boolean isBusinessUser() {
        return currentUser != null && currentUser.isBusinessAccount();
    }
}

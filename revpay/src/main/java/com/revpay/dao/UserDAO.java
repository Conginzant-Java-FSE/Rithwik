package com.revpay.dao;

import com.revpay.config.DatabaseConfig;
import com.revpay.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Data Access Object for User operations
public class UserDAO {

    private static final Logger logger = LogManager.getLogger(UserDAO.class);

    // Create a new user in the database
    public User createUser(User user) {
        String sql = "INSERT INTO users (email, phone, full_name, password_hash, transaction_pin_hash, security_question, security_answer, wallet_balance, account_type, failed_login_attempts, lockout_until) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getTransactionPinHash());
            stmt.setString(6, user.getSecurityQuestion());
            stmt.setString(7, user.getSecurityAnswer());
            stmt.setDouble(8, user.getWalletBalance());
            stmt.setString(9, user.getAccountType());
            stmt.setInt(10, user.getFailedLoginAttempts());
            stmt.setTimestamp(11, user.getLockoutUntil() != null ? Timestamp.valueOf(user.getLockoutUntil()) : null);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                    user.setCreatedAt(LocalDateTime.now());
                    logger.info("User created with ID: " + user.getUserId());
                }
            }
            return user;
        } catch (SQLException e) {
            logger.error("Error creating user: " + e.getMessage());
            throw new RuntimeException("Failed to create user", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find a user by email
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding user by email: " + e.getMessage());
            throw new RuntimeException("Failed to find user", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find a user by phone number
    public User findByPhone(String phone) {
        String sql = "SELECT * FROM users WHERE phone = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, phone);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding user by phone: " + e.getMessage());
            throw new RuntimeException("Failed to find user", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find a user by their ID
    public User findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding user by ID: " + e.getMessage());
            throw new RuntimeException("Failed to find user", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find a user by email, phone, or account ID
    public User findByIdentifier(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return null;
        }
        // Try to find by email first
        User user = findByEmail(identifier);
        if (user != null)
            return user;
        // Try to find by phone
        user = findByPhone(identifier);
        if (user != null)
            return user;
        // Try to find by user ID (if it's a number)
        try {
            int userId = Integer.parseInt(identifier.trim());
            user = findById(userId);
            if (user != null)
                return user;
        } catch (NumberFormatException e) {
            // Not a number, ignore
        }
        return null;
    }

    // Update a user's information
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET email = ?, phone = ?, full_name = ?, password_hash = ?, wallet_balance = ?, last_login = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getPasswordHash());
            stmt.setDouble(5, user.getWalletBalance());
            stmt.setTimestamp(6, user.getLastLogin() != null ? Timestamp.valueOf(user.getLastLogin()) : null);
            stmt.setInt(7, user.getUserId());
            int affectedRows = stmt.executeUpdate();
            logger.info("User updated: " + user.getUserId());
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating user: " + e.getMessage());
            throw new RuntimeException("Failed to update user", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    // Update user's wallet balance
    public boolean updateWalletBalance(int userId, double newBalance) {
        String sql = "UPDATE users SET wallet_balance = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, newBalance);
            stmt.setInt(2, userId);
            int affectedRows = stmt.executeUpdate();
            logger.info("Wallet balance updated for user: " + userId);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating wallet balance: " + e.getMessage());
            throw new RuntimeException("Failed to update wallet balance", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    // Update last login time
    public boolean updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating last login: " + e.getMessage());
            throw new RuntimeException("Failed to update last login", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    // Verify Transaction PIN
    public boolean verifyTransactionPin(int userId, String pinHash) {
        // This is a placeholder since we verify by hash matching in service,
        // but we might want a direct DB check. For now, we fetch user and check in
        // service.
        return true;
    }

    // Update Transaction PIN
    public boolean updateTransactionPin(int userId, String pinHash) {
        String sql = "UPDATE users SET transaction_pin_hash = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pinHash);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating transaction PIN: " + e.getMessage());
            return false;
        }
    }

    // Update failed login attempts
    public void updateFailedAttempts(int userId, int attempts) {
        String sql = "UPDATE users SET failed_login_attempts = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attempts);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating failed attempts: " + e.getMessage());
        }
    }

    // Lock account
    public void lockAccount(int userId, LocalDateTime lockoutUntil) {
        String sql = "UPDATE users SET lockout_until = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(lockoutUntil));
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error locking account: " + e.getMessage());
        }
    }

    // Unlock account (reset attempts and lockout time)
    public void unlockAccount(int userId) {
        String sql = "UPDATE users SET failed_login_attempts = 0, lockout_until = NULL WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error unlocking account: " + e.getMessage());
        }
    }

    // Map ResultSet to User object
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setFullName(rs.getString("full_name"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setTransactionPinHash(rs.getString("transaction_pin_hash"));
        user.setSecurityQuestion(rs.getString("security_question"));
        user.setSecurityAnswer(rs.getString("security_answer"));
        user.setWalletBalance(rs.getDouble("wallet_balance"));
        user.setAccountType(rs.getString("account_type"));
        user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        Timestamp lockoutUntil = rs.getTimestamp("lockout_until");
        if (lockoutUntil != null) {
            user.setLockoutUntil(lockoutUntil.toLocalDateTime());
        }
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        // Security fields (Removed)

        return user;
    }

    // Close database resources
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null) {
                // conn.close(); // Do not close shared connection
            }
        } catch (SQLException e) {
            logger.error("Error closing resources: " + e.getMessage());
        }
    }

    // Check if email already exists
    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

    // Check if phone already exists
    public boolean phoneExists(String phone) {
        return findByPhone(phone) != null;
    }

}

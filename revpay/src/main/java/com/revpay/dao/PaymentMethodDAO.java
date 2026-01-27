package com.revpay.dao;

import com.revpay.config.DatabaseConfig;
import com.revpay.model.PaymentMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Data Access Object for PaymentMethod operations
public class PaymentMethodDAO {

    private static final Logger logger = LogManager.getLogger(PaymentMethodDAO.class);

    // Create a new payment method
    public PaymentMethod createPaymentMethod(PaymentMethod method) {
        String sql = "INSERT INTO payment_methods (user_id, card_type, card_last_four, encrypted_card_number, card_holder_name, expiry_month, expiry_year, is_default) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // If this is set as default, unset all others first
            if (method.isDefault()) {
                unsetAllDefaults(method.getUserId());
            }
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, method.getUserId());
            stmt.setString(2, method.getCardType());
            stmt.setString(3, method.getCardLastFour());
            stmt.setString(4, method.getEncryptedCardNumber());
            stmt.setString(5, method.getCardHolderName());
            stmt.setString(6, method.getExpiryMonth());
            stmt.setString(7, method.getExpiryYear());
            stmt.setBoolean(8, method.isDefault());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    method.setPaymentId(rs.getInt(1));
                    method.setCreatedAt(LocalDateTime.now());
                    logger.info("Payment method created with ID: " + method.getPaymentId());
                }
            }
            return method;
        } catch (SQLException e) {
            logger.error("Error creating payment method: " + e.getMessage());
            throw new RuntimeException("Failed to create payment method", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find payment method by ID
    public PaymentMethod findById(int paymentId) {
        String sql = "SELECT * FROM payment_methods WHERE payment_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, paymentId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPaymentMethod(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding payment method: " + e.getMessage());
            throw new RuntimeException("Failed to find payment method", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Get all payment methods for a user
    public List<PaymentMethod> findByUserId(int userId) {
        String sql = "SELECT * FROM payment_methods WHERE user_id = ? ORDER BY is_default DESC, created_at DESC";
        List<PaymentMethod> methods = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                methods.add(mapResultSetToPaymentMethod(rs));
            }
            return methods;
        } catch (SQLException e) {
            logger.error("Error finding payment methods: " + e.getMessage());
            throw new RuntimeException("Failed to find payment methods", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Get default payment method for a user
    public PaymentMethod getDefaultPaymentMethod(int userId) {
        String sql = "SELECT * FROM payment_methods WHERE user_id = ? AND is_default = TRUE";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPaymentMethod(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding default payment method: " + e.getMessage());
            throw new RuntimeException("Failed to find default payment method", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Set a payment method as default
    public boolean setAsDefault(int paymentId, int userId) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            // Unset all defaults for this user
            unsetAllDefaults(userId, conn);
            // Set this one as default
            String sql = "UPDATE payment_methods SET is_default = TRUE WHERE payment_id = ? AND user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, paymentId);
            stmt.setInt(2, userId);
            int affectedRows = stmt.executeUpdate();
            conn.commit();
            logger.info("Payment method " + paymentId + " set as default");
            return affectedRows > 0;
        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                logger.error("Error rolling back: " + ex.getMessage());
            }
            logger.error("Error setting default payment method: " + e.getMessage());
            throw new RuntimeException("Failed to set default payment method", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error("Error closing connection: " + e.getMessage());
            }
        }
    }

    // Delete a payment method
    public boolean deletePaymentMethod(int paymentId) {
        String sql = "DELETE FROM payment_methods WHERE payment_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, paymentId);
            int affectedRows = stmt.executeUpdate();
            logger.info("Payment method deleted: " + paymentId);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting payment method: " + e.getMessage());
            throw new RuntimeException("Failed to delete payment method", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    // Unset all default payment methods for a user
    private void unsetAllDefaults(int userId) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            unsetAllDefaults(userId, conn);
        } catch (SQLException e) {
            logger.error("Error unsetting defaults: " + e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                logger.error("Error closing connection: " + e.getMessage());
            }
        }
    }

    // Unset all defaults using existing connection
    private void unsetAllDefaults(int userId, Connection conn) throws SQLException {
        String sql = "UPDATE payment_methods SET is_default = FALSE WHERE user_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.executeUpdate();
        stmt.close();
    }

    // Map ResultSet to PaymentMethod object
    private PaymentMethod mapResultSetToPaymentMethod(ResultSet rs) throws SQLException {
        PaymentMethod method = new PaymentMethod();
        method.setPaymentId(rs.getInt("payment_id"));
        method.setUserId(rs.getInt("user_id"));
        method.setCardType(rs.getString("card_type"));
        method.setCardLastFour(rs.getString("card_last_four"));
        method.setEncryptedCardNumber(rs.getString("encrypted_card_number"));
        method.setCardHolderName(rs.getString("card_holder_name"));
        method.setExpiryMonth(rs.getString("expiry_month"));
        method.setExpiryYear(rs.getString("expiry_year"));
        method.setDefault(rs.getBoolean("is_default"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            method.setCreatedAt(createdAt.toLocalDateTime());
        }
        return method;
    }

    // Close database resources
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null) {
                // conn.close(); // Shared connection
            }
        } catch (SQLException e) {
            logger.error("Error closing resources: " + e.getMessage());
        }
    }
}

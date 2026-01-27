package com.revpay.dao;

import com.revpay.config.DatabaseConfig;
import com.revpay.model.BusinessAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Data Access Object for BusinessAccount operations
public class BusinessAccountDAO {

    private static final Logger logger = LogManager.getLogger(BusinessAccountDAO.class);

    // Create a new business account
    public BusinessAccount createBusinessAccount(BusinessAccount business) {
        String sql = "INSERT INTO business_accounts (user_id, business_name, business_type, tax_id, address, is_verified) VALUES (?, ?, ?,?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, business.getUserId());
            stmt.setString(2, business.getBusinessName());
            stmt.setString(3, business.getBusinessType());
            stmt.setString(4, business.getTaxId());
            stmt.setString(5, business.getAddress());
            stmt.setBoolean(6, business.isVerified());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    business.setBusinessId(rs.getInt(1));
                    business.setCreatedAt(LocalDateTime.now());
                    logger.info("Business account created with ID: " + business.getBusinessId());
                }
            }
            return business;
        } catch (SQLException e) {
            logger.error("Error creating business account: " + e.getMessage());
            throw new RuntimeException("Failed to create business account", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find business account by ID
    public BusinessAccount findById(int businessId) {
        String sql = "SELECT * FROM business_accounts WHERE business_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, businessId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBusinessAccount(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding business account: " + e.getMessage());
            throw new RuntimeException("Failed to find business account", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find business account by user ID
    public BusinessAccount findByUserId(int userId) {
        String sql = "SELECT * FROM business_accounts WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBusinessAccount(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding business account by user: " + e.getMessage());
            throw new RuntimeException("Failed to find business account", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find all business accounts
    public List<BusinessAccount> findAll() {
        String sql = "SELECT * FROM business_accounts ORDER BY created_at DESC";
        List<BusinessAccount> businesses = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                businesses.add(mapResultSetToBusinessAccount(rs));
            }
            return businesses;
        } catch (SQLException e) {
            logger.error("Error finding all business accounts: " + e.getMessage());
            throw new RuntimeException("Failed to find business accounts", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Update business account
    public boolean updateBusinessAccount(BusinessAccount business) {
        String sql = "UPDATE business_accounts SET business_name = ?, business_type = ?, tax_id = ?, address = ?, is_verified = ? WHERE business_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, business.getBusinessName());
            stmt.setString(2, business.getBusinessType());
            stmt.setString(3, business.getTaxId());
            stmt.setString(4, business.getAddress());
            stmt.setBoolean(5, business.isVerified());
            stmt.setInt(6, business.getBusinessId());
            int affectedRows = stmt.executeUpdate();
            logger.info("Business account updated: " + business.getBusinessId());
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating business account: " + e.getMessage());
            throw new RuntimeException("Failed to update business account", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    // Map ResultSet to BusinessAccount object
    private BusinessAccount mapResultSetToBusinessAccount(ResultSet rs) throws SQLException {
        BusinessAccount business = new BusinessAccount();
        business.setBusinessId(rs.getInt("business_id"));
        business.setUserId(rs.getInt("user_id"));
        business.setBusinessName(rs.getString("business_name"));
        business.setBusinessType(rs.getString("business_type"));
        business.setTaxId(rs.getString("tax_id"));
        business.setAddress(rs.getString("address"));
        business.setVerified(rs.getBoolean("is_verified"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            business.setCreatedAt(createdAt.toLocalDateTime());
        }
        return business;
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

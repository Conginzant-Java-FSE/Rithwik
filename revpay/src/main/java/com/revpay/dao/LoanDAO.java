package com.revpay.dao;

import com.revpay.config.DatabaseConfig;
import com.revpay.model.Loan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Data Access Object for Loan operations
public class LoanDAO {

    private static final Logger logger = LogManager.getLogger(LoanDAO.class);

    // Create a new loan application
    public Loan createLoan(Loan loan) {
        String sql = "INSERT INTO loans (business_id, amount, purpose, status, interest_rate, term_months, monthly_payment, remaining_balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, loan.getBusinessId());
            stmt.setDouble(2, loan.getAmount());
            stmt.setString(3, loan.getPurpose());
            stmt.setString(4, loan.getStatus());
            stmt.setDouble(5, loan.getInterestRate());
            stmt.setInt(6, loan.getTermMonths());
            stmt.setDouble(7, loan.getMonthlyPayment());
            stmt.setDouble(8, loan.getRemainingBalance());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    loan.setLoanId(rs.getInt(1));
                    loan.setAppliedAt(LocalDateTime.now());
                    logger.info("Loan created with ID: " + loan.getLoanId());
                }
            }
            return loan;
        } catch (SQLException e) {
            logger.error("Error creating loan: " + e.getMessage());
            throw new RuntimeException("Failed to create loan", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find loan by ID
    public Loan findById(int loanId) {
        String sql = "SELECT l.*, b.business_name FROM loans l LEFT JOIN business_accounts b ON l.business_id = b.business_id WHERE l.loan_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, loanId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLoan(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding loan: " + e.getMessage());
            throw new RuntimeException("Failed to find loan", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find loans by business ID
    public List<Loan> findByBusinessId(int businessId) {
        String sql = "SELECT l.*, b.business_name FROM loans l LEFT JOIN business_accounts b ON l.business_id = b.business_id WHERE l.business_id = ? ORDER BY l.applied_at DESC";
        List<Loan> loans = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, businessId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
            return loans;
        } catch (SQLException e) {
            logger.error("Error finding loans: " + e.getMessage());
            throw new RuntimeException("Failed to find loans", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Update loan
    public boolean updateLoan(Loan loan) {
        String sql = "UPDATE loans SET status = ?, interest_rate = ?, monthly_payment = ?, remaining_balance = ?, approved_at = ? WHERE loan_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, loan.getStatus());
            stmt.setDouble(2, loan.getInterestRate());
            stmt.setDouble(3, loan.getMonthlyPayment());
            stmt.setDouble(4, loan.getRemainingBalance());
            if (loan.getApprovedAt() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(loan.getApprovedAt()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            stmt.setInt(6, loan.getLoanId());
            int affectedRows = stmt.executeUpdate();
            logger.info("Loan updated: " + loan.getLoanId());
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating loan: " + e.getMessage());
            throw new RuntimeException("Failed to update loan", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    // Map ResultSet to Loan object
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanId(rs.getInt("loan_id"));
        loan.setBusinessId(rs.getInt("business_id"));
        loan.setAmount(rs.getDouble("amount"));
        loan.setPurpose(rs.getString("purpose"));
        loan.setStatus(rs.getString("status"));
        loan.setInterestRate(rs.getDouble("interest_rate"));
        loan.setTermMonths(rs.getInt("term_months"));
        loan.setMonthlyPayment(rs.getDouble("monthly_payment"));
        loan.setRemainingBalance(rs.getDouble("remaining_balance"));
        Timestamp appliedAt = rs.getTimestamp("applied_at");
        if (appliedAt != null) {
            loan.setAppliedAt(appliedAt.toLocalDateTime());
        }
        Timestamp approvedAt = rs.getTimestamp("approved_at");
        if (approvedAt != null) {
            loan.setApprovedAt(approvedAt.toLocalDateTime());
        }
        try {
            loan.setBusinessName(rs.getString("business_name"));
        } catch (SQLException e) {
            // Column might not exist
        }
        return loan;
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

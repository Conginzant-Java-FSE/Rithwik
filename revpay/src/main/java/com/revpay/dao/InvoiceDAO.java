package com.revpay.dao;

import com.revpay.config.DatabaseConfig;
import com.revpay.model.Invoice;
import com.revpay.model.InvoiceItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Data Access Object for Invoice operations
public class InvoiceDAO {

    private static final Logger logger = LogManager.getLogger(InvoiceDAO.class);

    // Create a new invoice with its items
    public Invoice createInvoice(Invoice invoice) {
        String invoiceSql = "INSERT INTO invoices (business_id, customer_id, invoice_number, total_amount, status, due_date) VALUES (?, ?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO invoice_items (invoice_id, description, quantity, unit_price, total) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement invoiceStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            // Insert invoice
            invoiceStmt = conn.prepareStatement(invoiceSql, Statement.RETURN_GENERATED_KEYS);
            invoiceStmt.setInt(1, invoice.getBusinessId());
            invoiceStmt.setInt(2, invoice.getCustomerId());
            invoiceStmt.setString(3, invoice.getInvoiceNumber());
            invoiceStmt.setDouble(4, invoice.getTotalAmount());
            invoiceStmt.setString(5, invoice.getStatus());
            invoiceStmt.setDate(6, Date.valueOf(invoice.getDueDate()));
            int affectedRows = invoiceStmt.executeUpdate();
            if (affectedRows > 0) {
                rs = invoiceStmt.getGeneratedKeys();
                if (rs.next()) {
                    invoice.setInvoiceId(rs.getInt(1));
                    invoice.setCreatedAt(LocalDateTime.now());
                }
            }
            // Insert invoice items
            if (invoice.getItems() != null && !invoice.getItems().isEmpty()) {
                itemStmt = conn.prepareStatement(itemSql, Statement.RETURN_GENERATED_KEYS);
                for (InvoiceItem item : invoice.getItems()) {
                    itemStmt.setInt(1, invoice.getInvoiceId());
                    itemStmt.setString(2, item.getDescription());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getUnitPrice());
                    itemStmt.setDouble(5, item.getTotal());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }
            conn.commit();
            logger.info("Invoice created with ID: " + invoice.getInvoiceId());
            return invoice;
        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                logger.error("Error rolling back transaction: " + ex.getMessage());
            }
            logger.error("Error creating invoice: " + e.getMessage());
            throw new RuntimeException("Failed to create invoice", e);
        } finally {
            try {
                if (conn != null)
                    conn.setAutoCommit(true);
            } catch (SQLException e) {
            }
            closeResources(conn, invoiceStmt, rs);
            if (itemStmt != null)
                try {
                    itemStmt.close();
                } catch (SQLException e) {
                }
        }
    }

    // Find an invoice by ID with its items
    public Invoice findById(int invoiceId) {
        String sql = "SELECT i.*, b.business_name, u.full_name as customer_name FROM invoices i LEFT JOIN business_accounts b ON i.business_id = b.business_id LEFT JOIN users u ON i.customer_id = u.user_id WHERE i.invoice_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, invoiceId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                Invoice invoice = mapResultSetToInvoice(rs);
                invoice.setItems(findItemsByInvoiceId(invoiceId));
                return invoice;
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding invoice: " + e.getMessage());
            throw new RuntimeException("Failed to find invoice", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Find invoices by business ID
    public List<Invoice> findByBusinessId(int businessId) {
        String sql = "SELECT i.*, b.business_name, u.full_name as customer_name FROM invoices i LEFT JOIN business_accounts b ON i.business_id = b.business_id LEFT JOIN users u ON i.customer_id = u.user_id WHERE i.business_id = ? ORDER BY i.created_at DESC";
        return getInvoicesByQuery(sql, businessId);
    }

    // Find invoices by customer ID
    public List<Invoice> findByCustomerId(int customerId) {
        String sql = "SELECT i.*, b.business_name, u.full_name as customer_name FROM invoices i LEFT JOIN business_accounts b ON i.business_id = b.business_id LEFT JOIN users u ON i.customer_id = u.user_id WHERE i.customer_id = ? ORDER BY i.created_at DESC";
        return getInvoicesByQuery(sql, customerId);
    }

    // Find invoices by status for a business
    public List<Invoice> findByStatus(int businessId, String status) {
        String sql = "SELECT i.*, b.business_name, u.full_name as customer_name FROM invoices i LEFT JOIN business_accounts b ON i.business_id = b.business_id LEFT JOIN users u ON i.customer_id = u.user_id WHERE i.business_id = ? AND i.status = ? ORDER BY i.created_at DESC";
        List<Invoice> invoices = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, businessId);
            stmt.setString(2, status);
            rs = stmt.executeQuery();
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
            return invoices;
        } catch (SQLException e) {
            logger.error("Error finding invoices by status: " + e.getMessage());
            throw new RuntimeException("Failed to find invoices", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Update invoice status
    public boolean updateStatus(int invoiceId, String status) {
        String sql = "UPDATE invoices SET status = ? WHERE invoice_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, invoiceId);
            int affectedRows = stmt.executeUpdate();
            logger.info("Invoice " + invoiceId + " status updated to: " + status);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating invoice status: " + e.getMessage());
            throw new RuntimeException("Failed to update invoice", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    // Alias for updateStatus for backwards compatibility
    public boolean updateInvoiceStatus(int invoiceId, String status) {
        return updateStatus(invoiceId, status);
    }

    // Add an invoice item
    public boolean addInvoiceItem(InvoiceItem item) {
        String sql = "INSERT INTO invoice_items (invoice_id, description, quantity, unit_price, total) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, item.getInvoiceId());
            stmt.setString(2, item.getDescription());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getUnitPrice());
            stmt.setDouble(5, item.getTotal());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error adding invoice item: " + e.getMessage());
            throw new RuntimeException("Failed to add invoice item", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    // Find all invoices
    public List<Invoice> findAll() {
        String sql = "SELECT i.*, b.business_name, u.full_name as customer_name FROM invoices i LEFT JOIN business_accounts b ON i.business_id = b.business_id LEFT JOIN users u ON i.customer_id = u.user_id ORDER BY i.created_at DESC";
        List<Invoice> invoices = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
            return invoices;
        } catch (SQLException e) {
            logger.error("Error finding all invoices: " + e.getMessage());
            throw new RuntimeException("Failed to find invoices", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Get invoice items by invoice ID
    private List<InvoiceItem> findItemsByInvoiceId(int invoiceId) {
        String sql = "SELECT * FROM invoice_items WHERE invoice_id = ?";
        List<InvoiceItem> items = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, invoiceId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                InvoiceItem item = new InvoiceItem();
                item.setItemId(rs.getInt("item_id"));
                item.setInvoiceId(rs.getInt("invoice_id"));
                item.setDescription(rs.getString("description"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setTotal(rs.getDouble("total"));
                items.add(item);
            }
            return items;
        } catch (SQLException e) {
            logger.error("Error finding invoice items: " + e.getMessage());
            return items;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Generate a unique invoice number
    public String generateInvoiceNumber(int businessId) {
        String prefix = "INV-" + businessId + "-";
        String sql = "SELECT COUNT(*) + 1 as next_num FROM invoices WHERE business_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, businessId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int nextNum = rs.getInt("next_num");
                return prefix + String.format("%05d", nextNum);
            }
            return prefix + "00001";
        } catch (SQLException e) {
            logger.error("Error generating invoice number: " + e.getMessage());
            return prefix + System.currentTimeMillis();
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Helper to get invoices by query
    private List<Invoice> getInvoicesByQuery(String sql, int paramId) {
        List<Invoice> invoices = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, paramId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
            return invoices;
        } catch (SQLException e) {
            logger.error("Error getting invoices: " + e.getMessage());
            throw new RuntimeException("Failed to get invoices", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Map ResultSet to Invoice object
    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(rs.getInt("invoice_id"));
        invoice.setBusinessId(rs.getInt("business_id"));
        invoice.setCustomerId(rs.getInt("customer_id"));
        invoice.setInvoiceNumber(rs.getString("invoice_number"));
        invoice.setTotalAmount(rs.getDouble("total_amount"));
        invoice.setStatus(rs.getString("status"));
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            invoice.setDueDate(dueDate.toLocalDate());
        }
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            invoice.setCreatedAt(createdAt.toLocalDateTime());
        }
        // Try to get joined names
        try {
            invoice.setBusinessName(rs.getString("business_name"));
            invoice.setCustomerName(rs.getString("customer_name"));
        } catch (SQLException e) {
            // Columns might not exist
        }
        return invoice;
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

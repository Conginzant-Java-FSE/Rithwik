package com.revpay.service;

import com.revpay.dao.BusinessAccountDAO;
import com.revpay.dao.InvoiceDAO;
import com.revpay.dao.UserDAO;
import com.revpay.model.BusinessAccount;
import com.revpay.model.Invoice;
import com.revpay.model.InvoiceItem;
import com.revpay.model.User;
import com.revpay.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDate;
import java.util.List;

// Invoice service for business account invoice management
public class InvoiceService {

    private static final Logger logger = LogManager.getLogger(InvoiceService.class);
    private InvoiceDAO invoiceDAO;
    private BusinessAccountDAO businessAccountDAO;
    private UserDAO userDAO;
    private WalletService walletService;
    private NotificationService notificationService;

    public InvoiceService() {
        this.invoiceDAO = new InvoiceDAO();
        this.businessAccountDAO = new BusinessAccountDAO();
        this.userDAO = new UserDAO();
        this.walletService = new WalletService();
        this.notificationService = new NotificationService();
    }

    // Create a new invoice
    public Invoice createInvoice(int businessId, String customerIdentifier, LocalDate dueDate,
            List<InvoiceItem> items) {
        try {
            // Find customer
            User customer = userDAO.findByIdentifier(customerIdentifier);
            if (customer == null) {
                logger.warn("Customer not found: " + customerIdentifier);
                return null;
            }
            // Verify business exists
            BusinessAccount business = businessAccountDAO.findById(businessId);
            if (business == null) {
                logger.warn("Business not found: " + businessId);
                return null;
            }
            // Generate invoice number
            String invoiceNumber = generateInvoiceNumber(businessId);
            // Create invoice
            Invoice invoice = new Invoice(businessId, customer.getUserId(), invoiceNumber, dueDate);
            invoice.setItems(items);
            invoice.calculateTotal();
            // Save invoice
            Invoice createdInvoice = invoiceDAO.createInvoice(invoice);
            // Save invoice items
            for (InvoiceItem item : items) {
                item.setInvoiceId(createdInvoice.getInvoiceId());
                invoiceDAO.addInvoiceItem(item);
            }
            // Notify customer
            notificationService.notifyInvoiceCreated(customer.getUserId(), invoiceNumber, invoice.getTotalAmount(),
                    business.getBusinessName());
            logger.info("Invoice created: " + invoiceNumber + " for business: " + businessId);
            return createdInvoice;
        } catch (Exception e) {
            logger.error("Error creating invoice: " + e.getMessage());
            throw e;
        }
    }

    // Pay an invoice
    public boolean payInvoice(int invoiceId, int userId) {
        try {
            Invoice invoice = invoiceDAO.findById(invoiceId);
            if (invoice == null) {
                logger.warn("Invoice not found: " + invoiceId);
                return false;
            }
            // Verify customer
            if (invoice.getCustomerId() != userId) {
                logger.warn("User not authorized to pay this invoice");
                return false;
            }
            // Check if already paid
            if (invoice.isPaid()) {
                logger.warn("Invoice already paid: " + invoiceId);
                return false;
            }
            // Check sufficient balance
            if (!walletService.hasSufficientBalance(userId, invoice.getTotalAmount())) {
                logger.warn("Insufficient balance to pay invoice");
                return false;
            }
            // Transfer money from customer to business
            boolean transferred = walletService.transfer(userId, invoice.getBusinessId(), invoice.getTotalAmount());
            if (!transferred) {
                return false;
            }
            // Update invoice status
            boolean updated = invoiceDAO.updateInvoiceStatus(invoiceId, Constants.INVOICE_PAID);
            if (updated) {
                // Notify business
                notificationService.notifyInvoicePaid(invoice.getBusinessId(), invoice.getInvoiceNumber(),
                        invoice.getTotalAmount(), "Customer");
                logger.info("Invoice paid: " + invoiceId);
            }
            return updated;
        } catch (Exception e) {
            logger.error("Error paying invoice: " + e.getMessage());
            throw e;
        }
    }

    // Get all invoices for a business
    public List<Invoice> getBusinessInvoices(int businessId) {
        return invoiceDAO.findByBusinessId(businessId);
    }

    // Get all invoices for a customer
    public List<Invoice> getCustomerInvoices(int customerId) {
        return invoiceDAO.findByCustomerId(customerId);
    }

    // Get a specific invoice
    public Invoice getInvoice(int invoiceId) {
        return invoiceDAO.findById(invoiceId);
    }

    // Cancel an invoice
    public boolean cancelInvoice(int invoiceId, int businessId) {
        try {
            Invoice invoice = invoiceDAO.findById(invoiceId);
            if (invoice == null || invoice.getBusinessId() != businessId) {
                return false;
            }
            if (invoice.isPaid()) {
                logger.warn("Cannot cancel paid invoice");
                return false;
            }
            boolean cancelled = invoiceDAO.updateInvoiceStatus(invoiceId, Constants.INVOICE_CANCELLED);
            if (cancelled) {
                logger.info("Invoice cancelled: " + invoiceId);
            }
            return cancelled;
        } catch (Exception e) {
            logger.error("Error cancelling invoice: " + e.getMessage());
            throw e;
        }
    }

    // Generate unique invoice number
    private String generateInvoiceNumber(int businessId) {
        long timestamp = System.currentTimeMillis();
        return "INV-" + businessId + "-" + timestamp;
    }

    // Get invoices by status
    public List<Invoice> getInvoicesByStatus(int businessId, String status) {
        return invoiceDAO.findByStatus(businessId, status);
    }

    // Check overdue invoices for a specific business (wrapper)
    public void checkOverdueInvoices(int businessId) {
        // ideally filtering by business, but reusing global check for simple fix
        checkOverdueInvoices();
    }

    // Check and update overdue invoices
    public void checkOverdueInvoices() {
        List<Invoice> allInvoices = invoiceDAO.findAll();
        for (Invoice invoice : allInvoices) {
            if (invoice.isOverdue() && !invoice.isPaid()) {
                invoiceDAO.updateInvoiceStatus(invoice.getInvoiceId(), Constants.INVOICE_OVERDUE);
                logger.info("Invoice marked as overdue: " + invoice.getInvoiceNumber());
            }
        }
    }
}

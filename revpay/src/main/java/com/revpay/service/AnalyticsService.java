package com.revpay.service;

import com.revpay.dao.InvoiceDAO;
import com.revpay.dao.LoanDAO;
import com.revpay.dao.TransactionDAO;
import com.revpay.model.Invoice;
import com.revpay.model.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Analytics service for business reporting and insights
public class AnalyticsService {

    private static final Logger logger = LogManager.getLogger(AnalyticsService.class);
    private TransactionDAO transactionDAO;
    private InvoiceDAO invoiceDAO;
    private LoanDAO loanDAO;

    public AnalyticsService() {
        this.transactionDAO = new TransactionDAO();
        this.invoiceDAO = new InvoiceDAO();
        this.loanDAO = new LoanDAO();
    }

    // Get transaction summary for a user
    public Map<String, Object> getTransactionSummary(int userId) {
        Map<String, Object> summary = new HashMap<>();
        double totalSent = transactionDAO.getTotalSent(userId);
        double totalReceived = transactionDAO.getTotalReceived(userId);
        List<Transaction> transactions = transactionDAO.findByUserId(userId);
        summary.put("totalSent", totalSent);
        summary.put("totalReceived", totalReceived);
        summary.put("netFlow", totalReceived - totalSent);
        summary.put("transactionCount", transactions.size());
        logger.info("Transaction summary generated for user: " + userId);
        return summary;
    }

    // Get monthly transaction summary
    public Map<String, Object> getMonthlyTransactionSummary(int userId, int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        List<Transaction> transactions = transactionDAO.findWithFilters(userId, null, null, startDate, endDate, 0,
                Double.MAX_VALUE);
        double totalSent = transactions.stream().filter(t -> t.getSenderId() == userId)
                .mapToDouble(Transaction::getAmount).sum();
        double totalReceived = transactions.stream().filter(t -> t.getReceiverId() == userId)
                .mapToDouble(Transaction::getAmount).sum();
        Map<String, Object> summary = new HashMap<>();
        summary.put("month", month);
        summary.put("year", year);
        summary.put("totalSent", totalSent);
        summary.put("totalReceived", totalReceived);
        summary.put("netFlow", totalReceived - totalSent);
        summary.put("transactionCount", transactions.size());
        return summary;
    }

    // Get invoice summary for a business
    public Map<String, Object> getInvoiceSummary(int businessId) {
        List<Invoice> invoices = invoiceDAO.findByBusinessId(businessId);
        Map<String, Object> summary = new HashMap<>();
        double totalRevenue = invoices.stream().filter(Invoice::isPaid).mapToDouble(Invoice::getTotalAmount).sum();
        double pendingRevenue = invoices.stream().filter(i -> "PENDING".equals(i.getStatus()))
                .mapToDouble(Invoice::getTotalAmount).sum();
        double overdueRevenue = invoices.stream().filter(Invoice::isOverdue).mapToDouble(Invoice::getTotalAmount).sum();
        summary.put("totalInvoices", invoices.size());
        summary.put("totalRevenue", totalRevenue);
        summary.put("pendingRevenue", pendingRevenue);
        summary.put("overdueRevenue", overdueRevenue);
        summary.put("paidCount", invoices.stream().filter(Invoice::isPaid).count());
        summary.put("pendingCount", invoices.stream().filter(i -> "PENDING".equals(i.getStatus())).count());
        summary.put("overdueCount", invoices.stream().filter(Invoice::isOverdue).count());
        logger.info("Invoice summary generated for business: " + businessId);
        return summary;
    }

    // Get loan summary for a business
    public Map<String, Object> getLoanSummary(int businessId) {
        List loansList = loanDAO.findByBusinessId(businessId);
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalLoans", loansList.size());
        logger.info("Loan summary generated for business: " + businessId);
        return summary;
    }

    // Generate spending report by category (simplified)
    public Map<String, Double> getSpendingByCategory(int userId) {
        List<Transaction> transactions = transactionDAO.findByUserId(userId);
        Map<String, Double> categorySpending = new HashMap<>();
        for (Transaction t : transactions) {
            if (t.getSenderId() == userId) {
                String type = t.getTransactionType();
                categorySpending.put(type, categorySpending.getOrDefault(type, 0.0) + t.getAmount());
            }
        }
        return categorySpending;
    }

    // Get transaction count between two dates
    public int getTransactionCount(int userId, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionDAO.findWithFilters(userId, null, null, startDate, endDate, 0,
                Double.MAX_VALUE);
        return transactions.size();
    }

    // Get average transaction amount
    public double getAverageTransactionAmount(int userId) {
        List<Transaction> transactions = transactionDAO.findByUserId(userId);
        if (transactions.isEmpty()) {
            return 0.0;
        }
        double total = transactions.stream().mapToDouble(Transaction::getAmount).sum();
        return total / transactions.size();
    }

    // Format transaction summary for display
    public String formatTransactionSummary(Map<String, Object> summary) {
        StringBuilder sb = new StringBuilder();
        sb.append("Total Sent: $").append(String.format("%.2f", summary.get("totalSent"))).append("\n");
        sb.append("Total Received: $").append(String.format("%.2f", summary.get("totalReceived"))).append("\n");
        sb.append("Net Flow: $").append(String.format("%.2f", summary.get("netFlow"))).append("\n");
        sb.append("Transaction Count: ").append(summary.get("transactionCount"));
        return sb.toString();
    }

    // Get revenue report (alias/wrapper for getInvoiceSummary with specific format)
    public Map<String, Object> getRevenueReport(int businessId) {
        return getInvoiceSummary(businessId);
    }

    // Format revenue report
    public String formatRevenueReport(Map<String, Object> report) {
        StringBuilder sb = new StringBuilder();
        sb.append("Total Revenue: $").append(String.format("%.2f", report.get("totalRevenue"))).append("\n");
        sb.append("Pending Revenue: $").append(String.format("%.2f", report.get("pendingRevenue"))).append("\n");
        sb.append("Overdue Revenue: $").append(String.format("%.2f", report.get("overdueRevenue"))).append("\n");
        sb.append("Total Invoices: ").append(report.get("totalInvoices")).append("\n");
        sb.append("Paid: ").append(report.get("paidCount")).append(", ");
        sb.append("Pending: ").append(report.get("pendingCount")).append(", ");
        sb.append("Overdue: ").append(report.get("overdueCount"));
        return sb.toString();
    }

    // Get total outstanding invoices amount (Map for UI)
    public Map<String, Object> getOutstandingInvoices(int businessId) {
        Map<String, Object> invoiceSummary = getInvoiceSummary(businessId);
        Map<String, Object> result = new HashMap<>();
        double pendingRev = (double) invoiceSummary.get("pendingRevenue");
        double overdueRev = (double) invoiceSummary.get("overdueRevenue");
        result.put("totalOutstanding", pendingRev + overdueRev);
        result.put("pendingCount", invoiceSummary.get("pendingCount"));
        result.put("overdueCount", invoiceSummary.get("overdueCount"));
        return result;
    }

    // Get payment trends (Last 30 Days)
    public Map<String, Object> getPaymentTrends(int userId) {
        Map<String, Object> trends = new HashMap<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        List<Transaction> transactions = transactionDAO.findWithFilters(userId, null, null, startDate, endDate, 0,
                Double.MAX_VALUE);

        double totalAmount = transactions.stream().mapToDouble(Transaction::getAmount).sum();
        int count = transactions.size();
        double averageDaily = count > 0 ? totalAmount / 30.0 : 0.0;

        trends.put("period", "Last 30 Days");
        trends.put("totalAmount", totalAmount);
        trends.put("transactionCount", count);
        trends.put("averageDaily", averageDaily);

        return trends;
    }
}

package com.revpay.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Invoice model for business accounts
public class Invoice {

    private int invoiceId;
    private int businessId;
    private int customerId;
    private String invoiceNumber;
    private double totalAmount;
    private String status; // PENDING, PAID, OVERDUE, CANCELLED
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private List<InvoiceItem> items;
    // Additional fields for display
    private String businessName;
    private String customerName;

    public Invoice() {
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.items = new ArrayList<>();
    }

    public Invoice(int businessId, int customerId, String invoiceNumber, LocalDate dueDate) {
        this();
        this.businessId = businessId;
        this.customerId = customerId;
        this.invoiceNumber = invoiceNumber;
        this.dueDate = dueDate;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // Add an item to the invoice
    public void addItem(InvoiceItem item) {
        this.items.add(item);
        calculateTotal();
    }

    // Calculate total amount from all items
    public void calculateTotal() {
        this.totalAmount = 0;
        for (InvoiceItem item : items) {
            this.totalAmount += item.getTotal();
        }
    }

    // Check if invoice is paid
    public boolean isPaid() {
        return "PAID".equals(this.status);
    }

    // Check if invoice is overdue
    public boolean isOverdue() {
        if (dueDate != null && !"PAID".equals(this.status)) {
            return LocalDate.now().isAfter(dueDate);
        }
        return false;
    }

    // Get formatted due date
    public String getFormattedDueDate() {
        if (dueDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return dueDate.format(formatter);
        }
        return "";
    }

    // Get formatted total
    public String getFormattedTotal() {
        return String.format("$%.2f", totalAmount);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceNumber='" + invoiceNumber + '\'' +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", dueDate=" + dueDate +
                '}';
    }

    // Helper method to get formatted amount
    public String getFormattedAmount() {
        return String.format("$%.2f", totalAmount);
    }

    // Helper method to check if invoice is pending
    public boolean isPending() {
        return "PENDING".equals(status);
    }
}

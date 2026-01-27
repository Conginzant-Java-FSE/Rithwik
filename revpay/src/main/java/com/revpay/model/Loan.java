package com.revpay.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Loan model for business loan applications
public class Loan {

    private int loanId;
    private int businessId;
    private double amount;
    private String purpose;
    private String status; // PENDING, APPROVED, REJECTED, ACTIVE, PAID_OFF
    private double interestRate;
    private int termMonths;
    private double monthlyPayment;
    private double remainingBalance;
    private LocalDateTime appliedAt;
    private LocalDateTime approvedAt;
    private String businessName;

    public Loan() {
        this.status = "PENDING";
        this.appliedAt = LocalDateTime.now();
        this.interestRate = 0;
        this.monthlyPayment = 0;
        this.remainingBalance = 0;
    }

    public Loan(int businessId, double amount, String purpose, int termMonths) {
        this();
        this.businessId = businessId;
        this.amount = amount;
        this.purpose = purpose;
        this.termMonths = termMonths;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(double remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    // Check if loan is pending
    public boolean isPending() {
        return "PENDING".equals(this.status);
    }

    // Check if loan is active
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    // Check if loan is approved
    public boolean isApproved() {
        return "APPROVED".equals(this.status);
    }

    // Calculate monthly payment based on principal, rate, and term
    public void calculateMonthlyPayment() {
        if (termMonths <= 0 || interestRate < 0) {
            this.monthlyPayment = 0;
            return;
        }
        double monthlyRate = interestRate / 100.0 / 12.0;
        if (monthlyRate == 0) {
            // No interest - simple division
            this.monthlyPayment = amount / termMonths;
        } else {
            // Standard amortization formula
            double factor = Math.pow(1 + monthlyRate, termMonths);
            this.monthlyPayment = amount * (monthlyRate * factor) / (factor - 1);
        }
        // Round to 2 decimal places
        this.monthlyPayment = Math.round(this.monthlyPayment * 100.0) / 100.0;
    }

    // Make a payment on the loan
    public boolean makePayment(double paymentAmount) {
        if (paymentAmount <= 0 || paymentAmount > remainingBalance) {
            return false;
        }
        this.remainingBalance -= paymentAmount;
        this.remainingBalance = Math.round(this.remainingBalance * 100.0) / 100.0;
        if (this.remainingBalance <= 0) {
            this.remainingBalance = 0;
            this.status = "PAID_OFF";
        }
        return true;
    }

    // Get formatted amount
    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }

    // Get formatted date
    public String getFormattedDate() {
        if (appliedAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return appliedAt.format(formatter);
        }
        return "";
    }

    @Override
    public String toString() {
        return "Loan{" +
                "loanId=" + loanId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", interestRate=" + interestRate + "%" +
                ", termMonths=" + termMonths +
                '}';
    }
}

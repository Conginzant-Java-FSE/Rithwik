package com.revpay.service;

import com.revpay.dao.BusinessAccountDAO;
import com.revpay.dao.LoanDAO;
import com.revpay.model.BusinessAccount;
import com.revpay.model.Loan;
import com.revpay.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDateTime;
import java.util.List;

// Loan service for business loan applications and management
public class LoanService {

    private static final Logger logger = LogManager.getLogger(LoanService.class);
    private LoanDAO loanDAO;
    private BusinessAccountDAO businessAccountDAO;
    private WalletService walletService;
    private NotificationService notificationService;
    // Default interest rate for approved loans (simulated)
    private static final double DEFAULT_INTEREST_RATE = 8.5;

    public LoanService() {
        this.loanDAO = new LoanDAO();
        this.businessAccountDAO = new BusinessAccountDAO();
        this.walletService = new WalletService();
        this.notificationService = new NotificationService();
    }

    // Apply for a business loan
    public Loan applyForLoan(int businessId, double amount, String purpose, int termMonths) {
        try {
            // Validate business
            BusinessAccount business = businessAccountDAO.findById(businessId);
            if (business == null) {
                logger.warn("Business not found: " + businessId);
                return null;
            }
            // Validate amount and term
            if (amount <= 0 || termMonths <= 0) {
                logger.warn("Invalid loan amount or term");
                return null;
            }
            // Create loan application
            Loan loan = new Loan(businessId, amount, purpose, termMonths);
            Loan createdLoan = loanDAO.createLoan(loan);
            // Notify business
            notificationService.notifyLoanStatusChange(business.getUserId(), "PENDING", amount);
            logger.info("Loan application created: " + createdLoan.getLoanId() + " for business: " + businessId);
            return createdLoan;
        } catch (Exception e) {
            logger.error("Error applying for loan: " + e.getMessage());
            throw e;
        }
    }

    // Approve a loan (simulated - in real system, would be admin function)
    public boolean approveLoan(int loanId, double interestRate) {
        try {
            Loan loan = loanDAO.findById(loanId);
            if (loan == null) {
                logger.warn("Loan not found: " + loanId);
                return false;
            }
            if (!loan.isPending()) {
                logger.warn("Loan is not pending approval");
                return false;
            }
            // Set interest rate and calculate monthly payment
            loan.setInterestRate(interestRate > 0 ? interestRate : DEFAULT_INTEREST_RATE);
            loan.calculateMonthlyPayment();
            loan.setRemainingBalance(loan.getAmount());
            loan.setStatus(Constants.LOAN_APPROVED);
            loan.setApprovedAt(LocalDateTime.now());
            // Update loan
            boolean updated = loanDAO.updateLoan(loan);
            if (updated) {
                // Notify business
                notificationService.notifyLoanStatusChange(loan.getBusinessId(), "APPROVED", loan.getAmount());
                logger.info("Loan approved: " + loanId);
            }
            return updated;
        } catch (Exception e) {
            logger.error("Error approving loan: " + e.getMessage());
            throw e;
        }
    }

    // Activate an approved loan (disburse funds)
    public boolean activateLoan(int loanId) {
        try {
            Loan loan = loanDAO.findById(loanId);
            if (loan == null || !loan.isApproved()) {
                logger.warn("Loan not found or not approved");
                return false;
            }
            // Add loan amount to business wallet
            BusinessAccount business = businessAccountDAO.findById(loan.getBusinessId());
            if (business == null) {
                return false;
            }
            boolean fundsAdded = walletService.addMoney(business.getUserId(), loan.getAmount());
            if (!fundsAdded) {
                return false;
            }
            // Update loan status
            loan.setStatus(Constants.LOAN_ACTIVE);
            boolean updated = loanDAO.updateLoan(loan);
            if (updated) {
                logger.info("Loan activated: " + loanId + ", Funds disbursed: $" + loan.getAmount());
            }
            return updated;
        } catch (Exception e) {
            logger.error("Error activating loan: " + e.getMessage());
            throw e;
        }
    }

    // Make a loan payment
    public boolean makeLoanPayment(int loanId, double paymentAmount) {
        try {
            Loan loan = loanDAO.findById(loanId);
            if (loan == null || !loan.isActive()) {
                logger.warn("Loan not found or not active");
                return false;
            }
            // Make payment
            boolean paymentMade = loan.makePayment(paymentAmount);
            if (!paymentMade) {
                logger.warn("Invalid payment amount");
                return false;
            }
            // Update loan
            boolean updated = loanDAO.updateLoan(loan);
            if (updated) {
                logger.info("Loan payment made: $" + paymentAmount + " for loan: " + loanId);
                // Check if paid off
                if ("PAID_OFF".equals(loan.getStatus())) {
                    notificationService.notifyLoanStatusChange(loan.getBusinessId(), "PAID_OFF", loan.getAmount());
                }
            }
            return updated;
        } catch (Exception e) {
            logger.error("Error making loan payment: " + e.getMessage());
            throw e;
        }
    }

    // Get all loans for a business
    public List<Loan> getBusinessLoans(int businessId) {
        return loanDAO.findByBusinessId(businessId);
    }

    // Get a specific loan
    public Loan getLoan(int loanId) {
        return loanDAO.findById(loanId);
    }

    // Reject a loan application
    public boolean rejectLoan(int loanId, String reason) {
        try {
            Loan loan = loanDAO.findById(loanId);
            if (loan == null || !loan.isPending()) {
                return false;
            }
            loan.setStatus(Constants.LOAN_REJECTED);
            boolean updated = loanDAO.updateLoan(loan);
            if (updated) {
                notificationService.notifyLoanStatusChange(loan.getBusinessId(), "REJECTED", loan.getAmount());
                logger.info("Loan rejected: " + loanId);
            }
            return updated;
        } catch (Exception e) {
            logger.error("Error rejecting loan: " + e.getMessage());
            throw e;
        }
    }

    // Overload activateLoan to match BusinessMenu usage
    public boolean activateLoan(int loanId, int businessId) {
        // In a real app we'd verify the businessId matches the loan
        return activateLoan(loanId);
    }

    // Get all loans for a business
    public List<Loan> getLoans(int businessId) {
        return loanDAO.findByBusinessId(businessId);
    }

    // Get total outstanding balance for a business
    public double getTotalOutstandingBalance(int businessId) {
        List<Loan> loans = loanDAO.findByBusinessId(businessId);
        double total = 0;
        if (loans != null) {
            for (Loan loan : loans) {
                total += loan.getRemainingBalance();
            }
        }
        return total;
    }

    // Make a loan payment
    public boolean makePayment(int loanId, int businessId, double amount) {
        try {
            Loan loan = loanDAO.findById(loanId);
            if (loan == null || loan.getBusinessId() != businessId) {
                logger.warn("Loan not found or business mismatch");
                return false;
            }
            // Check wallet balance
            BusinessAccount business = businessAccountDAO.findById(businessId);
            if (business == null || !walletService.hasSufficientBalance(business.getUserId(), amount)) {
                logger.warn("Insufficient funds");
                return false;
            }
            // Debit wallet
            boolean debited = walletService.debit(business.getUserId(), amount, "Loan Repayment: " + loanId);
            if (!debited)
                return false;

            // Credit/Reduce loan
            return makeLoanPayment(loanId, amount);
        } catch (Exception e) {
            logger.error("Error processing payment: " + e.getMessage());
            return false;
        }
    }
}

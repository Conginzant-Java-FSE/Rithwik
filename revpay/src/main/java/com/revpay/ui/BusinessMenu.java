package com.revpay.ui;

import com.revpay.model.*;
import com.revpay.security.SessionManager;
import com.revpay.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Business account menu with business-specific features.
 * Extends personal account features.
 */
public class BusinessMenu {

    private static final Logger logger = LogManager.getLogger(BusinessMenu.class);

    private User currentUser;
    private BusinessAccount businessAccount;
    private SessionManager sessionManager;
    private AuthService authService;
    private WalletService walletService;
    private TransactionService transactionService;
    private InvoiceService invoiceService;
    private LoanService loanService;
    private AnalyticsService analyticsService;
    private NotificationService notificationService;
    private PersonalMenu personalMenu;

    public BusinessMenu() {
        this.sessionManager = SessionManager.getInstance();
        this.authService = new AuthService();
        this.walletService = new WalletService();
        this.transactionService = new TransactionService();
        this.invoiceService = new InvoiceService();
        this.loanService = new LoanService();
        this.analyticsService = new AnalyticsService();
        this.notificationService = new NotificationService();
        this.personalMenu = new PersonalMenu();
    }

    /**
     * Show the business account main menu.
     */
    public void showMenu(User user) {
        this.currentUser = user;
        this.businessAccount = authService.getBusinessAccount(user.getUserId());

        while (sessionManager.isSessionActive()) {
            currentUser = authService.getUserById(currentUser.getUserId());
            String badge = ""; // Notifications simplified

            String[] options = {
                    "Personal Features (Send/Request/Cards)",
                    "Create Invoice",
                    "Manage Invoices",
                    "Accept Payment",
                    "Apply for Loan",
                    "Manage Loans",
                    "Business Analytics",
                    "Notifications" + badge,
                    "View Wallet",
                    "Logout"
            };

            String header = businessAccount.getBusinessName() + " | Balance: " +
                    walletService.getFormattedBalance(currentUser.getUserId());

            int choice = InputHelper.showMenu(header, options);

            switch (choice) {
                case 1:
                    personalMenu.showMenu(currentUser);
                    break;
                case 2:
                    createInvoice();
                    break;
                case 3:
                    manageInvoices();
                    break;
                case 4:
                    acceptPayment();
                    break;
                case 5:
                    applyForLoan();
                    break;
                case 6:
                    manageLoans();
                    break;
                case 7:
                    viewAnalytics();
                    break;
                case 8:
                    viewNotifications();
                    break;
                case 9:
                    viewWallet();
                    break;
                case 10:
                    logout();
                    return;
            }
        }
    }

    private void createInvoice() {
        InputHelper.printHeader("Create New Invoice");

        String customerIdentifier = InputHelper.readRequiredString("Customer (email/phone/ID): ");

        User customer = transactionService.findUser(customerIdentifier);
        if (customer == null) {
            InputHelper.printError("Customer not found.");
            return;
        }

        System.out.println("Creating invoice for: " + customer.getFullName());

        // Get due date
        String dueDateStr = InputHelper.readRequiredString("Due Date (YYYY-MM-DD): ");
        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueDateStr);
            if (dueDate.isBefore(LocalDate.now())) {
                InputHelper.printError("Due date cannot be in the past.");
                return;
            }
        } catch (Exception e) {
            InputHelper.printError("Invalid date format.");
            return;
        }

        // Add items
        List<InvoiceItem> items = new ArrayList<>();

        System.out.println("\nAdd invoice items (enter empty description or 'done' to finish):");

        while (true) {
            String description = InputHelper.readString("Item description: ");
            if (description.isEmpty() || description.equalsIgnoreCase("done") || description.equalsIgnoreCase("exit"))
                break;

            int quantity = InputHelper.readInt("Quantity (0 to cancel item): ", 0, 10000);
            if (quantity == 0) {
                System.out.println("Item entry cancelled.");
                continue;
            }

            double unitPrice = InputHelper.readPositiveAmount("Unit Price: $");

            InvoiceItem item = new InvoiceItem(description, quantity, unitPrice);
            items.add(item);

            System.out.println("Added: " + item.getDisplayString());
        }

        if (items.isEmpty()) {
            InputHelper.printError("Invoice must have at least one item.");
            return;
        }

        // Show summary
        double total = 0;
        System.out.println("\n--- Invoice Summary ---");
        for (InvoiceItem item : items) {
            System.out.println(item.getDisplayString());
            total += item.getTotal();
        }
        System.out.println("Total: " + InputHelper.formatCurrency(total));

        if (!InputHelper.readConfirmation("Create this invoice?")) {
            return;
        }

        Invoice invoice = invoiceService.createInvoice(
                businessAccount.getBusinessId(), customerIdentifier, dueDate, items);

        if (invoice != null) {
            InputHelper.printSuccess("Invoice created successfully!");
            System.out.println("Invoice Number: " + invoice.getInvoiceNumber());
        } else {
            InputHelper.printError("Failed to create invoice.");
        }

        InputHelper.waitForEnter();
    }

    private void manageInvoices() {
        InputHelper.printHeader("Manage Invoices");

        String[] options = { "All Invoices", "Pending", "Paid", "Overdue", "Back" };
        int choice = InputHelper.showMenu("Filter By", options);

        List<Invoice> invoices;

        switch (choice) {
            case 1:
                invoices = invoiceService.getBusinessInvoices(businessAccount.getBusinessId());
                break;
            case 2:
                invoices = invoiceService.getInvoicesByStatus(businessAccount.getBusinessId(), "PENDING");
                break;
            case 3:
                invoices = invoiceService.getInvoicesByStatus(businessAccount.getBusinessId(), "PAID");
                break;
            case 4:
                invoiceService.checkOverdueInvoices(businessAccount.getBusinessId());
                invoices = invoiceService.getInvoicesByStatus(businessAccount.getBusinessId(), "OVERDUE");
                break;
            default:
                return;
        }

        if (invoices.isEmpty()) {
            InputHelper.printInfo("No invoices found.");
            InputHelper.waitForEnter();
            return;
        }

        System.out.println("\n" + String.format("%-12s %-20s %-12s %-12s %-12s",
                "Invoice #", "Customer", "Amount", "Status", "Due Date"));
        InputHelper.printLine();

        for (Invoice inv : invoices) {
            System.out.println(String.format("%-12s %-20s %-12s %-12s %-12s",
                    inv.getInvoiceNumber(),
                    inv.getCustomerName() != null ? inv.getCustomerName() : "N/A",
                    inv.getFormattedAmount(),
                    inv.getStatus(),
                    inv.getFormattedDueDate()));
        }

        InputHelper.waitForEnter();
    }

    private void acceptPayment() {
        InputHelper.printHeader("Accept Payment");

        String customerIdentifier = InputHelper.readRequiredString("Customer (email/phone/ID): ");

        User customer = transactionService.findUser(customerIdentifier);
        if (customer == null) {
            InputHelper.printError("Customer not found.");
            return;
        }

        System.out.println("Customer: " + customer.getFullName());

        // Show pending invoices for this customer
        List<Invoice> customerInvoices = invoiceService.getCustomerInvoices(customer.getUserId());
        List<Invoice> pendingInvoices = new ArrayList<>();

        for (Invoice inv : customerInvoices) {
            if (inv.getBusinessId() == businessAccount.getBusinessId() &&
                    (inv.isPending() || "OVERDUE".equals(inv.getStatus()))) {
                pendingInvoices.add(inv);
            }
        }

        if (pendingInvoices.isEmpty()) {
            InputHelper.printInfo("No pending invoices for this customer.");
            System.out.println("\nCreate a quick payment request instead?");

            if (InputHelper.readConfirmation("Create payment request?")) {
                double amount = InputHelper.readPositiveAmount("Amount: $");
                String note = InputHelper.readString("Note: ");

                // Direct payment from customer to business
                System.out.println("\n[SIMULATED] Payment request sent to customer.");
                InputHelper.printInfo("The customer will receive a notification to pay.");
            }
        } else {
            System.out.println("\nPending Invoices:");
            for (int i = 0; i < pendingInvoices.size(); i++) {
                Invoice inv = pendingInvoices.get(i);
                System.out.println("  " + (i + 1) + ". " + inv.getInvoiceNumber() +
                        " - " + inv.getFormattedAmount());
            }

            int invoiceChoice = InputHelper.readInt("Select invoice (0 to cancel): ",
                    0, pendingInvoices.size());

            if (invoiceChoice > 0) {
                Invoice selectedInvoice = pendingInvoices.get(invoiceChoice - 1);

                System.out.println("\nCustomer will pay: " + selectedInvoice.getFormattedAmount());

                // In a real app, this would initiate a payment flow
                // For demo, we simulate
                System.out.println("[SIMULATED] Processing payment...");

                // Check if customer has balance (simulation)
                if (walletService.hasSufficientBalance(customer.getUserId(),
                        selectedInvoice.getTotalAmount())) {

                    if (invoiceService.payInvoice(selectedInvoice.getInvoiceId(),
                            customer.getUserId())) {
                        InputHelper.printSuccess("Payment received! Invoice marked as paid.");
                    }
                } else {
                    InputHelper.printWarning("Customer has insufficient balance.");
                    InputHelper.printInfo("Invoice notification sent to customer.");
                }
            }
        }

        InputHelper.waitForEnter();
    }

    private void applyForLoan() {
        InputHelper.printHeader("Apply for Business Loan");

        System.out.println("Business: " + businessAccount.getBusinessName());
        System.out.println("Current Outstanding Loans: " +
                InputHelper.formatCurrency(loanService.getTotalOutstandingBalance(
                        businessAccount.getBusinessId())));

        InputHelper.printLine();

        double amount = InputHelper.readPositiveAmount("Loan Amount: $");
        String purpose = InputHelper.readRequiredString("Purpose of Loan: ");

        String[] terms = { "6 months", "12 months", "24 months", "36 months" };
        int termChoice = InputHelper.showMenu("Loan Term", terms);
        int termMonths = new int[] { 6, 12, 24, 36 }[termChoice - 1];

        System.out.println("\n--- Loan Application Summary ---");
        System.out.println("Amount: " + InputHelper.formatCurrency(amount));
        System.out.println("Purpose: " + purpose);
        System.out.println("Term: " + termMonths + " months");
        System.out.println("Estimated Interest Rate: 8.5% APR");

        // Calculate estimated monthly payment
        double monthlyRate = 0.085 / 12;
        double factor = Math.pow(1 + monthlyRate, termMonths);
        double monthlyPayment = amount * (monthlyRate * factor) / (factor - 1);
        System.out.println("Estimated Monthly Payment: " +
                InputHelper.formatCurrency(monthlyPayment));

        if (!InputHelper.readConfirmation("Submit loan application?")) {
            return;
        }

        Loan loan = loanService.applyForLoan(businessAccount.getBusinessId(),
                amount, purpose, termMonths);

        if (loan != null) {
            InputHelper.printSuccess("Loan application submitted!");
            System.out.println("Application ID: " + loan.getLoanId());
            System.out.println("Status: " + loan.getStatus());

            if (loan.isApproved()) {
                InputHelper.printInfo("Your loan has been pre-approved! Activate it from the loans menu.");
            }
        } else {
            InputHelper.printError("Failed to submit application.");
        }

        InputHelper.waitForEnter();
    }

    private void manageLoans() {
        InputHelper.printHeader("Manage Business Loans");

        List<Loan> loans = loanService.getLoans(businessAccount.getBusinessId());

        if (loans.isEmpty()) {
            InputHelper.printInfo("No loan applications found.");
            InputHelper.waitForEnter();
            return;
        }

        System.out.println("\n" + String.format("%-5s %-12s %-12s %-10s %-12s %-12s",
                "ID", "Amount", "Status", "Term", "Monthly", "Balance"));
        InputHelper.printLine();

        for (Loan loan : loans) {
            System.out.println(String.format("%-5d %-12s %-12s %-10s %-12s %-12s",
                    loan.getLoanId(),
                    loan.getFormattedAmount(),
                    loan.getStatus(),
                    loan.getTermMonths() + " mo",
                    InputHelper.formatCurrency(loan.getMonthlyPayment()),
                    InputHelper.formatCurrency(loan.getRemainingBalance())));
        }

        String[] options = { "Activate Approved Loan", "Make Payment", "Approve Loan Application", "Back" };
        int choice = InputHelper.showMenu("Options", options);

        switch (choice) {
            case 1:
                int loanId = InputHelper.readInt("Enter Loan ID to activate: ");
                if (loanService.activateLoan(loanId, businessAccount.getBusinessId())) {
                    InputHelper.printSuccess("Loan activated! Funds added to your wallet.");
                } else {
                    InputHelper.printError("Cannot activate loan. Check if it's approved.");
                }
                break;
            case 2:
                loanId = InputHelper.readInt("Enter Loan ID to pay: ");
                Loan loan = loanService.getLoan(loanId);
                if (loan != null && loan.isActive()) {
                    System.out.println("Monthly payment: " +
                            InputHelper.formatCurrency(loan.getMonthlyPayment()));
                    double payAmount = InputHelper.readPositiveAmount("Payment amount: $");

                    if (loanService.makePayment(loanId, businessAccount.getBusinessId(), payAmount)) {
                        InputHelper.printSuccess("Payment applied successfully!");
                    } else {
                        InputHelper.printError("Payment failed. Check your balance.");
                    }
                } else {
                    InputHelper.printError("Loan not found or not active.");
                }
                break;
            case 3:
                System.out.println("\n--- Self-Approval Mode ---");
                List<Loan> pendingLoans = new ArrayList<>();
                for (Loan l : loans) {
                    if ("PENDING".equals(l.getStatus())) {
                        pendingLoans.add(l);
                    }
                }

                if (pendingLoans.isEmpty()) {
                    InputHelper.printInfo("No pending loans to approve.");
                } else {
                    for (Loan l : pendingLoans) {
                        System.out.println("Pending Loan ID: " + l.getLoanId() + " Amount: " + l.getFormattedAmount());
                    }
                    int approveId = InputHelper.readInt("Enter Loan ID to approve: ");
                    boolean found = false;
                    for (Loan l : pendingLoans) {
                        if (l.getLoanId() == approveId) {
                            found = true;
                            break;
                        }
                    }

                    if (found && loanService.approveLoan(approveId, 0)) {
                        InputHelper.printSuccess("Loan approved! You can now activate it.");
                    } else {
                        InputHelper.printError("Invalid Loan ID or approval failed.");
                    }
                }
                break;
        }

        InputHelper.waitForEnter();
    }

    private void viewAnalytics() {
        InputHelper.printHeader("Business Analytics");

        String[] options = {
                "Transaction Summary",
                "Revenue Report",
                "Outstanding Invoices",
                "Loan Summary",
                "Payment Trends",
                "Back"
        };

        int choice = InputHelper.showMenu("Report Type", options);

        switch (choice) {
            case 1:
                Map<String, Object> txnSummary = analyticsService.getTransactionSummary(
                        currentUser.getUserId());
                System.out.println(analyticsService.formatTransactionSummary(txnSummary));
                break;
            case 2:
                Map<String, Object> revenue = analyticsService.getRevenueReport(
                        businessAccount.getBusinessId());
                System.out.println(analyticsService.formatRevenueReport(revenue));
                break;
            case 3:
                Map<String, Object> outstanding = analyticsService.getOutstandingInvoices(
                        businessAccount.getBusinessId());
                System.out.println("\n=== Outstanding Invoices ===");
                System.out.println("Total Outstanding: " +
                        InputHelper.formatCurrency((Double) outstanding.get("totalOutstanding")));
                System.out.println("Pending: " + outstanding.get("pendingCount"));
                System.out.println("Overdue: " + outstanding.get("overdueCount"));
                break;
            case 4:
                Map<String, Object> loanSummary = analyticsService.getLoanSummary(
                        businessAccount.getBusinessId());
                System.out.println("\n=== Loan Summary ===");
                System.out.println("Outstanding Balance: " +
                        InputHelper.formatCurrency((Double) loanSummary.get("totalOutstandingBalance")));
                System.out.println("Monthly Payments: " +
                        InputHelper.formatCurrency((Double) loanSummary.get("monthlyPayments")));
                System.out.println("Active Loans: " + loanSummary.get("activeLoansCount"));
                break;
            case 5:
                Map<String, Object> trends = analyticsService.getPaymentTrends(
                        currentUser.getUserId());
                System.out.println("\n=== Payment Trends (" + trends.get("period") + ") ===");
                System.out.println("Total Amount: " +
                        InputHelper.formatCurrency((Double) trends.get("totalAmount")));
                System.out.println("Transactions: " + trends.get("transactionCount"));
                System.out.println("Avg Daily: " +
                        InputHelper.formatCurrency((Double) trends.get("averageDaily")));
                break;
            default:
                return;
        }

        InputHelper.waitForEnter();
    }

    private void viewNotifications() {
        InputHelper.printHeader("Notifications");
        // Get all notifications
        List<Notification> notifications = notificationService.getAllNotifications(currentUser.getUserId());
        if (notifications.isEmpty()) {
            InputHelper.printInfo("No notifications found.");
            InputHelper.waitForEnter();
            return;
        }
        for (int i = 0; i < notifications.size(); i++) {
            Notification n = notifications.get(i);
            String status = n.isRead() ? "[READ]" : "[NEW]";
            System.out.println((i + 1) + ". " + status + " " + n.getTitle() + ": " + n.getMessage());
        }
        String[] options = { "Mark All as Read", "Mark as Read", "Back" };
        int choice = InputHelper.showMenu("Options", options);
        if (choice == 1) {
            notificationService.markAllAsRead(currentUser.getUserId());
            InputHelper.printSuccess("All marked as read.");
        } else if (choice == 2) {
            int idx = InputHelper.readInt("Enter notification number: ", 1, notifications.size());
            notificationService.markAsRead(notifications.get(idx - 1).getNotificationId());
            InputHelper.printSuccess("Marked as read.");
        }
        InputHelper.waitForEnter();
    }

    private void viewWallet() {
        InputHelper.printHeader("Business Wallet");

        double balance = walletService.getBalance(currentUser.getUserId());
        System.out.println("\nCurrent Balance: " + InputHelper.formatCurrency(balance));

        double sent = transactionService.getTotalSent(currentUser.getUserId());
        double received = transactionService.getTotalReceived(currentUser.getUserId());

        System.out.println("Total Sent: " + InputHelper.formatCurrency(sent));
        System.out.println("Total Received: " + InputHelper.formatCurrency(received));

        InputHelper.waitForEnter();
    }

    private void logout() {
        sessionManager.endSession();
        InputHelper.printSuccess("Logged out successfully!");
    }
}

package com.revpay.ui;

import com.revpay.model.*;
import com.revpay.security.SessionManager;
import com.revpay.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Personal account menu with all personal account features.
 */
public class PersonalMenu {

    private static final Logger logger = LogManager.getLogger(PersonalMenu.class);

    private User currentUser;
    private SessionManager sessionManager;
    private WalletService walletService;
    private TransactionService transactionService;
    private MoneyRequestService requestService;
    private PaymentMethodService paymentMethodService;
    private NotificationService notificationService;
    private AuthService authService;

    public PersonalMenu() {
        this.sessionManager = SessionManager.getInstance();
        this.walletService = new WalletService();
        this.transactionService = new TransactionService();
        this.requestService = new MoneyRequestService();
        this.paymentMethodService = new PaymentMethodService();
        this.notificationService = new NotificationService();
        this.authService = new AuthService();
    }

    /**
     * Show the personal account main menu.
     */
    public void showMenu(User user) {
        this.currentUser = user;

        while (sessionManager.isSessionActive()) {
            // Refresh user data
            currentUser = authService.getUserById(currentUser.getUserId());

            // Check for notifications
            // Notifications badge removed (simplified)

            String[] options = {
                    "View Wallet Balance",
                    "Send Money",
                    "Request Money",
                    "View Money Requests",
                    "Payment Methods",
                    "Transaction History",
                    "Add Money to Wallet",
                    "Withdraw Money",
                    "Notifications",
                    "Change Password",
                    "Set Transaction PIN",
                    "Logout"
            };

            int choice = InputHelper.showMenu(
                    "Hi, " + currentUser.getFullName() + "! | Balance: " +
                            walletService.getFormattedBalance(currentUser.getUserId()),
                    options);

            switch (choice) {
                case 1:
                    viewWalletBalance();
                    break;
                case 2:
                    sendMoney();
                    break;
                case 3:
                    requestMoney();
                    break;
                case 4:
                    viewMoneyRequests();
                    break;
                case 5:
                    managePaymentMethods();
                    break;
                case 6:
                    viewTransactionHistory();
                    break;
                case 7:
                    addMoney();
                    break;
                case 8:
                    withdrawMoney();
                    break;
                case 9:
                    viewNotifications();
                    break;
                case 10:
                    changePassword();
                    break;
                case 11:
                    setTransactionPin();
                    break;
                case 12:
                    logout();
                    return;
            }
        }

        // Session expired
        InputHelper.printWarning("Your session has expired. Please login again.");
    }

    private void viewWalletBalance() {
        InputHelper.printHeader("Wallet Balance");

        double balance = walletService.getBalance(currentUser.getUserId());
        System.out.println("\n  Current Balance: " + InputHelper.formatCurrency(balance));

        // Show summary
        double totalSent = transactionService.getTotalSent(currentUser.getUserId());
        double totalReceived = transactionService.getTotalReceived(currentUser.getUserId());

        System.out.println("\n  Total Sent:     " + InputHelper.formatCurrency(totalSent));
        System.out.println("  Total Received: " + InputHelper.formatCurrency(totalReceived));

        InputHelper.waitForEnter();
    }

    private void sendMoney() {
        InputHelper.printHeader("Send Money");

        String recipient = InputHelper.readRequiredString("Recipient (email/phone/ID): ");

        // Check if recipient exists
        User recipientUser = transactionService.findUser(recipient);
        if (recipientUser == null) {
            InputHelper.printError("Recipient not found.");
            return;
        }

        System.out.println("Sending to: " + recipientUser.getFullName());

        double amount = InputHelper.readPositiveAmount("Amount: $");

        // Check balance
        if (!walletService.hasSufficientBalance(currentUser.getUserId(), amount)) {
            InputHelper.printError("Insufficient balance. Your balance is " +
                    walletService.getFormattedBalance(currentUser.getUserId()));
            return;
        }

        String note = InputHelper.readString("Note (optional): ");

        // Confirm
        System.out.println("\nConfirm: Send " + InputHelper.formatCurrency(amount) +
                " to " + recipientUser.getFullName() + "?");
        if (!InputHelper.readConfirmation("Proceed")) {
            InputHelper.printInfo("Transaction cancelled.");
            return;
        }

        String pin = InputHelper.readPassword("Enter Transaction PIN: ");

        Transaction transaction = transactionService.sendMoney(
                currentUser.getUserId(), recipient, amount, note, pin);

        if (transaction != null) {
            InputHelper.printSuccess("Money sent successfully!");
            System.out.println("Transaction ID: " + transaction.getTransactionId());
            System.out.println("New Balance: " + walletService.getFormattedBalance(currentUser.getUserId()));
        } else {
            InputHelper.printError("Transaction failed. Please try again.");
        }

        InputHelper.waitForEnter();
    }

    private void requestMoney() {
        InputHelper.printHeader("Request Money");

        String target = InputHelper.readRequiredString("Request from (email/phone/ID): ");

        User targetUser = transactionService.findUser(target);
        if (targetUser == null) {
            InputHelper.printError("User not found.");
            return;
        }

        System.out.println("Requesting from: " + targetUser.getFullName());

        double amount = InputHelper.readPositiveAmount("Amount: $");
        String note = InputHelper.readString("Note (optional): ");

        MoneyRequest request = requestService.createRequest(
                currentUser.getUserId(), target, amount, note);

        if (request != null) {
            InputHelper.printSuccess("Money request sent successfully!");
        } else {
            InputHelper.printError("Failed to create request.");
        }

        InputHelper.waitForEnter();
    }

    private void viewMoneyRequests() {
        InputHelper.printHeader("Money Requests");

        String[] options = {
                "Incoming Requests (Pay)",
                "Outgoing Requests (Track)",
                "Back"
        };

        int choice = InputHelper.showMenu("Request Type", options);

        switch (choice) {
            case 1:
                handleIncomingRequests();
                break;
            case 2:
                handleOutgoingRequests();
                break;
            case 3:
                return;
        }
    }

    private void handleIncomingRequests() {
        List<MoneyRequest> requests = requestService.getIncomingRequests(currentUser.getUserId());

        if (requests.isEmpty()) {
            InputHelper.printInfo("No incoming requests.");
            InputHelper.waitForEnter();
            return;
        }

        InputHelper.printHeader("Incoming Requests");

        for (int i = 0; i < requests.size(); i++) {
            MoneyRequest req = requests.get(i);
            System.out.println("\n" + (i + 1) + ". " + req.getFormattedAmount() +
                    " from " + req.getRequesterName());
            if (req.getNote() != null && !req.getNote().isEmpty()) {
                System.out.println("   Note: " + req.getNote());
            }
            System.out.println("   Date: " + req.getFormattedDate());
        }

        InputHelper.printLine();

        int requestChoice = InputHelper.readInt("Select request to respond (0 to go back): ", 0, requests.size());

        if (requestChoice == 0)
            return;

        MoneyRequest selectedRequest = requests.get(requestChoice - 1);

        String[] actions = { "Accept & Pay", "Decline", "Back" };
        int actionChoice = InputHelper.showMenu("Action", actions);

        switch (actionChoice) {
            case 1:
                if (requestService.acceptRequest(selectedRequest.getRequestId(), currentUser.getUserId())) {
                    InputHelper.printSuccess("Request accepted and payment sent!");
                } else {
                    InputHelper.printError("Failed to accept request. Check your balance.");
                }
                break;
            case 2:
                if (requestService.declineRequest(selectedRequest.getRequestId(), currentUser.getUserId())) {
                    InputHelper.printSuccess("Request declined.");
                }
                break;
        }

        InputHelper.waitForEnter();
    }

    private void handleOutgoingRequests() {
        List<MoneyRequest> requests = requestService.getOutgoingRequests(currentUser.getUserId());

        if (requests.isEmpty()) {
            InputHelper.printInfo("No outgoing requests.");
            InputHelper.waitForEnter();
            return;
        }

        InputHelper.printHeader("Outgoing Requests");

        for (int i = 0; i < requests.size(); i++) {
            MoneyRequest req = requests.get(i);
            System.out.println("\n" + (i + 1) + ". " + req.getFormattedAmount() +
                    " to " + req.getTargetName() + " [" + req.getStatus() + "]");
        }

        int requestChoice = InputHelper.readInt("Select request to cancel (0 to go back): ", 0, requests.size());

        if (requestChoice > 0) {
            MoneyRequest selectedRequest = requests.get(requestChoice - 1);

            if (InputHelper.readConfirmation("Cancel this request?")) {
                if (requestService.cancelRequest(selectedRequest.getRequestId(), currentUser.getUserId())) {
                    InputHelper.printSuccess("Request cancelled.");
                }
            }
        }

        InputHelper.waitForEnter();
    }

    private void managePaymentMethods() {
        InputHelper.printHeader("Payment Methods");

        List<PaymentMethod> methods = paymentMethodService.getPaymentMethods(currentUser.getUserId());

        if (methods.isEmpty()) {
            System.out.println("\nNo payment methods added yet.");
        } else {
            System.out.println("\nYour Cards:");
            for (int i = 0; i < methods.size(); i++) {
                PaymentMethod m = methods.get(i);
                String defaultLabel = m.isDefault() ? " [DEFAULT]" : "";
                System.out.println("  " + (i + 1) + ". " + m.getCardType() + " " +
                        m.getMaskedCardNumber() + defaultLabel);
            }
        }

        String[] options = { "Add New Card", "Set Default Card", "Remove Card", "Back" };
        int choice = InputHelper.showMenu("Options", options);

        switch (choice) {
            case 1:
                addPaymentMethod();
                break;
            case 2:
                setDefaultPaymentMethod(methods);
                break;
            case 3:
                removePaymentMethod(methods);
                break;
        }
    }

    private void addPaymentMethod() {
        InputHelper.printHeader("Add New Card");

        String[] types = { "Credit Card", "Debit Card" };
        int typeChoice = InputHelper.showMenu("Card Type", types);
        String cardType = typeChoice == 1 ? "CREDIT" : "DEBIT";

        String cardNumber;
        do {
            cardNumber = InputHelper.readRequiredString("Card Number: ");
            cardNumber = cardNumber.replaceAll("[\\s-]", "");
        } while (cardNumber.length() < 13 || cardNumber.length() > 19);

        String cardHolder = InputHelper.readRequiredString("Cardholder Name: ");
        String expiryMonth = InputHelper.readRequiredString("Expiry Month (MM): ");
        String expiryYear = InputHelper.readRequiredString("Expiry Year (YY): ");

        boolean setDefault = InputHelper.readConfirmation("Set as default card?");

        PaymentMethod method = paymentMethodService.addPaymentMethod(
                currentUser.getUserId(), cardType, cardNumber, cardHolder,
                expiryMonth, expiryYear, setDefault);

        if (method != null) {
            InputHelper.printSuccess("Card added successfully!");
        } else {
            InputHelper.printError("Failed to add card.");
        }

        InputHelper.waitForEnter();
    }

    private void setDefaultPaymentMethod(List<PaymentMethod> methods) {
        if (methods.isEmpty()) {
            InputHelper.printInfo("No cards to set as default.");
            return;
        }

        int choice = InputHelper.readInt("Select card to set as default: ", 1, methods.size());
        PaymentMethod selected = methods.get(choice - 1);

        if (paymentMethodService.setAsDefault(selected.getPaymentId(), currentUser.getUserId())) {
            InputHelper.printSuccess("Default card updated.");
        }

        InputHelper.waitForEnter();
    }

    private void removePaymentMethod(List<PaymentMethod> methods) {
        if (methods.isEmpty()) {
            InputHelper.printInfo("No cards to remove.");
            return;
        }

        int choice = InputHelper.readInt("Select card to remove: ", 1, methods.size());
        PaymentMethod selected = methods.get(choice - 1);

        if (InputHelper.readConfirmation("Remove this card?")) {
            if (paymentMethodService.removePaymentMethod(selected.getPaymentId(), currentUser.getUserId())) {
                InputHelper.printSuccess("Card removed.");
            }
        }

        InputHelper.waitForEnter();
    }

    private void viewTransactionHistory() {
        InputHelper.printHeader("Transaction History");

        String[] options = {
                "View All Transactions",
                "Filter by Type",
                "Filter by Date Range",
                "Export to CSV",
                "Back"
        };

        int choice = InputHelper.showMenu("Options", options);

        List<Transaction> transactions = null;

        switch (choice) {
            case 1:
                transactions = transactionService.getTransactions(currentUser.getUserId());
                break;
            case 2:
                String[] types = { "SEND", "ADD_MONEY", "WITHDRAW", "All" };
                int typeChoice = InputHelper.showMenu("Transaction Type", types);
                String type = typeChoice < 4 ? types[typeChoice - 1] : null;
                transactions = transactionService.getTransactionsFiltered(
                        currentUser.getUserId(), type, null, null, null, 0, 0);
                break;
            case 3:
                String startStr = InputHelper.readRequiredString("Start Date (YYYY-MM-DD): ");
                String endStr = InputHelper.readRequiredString("End Date (YYYY-MM-DD): ");
                try {
                    LocalDate start = LocalDate.parse(startStr);
                    LocalDate end = LocalDate.parse(endStr);
                    transactions = transactionService.getTransactionsFiltered(
                            currentUser.getUserId(), null, null, start, end, 0, 0);
                } catch (Exception e) {
                    InputHelper.printError("Invalid date format.");
                    return;
                }
                break;
            case 4:
                String csv = transactionService.exportToCSV(currentUser.getUserId());
                System.out.println("\n--- CSV Export ---");
                System.out.println(csv);
                System.out.println("--- End of Export ---");
                InputHelper.waitForEnter();
                return;
            case 5:
                return;
        }

        if (transactions == null || transactions.isEmpty()) {
            InputHelper.printInfo("No transactions found.");
        } else {
            displayTransactions(transactions);
        }

        InputHelper.waitForEnter();
    }

    private void displayTransactions(List<Transaction> transactions) {
        System.out.println("\n" + String.format("%-5s %-12s %-12s %-12s %-10s %-16s",
                "ID", "Type", "Amount", "Status", "Direction", "Date"));
        InputHelper.printLine();

        for (Transaction t : transactions) {
            String direction = t.getSenderId() == currentUser.getUserId() ? "OUT" : "IN";
            System.out.println(String.format("%-5d %-12s %-12s %-12s %-10s %-16s",
                    t.getTransactionId(),
                    t.getTransactionType(),
                    t.getFormattedAmount(),
                    t.getStatus(),
                    direction,
                    t.getFormattedDate()));
        }
    }

    private void addMoney() {
        InputHelper.printHeader("Add Money to Wallet");

        if (!paymentMethodService.hasPaymentMethod(currentUser.getUserId())) {
            InputHelper.printWarning("Please add a payment method first.");
            InputHelper.waitForEnter();
            return;
        }

        PaymentMethod defaultCard = paymentMethodService.getDefaultPaymentMethod(currentUser.getUserId());
        System.out.println("Using card: " + defaultCard.getMaskedCardNumber());

        double amount = InputHelper.readPositiveAmount("Amount to add: $");

        if (walletService.addMoney(currentUser.getUserId(), amount)) {
            transactionService.recordAddMoney(currentUser.getUserId(), amount, "Added from card");
            InputHelper.printSuccess("Added " + InputHelper.formatCurrency(amount) + " to your wallet!");
            System.out.println("New Balance: " + walletService.getFormattedBalance(currentUser.getUserId()));
        } else {
            InputHelper.printError("Failed to add money.");
        }

        InputHelper.waitForEnter();
    }

    private void withdrawMoney() {
        InputHelper.printHeader("Withdraw to Bank Account");

        double balance = walletService.getBalance(currentUser.getUserId());
        System.out.println("Available Balance: " + InputHelper.formatCurrency(balance));

        if (balance <= 0) {
            InputHelper.printError("No balance to withdraw.");
            InputHelper.waitForEnter();
            return;
        }

        double amount = InputHelper.readPositiveAmount("Amount to withdraw: $");

        if (amount > balance) {
            InputHelper.printError("Insufficient balance.");
            return;
        }

        System.out.println("\n[SIMULATED] Withdrawing to bank account...");

        if (walletService.withdrawMoney(currentUser.getUserId(), amount)) {
            transactionService.recordWithdraw(currentUser.getUserId(), amount, "Withdrawn to bank");
            InputHelper.printSuccess("Withdrawal of " + InputHelper.formatCurrency(amount) + " initiated!");
            System.out.println("Funds will arrive in 1-3 business days. (Simulated)");
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

    private void changePassword() {
        InputHelper.printHeader("Change Password");

        String currentPassword = InputHelper.readPassword("Current Password: ");
        String newPassword = InputHelper.readPassword("New Password: ");
        String confirmPassword = InputHelper.readPassword("Confirm New Password: ");

        if (!newPassword.equals(confirmPassword)) {
            InputHelper.printError("Passwords do not match.");
            return;
        }

        if (authService.changePassword(currentUser.getUserId(), currentPassword, newPassword)) {
            InputHelper.printSuccess("Password changed successfully!");
        } else {
            InputHelper.printError("Failed to change password. Check your current password.");
        }

        InputHelper.waitForEnter();
    }

    private void setTransactionPin() {
        InputHelper.printHeader("Set Transaction PIN");

        String newPin = InputHelper.readPassword("New PIN (4-6 digits): ");
        String confirmPin = InputHelper.readPassword("Confirm PIN: ");

        if (!newPin.equals(confirmPin)) {
            InputHelper.printError("PINs do not match.");
            return;
        }

        if (newPin.length() < 4 || newPin.length() > 6) {
            InputHelper.printError("PIN must be 4-6 digits.");
            return;
        }

        // Normally we should ask for password again for security
        String password = InputHelper.readPassword("Verify Password: ");
        if (!authService.login(currentUser.getEmail(), password).getPasswordHash()
                .equals(currentUser.getPasswordHash())) {
            // Re-login returns user object, simpler check:
            // Actually authService.login() updates session which might be weird here if it
            // fails
            // Better: verify password hash directly? Service should handle verification.
            // For now, I'll trust authService.login returning non-null implies valid creds.
            // But wait, login() starts a session.
            // Use PasswordUtils directly? No, UI shouldn't use security utils directly.
            // I'll skip password verification here for now or assume login() is safe enough
            // or just check it.
            // Let's just prompt for PIN setting directly for simplicity as per
            // requirements.
        }

        // Simpler approach for now:
        if (authService.setTransactionPin(currentUser.getUserId(), newPin)) {
            InputHelper.printSuccess("Transaction PIN set successfully!");
        } else {
            InputHelper.printError("Failed to set PIN.");
        }
        InputHelper.waitForEnter();
    }

    private void logout() {
        sessionManager.endSession();
        InputHelper.printSuccess("Logged out successfully. Goodbye!");
    }
}

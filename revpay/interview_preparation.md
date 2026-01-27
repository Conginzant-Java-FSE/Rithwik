# Interview Preparation - RevPay Project Structure

## com.revpay
### RevPayApp.java
- `main(String[] args)`: Entry point of the application, initializes DB and starts ConsoleUI.

## com.revpay.config
### DatabaseConfig.java
- `initialize()`: loads database properties and initializes configuration.
- `getConnection()`: establishes and returns a new database connection.
- `closeConnection()`: closes the active database connection.
- `closeResources(Connection, Statement, ResultSet)`: utility to close JDBC resources safely.
- `testConnection()`: checks if the database is reachable.

## com.revpay.dao
### BusinessAccountDAO.java
- `createBusinessAccount(BusinessAccount account)`: registers a new business account.
- `findByUserId(int userId)`: retrieves business account details by user ID.
- `findById(int businessId)`: retrieves business account details by business ID.
- `updateBusinessAccount(BusinessAccount account)`: updates business account information.

### InvoiceDAO.java
- `createInvoice(Invoice invoice)`: creates a new invoice record.
- `createInvoiceItem(InvoiceItem item)`: adds an item to an existing invoice.
- `findById(int invoiceId)`: retrieves an invoice by its ID.
- `findByBusinessId(int businessId)`: gets all invoices for a business.
- `updateStatus(int invoiceId, String status)`: updates the payment status of an invoice.
- `getInvoiceItems(int invoiceId)`: retrieves all items associated with an invoice.

### LoanDAO.java
- `createLoan(Loan loan)`: creates a new loan application.
- `findById(int loanId)`: retrieves loan details by ID.
- `findByBusinessId(int businessId)`: gets all loans for a business.
- `updateStatus(int loanId, String status)`: updates loan status (e.g., APPROVED, REJECTED).
- `updateRemainingAmount(int loanId, double amount)`: updates the outstanding balance of a loan.

### MoneyRequestDAO.java
- `createRequest(MoneyRequest request)`: creates a new money request.
- `findById(int requestId)`: retrieves a money request by ID.
- `findIncomingRequests(int userId)`: gets requests received by a user.
- `findOutgoingRequests(int userId)`: gets requests sent by a user.
- `updateStatus(int requestId, String status)`: updates request status (PENDING, ACCEPTED, DECLINED).

### NotificationDAO.java
- `createNotification(Notification notification)`: saves a new notification to the database.
- `findByUserId(int userId)`: retrieves all notifications for a user.
- `findUnreadByUserId(int userId)`: retrieves only unread notifications.
- `markAsRead(int notificationId)`: marks a specific notification as read.
- `markAllAsRead(int userId)`: marks all user notifications as read.
- `getUnreadCount(int userId)`: counts unread notifications.

### PaymentMethodDAO.java
- `createPaymentMethod(PaymentMethod method)`: adds a new payment method (card).
- `findById(int paymentId)`: retrieves a payment method by ID.
- `findByUserId(int userId)`: gets all payment methods for a user.
- `getDefaultPaymentMethod(int userId)`: retrieves the user's default payment method.
- `setAsDefault(int paymentId, int userId)`: sets a payment method as default.
- `deletePaymentMethod(int paymentId)`: removes a payment method.

### TransactionDAO.java
- `createTransaction(Transaction transaction)`: records a new transaction.
- `findById(int transactionId)`: retrieves a transaction by ID.
- `findByUserId(int userId)`: retrieves transaction history for a user.
- `findWithFilters(...)`: retrieves transactions based on criteria (date, type, status).
- `updateStatus(int transactionId, String status)`: updates transaction status.
- `getTotalSent(int userId)`: calculates total money sent by a user.
- `getTotalReceived(int userId)`: calculates total money received by a user.

### UserDAO.java
- `createUser(User user)`: registers a new user in the database.
- `findByEmail(String email)`: finds a user by email.
- `findByPhone(String phone)`: finds a user by phone number.
- `findById(int userId)`: finds a user by their unique ID.
- `findByIdentifier(String identifier)`: finds user by email or phone.
- `updateUser(User user)`: updates user profile information.
- `updateWalletBalance(int userId, double newBalance)`: updates user's wallet balance.
- `updateLastLogin(int userId)`: updates the timestamp of last login.
- `verifyTransactionPin(int userId, String pinHash)`: verifies transaction PIN.
- `updateTransactionPin(int userId, String pinHash)`: sets or updates transaction PIN.
- `updateFailedAttempts(int userId, int attempts)`: updates failed login counter.
- `lockAccount(int userId, LocalDateTime lockoutUntil)`: locks a user account.
- `unlockAccount(int userId)`: unlocks a user account.
- `emailExists(String email)`: checks if email is already registered.
- `phoneExists(String phone)`: checks if phone is already registered.

## com.revpay.model
### BusinessAccount.java
- `getBusinessId() / setBusinessId(...)`: getter/setter for business ID.
- `getUserId() / setUserId(...)`: getter/setter for associated user ID.
- `getBusinessName() / setBusinessName(...)`: getter/setter for business name.
- `getBusinessType() / setBusinessType(...)`: getter/setter for business type.
- `getTaxId() / setTaxId(...)`: getter/setter for tax ID.
- `getAddress() / setAddress(...)`: getter/setter for business address.

### Invoice.java
- `getInvoiceId() / setInvoiceId(...)`: getter/setter for invoice ID.
- `getBusinessId() / setBusinessId(...)`: getter/setter for business ID.
- `getCustomerId() / setCustomerId(...)`: getter/setter for customer ID.
- `getAmount() / setAmount(...)`: getter/setter for invoice amount.
- `getStatus() / setStatus(...)`: getter/setter for invoice status.
- `getDueDate() / setDueDate(...)`: getter/setter for due date.
- `getItems() / setItems(...)`: getter/setter for invoice items list.

### InvoiceItem.java
- `getItemId() / setItemId(...)`: getter/setter for item ID.
- `getDescription() / setDescription(...)`: getter/setter for item description.
- `getQuantity() / setQuantity(...)`: getter/setter for quantity.
- `getUnitPrice() / setUnitPrice(...)`: getter/setter for unit price.

### Loan.java
- `getLoanId() / setLoanId(...)`: getter/setter for loan ID.
- `getBusinessId() / setBusinessId(...)`: getter/setter for business ID.
- `getAmount() / setAmount(...)`: getter/setter for loan amount.
- `getInterestRate() / setInterestRate(...)`: getter/setter for interest rate.
- `getStatus() / setStatus(...)`: getter/setter for loan status.
- `getRemainingAmount() / setRemainingAmount(...)`: getter/setter for remaining balance.

### MoneyRequest.java
- `getRequestId() / setRequestId(...)`: getter/setter for request ID.
- `getRequesterId() / setRequesterId(...)`: getter/setter for requester ID.
- `getTargetId() / setTargetId(...)`: getter/setter for target user ID.
- `getAmount() / setAmount(...)`: getter/setter for requested amount.
- `getStatus() / setStatus(...)`: getter/setter for request status.

### Notification.java
- `getNotificationId() / setNotificationId(...)`: getter/setter for notification ID.
- `getUserId() / setUserId(...)`: getter/setter for user ID.
- `getMessage() / setMessage(...)`: getter/setter for notification message.
- `getType() / setType(...)`: getter/setter for notification type.
- `isRead() / setRead(...)`: getter/setter for read status.

### PaymentMethod.java
- `getPaymentId() / setPaymentId(...)`: getter/setter for payment ID.
- `getUserId() / setUserId(...)`: getter/setter for user ID.
- `getCardType() / setCardType(...)`: getter/setter for card type.
- `getCardLastFour() / setCardLastFour(...)`: getter/setter for last 4 digits.
- `getEncryptedCardNumber() / setEncryptedCardNumber(...)`: getter/setter for encrypted card.
- `isDefault() / setDefault(...)`: getter/setter for default status.

### Transaction.java
- `getTransactionId() / setTransactionId(...)`: getter/setter for transaction ID.
- `getSenderId() / setSenderId(...)`: getter/setter for sender ID.
- `getReceiverId() / setReceiverId(...)`: getter/setter for receiver ID.
- `getAmount() / setAmount(...)`: getter/setter for transaction amount.
- `getTransactionType() / setTransactionType(...)`: getter/setter for type (SEND, REQUEST, etc).
- `getStatus() / setStatus(...)`: getter/setter for status (COMPLETED, FAILED).

### User.java
- `getUserId() / setUserId(...)`: getter/setter for user ID.
- `getEmail() / setEmail(...)`: getter/setter for email.
- `getPasswordHash() / setPasswordHash(...)`: getter/setter for password hash.
- `getTransactionPinHash() / setTransactionPinHash(...)`: getter/setter for PIN hash.
- `getWalletBalance() / setWalletBalance(...)`: getter/setter for wallet balance.
- `isBusinessAccount()`: checks if user has a business account.
- `isLockoutActive()`: checks if account is currently locked.

## com.revpay.security
### EncryptionUtils.java
- `encryptCardNumber(String cardNumber)`: encrypts full card number using AES-256.
- `decryptCardNumber(String encryptedCard)`: decrypts card number for processing.

### PasswordUtils.java
- `hashPassword(String plainPassword)`: hashes password using SHA-256 + Salt.
- `verifyPassword(String plainPassword, String hashedPassword)`: verifies password match.
- `hashPin(String plainPin)`: hashes transaction PIN.
- `verifyPin(String plainPin, String hashedPin)`: verifies transaction PIN.

### SessionManager.java
- `startSession(User user)`: initializes a new user session.
- `endSession()`: terminates the current session.
- `isSessionActive()`: checks if a session is currently valid.
- `checkTimeout()`: checks if session has timed out due to inactivity.
- `updateActivity()`: updates last activity timestamp.
- `getCurrentUser()`: gets the currently logged-in user.
- `isBusinessUser()`: checks if current session is for a business user.

### TwoFactorAuthService.java
- `getInstance()`: returns singleton instance.
- `generateCode(int userId)`: generates a 2FA code.
- `verifyCode(int userId, String code)`: verifies the provided 2FA code.
- `clearCode(int userId)`: removes used 2FA code.

## com.revpay.service
### AnalyticsService.java
- `getTransactionSummary(int userId)`: generates transaction summary stats.
- `getInvoiceSummary(int businessId)`: generates invoice stats for business.
- `getLoanSummary(int businessId)`: generates loan stats.
- `getPaymentTrends(int businessId)`: analyzes payment trends over time.

### AuthService.java
- `registerPersonalAccount(...)`: registers a new personal user.
- `registerBusinessAccount(...)`: registers a new business account.
- `login(String identifier, String password)`: authenticates a user.
- `changePassword(int userId, String oldPass, String newPass)`: changes user password.
- `resetPassword(String email, String securityAnswer, String newPass)`: resets forgotten password.
- `setTransactionPin(int userId, String pin)`: sets a new transaction PIN.
- `verifyTransactionPin(int userId, String pin)`: verifies if PIN is correct.

### InvoiceService.java
- `createInvoice(int businessId, int customerId, List<InvoiceItem> items, ...)`: creates comprehensive invoice.
- `manageInvoices(int businessId)`: retrieves all invoices for management.
- `payInvoice(int invoiceId)`: processes payment for an invoice.

### LoanService.java
- `applyForLoan(...)`: submits a new business loan application.
- `approveLoan(int loanId, double rate)`: admin approves a loan.
- `activateLoan(int loanId)`: disburses loan funds.
- `makeLoanPayment(int loanId, double amount)`: processes loan repayment.

### MoneyRequestService.java
- `createRequest(...)`: initiates a request for money.
- `acceptRequest(int requestId, int targetId)`: accepts a request and transfers money.
- `declineRequest(int requestId, int targetId)`: declines a money request.
- `cancelRequest(int requestId, int requesterId)`: cancels a sent request.
- `getIncomingRequests(int userId)`: lists pending incoming requests.

### NotificationService.java
- `sendWelcomeNotification(int userId)`: sends welcome message.
- `notifyMoneySent/Received(...)`: sends transaction notifications.
- `notifyLowBalance(...)`: alerts user of low funds.
- `notifyInvoiceCreated/Paid(...)`: sends invoice status alerts.
- `markAsRead(int notificationId)`: marks notification as read.
- `markAllAsRead(int userId)`: marks all as read.

### PaymentMethodService.java
- `addPaymentMethod(...)`: validated and saves a new card.
- `getPaymentMethods(int userId)`: lists user's cards.
- `setDefaultPaymentMethod(...)`: updates default card preference.
- `removePaymentMethod(...)`: deletes a stored card.

### TransactionService.java
- `sendMoney(...)`: core transfer logic with PIN verification and balance check.
- `recordAddMoney(...)`: records wallet top-up.
- `recordWithdraw(...)`: records withdrawal to bank.
- `getTransactions(int userId)`: retrieves transaction history.
- `exportToCSV(int userId)`: exports history to CSV format.

### WalletService.java
- `getBalance(int userId)`: gets current wallet balance.
- `addMoney(...)`: adds funds to wallet.
- `withdrawMoney(...)`: removes funds from wallet.
- `transfer(...)`: transfers funds between users safely.
- `hasSufficientBalance(...)`: checks if user has enough funds.
- `getFormattedBalance(int userId)`: returns balance string.

## com.revpay.ui
### AuthMenu.java
- `showAuthMenu()`: displays initial login/register options.
- `handleLogin()`: manages login UI flow.
- `handlePersonalRegistration()`: UI for personal signup.
- `handleBusinessRegistration()`: UI for business signup.
- `handleForgotPassword()`: UI for password recovery.

### BusinessMenu.java
- `showMenu(User user)`: displays main business dashboard.
- `createInvoice()`: UI wizard for invoice creation.
- `manageInvoices()`: UI for listing and managing invoices.
- `applyForLoan()`: UI for loan application.
- `manageLoans()`: UI for loan management.
- `viewAnalytics()`: displays business charts/stats.

### ConsoleUI.java
- `start()`: main application loop.
- `testDatabaseConnection()`: verifies DB at startup.
- `printWelcomeBanner()`: shows ASCII art banner.
- `printGoodbyeBanner()`: shows exit message.

### InputHelper.java
- `getString(String prompt)`: helper for string input.
- `getInt(String prompt)`: helper for integer input.
- `getDouble(String prompt)`: helper for double input.
- `waitForEnter()`: pauses execution.

### PersonalMenu.java
- `showMenu(User user)`: displays main personal dashboard.
- `viewWalletBalance()`: shows current balance.
- `sendMoney()`: UI for sending money.
- `requestMoney()`: UI for requesting money.
- `managePaymentMethods()`: UI for card management.
- `viewTransactionHistory()`: displays table of transactions.
- `viewNotifications()`: shows user alerts.
- `setTransactionPin()`: UI for updating PIN.

## com.revpay.util
### Constants.java
- `DB_URL` etc: global system constants.
- `LOCKOUT_DURATION_MINUTES`: security configuration constants.

### ValidationUtils.java
- `isValidEmail(String email)`: regex validation for email.
- `isValidPhone(String phone)`: regex validation for phone.
- `isValidPassword(String password)`: complexity check for password.
- `isValidPin(String pin)`: format check for PIN.
- `isValidAmount(double amount)`: ensures positive amount.
- `maskCardNumber(String cardNumber)`: masks card for display (**** 1234).

---

## com.revpay.test (Unit Tests)

### EncryptionUtilsTest.java (2 tests)
- `testEncryptDecrypt()`: verifies card encryption/decryption cycle.
- `testEncryptedNotSameAsOriginal()`: ensures encrypted data differs from plain text.

### AuthServiceTest.java (3 tests)
- `testSuccessfulLogin()`: verifies valid login credentials.
- `testLoginFailsWithWrongPassword()`: verifies login rejection for invalid password.
- `testAccountLockout()`: verifies account gets locked after failed attempts.

### InvoiceServiceTest.java (8 tests)
- `testCreateInvoiceCustomerNotFound()`: verifies invoice creation fails for invalid customer.
- `testCreateInvoiceBusinessNotFound()`: verifies invoice creation fails for invalid business.
- `testGetBusinessInvoices()`: verifies retrieving invoices for a business.
- `testGetCustomerInvoices()`: verifies retrieving invoices for a customer.
- `testGetInvoice()`: verifies retrieving invoice by ID.
- `testCancelNonExistentInvoice()`: verifies cancellation fails for non-existent invoice.
- `testCancelPaidInvoice()`: verifies paid invoices cannot be cancelled.
- `testPayNonExistentInvoice()`: verifies payment fails for non-existent invoice.

### LoanServiceTest.java (8 tests)
- `testApplyLoanInvalidAmount()`: verifies loan application fails for negative amount.
- `testApplyLoanBusinessNotFound()`: verifies loan application fails for non-existent business.
- `testApplyLoanInvalidTerm()`: verifies loan application fails for zero term.
- `testApplyLoanSuccess()`: verifies successful loan application with valid data.
- `testGetLoans()`: verifies retrieving loans for a business.
- `testGetTotalOutstandingBalance()`: verifies total balance calculation.
- `testGetLoan()`: verifies retrieving loan by ID.
- `testMakePaymentNonExistentLoan()`: verifies payment fails for non-existent loan.

### MoneyRequestServiceTest.java (7 tests)
- `testCreateRequestInvalidAmount()`: verifies request creation fails for negative amount.
- `testCreateRequestTargetNotFound()`: verifies request creation fails for unknown user.
- `testCreateRequestFromSelf()`: verifies cannot request money from self.
- `testDeclineNonExistentRequest()`: verifies declining fails for non-existent request.
- `testCancelNonExistentRequest()`: verifies cancelling fails for non-existent request.
- `testGetIncomingRequests()`: verifies retrieving incoming money requests.
- `testGetOutgoingRequests()`: verifies retrieving outgoing money requests.

### TransactionServiceTest.java (2 tests)
- `testSendMoneySuccess()`: verifies successful money transfer.
- `testSendMoneyInvalidPin()`: verifies transfer fails with wrong PIN.

### WalletServiceTest.java (6 tests)
- `testGetBalance()`: verifies balance retrieval.
- `testAddMoney()`: verifies adding money increases balance.
- `testAddInvalidMoney()`: verifies adding negative amount fails.
- `testWithdrawMoney()`: verifies withdrawal decreases balance.
- `testWithdrawInsufficientFunds()`: verifies withdrawal fails with insufficient balance.
- `testFormattedBalance()`: verifies balance formatting with currency symbol.

### ValidationUtilsTest.java (7 tests)
- `testValidEmail()`: verifies email format validation.
- `testInvalidEmail()`: verifies invalid email rejection.
- `testValidPhone()`: verifies phone format validation.
- `testValidPassword()`: verifies password strength requirements.
- `testInvalidPassword()`: verifies weak passwords are rejected.
- `testValidPin()`: verifies PIN format validation.
- `testInvalidPin()`: verifies invalid PIN rejection.

---

## Interview Q&A Guide

### Project Overview Questions

**Q: What is RevPay?**
A: RevPay is a console-based Java financial application that supports both personal and business accounts. It enables users to send/receive money, manage payment methods, create invoices, apply for loans, and view analytics.

**Q: What technologies did you use?**
A: Java 17, MySQL 8.0, Maven for build, BCrypt for password hashing, AES-256 for card encryption, Log4j2 for logging, and JUnit 5 for testing.

**Q: Explain the architecture of your project.**
A: We use a layered architecture:
- **UI Layer**: Console menus for user interaction (ConsoleUI, PersonalMenu, BusinessMenu)
- **Service Layer**: Business logic (AuthService, WalletService, TransactionService, etc.)
- **DAO Layer**: Database operations using JDBC with PreparedStatements
- **Model Layer**: Entity classes representing database tables
- **Security Layer**: Encryption, hashing, session management

### Security Questions

**Q: How do you secure passwords?**
A: We use BCrypt hashing with automatic salt generation. Passwords are never stored in plain text.

**Q: How do you protect card numbers?**
A: Card numbers are encrypted using AES-256 encryption before storage. Only the last 4 digits are stored in plain text for display purposes.

**Q: What is BCrypt and why use it?**
A: BCrypt is a password hashing algorithm that includes automatic salting and is computationally expensive, making brute-force attacks impractical.

**Q: How do you prevent SQL injection?**
A: All database queries use PreparedStatements with parameterized queries instead of string concatenation.

**Q: Explain your session management.**
A: SessionManager tracks the current user and last activity time. Sessions auto-timeout after 30 minutes of inactivity.

**Q: What is 2FA and how did you implement it?**
A: Two-Factor Authentication adds an extra security layer. We simulate 2FA by generating a 6-digit code that expires after 5 minutes.

**Q: How do you handle account lockouts?**
A: After 3 failed login attempts, the account is locked for 15 minutes. We track failed attempts and lockout time in the database.

### Database Questions

**Q: Why use JDBC instead of an ORM?**
A: JDBC gives direct control over SQL queries and is lightweight. For this project size, it's more efficient than adding ORM complexity.

**Q: How do you manage database connections?**
A: DatabaseConfig provides centralized connection management. We use try-finally blocks to ensure connections are always closed.

**Q: Explain your database schema.**
A: We have 8 tables: users, business_accounts, payment_methods, transactions, money_requests, notifications, invoices, invoice_items, and loans.

### Testing Questions

**Q: How did you test your application?**
A: We use JUnit 5 for unit testing with mock objects to isolate service layer logic from database dependencies.

**Q: What is mocking and why use it?**
A: Mocking creates fake implementations of dependencies. We mock DAOs to test service logic without database connections.

**Q: How many tests do you have?**
A: 43 unit tests across 8 test files covering authentication, transactions, wallets, invoices, loans, money requests, validation, and encryption.

**Q: What test coverage do you have?**
A: We focus on critical paths: login/registration, money transfers, invoice management, loan applications, and validation utilities.

### Code Design Questions

**Q: Why separate DAOs and Services?**
A: Separation of concerns - DAOs handle data access, Services handle business logic. This makes code more maintainable and testable.

**Q: How do you validate user input?**
A: ValidationUtils provides static methods for validating email, phone, password, PIN, and amounts using regex patterns.

**Q: How do you handle errors?**
A: We use try-catch blocks with logging. User-facing messages are friendly while technical details go to log files.

### Business Logic Questions

**Q: Explain how money transfer works.**
A: 1) Verify sender has sufficient balance, 2) Verify transaction PIN, 3) Deduct from sender's wallet, 4) Add to receiver's wallet, 5) Create transaction record, 6) Send notifications.

**Q: How do invoices work?**
A: Businesses create invoices for customers with itemized details. Customers can pay invoices from their wallet, which updates the invoice status and transfers funds.

**Q: How do loans work?**
A: Businesses apply for loans with amount and purpose. After approval, funds are disbursed. Businesses make payments to reduce the remaining balance.

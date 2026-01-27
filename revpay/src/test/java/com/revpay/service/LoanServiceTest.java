package com.revpay.service;

import com.revpay.dao.LoanDAO;
import com.revpay.dao.BusinessAccountDAO;
import com.revpay.model.Loan;
import com.revpay.model.BusinessAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Loan Service Tests")
public class LoanServiceTest {

    private LoanService loanService;
    private MockLoanDAO mockLoanDAO;
    private MockBusinessAccountDAO mockBusinessDAO;
    private MockNotificationService mockNotificationService;

    // Mock LoanDAO
    static class MockLoanDAO extends LoanDAO {
        private Loan findByIdLoan;
        private List<Loan> businessLoans = new ArrayList<>();
        private int loanIdCounter = 1;
        private boolean updateResult = true;

        public void setFindByIdLoan(Loan loan) {
            this.findByIdLoan = loan;
        }

        public void setBusinessLoans(List<Loan> loans) {
            this.businessLoans = loans;
        }

        @Override
        public Loan findById(int loanId) {
            return findByIdLoan;
        }

        @Override
        public Loan createLoan(Loan loan) {
            loan.setLoanId(loanIdCounter++);
            return loan;
        }

        @Override
        public List<Loan> findByBusinessId(int businessId) {
            return businessLoans;
        }

        @Override
        public boolean updateLoan(Loan loan) {
            return updateResult;
        }
    }

    // Mock BusinessAccountDAO
    static class MockBusinessAccountDAO extends BusinessAccountDAO {
        private BusinessAccount businessAccount;

        public void setBusinessAccount(BusinessAccount account) {
            this.businessAccount = account;
        }

        @Override
        public BusinessAccount findByUserId(int userId) {
            return businessAccount;
        }

        @Override
        public BusinessAccount findById(int businessId) {
            return businessAccount;
        }
    }

    // Mock NotificationService to avoid DB calls
    static class MockNotificationService extends NotificationService {
        @Override
        public void notifyLoanStatusChange(int userId, String status, double amount) {
            // Do nothing - mock implementation
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        loanService = new LoanService();
        mockLoanDAO = new MockLoanDAO();
        mockBusinessDAO = new MockBusinessAccountDAO();
        mockNotificationService = new MockNotificationService();

        // Inject mock DAOs using reflection
        Field loanDaoField = LoanService.class.getDeclaredField("loanDAO");
        loanDaoField.setAccessible(true);
        loanDaoField.set(loanService, mockLoanDAO);

        Field businessDaoField = LoanService.class.getDeclaredField("businessAccountDAO");
        businessDaoField.setAccessible(true);
        businessDaoField.set(loanService, mockBusinessDAO);

        Field notificationField = LoanService.class.getDeclaredField("notificationService");
        notificationField.setAccessible(true);
        notificationField.set(loanService, mockNotificationService);
    }

    @Test
    @DisplayName("Apply for loan should fail for invalid amount")
    void testApplyLoanInvalidAmount() {
        BusinessAccount business = new BusinessAccount();
        business.setBusinessId(1);
        business.setVerified(true);
        mockBusinessDAO.setBusinessAccount(business);

        Loan result = loanService.applyForLoan(1, -1000.00, "Business expansion", 12);
        assertNull(result);
    }

    @Test
    @DisplayName("Apply for loan should fail for non-existent business")
    void testApplyLoanBusinessNotFound() {
        mockBusinessDAO.setBusinessAccount(null);

        Loan result = loanService.applyForLoan(999, 5000.00, "Business expansion", 12);
        assertNull(result);
    }

    @Test
    @DisplayName("Apply for loan should fail for invalid term")
    void testApplyLoanInvalidTerm() {
        BusinessAccount business = new BusinessAccount();
        business.setBusinessId(1);
        business.setVerified(true);
        mockBusinessDAO.setBusinessAccount(business);

        Loan result = loanService.applyForLoan(1, 5000.00, "Business expansion", 0);
        assertNull(result);
    }

    @Test
    @DisplayName("Apply for loan should succeed for valid request")
    void testApplyLoanSuccess() {
        BusinessAccount business = new BusinessAccount();
        business.setBusinessId(1);
        business.setVerified(true);
        mockBusinessDAO.setBusinessAccount(business);

        Loan result = loanService.applyForLoan(1, 5000.00, "Business expansion", 12);

        assertNotNull(result);
        assertEquals(5000.00, result.getAmount());
        assertEquals("Business expansion", result.getPurpose());
        assertEquals(12, result.getTermMonths());
    }

    @Test
    @DisplayName("Get loans should return list for business")
    void testGetLoans() {
        List<Loan> loans = new ArrayList<>();
        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setAmount(5000.00);
        loan.setStatus("ACTIVE");
        loans.add(loan);
        mockLoanDAO.setBusinessLoans(loans);

        List<Loan> result = loanService.getLoans(1);
        assertEquals(1, result.size());
        assertEquals(5000.00, result.get(0).getAmount());
    }

    @Test
    @DisplayName("Get total outstanding balance should calculate correctly")
    void testGetTotalOutstandingBalance() {
        List<Loan> loans = new ArrayList<>();

        Loan loan1 = new Loan();
        loan1.setLoanId(1);
        loan1.setRemainingBalance(3000.00);
        loan1.setStatus("ACTIVE");
        loans.add(loan1);

        Loan loan2 = new Loan();
        loan2.setLoanId(2);
        loan2.setRemainingBalance(2000.00);
        loan2.setStatus("ACTIVE");
        loans.add(loan2);

        mockLoanDAO.setBusinessLoans(loans);

        double total = loanService.getTotalOutstandingBalance(1);
        assertEquals(5000.00, total);
    }

    @Test
    @DisplayName("Get loan should return loan by ID")
    void testGetLoan() {
        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setAmount(10000.00);
        loan.setPurpose("Equipment purchase");
        mockLoanDAO.setFindByIdLoan(loan);

        Loan result = loanService.getLoan(1);
        assertNotNull(result);
        assertEquals(10000.00, result.getAmount());
        assertEquals("Equipment purchase", result.getPurpose());
    }

    @Test
    @DisplayName("Make payment should fail for non-existent loan")
    void testMakePaymentNonExistentLoan() {
        mockLoanDAO.setFindByIdLoan(null);

        boolean result = loanService.makePayment(999, 1, 500.00);
        assertFalse(result);
    }
}

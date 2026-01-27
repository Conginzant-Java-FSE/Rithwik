package com.revpay.service;

import com.revpay.dao.UserDAO;
import com.revpay.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Wallet Service Tests")
public class WalletServiceTest {

    private WalletService walletService;
    private MockUserDAO mockUserDAO;

    // A simple mock DAO to avoid database connections
    static class MockUserDAO extends UserDAO {
        private User findByIdUser;
        private boolean updateBalanceResult = true;

        public void setFindByIdUser(User user) {
            this.findByIdUser = user;
        }

        public void setUpdateBalanceResult(boolean result) {
            this.updateBalanceResult = result;
        }

        @Override
        public User findById(int userId) {
            return findByIdUser;
        }

        @Override
        public boolean updateWalletBalance(int userId, double newBalance) {
            if (findByIdUser != null) {
                findByIdUser.setWalletBalance(newBalance);
            }
            return updateBalanceResult;
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        walletService = new WalletService();
        mockUserDAO = new MockUserDAO();

        // Use reflection to inject the mock DAO
        Field daoField = WalletService.class.getDeclaredField("userDAO");
        daoField.setAccessible(true);
        daoField.set(walletService, mockUserDAO);
    }

    @Test
    @DisplayName("Get Balance should return user's balance")
    void testGetBalance() {
        User user = new User();
        user.setUserId(1);
        user.setWalletBalance(150.00);
        mockUserDAO.setFindByIdUser(user);

        double balance = walletService.getBalance(1);
        assertEquals(150.00, balance);
    }

    @Test
    @DisplayName("Add Money should increase balance")
    void testAddMoney() {
        User user = new User();
        user.setUserId(1);
        user.setWalletBalance(100.00);
        mockUserDAO.setFindByIdUser(user);

        boolean result = walletService.addMoney(1, 50.00);

        assertTrue(result);
        assertEquals(150.00, user.getWalletBalance());
    }

    @Test
    @DisplayName("Add Money should fail for invalid amounts")
    void testAddInvalidMoney() {
        boolean result = walletService.addMoney(1, -50.00);
        assertFalse(result);
    }

    @Test
    @DisplayName("Withdraw Money should decrease balance")
    void testWithdrawMoney() {
        User user = new User();
        user.setUserId(1);
        user.setWalletBalance(100.00);
        mockUserDAO.setFindByIdUser(user);

        boolean result = walletService.withdrawMoney(1, 40.00);

        assertTrue(result);
        assertEquals(60.00, user.getWalletBalance());
    }

    @Test
    @DisplayName("Withdraw Money should fail if funds insufficient")
    void testWithdrawInsufficientFunds() {
        User user = new User();
        user.setUserId(1);
        user.setWalletBalance(20.00);
        mockUserDAO.setFindByIdUser(user);

        boolean result = walletService.withdrawMoney(1, 50.00);

        assertFalse(result);
        assertEquals(20.00, user.getWalletBalance()); // Balance unchanged
    }

    @Test
    @DisplayName("Formatted balance should be correct")
    void testFormattedBalance() {
        User user = new User();
        user.setUserId(1);
        user.setWalletBalance(1234.50);
        mockUserDAO.setFindByIdUser(user);

        String formatted = walletService.getFormattedBalance(1);
        // Depending on locale, this might be 1,234.50 or 1.234,50.
        // We'll just check it contains the numbers.
        assertTrue(formatted.contains("1,234.50") || formatted.contains("1.234,50"));
        assertTrue(formatted.contains("$"));
    }
}

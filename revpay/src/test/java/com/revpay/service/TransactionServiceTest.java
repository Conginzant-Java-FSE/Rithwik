package com.revpay.service;

import com.revpay.dao.TransactionDAO;
import com.revpay.dao.UserDAO;
import com.revpay.model.Transaction;
import com.revpay.model.User;
import com.revpay.security.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionDAO transactionDAO;
    @Mock
    private UserDAO userDAO;
    @Mock
    private WalletService walletService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void testSendMoneySuccess() {
        int senderId = 1;
        int receiverId = 2;
        String pin = "1234";
        double amount = 100.0;

        User sender = new User();
        sender.setUserId(senderId);
        sender.setTransactionPinHash(PasswordUtils.hashPin(pin));

        User receiver = new User();
        receiver.setUserId(receiverId);
        receiver.setEmail("receiver@example.com");

        when(userDAO.findByIdentifier("receiver@example.com")).thenReturn(receiver);
        when(userDAO.findById(senderId)).thenReturn(sender);
        when(walletService.hasSufficientBalance(senderId, amount)).thenReturn(true);
        when(walletService.transfer(senderId, receiverId, amount)).thenReturn(true);
        when(transactionDAO.createTransaction(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaction result = transactionService.sendMoney(senderId, "receiver@example.com", amount, "Note", pin);

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        verify(notificationService).notifyMoneySent(eq(senderId), eq(receiverId), eq(amount), any());
    }

    @Test
    void testSendMoneyInvalidPin() {
        int senderId = 1;
        String pin = "1234";

        User sender = new User();
        sender.setUserId(senderId);
        sender.setTransactionPinHash(PasswordUtils.hashPin(pin));

        when(userDAO.findByIdentifier("receiver@example.com")).thenReturn(new User()); // Valid receiver
        when(walletService.hasSufficientBalance(senderId, 100.0)).thenReturn(true);
        when(userDAO.findById(senderId)).thenReturn(sender);

        // Wrong PIN
        Transaction result = transactionService.sendMoney(senderId, "receiver@example.com", 100.0, "Note", "0000");

        assertNull(result);
        verify(walletService, never()).transfer(anyInt(), anyInt(), anyDouble());
    }
}

package com.revpay.service;

import com.revpay.dao.BusinessAccountDAO;
import com.revpay.dao.UserDAO;
import com.revpay.model.User;
import com.revpay.security.PasswordUtils;
import com.revpay.security.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private BusinessAccountDAO businessAccountDAO;

    // We need to handle SessionManager singleton. Ideally we mock it, but it's a
    // singleton.
    // For unit tests, we might skip it or use reflection to inject a mock if
    // possible,
    // or just let it run (it uses in-memory map).
    // Better strategy: AuthService constructor initializes sessionManager.
    // Since we are using InjectMocks, we'll try to inject a mock sessionManager if
    // we change the service to be testable
    // or accept the real one (which is harmless in memory).
    // However, InjectMocks on fields works.

    @InjectMocks
    private AuthService authService;

    // Helper to inject mock SessionManager since it's instantiated in constructor
    private void injectSessionManager() throws Exception {
        SessionManager mockSession = mock(SessionManager.class);
        Field field = AuthService.class.getDeclaredField("sessionManager");
        field.setAccessible(true);
        field.set(authService, mockSession);
    }

    @BeforeEach
    void setUp() throws Exception {
        injectSessionManager();
    }

    @Test
    void testLoginSuccess() {
        String email = "test@example.com";
        String password = "Password123";
        String hash = PasswordUtils.hashPassword(password);

        User user = new User();
        user.setUserId(1);
        user.setEmail(email);
        user.setPasswordHash(hash);
        user.setFailedLoginAttempts(0);

        when(userDAO.findByIdentifier(email)).thenReturn(user);

        User loggedInUser = authService.login(email, password);

        assertNotNull(loggedInUser);
        assertEquals(email, loggedInUser.getEmail());
        verify(userDAO).updateLastLogin(1);
    }

    @Test
    void testLoginFailureWrongPassword() {
        String email = "test@example.com";
        String password = "Password123";
        String hash = PasswordUtils.hashPassword(password);

        User user = new User();
        user.setUserId(1);
        user.setEmail(email);
        user.setPasswordHash(hash);

        when(userDAO.findByIdentifier(email)).thenReturn(user);

        User loggedInUser = authService.login(email, "WrongPass");

        assertNull(loggedInUser);
        verify(userDAO).updateFailedAttempts(eq(1), anyInt());
    }

    @Test
    void testLoginLockout() {
        String email = "locked@example.com";
        User user = new User();
        user.setUserId(2);
        user.setLockoutUntil(java.time.LocalDateTime.now().plusMinutes(10)); // Locked

        when(userDAO.findByIdentifier(email)).thenReturn(user);

        User loggedInUser = authService.login(email, "AnyPass");

        assertNull(loggedInUser);
        // Verify we didn't check password hash (optimization) or logic returns early
        // The service checks lockout before password verification
    }
}

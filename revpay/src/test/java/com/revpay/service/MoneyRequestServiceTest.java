package com.revpay.service;

import com.revpay.dao.MoneyRequestDAO;
import com.revpay.dao.UserDAO;
import com.revpay.model.MoneyRequest;
import com.revpay.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money Request Service Tests")
public class MoneyRequestServiceTest {

    private MoneyRequestService requestService;
    private MockUserDAO mockUserDAO;
    private MockMoneyRequestDAO mockRequestDAO;

    // Mock UserDAO
    static class MockUserDAO extends UserDAO {
        private User findByIdUser;
        private User findByIdentifierUser;

        public void setFindByIdUser(User user) {
            this.findByIdUser = user;
        }

        public void setFindByIdentifierUser(User user) {
            this.findByIdentifierUser = user;
        }

        @Override
        public User findById(int userId) {
            return findByIdUser;
        }

        @Override
        public User findByIdentifier(String identifier) {
            return findByIdentifierUser;
        }
    }

    // Mock MoneyRequestDAO
    static class MockMoneyRequestDAO extends MoneyRequestDAO {
        private MoneyRequest findByIdRequest;
        private List<MoneyRequest> incomingRequests = new ArrayList<>();
        private List<MoneyRequest> outgoingRequests = new ArrayList<>();
        private boolean updateStatusResult = true;
        private int requestIdCounter = 1;

        public void setFindByIdRequest(MoneyRequest request) {
            this.findByIdRequest = request;
        }

        public void setIncomingRequests(List<MoneyRequest> requests) {
            this.incomingRequests = requests;
        }

        public void setOutgoingRequests(List<MoneyRequest> requests) {
            this.outgoingRequests = requests;
        }

        @Override
        public MoneyRequest findById(int requestId) {
            return findByIdRequest;
        }

        @Override
        public MoneyRequest createRequest(MoneyRequest request) {
            request.setRequestId(requestIdCounter++);
            return request;
        }

        @Override
        public boolean updateStatus(int requestId, String status) {
            if (findByIdRequest != null) {
                findByIdRequest.setStatus(status);
            }
            return updateStatusResult;
        }

        @Override
        public List<MoneyRequest> findIncomingRequests(int userId) {
            return incomingRequests;
        }

        @Override
        public List<MoneyRequest> findOutgoingRequests(int userId) {
            return outgoingRequests;
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        requestService = new MoneyRequestService();
        mockUserDAO = new MockUserDAO();
        mockRequestDAO = new MockMoneyRequestDAO();

        // Inject mock DAOs using reflection
        Field userDaoField = MoneyRequestService.class.getDeclaredField("userDAO");
        userDaoField.setAccessible(true);
        userDaoField.set(requestService, mockUserDAO);

        Field requestDaoField = MoneyRequestService.class.getDeclaredField("requestDAO");
        requestDaoField.setAccessible(true);
        requestDaoField.set(requestService, mockRequestDAO);
    }

    @Test
    @DisplayName("Create request should fail for invalid amount")
    void testCreateRequestInvalidAmount() {
        MoneyRequest result = requestService.createRequest(1, "test@email.com", -50.00, "Test");
        assertNull(result);
    }

    @Test
    @DisplayName("Create request should fail if target user not found")
    void testCreateRequestTargetNotFound() {
        mockUserDAO.setFindByIdentifierUser(null);

        MoneyRequest result = requestService.createRequest(1, "unknown@email.com", 50.00, "Test");
        assertNull(result);
    }

    @Test
    @DisplayName("Create request should fail if requesting from self")
    void testCreateRequestFromSelf() {
        User user = new User();
        user.setUserId(1);
        user.setEmail("test@email.com");
        mockUserDAO.setFindByIdentifierUser(user);

        MoneyRequest result = requestService.createRequest(1, "test@email.com", 50.00, "Test");
        assertNull(result);
    }

    @Test
    @DisplayName("Decline request should fail for non-existent request")
    void testDeclineNonExistentRequest() {
        mockRequestDAO.setFindByIdRequest(null);

        boolean result = requestService.declineRequest(999, 1);
        assertFalse(result);
    }

    @Test
    @DisplayName("Cancel request should fail for non-existent request")
    void testCancelNonExistentRequest() {
        mockRequestDAO.setFindByIdRequest(null);

        boolean result = requestService.cancelRequest(999, 1);
        assertFalse(result);
    }

    @Test
    @DisplayName("Get incoming requests should return list")
    void testGetIncomingRequests() {
        List<MoneyRequest> requests = new ArrayList<>();
        requests.add(new MoneyRequest(2, 1, 50.00, "Test request"));
        mockRequestDAO.setIncomingRequests(requests);

        List<MoneyRequest> result = requestService.getIncomingRequests(1);
        assertEquals(1, result.size());
        assertEquals(50.00, result.get(0).getAmount());
    }

    @Test
    @DisplayName("Get outgoing requests should return list")
    void testGetOutgoingRequests() {
        List<MoneyRequest> requests = new ArrayList<>();
        requests.add(new MoneyRequest(1, 2, 75.00, "Outgoing request"));
        mockRequestDAO.setOutgoingRequests(requests);

        List<MoneyRequest> result = requestService.getOutgoingRequests(1);
        assertEquals(1, result.size());
        assertEquals(75.00, result.get(0).getAmount());
    }
}

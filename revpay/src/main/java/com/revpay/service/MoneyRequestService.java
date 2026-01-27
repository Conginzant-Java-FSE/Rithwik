package com.revpay.service;

import com.revpay.dao.MoneyRequestDAO;
import com.revpay.dao.UserDAO;
import com.revpay.model.MoneyRequest;
import com.revpay.model.User;
import com.revpay.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

// Money request service for requesting and managing money requests
public class MoneyRequestService {

    private static final Logger logger = LogManager.getLogger(MoneyRequestService.class);
    private MoneyRequestDAO requestDAO;
    private UserDAO userDAO;
    private WalletService walletService;
    private NotificationService notificationService;
    private TransactionService transactionService;

    public MoneyRequestService() {
        this.requestDAO = new MoneyRequestDAO();
        this.userDAO = new UserDAO();
        this.walletService = new WalletService();
        this.notificationService = new NotificationService();
        this.transactionService = new TransactionService();
    }

    // Create a money request
    public MoneyRequest createRequest(int requesterId, String targetIdentifier, double amount, String note) {
        try {
            // Validate amount
            if (amount <= 0) {
                logger.warn("Invalid request amount: " + amount);
                return null;
            }
            // Find target user
            User target = userDAO.findByIdentifier(targetIdentifier);
            if (target == null) {
                logger.warn("Target user not found: " + targetIdentifier);
                return null;
            }
            // Cannot request from self
            if (target.getUserId() == requesterId) {
                logger.warn("Cannot request money from self");
                return null;
            }
            // Get requester name
            User requester = userDAO.findById(requesterId);
            // Create request
            MoneyRequest request = new MoneyRequest(requesterId, target.getUserId(), amount, note);
            MoneyRequest createdRequest = requestDAO.createRequest(request);
            // Send notification to target
            notificationService.notifyMoneyRequested(requesterId, target.getUserId(), amount, requester.getFullName());
            logger.info(String.format("Money request created: $%.2f from %d to %d", amount, requesterId,
                    target.getUserId()));
            return createdRequest;
        } catch (Exception e) {
            logger.error("Error creating money request: " + e.getMessage());
            throw e;
        }
    }

    // Accept a money request (pay the requester)
    public boolean acceptRequest(int requestId, int targetId) {
        try {
            MoneyRequest request = requestDAO.findById(requestId);
            if (request == null || request.getTargetId() != targetId) {
                logger.warn("Request not found or unauthorized: " + requestId);
                return false;
            }
            if (!request.isPending()) {
                logger.warn("Request is no longer pending: " + requestId);
                return false;
            }
            // Check balance
            if (!walletService.hasSufficientBalance(targetId, request.getAmount())) {
                logger.warn("Insufficient balance to accept request");
                return false;
            }
            // Get names for notification
            User target = userDAO.findById(targetId);
            User requester = userDAO.findById(request.getRequesterId());
            // Transfer money
            boolean transferred = walletService.transfer(targetId, request.getRequesterId(), request.getAmount());
            if (!transferred) {
                return false;
            }
            // Update request status
            requestDAO.updateStatus(requestId, Constants.REQUEST_ACCEPTED);
            // Send notification
            notificationService.notifyRequestAccepted(request.getRequesterId(), request.getAmount(),
                    target.getFullName());
            logger.info("Request accepted: " + requestId);
            return true;
        } catch (Exception e) {
            logger.error("Error accepting request: " + e.getMessage());
            throw e;
        }
    }

    // Decline a money request
    public boolean declineRequest(int requestId, int targetId) {
        try {
            MoneyRequest request = requestDAO.findById(requestId);
            if (request == null || request.getTargetId() != targetId) {
                logger.warn("Request not found or unauthorized: " + requestId);
                return false;
            }
            if (!request.isPending()) {
                logger.warn("Request is no longer pending: " + requestId);
                return false;
            }
            User target = userDAO.findById(targetId);
            // Update status
            requestDAO.updateStatus(requestId, Constants.REQUEST_DECLINED);
            // Send notification
            notificationService.notifyRequestDeclined(request.getRequesterId(), request.getAmount(),
                    target.getFullName());
            logger.info("Request declined: " + requestId);
            return true;
        } catch (Exception e) {
            logger.error("Error declining request: " + e.getMessage());
            throw e;
        }
    }

    // Cancel a money request (by the requester)
    public boolean cancelRequest(int requestId, int requesterId) {
        try {
            MoneyRequest request = requestDAO.findById(requestId);
            if (request == null || request.getRequesterId() != requesterId) {
                logger.warn("Request not found or unauthorized: " + requestId);
                return false;
            }
            if (!request.isPending()) {
                logger.warn("Request is no longer pending: " + requestId);
                return false;
            }
            // Update status
            requestDAO.updateStatus(requestId, Constants.REQUEST_CANCELLED);
            logger.info("Request cancelled: " + requestId);
            return true;
        } catch (Exception e) {
            logger.error("Error canceling request: " + e.getMessage());
            throw e;
        }
    }

    // Get incoming pending requests for a user
    public List<MoneyRequest> getIncomingRequests(int userId) {
        return requestDAO.findIncomingRequests(userId);
    }

    // Get outgoing pending requests for a user
    public List<MoneyRequest> getOutgoingRequests(int userId) {
        return requestDAO.findOutgoingRequests(userId);
    }

    // Get all requests for a user
    public List<MoneyRequest> getAllRequests(int userId) {
        return requestDAO.findAllByUserId(userId);
    }

    // Get a specific request by ID
    public MoneyRequest getRequest(int requestId) {
        return requestDAO.findById(requestId);
    }
}

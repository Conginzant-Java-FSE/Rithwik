package com.revpay.util;

// Application constants used throughout the RevPay system
public class Constants {

    // Account types
    public static final String ACCOUNT_TYPE_PERSONAL = "PERSONAL";
    public static final String ACCOUNT_TYPE_BUSINESS = "BUSINESS";
    // Transaction types
    public static final String TRANSACTION_SEND = "SEND";
    public static final String TRANSACTION_REQUEST = "REQUEST";
    public static final String TRANSACTION_ADD_MONEY = "ADD_MONEY";
    public static final String TRANSACTION_WITHDRAW = "WITHDRAW";
    public static final String TRANSACTION_INVOICE_PAYMENT = "INVOICE_PAYMENT";
    // Transaction status
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    // Money request status
    public static final String REQUEST_PENDING = "PENDING";
    public static final String REQUEST_ACCEPTED = "ACCEPTED";
    public static final String REQUEST_DECLINED = "DECLINED";
    public static final String REQUEST_CANCELLED = "CANCELLED";
    // Notification types
    public static final String NOTIFICATION_TRANSACTION = "TRANSACTION";
    public static final String NOTIFICATION_REQUEST = "REQUEST";
    public static final String NOTIFICATION_ALERT = "ALERT";
    public static final String NOTIFICATION_INVOICE = "INVOICE";
    public static final String NOTIFICATION_LOAN = "LOAN";
    // Invoice status
    public static final String INVOICE_PENDING = "PENDING";
    public static final String INVOICE_PAID = "PAID";
    public static final String INVOICE_OVERDUE = "OVERDUE";
    public static final String INVOICE_CANCELLED = "CANCELLED";
    // Loan status
    public static final String LOAN_PENDING = "PENDING";
    public static final String LOAN_APPROVED = "APPROVED";
    public static final String LOAN_REJECTED = "REJECTED";
    public static final String LOAN_ACTIVE = "ACTIVE";
    public static final String LOAN_PAID_OFF = "PAID_OFF";
    // Card types
    public static final String CARD_CREDIT = "CREDIT";
    public static final String CARD_DEBIT = "DEBIT";
    // Security settings
    public static final int MAX_FAILED_ATTEMPTS = 3;
    public static final int LOCKOUT_DURATION_MINUTES = 15;
    public static final int SESSION_TIMEOUT_MINUTES = 30;
    public static final int PIN_MIN_LENGTH = 4;
    public static final int PIN_MAX_LENGTH = 6;
    public static final int PASSWORD_MIN_LENGTH = 8;
    // Low balance threshold for alerts
    public static final double LOW_BALANCE_THRESHOLD = 100.00;
    // Currency symbol
    public static final String CURRENCY_SYMBOL = "$";
    // Date format
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private Constants() {
    }
}

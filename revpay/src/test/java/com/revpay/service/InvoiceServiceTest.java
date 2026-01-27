package com.revpay.service;

import com.revpay.dao.InvoiceDAO;
import com.revpay.dao.BusinessAccountDAO;
import com.revpay.dao.UserDAO;
import com.revpay.model.Invoice;
import com.revpay.model.InvoiceItem;
import com.revpay.model.BusinessAccount;
import com.revpay.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Invoice Service Tests")
public class InvoiceServiceTest {

    private InvoiceService invoiceService;
    private MockInvoiceDAO mockInvoiceDAO;
    private MockBusinessAccountDAO mockBusinessDAO;
    private MockUserDAO mockUserDAO;

    // Mock InvoiceDAO
    static class MockInvoiceDAO extends InvoiceDAO {
        private Invoice findByIdInvoice;
        private List<Invoice> businessInvoices = new ArrayList<>();
        private List<Invoice> customerInvoices = new ArrayList<>();
        private int invoiceIdCounter = 1;
        private boolean updateResult = true;

        public void setFindByIdInvoice(Invoice invoice) {
            this.findByIdInvoice = invoice;
        }

        public void setBusinessInvoices(List<Invoice> invoices) {
            this.businessInvoices = invoices;
        }

        public void setCustomerInvoices(List<Invoice> invoices) {
            this.customerInvoices = invoices;
        }

        @Override
        public Invoice findById(int invoiceId) {
            return findByIdInvoice;
        }

        @Override
        public Invoice createInvoice(Invoice invoice) {
            invoice.setInvoiceId(invoiceIdCounter++);
            return invoice;
        }

        @Override
        public List<Invoice> findByBusinessId(int businessId) {
            return businessInvoices;
        }

        @Override
        public List<Invoice> findByCustomerId(int customerId) {
            return customerInvoices;
        }

        @Override
        public boolean updateInvoiceStatus(int invoiceId, String status) {
            if (findByIdInvoice != null) {
                findByIdInvoice.setStatus(status);
            }
            return updateResult;
        }

        @Override
        public boolean addInvoiceItem(InvoiceItem item) {
            // Mock implementation - return true
            return true;
        }
    }

    // Mock BusinessAccountDAO
    static class MockBusinessAccountDAO extends BusinessAccountDAO {
        private BusinessAccount businessAccount;

        public void setBusinessAccount(BusinessAccount account) {
            this.businessAccount = account;
        }

        @Override
        public BusinessAccount findById(int businessId) {
            return businessAccount;
        }
    }

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

    @BeforeEach
    void setUp() throws Exception {
        invoiceService = new InvoiceService();
        mockInvoiceDAO = new MockInvoiceDAO();
        mockBusinessDAO = new MockBusinessAccountDAO();
        mockUserDAO = new MockUserDAO();

        // Inject mock DAOs using reflection
        Field invoiceDaoField = InvoiceService.class.getDeclaredField("invoiceDAO");
        invoiceDaoField.setAccessible(true);
        invoiceDaoField.set(invoiceService, mockInvoiceDAO);

        Field businessDaoField = InvoiceService.class.getDeclaredField("businessAccountDAO");
        businessDaoField.setAccessible(true);
        businessDaoField.set(invoiceService, mockBusinessDAO);

        Field userDaoField = InvoiceService.class.getDeclaredField("userDAO");
        userDaoField.setAccessible(true);
        userDaoField.set(invoiceService, mockUserDAO);
    }

    @Test
    @DisplayName("Create invoice should fail for non-existent customer")
    void testCreateInvoiceCustomerNotFound() {
        mockUserDAO.setFindByIdentifierUser(null);

        List<InvoiceItem> items = new ArrayList<>();
        items.add(new InvoiceItem("Test Item", 1, 100.00));

        Invoice result = invoiceService.createInvoice(1, "unknown@email.com", LocalDate.now().plusDays(30), items);
        assertNull(result);
    }

    @Test
    @DisplayName("Create invoice should fail for non-existent business")
    void testCreateInvoiceBusinessNotFound() {
        User customer = new User();
        customer.setUserId(2);
        customer.setEmail("customer@email.com");
        mockUserDAO.setFindByIdentifierUser(customer);
        mockBusinessDAO.setBusinessAccount(null);

        List<InvoiceItem> items = new ArrayList<>();
        items.add(new InvoiceItem("Test Item", 1, 100.00));

        Invoice result = invoiceService.createInvoice(999, "customer@email.com", LocalDate.now().plusDays(30), items);
        assertNull(result);
    }

    @Test
    @DisplayName("Get business invoices should return list")
    void testGetBusinessInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        Invoice inv = new Invoice();
        inv.setInvoiceId(1);
        inv.setTotalAmount(500.00);
        inv.setStatus("PENDING");
        invoices.add(inv);
        mockInvoiceDAO.setBusinessInvoices(invoices);

        List<Invoice> result = invoiceService.getBusinessInvoices(1);
        assertEquals(1, result.size());
        assertEquals(500.00, result.get(0).getTotalAmount());
    }

    @Test
    @DisplayName("Get customer invoices should return list")
    void testGetCustomerInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        Invoice inv = new Invoice();
        inv.setInvoiceId(1);
        inv.setTotalAmount(250.00);
        inv.setStatus("PENDING");
        invoices.add(inv);
        mockInvoiceDAO.setCustomerInvoices(invoices);

        List<Invoice> result = invoiceService.getCustomerInvoices(1);
        assertEquals(1, result.size());
        assertEquals(250.00, result.get(0).getTotalAmount());
    }

    @Test
    @DisplayName("Get invoice should return invoice by ID")
    void testGetInvoice() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(1);
        invoice.setInvoiceNumber("INV-1-123456");
        invoice.setTotalAmount(1000.00);
        mockInvoiceDAO.setFindByIdInvoice(invoice);

        Invoice result = invoiceService.getInvoice(1);
        assertNotNull(result);
        assertEquals("INV-1-123456", result.getInvoiceNumber());
        assertEquals(1000.00, result.getTotalAmount());
    }

    @Test
    @DisplayName("Cancel invoice should fail for non-existent invoice")
    void testCancelNonExistentInvoice() {
        mockInvoiceDAO.setFindByIdInvoice(null);

        boolean result = invoiceService.cancelInvoice(999, 1);
        assertFalse(result);
    }

    @Test
    @DisplayName("Cancel invoice should fail for paid invoice")
    void testCancelPaidInvoice() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(1);
        invoice.setBusinessId(1);
        invoice.setStatus("PAID");
        mockInvoiceDAO.setFindByIdInvoice(invoice);

        boolean result = invoiceService.cancelInvoice(1, 1);
        assertFalse(result);
    }

    @Test
    @DisplayName("Pay invoice should fail for non-existent invoice")
    void testPayNonExistentInvoice() {
        mockInvoiceDAO.setFindByIdInvoice(null);

        boolean result = invoiceService.payInvoice(999, 1);
        assertFalse(result);
    }
}

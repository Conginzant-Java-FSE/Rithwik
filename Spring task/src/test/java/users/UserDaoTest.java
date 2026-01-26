package users;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoTest {
    private static UserDao dao;

    @BeforeAll
    static void setup() {
        dao = new UserDaoImpl();
        System.out.println("Before all tests");
    }

    @Test
    @Order(1)
    void testRegister() {
        User user = new User();
        user.setUsername("testuser1");
        user.setEmail("test1@gmail.com");
        user.setPassword("123");
        boolean result = dao.register(user);
        assertTrue(result);
    }

    @Test
    @Order(2)
    void testLogin() {
        User user = dao.login("testuser1", "123");
        assertNotNull(user);
        assertEquals("testuser1", user.getUsername());
    }

    @Test
    @Order(3)
    void testViewProfile() {
        User user = dao.login("testuser1", "123");
        User profile = dao.viewProfile(user.getId());
        assertNotNull(profile);
        assertEquals("test1@gmail.com", profile.getEmail());
    }

    @Test
    @Order(4)
    void testUpdateProfile() {
        User user = dao.login("testuser1", "123");
        boolean updated = dao.updateProfile(user.getId(), "newmail@gmail.com");
        assertTrue(updated);
    }

}

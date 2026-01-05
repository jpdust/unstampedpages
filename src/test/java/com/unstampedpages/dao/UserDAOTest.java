package com.unstampedpages.dao;

import com.unstampedpages.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserDAOTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserDAO userDAO;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(null, "John", "Doe", 30, "john@example.com");
    }

    @Test
    void save_shouldPersistUserAndGenerateId() {
        User savedUser = userDAO.save(testUser);

        assertNotNull(savedUser.getUserId());
        assertEquals("John", savedUser.getFirstName());
        assertEquals("Doe", savedUser.getLastName());
        assertEquals(30, savedUser.getAge());
        assertEquals("john@example.com", savedUser.getEmail());
    }

    @Test
    void save_shouldUpdateExistingUser() {
        entityManager.persist(testUser);
        entityManager.flush();
        Long userId = testUser.getUserId();

        testUser.setFirstName("Jane");
        testUser.setLastName("Smith");
        testUser.setAge(25);
        testUser.setEmail("jane@example.com");
        userDAO.saveAndFlush(testUser);

        entityManager.clear();
        User updatedUser = entityManager.find(User.class, userId);

        assertEquals("Jane", updatedUser.getFirstName());
        assertEquals("Smith", updatedUser.getLastName());
        assertEquals(25, updatedUser.getAge());
        assertEquals("jane@example.com", updatedUser.getEmail());
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        entityManager.persist(testUser);
        entityManager.flush();

        Optional<User> foundUser = userDAO.findById(testUser.getUserId());

        assertTrue(foundUser.isPresent());
        assertEquals("John", foundUser.get().getFirstName());
        assertEquals("Doe", foundUser.get().getLastName());
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        Optional<User> foundUser = userDAO.findById(999L);

        assertTrue(foundUser.isEmpty());
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoUsers() {
        List<User> users = userDAO.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        User user1 = new User(null, "John", "Doe", 30, "john@example.com");
        User user2 = new User(null, "Jane", "Smith", 25, "jane@example.com");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        List<User> users = userDAO.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void existsById_shouldReturnTrueWhenUserExists() {
        entityManager.persist(testUser);
        entityManager.flush();

        boolean exists = userDAO.existsById(testUser.getUserId());

        assertTrue(exists);
    }

    @Test
    void existsById_shouldReturnFalseWhenUserNotExists() {
        boolean exists = userDAO.existsById(999L);

        assertFalse(exists);
    }

    @Test
    void deleteById_shouldRemoveUser() {
        entityManager.persist(testUser);
        entityManager.flush();
        Long userId = testUser.getUserId();

        userDAO.deleteById(userId);
        entityManager.flush();
        entityManager.clear();

        User deletedUser = entityManager.find(User.class, userId);
        assertNull(deletedUser);
    }

    @Test
    void delete_shouldRemoveUser() {
        entityManager.persist(testUser);
        entityManager.flush();
        Long userId = testUser.getUserId();

        userDAO.delete(testUser);
        entityManager.flush();
        entityManager.clear();

        User deletedUser = entityManager.find(User.class, userId);
        assertNull(deletedUser);
    }

    @Test
    void count_shouldReturnZeroWhenNoUsers() {
        long count = userDAO.count();

        assertEquals(0, count);
    }

    @Test
    void count_shouldReturnCorrectCount() {
        entityManager.persist(new User(null, "John", "Doe", 30, "john@example.com"));
        entityManager.persist(new User(null, "Jane", "Smith", 25, "jane@example.com"));
        entityManager.persist(new User(null, "Bob", "Wilson", 35, "bob@example.com"));
        entityManager.flush();

        long count = userDAO.count();

        assertEquals(3, count);
    }

    @Test
    void save_shouldEnforceUniqueEmailConstraint() {
        entityManager.persist(testUser);
        entityManager.flush();

        User duplicateEmailUser = new User(null, "Jane", "Smith", 25, "john@example.com");

        assertThrows(Exception.class, () -> {
            userDAO.saveAndFlush(duplicateEmailUser);
        });
    }

    @Test
    void findAll_shouldReturnUsersWithCorrectData() {
        User user1 = new User(null, "John", "Doe", 30, "john@example.com");
        User user2 = new User(null, "Jane", "Smith", 25, "jane@example.com");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        List<User> users = userDAO.findAll();

        User john = users.stream().filter(u -> u.getEmail().equals("john@example.com")).findFirst().orElse(null);
        User jane = users.stream().filter(u -> u.getEmail().equals("jane@example.com")).findFirst().orElse(null);

        assertNotNull(john);
        assertEquals("John", john.getFirstName());
        assertEquals("Doe", john.getLastName());
        assertEquals(30, john.getAge());

        assertNotNull(jane);
        assertEquals("Jane", jane.getFirstName());
        assertEquals("Smith", jane.getLastName());
        assertEquals(25, jane.getAge());
    }
}

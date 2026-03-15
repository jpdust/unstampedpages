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
    private User savedUser;
    private Optional<User> foundUser;
    private List<User> users;
    private boolean exists;
    private long count;
    private Long userId;

    @BeforeEach
    void setUp() {
        testUser = new User(null, "John", "Doe", 30, "john@example.com");
    }

    @Test
    void save_shouldPersistUserAndGenerateId() {
        givenNewUser();
        whenSavingUser();
        thenUserIsPersistedWithGeneratedId();
    }

    @Test
    void save_shouldUpdateExistingUser() {
        givenPersistedUser();
        whenUpdatingUserDetails();
        thenUserDetailsAreUpdated();
    }

    @Test
    void save_shouldEnforceUniqueEmailConstraint() {
        givenPersistedUser();
        whenSavingUserWithDuplicateEmail();
        thenExceptionIsThrown();
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        givenPersistedUser();
        whenFindingUserById();
        thenUserIsFound();
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        givenNoUsers();
        whenFindingNonExistentUser();
        thenEmptyOptionalIsReturned();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoUsers() {
        givenNoUsers();
        whenFindingAllUsers();
        thenEmptyListIsReturned();
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        givenMultiplePersistedUsers();
        whenFindingAllUsers();
        thenAllUsersAreReturned();
    }

    @Test
    void findAll_shouldReturnUsersWithCorrectData() {
        givenMultiplePersistedUsers();
        whenFindingAllUsers();
        thenUsersHaveCorrectData();
    }

    @Test
    void existsById_shouldReturnTrueWhenUserExists() {
        givenPersistedUser();
        whenCheckingIfUserExists();
        thenUserExists();
    }

    @Test
    void existsById_shouldReturnFalseWhenUserNotExists() {
        givenNoUsers();
        whenCheckingIfNonExistentUserExists();
        thenUserDoesNotExist();
    }

    @Test
    void deleteById_shouldRemoveUser() {
        givenPersistedUser();
        whenDeletingUserById();
        thenUserIsRemoved();
    }

    @Test
    void delete_shouldRemoveUser() {
        givenPersistedUser();
        whenDeletingUser();
        thenUserIsRemoved();
    }

    @Test
    void count_shouldReturnZeroWhenNoUsers() {
        givenNoUsers();
        whenCountingUsers();
        thenCountIsZero();
    }

    @Test
    void count_shouldReturnCorrectCount() {
        givenThreePersistedUsers();
        whenCountingUsers();
        thenCountIsThree();
    }

    private void givenNewUser() {
        // testUser is already set up in @BeforeEach
    }

    private void givenPersistedUser() {
        entityManager.persist(testUser);
        entityManager.flush();
        userId = testUser.getUserId();
    }

    private void givenNoUsers() {
        // No setup needed - database is empty
    }

    private void givenMultiplePersistedUsers() {
        User user1 = new User(null, "John", "Doe", 30, "john@example.com");
        User user2 = new User(null, "Jane", "Smith", 25, "jane@example.com");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();
    }

    private void givenThreePersistedUsers() {
        entityManager.persist(new User(null, "John", "Doe", 30, "john@example.com"));
        entityManager.persist(new User(null, "Jane", "Smith", 25, "jane@example.com"));
        entityManager.persist(new User(null, "Bob", "Wilson", 35, "bob@example.com"));
        entityManager.flush();
    }

    private void whenSavingUser() {
        savedUser = userDAO.save(testUser);
    }

    private void whenUpdatingUserDetails() {
        testUser.setFirstName("Jane");
        testUser.setLastName("Smith");
        testUser.setAge(25);
        testUser.setEmail("jane@example.com");
        userDAO.saveAndFlush(testUser);
        entityManager.clear();
    }

    private void whenSavingUserWithDuplicateEmail() {
        // Action happens in thenExceptionIsThrown
    }

    private void whenFindingUserById() {
        foundUser = userDAO.findById(testUser.getUserId());
    }

    private void whenFindingNonExistentUser() {
        foundUser = userDAO.findById(999L);
    }

    private void whenFindingAllUsers() {
        users = userDAO.findAll();
    }

    private void whenCheckingIfUserExists() {
        exists = userDAO.existsById(testUser.getUserId());
    }

    private void whenCheckingIfNonExistentUserExists() {
        exists = userDAO.existsById(999L);
    }

    private void whenDeletingUserById() {
        userDAO.deleteById(userId);
        entityManager.flush();
        entityManager.clear();
    }

    private void whenDeletingUser() {
        userDAO.delete(testUser);
        entityManager.flush();
        entityManager.clear();
    }

    private void whenCountingUsers() {
        count = userDAO.count();
    }

    private void thenUserIsPersistedWithGeneratedId() {
        assertNotNull(savedUser.getUserId());
        assertEquals("John", savedUser.getFirstName());
        assertEquals("Doe", savedUser.getLastName());
        assertEquals(30, savedUser.getAge());
        assertEquals("john@example.com", savedUser.getEmail());
    }

    private void thenUserDetailsAreUpdated() {
        User updatedUser = entityManager.find(User.class, userId);
        assertEquals("Jane", updatedUser.getFirstName());
        assertEquals("Smith", updatedUser.getLastName());
        assertEquals(25, updatedUser.getAge());
        assertEquals("jane@example.com", updatedUser.getEmail());
    }

    private void thenExceptionIsThrown() {
        User duplicateEmailUser = new User(null, "Jane", "Smith", 25, "john@example.com");
        assertThrows(Exception.class, () -> {
            userDAO.saveAndFlush(duplicateEmailUser);
        });
    }

    private void thenUserIsFound() {
        assertTrue(foundUser.isPresent());
        assertEquals("John", foundUser.get().getFirstName());
        assertEquals("Doe", foundUser.get().getLastName());
    }

    private void thenEmptyOptionalIsReturned() {
        assertTrue(foundUser.isEmpty());
    }

    private void thenEmptyListIsReturned() {
        assertTrue(users.isEmpty());
    }

    private void thenAllUsersAreReturned() {
        assertEquals(2, users.size());
    }

    private void thenUsersHaveCorrectData() {
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

    private void thenUserExists() {
        assertTrue(exists);
    }

    private void thenUserDoesNotExist() {
        assertFalse(exists);
    }

    private void thenUserIsRemoved() {
        User deletedUser = entityManager.find(User.class, userId);
        assertNull(deletedUser);
    }

    private void thenCountIsZero() {
        assertEquals(0, count);
    }

    private void thenCountIsThree() {
        assertEquals(3, count);
    }
}

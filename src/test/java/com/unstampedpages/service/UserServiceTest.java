package com.unstampedpages.service;

import com.unstampedpages.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void createUser_shouldCreateUserWithCorrectFields() {
        User user = userService.createUser("John", "Doe", 30, "john@example.com");

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(30, user.getAge());
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    void createUser_shouldAutoIncrementId() {
        User user1 = userService.createUser("John", "Doe", 30, "john@example.com");
        User user2 = userService.createUser("Jane", "Smith", 25, "jane@example.com");

        assertEquals(1L, user1.getId());
        assertEquals(2L, user2.getId());
    }

    @Test
    void getUser_shouldReturnUserWhenExists() {
        userService.createUser("John", "Doe", 30, "john@example.com");

        Optional<User> result = userService.getUser(1L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void getUser_shouldReturnEmptyWhenNotExists() {
        Optional<User> result = userService.getUser(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenNoUsers() {
        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        userService.createUser("John", "Doe", 30, "john@example.com");
        userService.createUser("Jane", "Smith", 25, "jane@example.com");

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        userService.createUser("John", "Doe", 30, "john@example.com");

        Optional<User> result = userService.updateUser(1L, "Jane", "Smith", 25, "jane@example.com");

        assertTrue(result.isPresent());
        User updated = result.get();
        assertEquals("Jane", updated.getFirstName());
        assertEquals("Smith", updated.getLastName());
        assertEquals(25, updated.getAge());
        assertEquals("jane@example.com", updated.getEmail());
    }

    @Test
    void updateUser_shouldReturnEmptyWhenUserNotExists() {
        Optional<User> result = userService.updateUser(999L, "Jane", "Smith", 25, "jane@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteUser_shouldReturnTrueWhenUserExists() {
        userService.createUser("John", "Doe", 30, "john@example.com");

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        assertTrue(userService.getUser(1L).isEmpty());
    }

    @Test
    void deleteUser_shouldReturnFalseWhenUserNotExists() {
        boolean result = userService.deleteUser(999L);

        assertFalse(result);
    }
}

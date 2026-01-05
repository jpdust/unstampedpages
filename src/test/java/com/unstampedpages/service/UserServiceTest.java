package com.unstampedpages.service;

import com.unstampedpages.dao.UserDAO;
import com.unstampedpages.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userDAO);
    }

    @Test
    void createUser_shouldCreateUserWithCorrectFields() {
        User savedUser = new User(1L, "John", "Doe", 30, "john@example.com");
        when(userDAO.save(any(User.class))).thenReturn(savedUser);

        User user = userService.createUser("John", "Doe", 30, "john@example.com");

        assertNotNull(user);
        assertEquals(1L, user.getUserId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(30, user.getAge());
        assertEquals("john@example.com", user.getEmail());
        verify(userDAO).save(any(User.class));
    }

    @Test
    void createUser_shouldPassCorrectUserToDAO() {
        User savedUser = new User(1L, "John", "Doe", 30, "john@example.com");
        when(userDAO.save(userCaptor.capture())).thenReturn(savedUser);

        userService.createUser("John", "Doe", 30, "john@example.com");

        User capturedUser = userCaptor.getValue();
        assertNull(capturedUser.getUserId());
        assertEquals("John", capturedUser.getFirstName());
        assertEquals("Doe", capturedUser.getLastName());
        assertEquals(30, capturedUser.getAge());
        assertEquals("john@example.com", capturedUser.getEmail());
    }

    @Test
    void createUser_shouldInvokeDAOSaveExactlyOnce() {
        User savedUser = new User(1L, "John", "Doe", 30, "john@example.com");
        when(userDAO.save(any(User.class))).thenReturn(savedUser);

        userService.createUser("John", "Doe", 30, "john@example.com");

        verify(userDAO, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userDAO);
    }

    @Test
    void getUser_shouldReturnUserWhenExists() {
        User user = new User(1L, "John", "Doe", 30, "john@example.com");
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUser(1L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        verify(userDAO).findById(1L);
    }

    @Test
    void getUser_shouldReturnEmptyWhenNotExists() {
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUser(999L);

        assertTrue(result.isEmpty());
        verify(userDAO).findById(999L);
    }

    @Test
    void getUser_shouldInvokeDAOFindByIdWithCorrectId() {
        when(userDAO.findById(42L)).thenReturn(Optional.empty());

        userService.getUser(42L);

        verify(userDAO, times(1)).findById(42L);
        verifyNoMoreInteractions(userDAO);
    }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenNoUsers() {
        when(userDAO.findAll()).thenReturn(Collections.emptyList());

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
        verify(userDAO).findAll();
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        User user1 = new User(1L, "John", "Doe", 30, "john@example.com");
        User user2 = new User(2L, "Jane", "Smith", 25, "jane@example.com");
        when(userDAO.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userDAO).findAll();
    }

    @Test
    void getAllUsers_shouldInvokeDAOFindAllExactlyOnce() {
        when(userDAO.findAll()).thenReturn(Collections.emptyList());

        userService.getAllUsers();

        verify(userDAO, times(1)).findAll();
        verifyNoMoreInteractions(userDAO);
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        User existingUser = new User(1L, "John", "Doe", 30, "john@example.com");
        User updatedUser = new User(1L, "Jane", "Smith", 25, "jane@example.com");
        when(userDAO.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userDAO.save(any(User.class))).thenReturn(updatedUser);

        Optional<User> result = userService.updateUser(1L, "Jane", "Smith", 25, "jane@example.com");

        assertTrue(result.isPresent());
        assertEquals("Jane", result.get().getFirstName());
        assertEquals("Smith", result.get().getLastName());
        assertEquals(25, result.get().getAge());
        assertEquals("jane@example.com", result.get().getEmail());
        verify(userDAO).findById(1L);
        verify(userDAO).save(any(User.class));
    }

    @Test
    void updateUser_shouldPassUpdatedUserToDAO() {
        User existingUser = new User(1L, "John", "Doe", 30, "john@example.com");
        when(userDAO.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userDAO.save(userCaptor.capture())).thenReturn(existingUser);

        userService.updateUser(1L, "Jane", "Smith", 25, "jane@example.com");

        User capturedUser = userCaptor.getValue();
        assertEquals(1L, capturedUser.getUserId());
        assertEquals("Jane", capturedUser.getFirstName());
        assertEquals("Smith", capturedUser.getLastName());
        assertEquals(25, capturedUser.getAge());
        assertEquals("jane@example.com", capturedUser.getEmail());
    }

    @Test
    void updateUser_shouldReturnEmptyWhenUserNotExists() {
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.updateUser(999L, "Jane", "Smith", 25, "jane@example.com");

        assertTrue(result.isEmpty());
        verify(userDAO).findById(999L);
        verify(userDAO, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldInvokeDAOFindByIdThenSave() {
        User existingUser = new User(1L, "John", "Doe", 30, "john@example.com");
        when(userDAO.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userDAO.save(any(User.class))).thenReturn(existingUser);

        userService.updateUser(1L, "Jane", "Smith", 25, "jane@example.com");

        var inOrder = inOrder(userDAO);
        inOrder.verify(userDAO).findById(1L);
        inOrder.verify(userDAO).save(any(User.class));
        verifyNoMoreInteractions(userDAO);
    }

    @Test
    void deleteUser_shouldReturnTrueWhenUserExists() {
        when(userDAO.existsById(1L)).thenReturn(true);
        doNothing().when(userDAO).deleteById(1L);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userDAO).existsById(1L);
        verify(userDAO).deleteById(1L);
    }

    @Test
    void deleteUser_shouldReturnFalseWhenUserNotExists() {
        when(userDAO.existsById(999L)).thenReturn(false);

        boolean result = userService.deleteUser(999L);

        assertFalse(result);
        verify(userDAO).existsById(999L);
        verify(userDAO, never()).deleteById(anyLong());
    }

    @Test
    void deleteUser_shouldInvokeDAOExistsByIdThenDeleteById() {
        when(userDAO.existsById(1L)).thenReturn(true);
        doNothing().when(userDAO).deleteById(1L);

        userService.deleteUser(1L);

        var inOrder = inOrder(userDAO);
        inOrder.verify(userDAO).existsById(1L);
        inOrder.verify(userDAO).deleteById(1L);
        verifyNoMoreInteractions(userDAO);
    }

    @Test
    void deleteUser_shouldInvokeDAOWithCorrectId() {
        when(userDAO.existsById(42L)).thenReturn(true);
        doNothing().when(userDAO).deleteById(42L);

        userService.deleteUser(42L);

        verify(userDAO).existsById(42L);
        verify(userDAO).deleteById(42L);
    }
}

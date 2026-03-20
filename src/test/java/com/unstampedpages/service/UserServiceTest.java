package com.unstampedpages.service;

import com.unstampedpages.dao.UserDAO;
import com.unstampedpages.dto.UserDTO;
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
    private UserDTO resultUserDTO;
    private Optional<UserDTO> optionalResult;
    private List<UserDTO> userDTOList;
    private boolean booleanResult;

    @BeforeEach
    void setUp() {
        userService = new UserService(userDAO);
    }

    @Test
    void createUser_shouldCreateUserWithCorrectFields() {
        givenDAOWillSaveUser();
        whenCreatingUser();
        thenUserIsCreatedWithCorrectFields();
    }

    @Test
    void createUser_shouldPassCorrectUserToDAO() {
        givenDAOWillCaptureAndSaveUser();
        whenCreatingUser();
        thenCorrectUserWasPassedToDAO();
    }

    @Test
    void createUser_shouldInvokeDAOSaveExactlyOnce() {
        givenDAOWillSaveUser();
        whenCreatingUser();
        thenDAOSaveWasInvokedExactlyOnce();
    }

    @Test
    void getUser_shouldReturnUserWhenExists() {
        givenUserExists();
        whenGettingUser();
        thenUserIsReturned();
    }

    @Test
    void getUser_shouldReturnEmptyWhenNotExists() {
        givenUserDoesNotExist();
        whenGettingNonExistentUser();
        thenEmptyOptionalIsReturned();
    }

    @Test
    void getUser_shouldInvokeDAOFindByIdWithCorrectId() {
        givenUserDoesNotExistWithId(42L);
        whenGettingUserById(42L);
        thenDAOFindByIdWasInvokedOnlyOnce(42L);
    }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenNoUsers() {
        givenNoUsersExist();
        whenGettingAllUsers();
        thenEmptyListIsReturned();
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        givenMultipleUsersExist();
        whenGettingAllUsers();
        thenAllUsersAreReturned();
    }

    @Test
    void getAllUsers_shouldInvokeDAOFindAllExactlyOnce() {
        givenNoUsersExist();
        whenGettingAllUsers();
        thenDAOFindAllWasInvokedExactlyOnce();
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        givenExistingUserForUpdate();
        whenUpdatingUser();
        thenUserIsUpdated();
    }

    @Test
    void updateUser_shouldPassUpdatedUserToDAO() {
        givenExistingUserForUpdateCapture();
        whenUpdatingUser();
        thenUpdatedUserWasPassedToDAO();
    }

    @Test
    void updateUser_shouldReturnEmptyWhenUserNotExists() {
        givenUserDoesNotExistForUpdate();
        whenUpdatingNonExistentUser();
        thenEmptyOptionalIsReturnedAndSaveNotCalled();
    }

    @Test
    void updateUser_shouldInvokeDAOFindByIdThenSave() {
        givenExistingUserForUpdateVerification();
        whenUpdatingUser();
        thenDAOFindByIdAndSaveWereCalledInOrder();
    }

    @Test
    void deleteUser_shouldReturnTrueWhenUserExists() {
        givenUserExistsForDeletion();
        whenDeletingUser();
        thenTrueIsReturnedAndUserDeleted();
    }

    @Test
    void deleteUser_shouldReturnFalseWhenUserNotExists() {
        givenUserDoesNotExistForDeletion();
        whenDeletingNonExistentUser();
        thenFalseIsReturnedAndDeleteNotCalled();
    }

    @Test
    void deleteUser_shouldInvokeDAOExistsByIdThenDeleteById() {
        givenUserExistsForDeletionVerification();
        whenDeletingUser();
        thenDAOExistsByIdAndDeleteByIdWereCalledInOrder();
    }

    @Test
    void deleteUser_shouldInvokeDAOWithCorrectId() {
        givenUserExistsForDeletionWithId(42L);
        whenDeletingUserById(42L);
        thenDAOWasInvokedWithCorrectId(42L);
    }

    private void givenDAOWillSaveUser() {
        User savedUser = new User(1L, "John", "Doe", 30, "john@example.com");
        when(userDAO.save(any(User.class))).thenReturn(savedUser);
    }

    private void givenDAOWillCaptureAndSaveUser() {
        User savedUser = new User(1L, "John", "Doe", 30, "john@example.com");
        when(userDAO.save(userCaptor.capture())).thenReturn(savedUser);
    }

    private void givenUserExists() {
        User user = new User(1L, "John", "Doe", 30, "john@example.com");
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));
    }

    private void givenUserDoesNotExist() {
        when(userDAO.findById(999L)).thenReturn(Optional.empty());
    }

    private void givenUserDoesNotExistWithId(Long id) {
        when(userDAO.findById(id)).thenReturn(Optional.empty());
    }

    private void givenNoUsersExist() {
        when(userDAO.findAll()).thenReturn(Collections.emptyList());
    }

    private void givenMultipleUsersExist() {
        User user1 = new User(1L, "John", "Doe", 30, "john@example.com");
        User user2 = new User(2L, "Jane", "Smith", 25, "jane@example.com");
        when(userDAO.findAll()).thenReturn(Arrays.asList(user1, user2));
    }

    private void givenExistingUserForUpdate() {
        User existingUser = new User(1L, "John", "Doe", 30, "john@example.com");
        User updatedUser = new User(1L, "Jane", "Smith", 25, "jane@example.com");
        when(userDAO.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userDAO.save(any(User.class))).thenReturn(updatedUser);
    }

    private void givenExistingUserForUpdateCapture() {
        User existingUser = new User(1L, "John", "Doe", 30, "john@example.com");
        when(userDAO.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userDAO.save(userCaptor.capture())).thenReturn(existingUser);
    }

    private void givenUserDoesNotExistForUpdate() {
        when(userDAO.findById(999L)).thenReturn(Optional.empty());
    }

    private void givenExistingUserForUpdateVerification() {
        User existingUser = new User(1L, "John", "Doe", 30, "john@example.com");
        when(userDAO.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userDAO.save(any(User.class))).thenReturn(existingUser);
    }

    private void givenUserExistsForDeletion() {
        when(userDAO.existsById(1L)).thenReturn(true);
        doNothing().when(userDAO).deleteById(1L);
    }

    private void givenUserDoesNotExistForDeletion() {
        when(userDAO.existsById(999L)).thenReturn(false);
    }

    private void givenUserExistsForDeletionVerification() {
        when(userDAO.existsById(1L)).thenReturn(true);
        doNothing().when(userDAO).deleteById(1L);
    }

    private void givenUserExistsForDeletionWithId(Long id) {
        when(userDAO.existsById(id)).thenReturn(true);
        doNothing().when(userDAO).deleteById(id);
    }

    private void whenCreatingUser() {
        resultUserDTO = userService.createUser("John", "Doe", 30, "john@example.com");
    }

    private void whenGettingUser() {
        optionalResult = userService.getUser(1L);
    }

    private void whenGettingNonExistentUser() {
        optionalResult = userService.getUser(999L);
    }

    private void whenGettingUserById(Long id) {
        userService.getUser(id);
    }

    private void whenGettingAllUsers() {
        userDTOList = userService.getAllUsers();
    }

    private void whenUpdatingUser() {
        optionalResult = userService.updateUser(1L, "Jane", "Smith", 25, "jane@example.com");
    }

    private void whenUpdatingNonExistentUser() {
        optionalResult = userService.updateUser(999L, "Jane", "Smith", 25, "jane@example.com");
    }

    private void whenDeletingUser() {
        booleanResult = userService.deleteUser(1L);
    }

    private void whenDeletingNonExistentUser() {
        booleanResult = userService.deleteUser(999L);
    }

    private void whenDeletingUserById(Long id) {
        userService.deleteUser(id);
    }

    private void thenUserIsCreatedWithCorrectFields() {
        assertNotNull(resultUserDTO);
        assertEquals(1L, resultUserDTO.getUserId());
        assertEquals("John", resultUserDTO.getFirstName());
        assertEquals("Doe", resultUserDTO.getLastName());
        assertEquals(30, resultUserDTO.getAge());
        assertEquals("john@example.com", resultUserDTO.getEmail());
        verify(userDAO).save(any(User.class));
    }

    private void thenCorrectUserWasPassedToDAO() {
        User capturedUser = userCaptor.getValue();
        assertNull(capturedUser.getUserId());
        assertEquals("John", capturedUser.getFirstName());
        assertEquals("Doe", capturedUser.getLastName());
        assertEquals(30, capturedUser.getAge());
        assertEquals("john@example.com", capturedUser.getEmail());
    }

    private void thenDAOSaveWasInvokedExactlyOnce() {
        verify(userDAO, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userDAO);
    }

    private void thenUserIsReturned() {
        assertTrue(optionalResult.isPresent());
        assertEquals("John", optionalResult.get().getFirstName());
        verify(userDAO).findById(1L);
    }

    private void thenEmptyOptionalIsReturned() {
        assertTrue(optionalResult.isEmpty());
        verify(userDAO).findById(999L);
    }

    private void thenDAOFindByIdWasInvokedOnlyOnce(Long id) {
        verify(userDAO, times(1)).findById(id);
        verifyNoMoreInteractions(userDAO);
    }

    private void thenEmptyListIsReturned() {
        assertNotNull(userDTOList);
        assertTrue(userDTOList.isEmpty());
        verify(userDAO).findAll();
    }

    private void thenAllUsersAreReturned() {
        assertEquals(2, userDTOList.size());
        verify(userDAO).findAll();
    }

    private void thenDAOFindAllWasInvokedExactlyOnce() {
        verify(userDAO, times(1)).findAll();
        verifyNoMoreInteractions(userDAO);
    }

    private void thenUserIsUpdated() {
        assertTrue(optionalResult.isPresent());
        assertEquals("Jane", optionalResult.get().getFirstName());
        assertEquals("Smith", optionalResult.get().getLastName());
        assertEquals(25, optionalResult.get().getAge());
        assertEquals("jane@example.com", optionalResult.get().getEmail());
        verify(userDAO).findById(1L);
        verify(userDAO).save(any(User.class));
    }

    private void thenUpdatedUserWasPassedToDAO() {
        User capturedUser = userCaptor.getValue();
        assertEquals(1L, capturedUser.getUserId());
        assertEquals("Jane", capturedUser.getFirstName());
        assertEquals("Smith", capturedUser.getLastName());
        assertEquals(25, capturedUser.getAge());
        assertEquals("jane@example.com", capturedUser.getEmail());
    }

    private void thenEmptyOptionalIsReturnedAndSaveNotCalled() {
        assertTrue(optionalResult.isEmpty());
        verify(userDAO).findById(999L);
        verify(userDAO, never()).save(any(User.class));
    }

    private void thenDAOFindByIdAndSaveWereCalledInOrder() {
        var inOrder = inOrder(userDAO);
        inOrder.verify(userDAO).findById(1L);
        inOrder.verify(userDAO).save(any(User.class));
        verifyNoMoreInteractions(userDAO);
    }

    private void thenTrueIsReturnedAndUserDeleted() {
        assertTrue(booleanResult);
        verify(userDAO).existsById(1L);
        verify(userDAO).deleteById(1L);
    }

    private void thenFalseIsReturnedAndDeleteNotCalled() {
        assertFalse(booleanResult);
        verify(userDAO).existsById(999L);
        verify(userDAO, never()).deleteById(anyLong());
    }

    private void thenDAOExistsByIdAndDeleteByIdWereCalledInOrder() {
        var inOrder = inOrder(userDAO);
        inOrder.verify(userDAO).existsById(1L);
        inOrder.verify(userDAO).deleteById(1L);
        verifyNoMoreInteractions(userDAO);
    }

    private void thenDAOWasInvokedWithCorrectId(Long id) {
        verify(userDAO).existsById(id);
        verify(userDAO).deleteById(id);
    }
}

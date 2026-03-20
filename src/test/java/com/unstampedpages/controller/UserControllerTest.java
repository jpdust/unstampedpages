package com.unstampedpages.controller;

import com.unstampedpages.dao.UserDAO;
import com.unstampedpages.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDAO mockUserDAO;

    private User user;
    private User savedUser;
    private ResultActions resultActions;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public UserDAO mockUserDAO() {
            return mock(UserDAO.class);
        }
    }

    @BeforeEach
    void setUp() {
        reset(mockUserDAO);
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        givenUserWillBeSaved();
        whenCreatingUser();
        thenCreatedUserIsReturned();
    }

    @Test
    void createUser_shouldInvokeDAOSave() throws Exception {
        givenUserWillBeSavedWithDetails("Jane", "Smith", 25, "jane@example.com");
        whenCreatingUserWithDetails("Jane", "Smith", 25, "jane@example.com");
        thenDAOSaveWasInvokedWithCorrectDetails("Jane", "Smith", 25, "jane@example.com");
    }

    @Test
    void getUser_shouldReturnUserWhenExists() throws Exception {
        givenUser();
        whenGettingUser();
        thenCorrectUserRetrieved();
    }

    @Test
    void getUser_shouldReturn404WhenNotExists() throws Exception {
        givenUserDoesNotExist();
        whenGettingNonExistentUser();
        thenNotFoundIsReturned();
    }

    @Test
    void getUser_shouldInvokeDAOFindById() throws Exception {
        givenUserDoesNotExistWithId(42L);
        whenGettingUserById(42L);
        thenDAOFindByIdWasInvokedOnlyOnce(42L);
    }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenNoUsers() throws Exception {
        givenNoUsersExist();
        whenGettingAllUsers();
        thenEmptyListIsReturned();
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        givenMultipleUsersExist();
        whenGettingAllUsers();
        thenAllUsersAreReturned();
    }

    @Test
    void getAllUsers_shouldInvokeDAOFindAll() throws Exception {
        givenNoUsersExist();
        whenGettingAllUsers();
        thenDAOFindAllWasInvokedOnlyOnce();
    }

    @Test
    void updateUser_shouldReturnUpdatedUserWhenExists() throws Exception {
        givenExistingUserForUpdate();
        whenUpdatingUser();
        thenUpdatedUserIsReturned();
    }

    @Test
    void updateUser_shouldReturn404WhenNotExists() throws Exception {
        givenUserDoesNotExistForUpdate();
        whenUpdatingNonExistentUser();
        thenNotFoundIsReturnedAndSaveNotCalled();
    }

    @Test
    void updateUser_shouldInvokeDAOFindByIdThenSave() throws Exception {
        givenExistingUserForUpdateVerification();
        whenUpdatingUserForVerification();
        thenDAOFindByIdAndSaveWereCalledInOrder();
    }

    @Test
    void deleteUser_shouldReturn204WhenUserExists() throws Exception {
        givenUserExistsForDeletion();
        whenDeletingUser();
        thenNoContentIsReturned();
    }

    @Test
    void deleteUser_shouldReturn404WhenUserNotExists() throws Exception {
        givenUserDoesNotExistForDeletion();
        whenDeletingNonExistentUser();
        thenNotFoundIsReturnedAndDeleteNotCalled();
    }

    @Test
    void deleteUser_shouldInvokeDAOExistsByIdThenDeleteById() throws Exception {
        givenUserExistsForDeletion();
        whenDeletingUserForVerification();
        thenDAOExistsByIdAndDeleteByIdWereCalledInOrder();
    }

    private void givenUser() {
        user = new User(1L, "John", "Doe", 30, "john@example.com");
        when(mockUserDAO.findById(1L)).thenReturn(Optional.of(user));
    }

    private void givenUserWillBeSaved() {
        savedUser = new User(1L, "John", "Doe", 30, "john@example.com");
        when(mockUserDAO.save(any(User.class))).thenReturn(savedUser);
    }

    private void givenUserWillBeSavedWithDetails(String firstName, String lastName, int age, String email) {
        savedUser = new User(1L, firstName, lastName, age, email);
        when(mockUserDAO.save(any(User.class))).thenReturn(savedUser);
    }

    private void givenUserDoesNotExist() {
        when(mockUserDAO.findById(999L)).thenReturn(Optional.empty());
    }

    private void givenUserDoesNotExistWithId(Long id) {
        when(mockUserDAO.findById(id)).thenReturn(Optional.empty());
    }

    private void givenNoUsersExist() {
        when(mockUserDAO.findAll()).thenReturn(Collections.emptyList());
    }

    private void givenMultipleUsersExist() {
        User user1 = new User(1L, "John", "Doe", 30, "john@example.com");
        User user2 = new User(2L, "Jane", "Smith", 25, "jane@example.com");
        when(mockUserDAO.findAll()).thenReturn(Arrays.asList(user1, user2));
    }

    private void givenExistingUserForUpdate() {
        User existingUser = new User(1L, "John", "Doe", 30, "john@example.com");
        User updatedUser = new User(1L, "Jane", "Smith", 25, "jane@example.com");
        when(mockUserDAO.findById(1L)).thenReturn(Optional.of(existingUser));
        when(mockUserDAO.save(any(User.class))).thenReturn(updatedUser);
    }

    private void givenUserDoesNotExistForUpdate() {
        when(mockUserDAO.findById(999L)).thenReturn(Optional.empty());
    }

    private void givenExistingUserForUpdateVerification() {
        User existingUser = new User(1L, "John", "Doe", 30, "john@example.com");
        when(mockUserDAO.findById(1L)).thenReturn(Optional.of(existingUser));
        when(mockUserDAO.save(any(User.class))).thenReturn(existingUser);
    }

    private void givenUserExistsForDeletion() {
        when(mockUserDAO.existsById(1L)).thenReturn(true);
        doNothing().when(mockUserDAO).deleteById(1L);
    }

    private void givenUserDoesNotExistForDeletion() {
        when(mockUserDAO.existsById(999L)).thenReturn(false);
    }

    private void whenCreatingUser() throws Exception {
        resultActions = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"firstName":"John","lastName":"Doe","age":30,"email":"john@example.com"}
                    """));
    }

    private void whenCreatingUserWithDetails(String firstName, String lastName, int age, String email) throws Exception {
        resultActions = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {"firstName":"%s","lastName":"%s","age":%d,"email":"%s"}
                    """, firstName, lastName, age, email)));
    }

    private void whenGettingUser() throws Exception {
        resultActions = mockMvc.perform(get("/users/1"));
    }

    private void whenGettingNonExistentUser() throws Exception {
        resultActions = mockMvc.perform(get("/users/999"));
    }

    private void whenGettingUserById(Long id) throws Exception {
        resultActions = mockMvc.perform(get("/users/" + id));
    }

    private void whenGettingAllUsers() throws Exception {
        resultActions = mockMvc.perform(get("/users"));
    }

    private void whenUpdatingUser() throws Exception {
        resultActions = mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"firstName":"Jane","lastName":"Smith","age":25,"email":"jane@example.com"}
                    """));
    }

    private void whenUpdatingNonExistentUser() throws Exception {
        resultActions = mockMvc.perform(put("/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"firstName":"Jane","lastName":"Smith","age":25,"email":"jane@example.com"}
                    """));
    }

    private void whenUpdatingUserForVerification() throws Exception {
        resultActions = mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"firstName":"Updated","lastName":"User","age":40,"email":"updated@example.com"}
                    """));
    }

    private void whenDeletingUser() throws Exception {
        resultActions = mockMvc.perform(delete("/users/1"));
    }

    private void whenDeletingNonExistentUser() throws Exception {
        resultActions = mockMvc.perform(delete("/users/999"));
    }

    private void whenDeletingUserForVerification() throws Exception {
        resultActions = mockMvc.perform(delete("/users/1"));
    }

    private void thenCreatedUserIsReturned() throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(mockUserDAO, times(1)).save(any(User.class));
    }

    private void thenDAOSaveWasInvokedWithCorrectDetails(String firstName, String lastName, int age, String email) {
        verify(mockUserDAO).save(argThat(user ->
                user.getFirstName().equals(firstName) &&
                user.getLastName().equals(lastName) &&
                user.getAge() == age &&
                user.getEmail().equals(email)
        ));
    }

    private void thenCorrectUserRetrieved() throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(mockUserDAO, times(1)).findById(1L);
    }

    private void thenNotFoundIsReturned() throws Exception {
        resultActions.andExpect(status().isNotFound());
        verify(mockUserDAO, times(1)).findById(999L);
    }

    private void thenDAOFindByIdWasInvokedOnlyOnce(Long id) throws Exception {
        resultActions.andExpect(status().isNotFound());
        verify(mockUserDAO).findById(id);
        verifyNoMoreInteractions(mockUserDAO);
    }

    private void thenEmptyListIsReturned() throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(mockUserDAO, times(1)).findAll();
    }

    private void thenAllUsersAreReturned() throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));

        verify(mockUserDAO, times(1)).findAll();
    }

    private void thenDAOFindAllWasInvokedOnlyOnce() throws Exception {
        resultActions.andExpect(status().isOk());
        verify(mockUserDAO).findAll();
        verifyNoMoreInteractions(mockUserDAO);
    }

    private void thenUpdatedUserIsReturned() throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.email").value("jane@example.com"));

        verify(mockUserDAO).findById(1L);
        verify(mockUserDAO).save(any(User.class));
    }

    private void thenNotFoundIsReturnedAndSaveNotCalled() throws Exception {
        resultActions.andExpect(status().isNotFound());
        verify(mockUserDAO).findById(999L);
        verify(mockUserDAO, never()).save(any(User.class));
    }

    private void thenDAOFindByIdAndSaveWereCalledInOrder() throws Exception {
        resultActions.andExpect(status().isOk());
        var inOrder = inOrder(mockUserDAO);
        inOrder.verify(mockUserDAO).findById(1L);
        inOrder.verify(mockUserDAO).save(any(User.class));
    }

    private void thenNoContentIsReturned() throws Exception {
        resultActions.andExpect(status().isNoContent());
        verify(mockUserDAO).existsById(1L);
        verify(mockUserDAO).deleteById(1L);
    }

    private void thenNotFoundIsReturnedAndDeleteNotCalled() throws Exception {
        resultActions.andExpect(status().isNotFound());
        verify(mockUserDAO).existsById(999L);
        verify(mockUserDAO, never()).deleteById(anyLong());
    }

    private void thenDAOExistsByIdAndDeleteByIdWereCalledInOrder() throws Exception {
        resultActions.andExpect(status().isNoContent());
        var inOrder = inOrder(mockUserDAO);
        inOrder.verify(mockUserDAO).existsById(1L);
        inOrder.verify(mockUserDAO).deleteById(1L);
        verifyNoMoreInteractions(mockUserDAO);
    }
}

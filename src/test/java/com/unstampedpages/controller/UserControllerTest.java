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
        User savedUser = new User(1L, "John", "Doe", 30, "john@example.com");
        when(mockUserDAO.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"firstName":"John","lastName":"Doe","age":30,"email":"john@example.com"}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(mockUserDAO, times(1)).save(any(User.class));
    }

    @Test
    void createUser_shouldInvokeDAOSave() throws Exception {
        User savedUser = new User(1L, "Jane", "Smith", 25, "jane@example.com");
        when(mockUserDAO.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"firstName":"Jane","lastName":"Smith","age":25,"email":"jane@example.com"}
                            """))
                .andExpect(status().isOk());

        verify(mockUserDAO).save(argThat(user ->
                user.getFirstName().equals("Jane") &&
                user.getLastName().equals("Smith") &&
                user.getAge() == 25 &&
                user.getEmail().equals("jane@example.com")
        ));
    }

    @Test
    void getUser_shouldReturnUserWhenExists() throws Exception {
        User user = new User(1L, "John", "Doe", 30, "john@example.com");
        when(mockUserDAO.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(mockUserDAO, times(1)).findById(1L);
    }

    @Test
    void getUser_shouldReturn404WhenNotExists() throws Exception {
        when(mockUserDAO.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());

        verify(mockUserDAO, times(1)).findById(999L);
    }

    @Test
    void getUser_shouldInvokeDAOFindById() throws Exception {
        when(mockUserDAO.findById(42L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/42"))
                .andExpect(status().isNotFound());

        verify(mockUserDAO).findById(42L);
        verifyNoMoreInteractions(mockUserDAO);
    }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenNoUsers() throws Exception {
        when(mockUserDAO.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(mockUserDAO, times(1)).findAll();
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        User user1 = new User(1L, "John", "Doe", 30, "john@example.com");
        User user2 = new User(2L, "Jane", "Smith", 25, "jane@example.com");
        when(mockUserDAO.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));

        verify(mockUserDAO, times(1)).findAll();
    }

    @Test
    void getAllUsers_shouldInvokeDAOFindAll() throws Exception {
        when(mockUserDAO.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(mockUserDAO).findAll();
        verifyNoMoreInteractions(mockUserDAO);
    }

    @Test
    void updateUser_shouldReturnUpdatedUserWhenExists() throws Exception {
        User existingUser = new User(1L, "John", "Doe", 30, "john@example.com");
        User updatedUser = new User(1L, "Jane", "Smith", 25, "jane@example.com");
        when(mockUserDAO.findById(1L)).thenReturn(Optional.of(existingUser));
        when(mockUserDAO.save(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"firstName":"Jane","lastName":"Smith","age":25,"email":"jane@example.com"}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.email").value("jane@example.com"));

        verify(mockUserDAO).findById(1L);
        verify(mockUserDAO).save(any(User.class));
    }

    @Test
    void updateUser_shouldReturn404WhenNotExists() throws Exception {
        when(mockUserDAO.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"firstName":"Jane","lastName":"Smith","age":25,"email":"jane@example.com"}
                            """))
                .andExpect(status().isNotFound());

        verify(mockUserDAO).findById(999L);
        verify(mockUserDAO, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldInvokeDAOFindByIdThenSave() throws Exception {
        User existingUser = new User(1L, "John", "Doe", 30, "john@example.com");
        when(mockUserDAO.findById(1L)).thenReturn(Optional.of(existingUser));
        when(mockUserDAO.save(any(User.class))).thenReturn(existingUser);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"firstName":"Updated","lastName":"User","age":40,"email":"updated@example.com"}
                            """))
                .andExpect(status().isOk());

        var inOrder = inOrder(mockUserDAO);
        inOrder.verify(mockUserDAO).findById(1L);
        inOrder.verify(mockUserDAO).save(any(User.class));
    }

    @Test
    void deleteUser_shouldReturn204WhenUserExists() throws Exception {
        when(mockUserDAO.existsById(1L)).thenReturn(true);
        doNothing().when(mockUserDAO).deleteById(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(mockUserDAO).existsById(1L);
        verify(mockUserDAO).deleteById(1L);
    }

    @Test
    void deleteUser_shouldReturn404WhenUserNotExists() throws Exception {
        when(mockUserDAO.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());

        verify(mockUserDAO).existsById(999L);
        verify(mockUserDAO, never()).deleteById(anyLong());
    }

    @Test
    void deleteUser_shouldInvokeDAOExistsByIdThenDeleteById() throws Exception {
        when(mockUserDAO.existsById(1L)).thenReturn(true);
        doNothing().when(mockUserDAO).deleteById(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        var inOrder = inOrder(mockUserDAO);
        inOrder.verify(mockUserDAO).existsById(1L);
        inOrder.verify(mockUserDAO).deleteById(1L);
        verifyNoMoreInteractions(mockUserDAO);
    }
}

package com.unstampedpages.controller;

import com.unstampedpages.model.User;
import com.unstampedpages.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public UserService userService() {
            return mock(UserService.class);
        }
    }

    @Test
    private void createUser_shouldReturnCreatedUser() throws Exception {
        User user = new User(1L, "John", "Doe", 30, "john@example.com");
        when(userService.createUser(anyString(), anyString(), anyInt(), anyString())).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"firstName":"John","lastName":"Doe","age":30,"email":"john@example.com"}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    private void getUser_shouldReturnUserWhenExists() throws Exception {
        User user = new User(1L, "John", "Doe", 30, "john@example.com");
        when(userService.getUser(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    private void getUser_shouldReturn404WhenNotExists() throws Exception {
        when(userService.getUser(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    private void getAllUsers_shouldReturnEmptyListWhenNoUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    private void getAllUsers_shouldReturnAllUsers() throws Exception {
        User user1 = new User(1L, "John", "Doe", 30, "john@example.com");
        User user2 = new User(2L, "Jane", "Smith", 25, "jane@example.com");
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    private void updateUser_shouldReturnUpdatedUserWhenExists() throws Exception {
        User updatedUser = new User(1L, "Jane", "Smith", 25, "jane@example.com");
        when(userService.updateUser(eq(1L), anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(Optional.of(updatedUser));

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
    }

    @Test
    private void updateUser_shouldReturn404WhenNotExists() throws Exception {
        when(userService.updateUser(eq(999L), anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"firstName":"Jane","lastName":"Smith","age":25,"email":"jane@example.com"}
                            """))
                .andExpect(status().isNotFound());
    }

    @Test
    private void deleteUser_shouldReturn204WhenUserExists() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    private void deleteUser_shouldReturn404WhenUserNotExists() throws Exception {
        when(userService.deleteUser(999L)).thenReturn(false);

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());
    }
}

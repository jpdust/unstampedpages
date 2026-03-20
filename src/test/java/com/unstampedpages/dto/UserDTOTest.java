package com.unstampedpages.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
    }

    @Test
    void noArgConstructor_shouldCreateEmptyUserDTO() {
        givenNewUserDTOFromNoArgConstructor();
        thenAllFieldsAreNull();
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        givenUserDTOWithAllArgs();
        thenAllFieldsAreSetCorrectly();
    }

    @Test
    void setUserId_shouldUpdateUserId() {
        givenNewUserDTOFromNoArgConstructor();
        whenSettingUserId(1L);
        thenUserIdIsUpdated(1L);
    }

    @Test
    void setFirstName_shouldUpdateFirstName() {
        givenNewUserDTOFromNoArgConstructor();
        whenSettingFirstName("John");
        thenFirstNameIsUpdated("John");
    }

    @Test
    void setLastName_shouldUpdateLastName() {
        givenNewUserDTOFromNoArgConstructor();
        whenSettingLastName("Doe");
        thenLastNameIsUpdated("Doe");
    }

    @Test
    void setAge_shouldUpdateAge() {
        givenNewUserDTOFromNoArgConstructor();
        whenSettingAge(30);
        thenAgeIsUpdated(30);
    }

    @Test
    void setEmail_shouldUpdateEmail() {
        givenNewUserDTOFromNoArgConstructor();
        whenSettingEmail("john@example.com");
        thenEmailIsUpdated("john@example.com");
    }

    @Test
    void setters_shouldAllowUpdatingAllFields() {
        givenNewUserDTOFromNoArgConstructor();
        whenSettingAllFields();
        thenAllFieldsAreUpdatedCorrectly();
    }

    private void givenNewUserDTOFromNoArgConstructor() {
        userDTO = new UserDTO();
    }

    private void givenUserDTOWithAllArgs() {
        userDTO = new UserDTO(1L, "John", "Doe", 30, "john@example.com");
    }

    private void whenSettingUserId(Long userId) {
        userDTO.setUserId(userId);
    }

    private void whenSettingFirstName(String firstName) {
        userDTO.setFirstName(firstName);
    }

    private void whenSettingLastName(String lastName) {
        userDTO.setLastName(lastName);
    }

    private void whenSettingAge(int age) {
        userDTO.setAge(age);
    }

    private void whenSettingEmail(String email) {
        userDTO.setEmail(email);
    }

    private void whenSettingAllFields() {
        userDTO.setUserId(2L);
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Smith");
        userDTO.setAge(25);
        userDTO.setEmail("jane@example.com");
    }

    private void thenAllFieldsAreNull() {
        assertNull(userDTO.getUserId());
        assertNull(userDTO.getFirstName());
        assertNull(userDTO.getLastName());
        assertEquals(0, userDTO.getAge());
        assertNull(userDTO.getEmail());
    }

    private void thenAllFieldsAreSetCorrectly() {
        assertEquals(1L, userDTO.getUserId());
        assertEquals("John", userDTO.getFirstName());
        assertEquals("Doe", userDTO.getLastName());
        assertEquals(30, userDTO.getAge());
        assertEquals("john@example.com", userDTO.getEmail());
    }

    private void thenUserIdIsUpdated(Long expectedUserId) {
        assertEquals(expectedUserId, userDTO.getUserId());
    }

    private void thenFirstNameIsUpdated(String expectedFirstName) {
        assertEquals(expectedFirstName, userDTO.getFirstName());
    }

    private void thenLastNameIsUpdated(String expectedLastName) {
        assertEquals(expectedLastName, userDTO.getLastName());
    }

    private void thenAgeIsUpdated(int expectedAge) {
        assertEquals(expectedAge, userDTO.getAge());
    }

    private void thenEmailIsUpdated(String expectedEmail) {
        assertEquals(expectedEmail, userDTO.getEmail());
    }

    private void thenAllFieldsAreUpdatedCorrectly() {
        assertEquals(2L, userDTO.getUserId());
        assertEquals("Jane", userDTO.getFirstName());
        assertEquals("Smith", userDTO.getLastName());
        assertEquals(25, userDTO.getAge());
        assertEquals("jane@example.com", userDTO.getEmail());
    }
}

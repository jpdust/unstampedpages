package com.unstampedpages.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    private UserDTO userDTO;
    private UserDTO anotherUserDTO;

    @Test
    void constructor_shouldSetAllFields() {
        givenUserDTOWithAllArgs();
        thenAllFieldsAreSetCorrectly();
    }

    @Test
    void constructor_shouldAllowNullUserId() {
        givenUserDTOWithNullUserId();
        thenUserIdIsNull();
    }

    @Test
    void accessors_shouldReturnCorrectValues() {
        givenUserDTOWithAllArgs();
        thenAccessorsReturnCorrectValues();
    }

    @Test
    void equals_shouldReturnTrueForSameValues() {
        givenTwoIdenticalUserDTOs();
        thenTheyAreEqual();
    }

    @Test
    void equals_shouldReturnFalseForDifferentValues() {
        givenTwoDifferentUserDTOs();
        thenTheyAreNotEqual();
    }

    @Test
    void hashCode_shouldBeSameForEqualObjects() {
        givenTwoIdenticalUserDTOs();
        thenHashCodesAreEqual();
    }

    @Test
    void toString_shouldContainAllFields() {
        givenUserDTOWithAllArgs();
        thenToStringContainsAllFields();
    }

    private void givenUserDTOWithAllArgs() {
        userDTO = new UserDTO(1L, "John", "Doe", 30, "john@example.com");
    }

    private void givenUserDTOWithNullUserId() {
        userDTO = new UserDTO(null, "John", "Doe", 30, "john@example.com");
    }

    private void givenTwoIdenticalUserDTOs() {
        userDTO = new UserDTO(1L, "John", "Doe", 30, "john@example.com");
        anotherUserDTO = new UserDTO(1L, "John", "Doe", 30, "john@example.com");
    }

    private void givenTwoDifferentUserDTOs() {
        userDTO = new UserDTO(1L, "John", "Doe", 30, "john@example.com");
        anotherUserDTO = new UserDTO(2L, "Jane", "Smith", 25, "jane@example.com");
    }

    private void thenAllFieldsAreSetCorrectly() {
        assertEquals(1L, userDTO.userId());
        assertEquals("John", userDTO.firstName());
        assertEquals("Doe", userDTO.lastName());
        assertEquals(30, userDTO.age());
        assertEquals("john@example.com", userDTO.email());
    }

    private void thenUserIdIsNull() {
        assertNull(userDTO.userId());
        assertEquals("John", userDTO.firstName());
    }

    private void thenAccessorsReturnCorrectValues() {
        assertAll(
            () -> assertEquals(1L, userDTO.userId()),
            () -> assertEquals("John", userDTO.firstName()),
            () -> assertEquals("Doe", userDTO.lastName()),
            () -> assertEquals(30, userDTO.age()),
            () -> assertEquals("john@example.com", userDTO.email())
        );
    }

    private void thenTheyAreEqual() {
        assertEquals(userDTO, anotherUserDTO);
    }

    private void thenTheyAreNotEqual() {
        assertNotEquals(userDTO, anotherUserDTO);
    }

    private void thenHashCodesAreEqual() {
        assertEquals(userDTO.hashCode(), anotherUserDTO.hashCode());
    }

    private void thenToStringContainsAllFields() {
        String toString = userDTO.toString();
        assertAll(
            () -> assertTrue(toString.contains("1")),
            () -> assertTrue(toString.contains("John")),
            () -> assertTrue(toString.contains("Doe")),
            () -> assertTrue(toString.contains("30")),
            () -> assertTrue(toString.contains("john@example.com"))
        );
    }
}

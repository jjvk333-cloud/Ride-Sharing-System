package com.velto.controller;

import com.velto.model.Driver;
import com.velto.model.Passenger;
import com.velto.model.Role;
import com.velto.model.User;
import com.velto.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserControllerTests {

    private final UserService userService = Mockito.mock(UserService.class);
    private final UserController userController = new UserController(userService);

    @Test
    @DisplayName("GET /api/users returns all users")
    void testGetAllUsers() {
        User u1 = new Passenger("Alice", "alice@velto.com", "pass", "999");
        User u2 = new Driver("Bob", "bob@velto.com", "pass", "888");
        when(userService.findAllUsers()).thenReturn(List.of(u1, u2));

        ResponseEntity<List<User>> response = userController.getUsers(null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
    }

    @Test
    @DisplayName("GET /api/users?role=DRIVER filters users by role")
    void testGetUsersByRole() {
        User u1 = new Driver("Bob", "bob@velto.com", "pass", "888");
        when(userService.findUsersByRole(Role.DRIVER)).thenReturn(List.of(u1));

        ResponseEntity<List<User>> response = userController.getUsers(Role.DRIVER);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals(Role.DRIVER, response.getBody().get(0).getRole());
    }

    @Test
    @DisplayName("GET /api/users/{id} returns user by ID")
    void testGetUserById() {
        User u1 = new Passenger("Alice", "alice@velto.com", "pass", "999");
        u1.setId("user-123");
        when(userService.findById("user-123")).thenReturn(Optional.of(u1));

        ResponseEntity<User> response = userController.getUserById("user-123");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Alice", response.getBody().getName());
    }
}

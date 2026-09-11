package com.velto.controller;

import com.velto.exception.UserNotFoundException;
import com.velto.model.Role;
import com.velto.model.User;
import com.velto.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) Role role) {
        if (role != null) {
            return ResponseEntity.ok(userService.findUsersByRole(role));
        }
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        return ResponseEntity.ok(user);
    }
}

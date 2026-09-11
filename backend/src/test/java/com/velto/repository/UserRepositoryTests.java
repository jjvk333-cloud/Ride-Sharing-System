package com.velto.repository;

import com.velto.model.Role;
import com.velto.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    private static final String TEST_EMAIL = "test.passenger@velto.com";

    @BeforeEach
    @AfterEach
    void cleanUp() {
        userRepository.findByEmail(TEST_EMAIL).ifPresent(user -> userRepository.deleteById(user.getId()));
    }

    @Test
    void testSaveAndFindByEmail() {
        User user = new User("Test Passenger", TEST_EMAIL, "secret123", Role.PASSENGER, "9876543210");
        User saved = userRepository.save(user);

        assertNotNull(saved.getId(), "Generated MongoDB ID should not be null");
        assertEquals("Test Passenger", saved.getName());

        Optional<User> found = userRepository.findByEmail(TEST_EMAIL);
        assertTrue(found.isPresent(), "User should be found by email");
        assertEquals(Role.PASSENGER, found.get().getRole());
        assertTrue(userRepository.existsByEmail(TEST_EMAIL));
    }
}

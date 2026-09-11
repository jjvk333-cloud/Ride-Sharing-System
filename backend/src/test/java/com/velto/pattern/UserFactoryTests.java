package com.velto.pattern;

import com.velto.dto.RegisterRequest;
import com.velto.model.*;
import com.velto.pattern.factory.UserFactory;
import com.velto.pattern.factory.UserFactoryImpl;
import com.velto.repository.UserRepository;
import com.velto.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserFactoryTests {

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private static final String PASSENGER_EMAIL = "factory.passenger@velto.com";
    private static final String DRIVER_EMAIL = "factory.driver@velto.com";
    private static final String ADMIN_EMAIL = "factory.admin@velto.com";

    @BeforeEach
    @AfterEach
    void cleanup() {
        userRepository.findByEmail(PASSENGER_EMAIL).ifPresent(u -> userRepository.deleteById(u.getId()));
        userRepository.findByEmail(DRIVER_EMAIL).ifPresent(u -> userRepository.deleteById(u.getId()));
        userRepository.findByEmail(ADMIN_EMAIL).ifPresent(u -> userRepository.deleteById(u.getId()));
    }

    @Test
    @DisplayName("Factory Method instantiates Passenger correctly")
    void testCreatePassenger() {
        User user = userFactory.createUser(Role.PASSENGER, "Alice Passenger", "alice@velto.com", "pass123", "1112223333");

        assertNotNull(user);
        assertInstanceOf(Passenger.class, user);
        assertEquals(Role.PASSENGER, user.getRole());
        assertEquals("Alice Passenger", user.getName());
    }

    @Test
    @DisplayName("Factory Method instantiates Driver correctly")
    void testCreateDriver() {
        User user = userFactory.createUser(Role.DRIVER, "Bob Driver", "bob@velto.com", "pass123", "4445556666");

        assertNotNull(user);
        assertInstanceOf(Driver.class, user);
        assertEquals(Role.DRIVER, user.getRole());
        assertEquals("Bob Driver", user.getName());
        assertTrue(((Driver) user).isAvailable());
    }

    @Test
    @DisplayName("Factory Method instantiates Admin correctly")
    void testCreateAdmin() {
        User user = userFactory.createUser(Role.ADMIN, "Charlie Admin", "charlie@velto.com", "pass123", "7778889999");

        assertNotNull(user);
        assertInstanceOf(Admin.class, user);
        assertEquals(Role.ADMIN, user.getRole());
        assertEquals("OPERATIONS", ((Admin) user).getDepartment());
    }

    @Test
    @DisplayName("UserService uses Factory Method to persist polymorphic users to MongoDB")
    void testUserServicePolymorphicPersistence() {
        // Register Passenger
        RegisterRequest pReq = new RegisterRequest("Pass One", PASSENGER_EMAIL, "secret123", Role.PASSENGER, "1234567890");
        User savedPassenger = userService.registerUser(pReq);
        assertInstanceOf(Passenger.class, savedPassenger);

        // Register Driver
        RegisterRequest dReq = new RegisterRequest("Driver One", DRIVER_EMAIL, "secret123", Role.DRIVER, "0987654321");
        dReq.setVehicleNumber("KA-01-AB-1234");
        dReq.setVehicleType("Sedan");
        dReq.setLicenseNumber("DL-998877");
        User savedDriver = userService.registerUser(dReq);
        assertInstanceOf(Driver.class, savedDriver);
        assertEquals("KA-01-AB-1234", ((Driver) savedDriver).getVehicleNumber());

        // Verify retrieval from MongoDB maintains concrete type
        User retrievedDriver = userRepository.findByEmail(DRIVER_EMAIL).orElseThrow();
        assertInstanceOf(Driver.class, retrievedDriver);
        assertEquals("Sedan", ((Driver) retrievedDriver).getVehicleType());
    }
}

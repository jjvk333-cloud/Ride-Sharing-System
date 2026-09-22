package com.velto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velto.dto.CreateBookingRequest;
import com.velto.dto.CreateRideRequest;
import com.velto.model.*;
import com.velto.pattern.strategy.PricingType;
import com.velto.repository.BookingRepository;
import com.velto.repository.RideRepository;
import com.velto.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User passengerUser;
    private User driverUser;
    private Ride testRide;

    @BeforeEach
    void setup() {
        bookingRepository.deleteAll();
        rideRepository.deleteAll();

        passengerUser = userRepository.save(new Passenger("Sec Passenger", "sec.passenger@velto.com", "$2a$10$abcdef", "9876543210"));
        driverUser = userRepository.save(new Driver("Sec Driver", "sec.driver@velto.com", "$2a$10$abcdef", "9876543211"));

        testRide = rideRepository.save(new Ride.Builder()
                .driverId(driverUser.getId())
                .driverName(driverUser.getName())
                .pickup("Whitefield")
                .destination("Electronic City")
                .date("2026-10-10")
                .time("10:00")
                .seats(4)
                .availableSeats(4)
                .vehicleType("Sedan")
                .price(250.0)
                .status(RideStatus.REQUESTED)
                .build());
    }

    @AfterEach
    void cleanup() {
        bookingRepository.deleteAll();
        rideRepository.deleteAll();
        if (passengerUser != null && passengerUser.getId() != null) userRepository.deleteById(passengerUser.getId());
        if (driverUser != null && driverUser.getId() != null) userRepository.deleteById(driverUser.getId());
    }

    @Test
    @DisplayName("Public endpoints like /api/rides and /api/health permit unauthenticated requests")
    void testPublicEndpointsPermitted() throws Exception {
        mockMvc.perform(get("/api/rides"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "driver", roles = {"DRIVER"})
    @DisplayName("Driver role can publish new rides")
    void testDriverCanCreateRide() throws Exception {
        CreateRideRequest request = new CreateRideRequest(
                driverUser.getId(),
                driverUser.getName(),
                "Indiranagar",
                "Koramangala",
                "2026-10-10",
                "14:00",
                3,
                "Sedan",
                180.0,
                "AC"
        );

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pickup").value("Indiranagar"));
    }

    @Test
    @WithMockUser(username = "passenger", roles = {"PASSENGER"})
    @DisplayName("Passenger role can create bookings")
    void testPassengerCanBookRide() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(
                testRide.getId(),
                passengerUser.getId(),
                1,
                PricingType.STANDARD,
                "MOCK"
        );

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").exists())
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(username = "passenger", roles = {"PASSENGER"})
    @DisplayName("Passenger role is forbidden (403) from updating system configuration")
    void testPassengerForbiddenFromAdminConfig() throws Exception {
        mockMvc.perform(post("/api/config/reset"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Admin role is allowed to access and reset system configuration")
    void testAdminCanAccessConfig() throws Exception {
        mockMvc.perform(post("/api/config/reset"))
                .andExpect(status().isOk());
    }
}

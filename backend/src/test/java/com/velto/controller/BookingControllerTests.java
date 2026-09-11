package com.velto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velto.dto.CreateBookingRequest;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTests {

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

    private User testPassenger;
    private Ride testRide;

    @BeforeEach
    void setup() {
        bookingRepository.deleteAll();
        rideRepository.deleteAll();

        testPassenger = new Passenger("Ctrl Passenger", "ctrl.pass@velto.com", "pass123", "8887776666");
        testPassenger = userRepository.save(testPassenger);

        testRide = new Ride.Builder()
                .driverId("driver-888")
                .driverName("Driver Dan")
                .pickup("Hebbal")
                .destination("BTM Layout")
                .date("2026-09-30")
                .time("14:00")
                .seats(4)
                .availableSeats(4)
                .vehicleType("Sedan")
                .price(200.0)
                .status(RideStatus.REQUESTED)
                .build();
        testRide = rideRepository.save(testRide);
    }

    @AfterEach
    void cleanup() {
        bookingRepository.deleteAll();
        rideRepository.deleteAll();
        if (testPassenger != null && testPassenger.getId() != null) {
            userRepository.deleteById(testPassenger.getId());
        }
    }

    @Test
    @DisplayName("POST /api/bookings books ride through Facade and returns 201 Created")
    void testCreateBookingEndpoint() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(
                testRide.getId(),
                testPassenger.getId(),
                1,
                PricingType.STANDARD
        );

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").exists())
                .andExpect(jsonPath("$.seatsBooked").value(1))
                .andExpect(jsonPath("$.amountPaid").value(200.0))
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"));
    }

    @Test
    @DisplayName("GET /api/bookings/user/{userId} returns booking history for user")
    void testGetBookingsByUser() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(
                testRide.getId(),
                testPassenger.getId(),
                1,
                PricingType.STANDARD
        );

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/bookings/user/" + testPassenger.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].passengerId").value(testPassenger.getId()));
    }
}

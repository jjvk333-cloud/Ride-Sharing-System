package com.velto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velto.dto.CreateRideRequest;
import com.velto.model.Ride;
import com.velto.repository.RideRepository;
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
class RideControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    @AfterEach
    void cleanup() {
        rideRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/rides creates ride using Builder and returns 201 Created")
    void testCreateRide() throws Exception {
        CreateRideRequest request = new CreateRideRequest(
                "drv-1", "Suresh Driver", "Koramangala", "Whitefield", "2026-09-25", "10:00", 3, "Sedan", 350.0, "AC"
        );

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pickup").value("Koramangala"))
                .andExpect(jsonPath("$.destination").value("Whitefield"))
                .andExpect(jsonPath("$.seats").value(3))
                .andExpect(jsonPath("$.availableSeats").value(3))
                .andExpect(jsonPath("$.status").value("REQUESTED"));
    }

    @Test
    @DisplayName("GET /api/rides returns available rides and supports search query")
    void testSearchRides() throws Exception {
        CreateRideRequest r1 = new CreateRideRequest("drv-1", "Driver A", "Indiranagar", "Airport", "2026-09-25", "06:00", 4, "SUV", 600.0, "Luggage");
        CreateRideRequest r2 = new CreateRideRequest("drv-2", "Driver B", "HSR Layout", "Electronic City", "2026-09-25", "08:30", 3, "Hatchback", 150.0, "AC");

        mockMvc.perform(post("/api/rides").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r1)));
        mockMvc.perform(post("/api/rides").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r2)));

        // Search Airport
        mockMvc.perform(get("/api/rides?destination=Airport"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].destination").value("Airport"));
    }

    @Test
    @DisplayName("GET /api/rides/{id} returns ride details or 404 if not found")
    void testGetRideById() throws Exception {
        CreateRideRequest request = new CreateRideRequest("drv-1", "Driver A", "Indiranagar", "Airport", "2026-09-25", "06:00", 4, "SUV", 600.0, "Luggage");

        String response = mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Ride created = objectMapper.readValue(response, Ride.class);

        mockMvc.perform(get("/api/rides/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.pickup").value("Indiranagar"));

        // Non-existent ID -> 404
        mockMvc.perform(get("/api/rides/non-existent-id"))
                .andExpect(status().isNotFound());
    }
}

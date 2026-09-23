package com.velto.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RouteServiceTests {

    private final RouteService routeService = new RouteService();

    @Test
    @DisplayName("RouteService returns valid coordinates for known landmarks")
    void testKnownCoordinates() {
        double[] coords = routeService.getCoordinates("Pune Railway Station");
        assertNotNull(coords);
        assertEquals(2, coords.length);
        assertEquals(18.5289, coords[0], 0.001);
        assertEquals(73.8744, coords[1], 0.001);
    }

    @Test
    @DisplayName("RouteService generates deterministic coordinates for arbitrary user-entered locations")
    void testArbitraryLocationCoordinates() {
        double[] coords1 = routeService.getCoordinates("Custom Campus Gate 3");
        double[] coords2 = routeService.getCoordinates("Custom Campus Gate 3");

        assertNotNull(coords1);
        assertNotNull(coords2);
        assertEquals(coords1[0], coords2[0], 0.0001, "Coordinates should be deterministic for identical input");
        assertEquals(coords1[1], coords2[1], 0.0001, "Coordinates should be deterministic for identical input");
    }

    @Test
    @DisplayName("RouteService accurately computes road distance between locations")
    void testCalculateDistance() {
        double dist = routeService.calculateDistance("Pune Railway Station", "Hinjewadi Phase 1");
        assertTrue(dist >= 15.0 && dist <= 25.0, "Distance between Pune Station and Hinjewadi should be ~18-20 km, got: " + dist);
    }

    @Test
    @DisplayName("RouteService enforces minimum road distance for very close locations")
    void testMinimumRoadDistance() {
        double dist = routeService.calculateDistance("Pune Station", "Pune Station");
        assertTrue(dist >= 2.0, "Minimum road distance should be at least 2.0 km");
    }
}

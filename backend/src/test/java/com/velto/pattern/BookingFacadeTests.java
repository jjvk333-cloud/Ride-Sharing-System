package com.velto.pattern;

import com.velto.dto.BookingResponse;
import com.velto.dto.CreateBookingRequest;
import com.velto.exception.BookingException;
import com.velto.model.*;
import com.velto.pattern.facade.RideBookingFacade;
import com.velto.pattern.strategy.PricingType;
import com.velto.repository.BookingRepository;
import com.velto.repository.RideRepository;
import com.velto.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingFacadeTests {

    @Autowired
    private RideBookingFacade bookingFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User testPassenger;
    private Ride testRide;

    @BeforeEach
    void setup() {
        bookingRepository.deleteAll();
        rideRepository.deleteAll();

        testPassenger = new Passenger("Facade Passenger", "facade.pass@velto.com", "pass123", "9998881111");
        testPassenger = userRepository.save(testPassenger);

        testRide = new Ride.Builder()
                .driverId("driver-999")
                .driverName("Driver Dave")
                .pickup("Jayanagar")
                .destination("Bellandur")
                .date("2026-09-30")
                .time("11:00")
                .seats(3)
                .availableSeats(3)
                .vehicleType("Sedan")
                .price(150.0)
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
    @DisplayName("RideBookingFacade successfully coordinates booking, pricing, and seat decrement")
    void testFacadeBookingSuccess() {
        CreateBookingRequest request = new CreateBookingRequest(
                testRide.getId(),
                testPassenger.getId(),
                2,
                PricingType.STANDARD
        );

        BookingResponse response = bookingFacade.bookRide(request);

        assertNotNull(response);
        assertNotNull(response.getBookingId());
        assertEquals(2, response.getSeatsBooked());
        // 150.0 * 2 = 300.0
        assertEquals(300.0, response.getAmountPaid());
        assertEquals(BookingStatus.CONFIRMED, response.getBookingStatus());
        assertEquals(PaymentStatus.PAID, response.getPaymentStatus());

        // Verify available seats decremented in MongoDB
        Ride updatedRide = rideRepository.findById(testRide.getId()).orElseThrow();
        assertEquals(1, updatedRide.getAvailableSeats(), "Available seats should decrement from 3 to 1");
    }

    @Test
    @DisplayName("RideBookingFacade throws BookingException when requested seats exceed availability")
    void testFacadeInsufficientSeats() {
        CreateBookingRequest request = new CreateBookingRequest(
                testRide.getId(),
                testPassenger.getId(),
                5, // Exceeds available (3)
                PricingType.STANDARD
        );

        assertThrows(BookingException.class, () -> bookingFacade.bookRide(request));
    }
}

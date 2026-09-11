package com.velto.pattern;

import com.velto.model.*;
import com.velto.pattern.facade.RideBookingFacade;
import com.velto.pattern.strategy.PricingType;
import com.velto.dto.CreateBookingRequest;
import com.velto.repository.BookingRepository;
import com.velto.repository.NotificationRepository;
import com.velto.repository.RideRepository;
import com.velto.repository.UserRepository;
import com.velto.service.RideService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RideObserverTests {

    @Autowired
    private RideService rideService;

    @Autowired
    private RideBookingFacade bookingFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private User driver;
    private User passenger;
    private User admin;
    private Ride ride;

    @BeforeEach
    void setup() {
        notificationRepository.deleteAll();
        bookingRepository.deleteAll();
        rideRepository.deleteAll();

        driver = userRepository.save(new Driver("Observer Driver", "obs.driver@velto.com", "pass123", "1111111111"));
        passenger = userRepository.save(new Passenger("Observer Passenger", "obs.pass@velto.com", "pass123", "2222222222"));
        admin = userRepository.save(new Admin("Observer Admin", "obs.admin@velto.com", "pass123", "3333333333"));

        ride = rideRepository.save(new Ride.Builder()
                .driverId(driver.getId())
                .driverName(driver.getName())
                .pickup("Malleswaram")
                .destination("Banashankari")
                .date("2026-10-05")
                .time("15:00")
                .seats(3)
                .availableSeats(3)
                .vehicleType("Sedan")
                .price(180.0)
                .status(RideStatus.REQUESTED)
                .build());

        // Book ride so passenger is attached to the ride
        bookingFacade.bookRide(new CreateBookingRequest(ride.getId(), passenger.getId(), 1, PricingType.STANDARD));
    }

    @AfterEach
    void cleanup() {
        notificationRepository.deleteAll();
        bookingRepository.deleteAll();
        rideRepository.deleteAll();
        if (driver != null && driver.getId() != null) userRepository.deleteById(driver.getId());
        if (passenger != null && passenger.getId() != null) userRepository.deleteById(passenger.getId());
        if (admin != null && admin.getId() != null) userRepository.deleteById(admin.getId());
    }

    @Test
    @DisplayName("Observer Pattern notifies Passenger, Driver, and Admin on ride status change")
    void testRideStatusChangeBroadcastsNotifications() {
        // Transition: REQUESTED -> CONFIRMED
        rideService.updateRideStatus(ride.getId(), RideStatus.CONFIRMED);

        // Check Passenger notification
        List<Notification> passengerNotifs = notificationRepository.findByUserIdOrderByCreatedAtDesc(passenger.getId());
        assertFalse(passengerNotifs.isEmpty(), "Passenger should receive notification");
        assertTrue(passengerNotifs.get(0).getMessage().contains("CONFIRMED"));

        // Check Driver notification
        List<Notification> driverNotifs = notificationRepository.findByUserIdOrderByCreatedAtDesc(driver.getId());
        assertFalse(driverNotifs.isEmpty(), "Driver should receive notification");
        assertTrue(driverNotifs.get(0).getMessage().contains("CONFIRMED"));

        // Check Admin audit notification
        List<Notification> adminNotifs = notificationRepository.findByUserIdOrderByCreatedAtDesc(admin.getId());
        assertFalse(adminNotifs.isEmpty(), "Admin should receive audit notification");
        assertTrue(adminNotifs.get(0).getMessage().contains("CONFIRMED"));
    }
}

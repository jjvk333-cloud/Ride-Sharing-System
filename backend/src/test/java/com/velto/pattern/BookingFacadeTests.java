package com.velto.pattern;

import com.velto.dto.BookingResponse;
import com.velto.dto.CreateBookingRequest;
import com.velto.exception.BookingException;
import com.velto.exception.PaymentException;
import com.velto.model.*;
import com.velto.pattern.facade.RideBookingFacade;
import com.velto.pattern.strategy.PricingType;
import com.velto.repository.BookingRepository;
import com.velto.repository.NotificationRepository;
import com.velto.repository.PaymentRepository;
import com.velto.repository.RideRepository;
import com.velto.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private User testPassenger;
    private Ride testRide;

    @BeforeEach
    void setup() {
        notificationRepository.deleteAll();
        paymentRepository.deleteAll();
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
        notificationRepository.deleteAll();
        paymentRepository.deleteAll();
        bookingRepository.deleteAll();
        rideRepository.deleteAll();
        if (testPassenger != null && testPassenger.getId() != null) {
            userRepository.deleteById(testPassenger.getId());
        }
    }

    @Test
    @DisplayName("RideBookingFacade successfully coordinates booking, pricing, payment adapter, and notifications")
    void testFacadeBookingSuccess() {
        CreateBookingRequest request = new CreateBookingRequest(
                testRide.getId(),
                testPassenger.getId(),
                2,
                PricingType.STANDARD,
                "MOCK"
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

        // Verify Payment document was persisted by Adapter
        List<Payment> payments = paymentRepository.findByBookingId(response.getBookingId());
        assertFalse(payments.isEmpty(), "Payment record should be created and saved in MongoDB");
        Payment p = payments.get(0);
        assertEquals(300.0, p.getAmount());
        assertEquals(PaymentStatus.PAID, p.getPaymentStatus());
        assertEquals("MOCK", p.getPaymentMethod());

        // Verify Passenger Notification created by Observer
        List<Notification> notifs = notificationRepository.findByUserIdOrderByCreatedAtDesc(testPassenger.getId());
        assertFalse(notifs.isEmpty(), "Passenger should receive confirmation notification");
        assertTrue(notifs.get(0).getMessage().contains("Booking Confirmed"));
    }

    @Test
    @DisplayName("RideBookingFacade successfully processes UPI payment via adapter")
    void testFacadeUpiPaymentSuccess() {
        CreateBookingRequest request = new CreateBookingRequest(
                testRide.getId(),
                testPassenger.getId(),
                1,
                PricingType.STANDARD,
                "UPI"
        );
        request.setUpiId("facade@okaxis");

        BookingResponse response = bookingFacade.bookRide(request);

        assertNotNull(response);
        assertEquals(150.0, response.getAmountPaid());
        assertEquals(PaymentStatus.PAID, response.getPaymentStatus());

        List<Payment> payments = paymentRepository.findByBookingId(response.getBookingId());
        assertEquals(1, payments.size());
        assertEquals("UPI", payments.get(0).getPaymentMethod());
        assertTrue(payments.get(0).getTransactionId().startsWith("TXN-UPI-"));
    }

    @Test
    @DisplayName("RideBookingFacade rolls back/aborts when payment adapter declines payment")
    void testFacadePaymentFailureAbortsBooking() {
        CreateBookingRequest request = new CreateBookingRequest(
                testRide.getId(),
                testPassenger.getId(),
                1,
                PricingType.STANDARD,
                "CARD"
        );
        // Invalid card number triggers Card Gateway decline
        request.setCardNumber("4111-INVALID");
        request.setExpiryDate("12/28");
        request.setCvv("123");

        assertThrows(PaymentException.class, () -> bookingFacade.bookRide(request));

        // Invariant check: Seats must NOT be decremented
        Ride unmodifiedRide = rideRepository.findById(testRide.getId()).orElseThrow();
        assertEquals(3, unmodifiedRide.getAvailableSeats(), "Seats must remain 3 after failed payment");

        // Booking record must NOT be created
        assertEquals(0, bookingRepository.count(), "No booking record should be saved on payment failure");
    }

    @Test
    @DisplayName("RideBookingFacade throws BookingException when requested seats exceed availability")
    void testFacadeInsufficientSeats() {
        CreateBookingRequest request = new CreateBookingRequest(
                testRide.getId(),
                testPassenger.getId(),
                5, // Exceeds available (3)
                PricingType.STANDARD,
                "MOCK"
        );

        assertThrows(BookingException.class, () -> bookingFacade.bookRide(request));
    }

    @Test
    @DisplayName("RideBookingFacade cancelBooking restores seats and updates payment status to REFUNDED")
    void testFacadeCancellation() {
        CreateBookingRequest request = new CreateBookingRequest(
                testRide.getId(),
                testPassenger.getId(),
                2,
                PricingType.STANDARD,
                "MOCK"
        );

        BookingResponse response = bookingFacade.bookRide(request);
        assertEquals(1, rideRepository.findById(testRide.getId()).orElseThrow().getAvailableSeats());

        // Cancel
        Booking cancelled = bookingFacade.cancelBooking(response.getBookingId());
        assertEquals(BookingStatus.CANCELLED, cancelled.getBookingStatus());
        assertEquals(PaymentStatus.REFUNDED, cancelled.getPaymentStatus());

        // Verify seats restored to 3
        assertEquals(3, rideRepository.findById(testRide.getId()).orElseThrow().getAvailableSeats());
    }
}

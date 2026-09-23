package com.velto.pattern.facade;

import com.velto.dto.BookingResponse;
import com.velto.dto.CreateBookingRequest;
import com.velto.exception.BookingException;
import com.velto.exception.PaymentException;
import com.velto.exception.RideNotFoundException;
import com.velto.exception.UserNotFoundException;
import com.velto.model.*;
import com.velto.pattern.adapter.PaymentProcessor;
import com.velto.pattern.adapter.PaymentProcessorFactory;
import com.velto.pattern.adapter.PaymentRequest;
import com.velto.pattern.adapter.PaymentResponse;
import com.velto.pattern.observer.RideEvent;
import com.velto.pattern.observer.RideEventSubject;
import com.velto.pattern.strategy.PricingContext;
import com.velto.repository.BookingRepository;
import com.velto.repository.NotificationRepository;
import com.velto.repository.PaymentRepository;
import com.velto.repository.RideRepository;
import com.velto.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * =========================================================================
 * DESIGN PATTERN 4 — FACADE PATTERN (Structural)
 * =========================================================================
 * The RideBookingFacade is the master orchestration subsystem for VELTO:
 * 1. User & Account Verification (Domain Model)
 * 2. Ride State & Status Invariant Checks (State Pattern)
 * 3. Atomic Seat Inventory Verification & Reservation
 * 4. Dynamic Pricing Calculation (Strategy Pattern)
 * 5. Payment Processing via Gateway Adapters (Adapter Pattern)
 * 6. Booking Document Persistence (MongoDB)
 * 7. Multi-Actor Notification Broadcasting (Observer Pattern)
 * 8. Booking Cancellation & Seat Restoration Subsystem
 *
 * Clients (Controllers and Frontend) invoke simple, unified methods without
 * directly coupling to the underlying payment gateways, strategies, or repositories.
 */
@Component
public class RideBookingFacade {

    private static final Logger log = LoggerFactory.getLogger(RideBookingFacade.class);

    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PricingContext pricingContext;
    private final PaymentProcessorFactory paymentProcessorFactory;
    private final RideEventSubject rideEventSubject;
    private final NotificationRepository notificationRepository;

    public RideBookingFacade(UserRepository userRepository,
                             RideRepository rideRepository,
                             BookingRepository bookingRepository,
                             PaymentRepository paymentRepository,
                             PricingContext pricingContext,
                             PaymentProcessorFactory paymentProcessorFactory,
                             RideEventSubject rideEventSubject,
                             NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.pricingContext = pricingContext;
        this.paymentProcessorFactory = paymentProcessorFactory;
        this.rideEventSubject = rideEventSubject;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Master Facade method coordinating the end-to-end booking workflow across
     * Strategy, Adapter, State, and Observer subsystems.
     */
    public BookingResponse bookRide(CreateBookingRequest request) {
        log.info("RideBookingFacade: Commencing atomic booking workflow for passenger '{}' on ride '{}'...",
                request.getPassengerId(), request.getRideId());

        // Step 1: Validate passenger exists
        User passenger = userRepository.findById(request.getPassengerId())
                .orElseThrow(() -> new UserNotFoundException("Passenger not found with ID: " + request.getPassengerId()));

        // Step 2: Validate ride exists
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found with ID: " + request.getRideId()));

        // Step 3: Validate ride lifecycle state
        if (ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            throw new BookingException("Cannot book a ride that is already " + ride.getStatus());
        }

        // Step 4: Check seat inventory
        if (ride.getAvailableSeats() < request.getSeats()) {
            throw new BookingException("Not enough seats available. Requested: " +
                    request.getSeats() + ", Available: " + ride.getAvailableSeats());
        }

        // Step 5: Execute dynamic fare calculation via Strategy Pattern
        pricingContext.setStrategyByType(request.getPricingType());
        double totalAmount = pricingContext.calculatePrice(ride.getPrice(), request.getSeats());
        log.info("RideBookingFacade [Strategy: {}]: Calculated total fare ₹{} for {} seat(s)",
                pricingContext.getStrategy().getStrategyName(), totalAmount, request.getSeats());

        // Step 6: Process payment via Adapter Pattern
        String method = (request.getPaymentMethod() != null && !request.getPaymentMethod().isBlank())
                ? request.getPaymentMethod().toUpperCase()
                : "MOCK";
        PaymentProcessor processor = paymentProcessorFactory.getProcessor(method);

        PaymentRequest paymentRequest = new PaymentRequest(
                "PENDING-" + ride.getId(),
                passenger.getId(),
                totalAmount,
                method
        );
        paymentRequest.setUpiId(request.getUpiId());
        paymentRequest.setCardNumber(request.getCardNumber());
        paymentRequest.setExpiryDate(request.getExpiryDate());
        paymentRequest.setCvv(request.getCvv());

        PaymentResponse paymentResponse = processor.processPayment(paymentRequest);
        if (!paymentResponse.isSuccess()) {
            log.error("RideBookingFacade: Payment declined via {} adapter: {}", method, paymentResponse.getMessage());
            throw new PaymentException("Payment failed via " + method + " adapter: " + paymentResponse.getMessage());
        }

        // Step 7: Update available seats atomically
        ride.setAvailableSeats(ride.getAvailableSeats() - request.getSeats());
        rideRepository.save(ride);

        // Step 8: Persist Booking record using GoF Builder Pattern (BookingBuilder)
        Booking booking = new com.velto.pattern.builder.BookingBuilder()
                .rideId(ride.getId())
                .passengerId(passenger.getId())
                .passengerName(passenger.getName())
                .seats(request.getSeats())
                .amount(totalAmount)
                .distance(ride.getDistance())
                .pricingType(request.getPricingType())
                .paymentStatus(PaymentStatus.PAID)
                .bookingStatus(BookingStatus.CONFIRMED)
                .build();
        Booking savedBooking = bookingRepository.save(booking);

        // Step 9: Persist reconciled Payment document
        Payment payment = new Payment();
        payment.setBookingId(savedBooking.getId());
        payment.setPassengerId(passenger.getId());
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(method);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setTransactionId(paymentResponse.getTransactionId());
        payment.setGatewayReference(paymentResponse.getGatewayReference());
        payment.setMessage(paymentResponse.getMessage());
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        log.info("RideBookingFacade: Booking #{} confirmed and Payment #{} recorded.",
                savedBooking.getId(), payment.getTransactionId());

        // Step 10: Dispatch Observer Pattern notifications to Passenger and Driver
        try {
            Notification passNotif = new Notification(
                    passenger.getId(),
                    String.format("Booking Confirmed: Your ride from '%s' to '%s' (%.1f km) is confirmed. Fare: ₹%.2f (%s)",
                            ride.getPickup(), ride.getDestination(), ride.getDistance(), totalAmount, method),
                    "BOOKING_CONFIRMED"
            );
            notificationRepository.save(passNotif);

            if (ride.getDriverId() != null) {
                Notification driverNotif = new Notification(
                        ride.getDriverId(),
                        String.format("New Passenger Booked: %s booked %d seat(s) on your ride to %s (%.1f km).",
                                passenger.getName(), request.getSeats(), ride.getDestination(), ride.getDistance()),
                        "RIDE_BOOKING_UPDATE"
                );
                notificationRepository.save(driverNotif);
            }
        } catch (Exception e) {
            log.warn("RideBookingFacade: Non-fatal notification error: {}", e.getMessage());
        }

        // Step 11: Return unified response
        return new BookingResponse(
                savedBooking.getId(),
                ride.getId(),
                passenger.getId(),
                passenger.getName(),
                ride.getPickup(),
                ride.getDestination(),
                ride.getDate(),
                ride.getTime(),
                savedBooking.getSeats(),
                savedBooking.getAmount(),
                ride.getDistance(),
                savedBooking.getPricingType(),
                savedBooking.getPaymentStatus(),
                savedBooking.getBookingStatus(),
                savedBooking.getCreatedAt(),
                "Ride booked and payment settled via " + method + " adapter successfully!"
        );
    }

    /**
     * Facade method coordinating booking cancellation:
     * Restores seats to the ride, marks booking status as CANCELLED,
     * updates payment status to REFUNDED, and broadcasts alerts to all observers.
     */
    public Booking cancelBooking(String bookingId) {
        log.info("RideBookingFacade: Processing cancellation for booking '{}'...", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking not found with ID: " + bookingId));

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("Booking is already cancelled.");
        }

        // Restore seat inventory
        rideRepository.findById(booking.getRideId()).ifPresent(ride -> {
            ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeats());
            rideRepository.save(ride);
            log.info("Restored {} seat(s) to ride '{}'. Current available seats: {}",
                    booking.getSeats(), ride.getId(), ride.getAvailableSeats());

            // Observer notification to driver
            if (ride.getDriverId() != null) {
                Notification driverAlert = new Notification(
                        ride.getDriverId(),
                        String.format("Passenger Cancellation: %s cancelled %d seat(s) on ride to %s.",
                                booking.getPassengerName(), booking.getSeats(), ride.getDestination()),
                        "BOOKING_CANCELLED"
                );
                notificationRepository.save(driverAlert);
            }
        });

        // Mark booking cancelled and refunded
        booking.setBookingStatus(BookingStatus.CANCELLED);
        if (booking.getPaymentStatus() == PaymentStatus.PAID) {
            booking.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        Booking updated = bookingRepository.save(booking);

        // Observer notification to passenger
        Notification passengerAlert = new Notification(
                booking.getPassengerId(),
                "Your booking has been cancelled and refund processed.",
                "BOOKING_REFUNDED"
        );
        notificationRepository.save(passengerAlert);

        return updated;
    }

    /**
     * Maps an internal Booking entity into a rich BookingResponse enriched with
     * pickup, destination, schedule, vehicle type, and driver details from the associated Ride.
     */
    public BookingResponse mapToBookingResponse(Booking booking) {
        if (booking == null) return null;

        Ride ride = rideRepository.findById(booking.getRideId()).orElse(null);
        String pickup = (ride != null) ? ride.getPickup() : "Pickup Location";
        String destination = (ride != null) ? ride.getDestination() : "Destination";
        String date = (ride != null) ? ride.getDate() : "";
        String time = (ride != null) ? ride.getTime() : "";
        double distance = (ride != null && ride.getDistance() > 0) ? ride.getDistance() : booking.getDistance();

        return new BookingResponse(
                booking.getId(),
                booking.getRideId(),
                booking.getPassengerId(),
                booking.getPassengerName(),
                pickup,
                destination,
                date,
                time,
                booking.getSeats(),
                booking.getAmount(),
                distance,
                booking.getPricingType(),
                booking.getPaymentStatus(),
                booking.getBookingStatus(),
                booking.getCreatedAt(),
                "Booking details retrieved successfully."
        );
    }
}

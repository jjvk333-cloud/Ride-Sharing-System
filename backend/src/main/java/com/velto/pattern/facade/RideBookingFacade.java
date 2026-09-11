package com.velto.pattern.facade;

import com.velto.dto.BookingResponse;
import com.velto.dto.CreateBookingRequest;
import com.velto.exception.BookingException;
import com.velto.exception.RideNotFoundException;
import com.velto.exception.UserNotFoundException;
import com.velto.model.*;
import com.velto.pattern.strategy.PricingContext;
import com.velto.repository.BookingRepository;
import com.velto.repository.RideRepository;
import com.velto.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * =========================================================================
 * DESIGN PATTERN 4 — FACADE PATTERN
 * =========================================================================
 * The RideBookingFacade coordinates the complex booking subsystem:
 * 1. User/Passenger Verification
 * 2. Ride Existence & Status Validation
 * 3. Seat Availability Verification & Atomic Seat Decrement
 * 4. Dynamic Pricing Strategy Execution
 * 5. Mock Payment Processing
 * 6. Booking Document Creation & Persistence
 * 7. Booking Cancellation & Seat Restoration Subsystem
 *
 * Clients (Controllers/Frontend) communicate with simple single methods.
 */
@Component
public class RideBookingFacade {

    private static final Logger log = LoggerFactory.getLogger(RideBookingFacade.class);

    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;
    private final PricingContext pricingContext;

    public RideBookingFacade(UserRepository userRepository,
                             RideRepository rideRepository,
                             BookingRepository bookingRepository,
                             PricingContext pricingContext) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.bookingRepository = bookingRepository;
        this.pricingContext = pricingContext;
    }

    /**
     * Facade method coordinating the end-to-end ride booking workflow.
     */
    public BookingResponse bookRide(CreateBookingRequest request) {
        log.info("RideBookingFacade: Processing booking for passenger '{}' on ride '{}'...",
                request.getPassengerId(), request.getRideId());

        // Step 1: Validate passenger exists
        User passenger = userRepository.findById(request.getPassengerId())
                .orElseThrow(() -> new UserNotFoundException("Passenger not found with ID: " + request.getPassengerId()));

        // Step 2: Validate ride exists
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found with ID: " + request.getRideId()));

        // Step 3: Check ride status
        if (ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            throw new BookingException("Cannot book a ride that is already " + ride.getStatus());
        }

        // Step 4: Check seat availability
        if (ride.getAvailableSeats() < request.getSeats()) {
            throw new BookingException("Not enough seats available. Requested: " +
                    request.getSeats() + ", Available: " + ride.getAvailableSeats());
        }

        // Step 5: Execute dynamic pricing calculation via Strategy Pattern
        pricingContext.setStrategyByType(request.getPricingType());
        double totalAmount = pricingContext.calculatePrice(ride.getPrice(), request.getSeats());

        // Step 6: Process mock payment (Facade integration point)
        PaymentStatus paymentStatus = PaymentStatus.PAID;

        // Step 7: Update ride available seats
        ride.setAvailableSeats(ride.getAvailableSeats() - request.getSeats());
        rideRepository.save(ride);

        // Step 8: Create and persist booking record
        Booking booking = new Booking(
                ride.getId(),
                passenger.getId(),
                passenger.getName(),
                request.getSeats(),
                totalAmount,
                request.getPricingType(),
                paymentStatus,
                BookingStatus.CONFIRMED
        );
        Booking savedBooking = bookingRepository.save(booking);

        log.info("RideBookingFacade: Booking confirmed successfully! Booking ID: {}", savedBooking.getId());

        // Step 9: Return clean unified response
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
                savedBooking.getPricingType(),
                savedBooking.getPaymentStatus(),
                savedBooking.getBookingStatus(),
                savedBooking.getCreatedAt(),
                "Ride booked and confirmed successfully!"
        );
    }

    /**
     * Facade method coordinating booking cancellation:
     * Restores seats to the ride, updates booking status to CANCELLED,
     * and sets payment status to REFUNDED if applicable.
     */
    public Booking cancelBooking(String bookingId) {
        log.info("RideBookingFacade: Processing cancellation for booking '{}'...", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking not found with ID: " + bookingId));

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("Booking is already cancelled.");
        }

        // Restore seats to the ride
        rideRepository.findById(booking.getRideId()).ifPresent(ride -> {
            ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeats());
            rideRepository.save(ride);
            log.info("Restored {} seats to ride '{}'. New available seats: {}",
                    booking.getSeats(), ride.getId(), ride.getAvailableSeats());
        });

        // Mark cancelled
        booking.setBookingStatus(BookingStatus.CANCELLED);
        if (booking.getPaymentStatus() == PaymentStatus.PAID) {
            booking.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        return bookingRepository.save(booking);
    }
}

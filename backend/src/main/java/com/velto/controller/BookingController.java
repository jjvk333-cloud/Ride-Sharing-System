package com.velto.controller;

import com.velto.dto.BookingResponse;
import com.velto.dto.CreateBookingRequest;
import com.velto.exception.BookingException;
import com.velto.model.Booking;
import com.velto.pattern.facade.RideBookingFacade;
import com.velto.repository.BookingRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing simple ride booking endpoints,
 * backed internally by the GoF RideBookingFacade.
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final RideBookingFacade rideBookingFacade;
    private final BookingRepository bookingRepository;

    public BookingController(RideBookingFacade rideBookingFacade, BookingRepository bookingRepository) {
        this.rideBookingFacade = rideBookingFacade;
        this.bookingRepository = bookingRepository;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = rideBookingFacade.bookRide(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingException("Booking not found with ID: " + id));
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable String userId) {
        List<Booking> bookings = bookingRepository.findByPassengerId(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<List<Booking>> getBookingsByRide(@PathVariable String rideId) {
        List<Booking> bookings = bookingRepository.findByRideId(rideId);
        return ResponseEntity.ok(bookings);
    }
}

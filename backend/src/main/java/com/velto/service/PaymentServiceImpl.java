package com.velto.service;

import com.velto.exception.PaymentException;
import com.velto.exception.ResourceNotFoundException;
import com.velto.model.Booking;
import com.velto.model.Payment;
import com.velto.model.PaymentStatus;
import com.velto.pattern.adapter.PaymentProcessor;
import com.velto.pattern.adapter.PaymentProcessorFactory;
import com.velto.pattern.adapter.PaymentRequest;
import com.velto.pattern.adapter.PaymentResponse;
import com.velto.repository.BookingRepository;
import com.velto.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentProcessorFactory processorFactory;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BookingRepository bookingRepository,
                              PaymentProcessorFactory processorFactory) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.processorFactory = processorFactory;
    }

    @Override
    public Payment processPayment(PaymentRequest request) {
        // 1. Verify booking exists
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + request.getBookingId()));

        if (booking.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new PaymentException("Booking " + request.getBookingId() + " has been cancelled and refunded.");
        }

        // 2. Select appropriate adapter via factory
        PaymentProcessor processor = processorFactory.getProcessor(request.getPaymentMethod());

        // 3. Delegate to adapter
        PaymentResponse response = processor.processPayment(request);

        // 4. Create and persist Payment record
        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setPassengerId(request.getPassengerId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod().toUpperCase());
        payment.setTransactionId(response.getTransactionId());
        payment.setGatewayReference(response.getGatewayReference());
        payment.setMessage(response.getMessage());

        if (response.isSuccess()) {
            payment.setPaymentStatus(PaymentStatus.PAID);
            payment.setCompletedAt(LocalDateTime.now());
            // Update booking payment status
            booking.setPaymentStatus(PaymentStatus.PAID);
            bookingRepository.save(booking);
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
        }

        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPaymentById(String id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
    }

    @Override
    public List<Payment> getPaymentsByBookingId(String bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    @Override
    public List<Payment> getPaymentsByPassengerId(String passengerId) {
        return paymentRepository.findByPassengerId(passengerId);
    }
}

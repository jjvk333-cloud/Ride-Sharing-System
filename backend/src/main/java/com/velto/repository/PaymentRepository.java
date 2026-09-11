package com.velto.repository;

import com.velto.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByBookingId(String bookingId);
    List<Payment> findByPassengerId(String passengerId);
    Optional<Payment> findByTransactionId(String transactionId);
}

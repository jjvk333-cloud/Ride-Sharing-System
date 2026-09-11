package com.velto.pattern;

import com.velto.exception.PaymentException;
import com.velto.model.*;
import com.velto.pattern.adapter.*;
import com.velto.pattern.adapter.thirdparty.ThirdPartyCardGateway;
import com.velto.pattern.adapter.thirdparty.ThirdPartyMockGateway;
import com.velto.pattern.adapter.thirdparty.ThirdPartyUpiGateway;
import com.velto.pattern.strategy.PricingType;
import com.velto.repository.BookingRepository;
import com.velto.repository.PaymentRepository;
import com.velto.service.PaymentService;
import com.velto.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentAdapterTests {

    private ThirdPartyUpiGateway upiGateway;
    private ThirdPartyCardGateway cardGateway;
    private ThirdPartyMockGateway mockGateway;

    private UpiPaymentAdapter upiAdapter;
    private CardPaymentAdapter cardAdapter;
    private MockPaymentAdapter mockAdapter;
    private PaymentProcessorFactory factory;

    private PaymentRepository paymentRepository;
    private BookingRepository bookingRepository;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        upiGateway = new ThirdPartyUpiGateway();
        cardGateway = new ThirdPartyCardGateway();
        mockGateway = new ThirdPartyMockGateway();

        upiAdapter = new UpiPaymentAdapter(upiGateway);
        cardAdapter = new CardPaymentAdapter(cardGateway);
        mockAdapter = new MockPaymentAdapter(mockGateway);

        factory = new PaymentProcessorFactory(List.of(upiAdapter, cardAdapter, mockAdapter));

        paymentRepository = Mockito.mock(PaymentRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        paymentService = new PaymentServiceImpl(paymentRepository, bookingRepository, factory);
    }

    @Test
    @DisplayName("UPI Adapter successfully authorizes valid UPI payment")
    void testUpiAdapterSuccess() {
        PaymentRequest req = new PaymentRequest("book-1", "pass-1", 250.0, "UPI");
        req.setUpiId("student@okaxis");

        PaymentResponse resp = upiAdapter.processPayment(req);

        assertTrue(resp.isSuccess());
        assertNotNull(resp.getTransactionId());
        assertTrue(resp.getTransactionId().startsWith("TXN-UPI-"));
        assertTrue(resp.getGatewayReference().startsWith("UPI-NPCI-"));
    }

    @Test
    @DisplayName("UPI Adapter fails when invalid UPI ID provided")
    void testUpiAdapterInvalidVpa() {
        PaymentRequest req = new PaymentRequest("book-1", "pass-1", 250.0, "UPI");
        req.setUpiId("invalid_vpa_without_at");

        PaymentResponse resp = upiAdapter.processPayment(req);

        assertFalse(resp.isSuccess());
        assertNull(resp.getGatewayReference());
        assertTrue(resp.getMessage().contains("Invalid Virtual Payment Address"));
    }

    @Test
    @DisplayName("Card Adapter successfully processes valid 16-digit card charge")
    void testCardAdapterSuccess() {
        PaymentRequest req = new PaymentRequest("book-2", "pass-1", 500.0, "CARD");
        req.setCardNumber("4111 2222 3333 4444");
        req.setExpiryDate("12/28");
        req.setCvv("123");

        PaymentResponse resp = cardAdapter.processPayment(req);

        assertTrue(resp.isSuccess());
        assertNotNull(resp.getTransactionId());
        assertTrue(resp.getTransactionId().startsWith("TXN-CARD-"));
        assertTrue(resp.getGatewayReference().startsWith("AUTH-STRIPE-"));
    }

    @Test
    @DisplayName("Card Adapter handles card decline for test 0000 ending card")
    void testCardAdapterDeclined() {
        PaymentRequest req = new PaymentRequest("book-2", "pass-1", 500.0, "CARD");
        req.setCardNumber("4111 2222 3333 0000");
        req.setExpiryDate("12/28");
        req.setCvv("123");

        PaymentResponse resp = cardAdapter.processPayment(req);

        assertFalse(resp.isSuccess());
        assertTrue(resp.getMessage().contains("Card declined"));
    }

    @Test
    @DisplayName("Mock Adapter executes direct cash settlement")
    void testMockAdapterSuccess() {
        PaymentRequest req = new PaymentRequest("book-3", "pass-1", 100.0, "MOCK");

        PaymentResponse resp = mockAdapter.processPayment(req);

        assertTrue(resp.isSuccess());
        assertTrue(resp.getTransactionId().startsWith("TXN-MOCK-"));
        assertTrue(resp.getGatewayReference().startsWith("REC-"));
    }

    @Test
    @DisplayName("Factory correctly resolves adapters and aliases")
    void testFactoryResolution() {
        assertSame(upiAdapter, factory.getProcessor("UPI"));
        assertSame(cardAdapter, factory.getProcessor("CARD"));
        assertSame(cardAdapter, factory.getProcessor("CREDIT_CARD"));
        assertSame(cardAdapter, factory.getProcessor("DEBIT_CARD"));
        assertSame(mockAdapter, factory.getProcessor("MOCK"));
        assertSame(mockAdapter, factory.getProcessor("CASH"));

        assertThrows(PaymentException.class, () -> factory.getProcessor("BITCOIN"));
    }

    @Test
    @DisplayName("PaymentService end-to-end updates Booking status to PAID")
    void testPaymentServiceSuccessUpdatesBooking() {
        Booking booking = new Booking("ride-100", "pass-1", "John", 2, 300.0,
                PricingType.STANDARD, PaymentStatus.PENDING, BookingStatus.CONFIRMED);
        booking.setId("booking-xyz");

        when(bookingRepository.findById("booking-xyz")).thenReturn(Optional.of(booking));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            p.setId("pay-999");
            return p;
        });

        PaymentRequest request = new PaymentRequest("booking-xyz", "pass-1", 300.0, "UPI");
        request.setUpiId("john@okaxis");

        Payment result = paymentService.processPayment(request);

        assertNotNull(result);
        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
        assertEquals(PaymentStatus.PAID, booking.getPaymentStatus());
        verify(bookingRepository, times(1)).save(booking);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}

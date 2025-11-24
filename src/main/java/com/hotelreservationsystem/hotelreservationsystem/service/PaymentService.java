package com.hotelreservationsystem.hotelreservationsystem.service;

import com.hotelreservationsystem.hotelreservationsystem.model.Booking;
import com.hotelreservationsystem.hotelreservationsystem.model.Payment;
import com.hotelreservationsystem.hotelreservationsystem.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Save or update a payment
     */
    public Payment savePayment(Payment payment) {
        System.out.println("PaymentService: Saving payment for booking " + 
            (payment.getBooking() != null ? payment.getBooking().getBookingId() : "null"));
        return paymentRepository.save(payment);
    }

    /**
     * Get payment by ID
     */
    public Optional<Payment> getPaymentById(Long paymentId) {
        System.out.println("PaymentService: Fetching payment with ID " + paymentId);
        return paymentRepository.findById(paymentId);
    }

    /**
     * Get latest payment for a booking
     */
    public Payment getLatestPaymentByBooking(Booking booking) {
        System.out.println("PaymentService: Fetching latest payment for booking " + booking.getBookingId());
        List<Payment> payments = paymentRepository.findByBookingOrderByCreatedAtDesc(booking);
        return payments.isEmpty() ? null : payments.get(0);
    }

    /**
     * Get all payments for a booking
     */
    public List<Payment> getPaymentsByBooking(Booking booking) {
        System.out.println("PaymentService: Fetching all payments for booking " + booking.getBookingId());
        return paymentRepository.findByBookingOrderByCreatedAtDesc(booking);
    }

    /**
     * Get payment by transaction ID
     */
    public Payment getPaymentByTransactionId(String transactionId) {
        System.out.println("PaymentService: Fetching payment with transaction ID " + transactionId);
        Optional<Payment> payment = paymentRepository.findByTransactionId(transactionId);
        return payment.orElse(null);
    }

    /**
     * Delete payment (for admin use only)
     */
    public void deletePayment(Long paymentId) {
        System.out.println("PaymentService: Deleting payment with ID " + paymentId);
        paymentRepository.deleteById(paymentId);
    }

    /**
     * Get all payments
     */
    public List<Payment> getAllPayments() {
        System.out.println("PaymentService: Fetching all payments");
        return paymentRepository.findAll();
    }
}

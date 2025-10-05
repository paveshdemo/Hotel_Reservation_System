package com.hotelreservationsystem.hotelreservationsystem.service;

import com.hotelreservationsystem.hotelreservationsystem.model.*;
import com.hotelreservationsystem.hotelreservationsystem.repository.BookingRepository;
import com.hotelreservationsystem.hotelreservationsystem.repository.PaymentRepository;
import com.hotelreservationsystem.hotelreservationsystem.util.QRCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Base64;

@Service
@Transactional
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Create a new payment record
     */
    public Payment createPayment(Booking booking, PaymentMethod paymentMethod, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(amount);
        payment.setCurrency("LKR");
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }

    /**
     * Update payment status
     */
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        PaymentStatus previousStatus = payment.getPaymentStatus();
        payment.setPaymentStatus(status);
        if (PaymentStatus.COMPLETED.equals(status)) {
            payment.setCompletedAt(LocalDateTime.now());
        }

        Payment savedPayment = paymentRepository.save(payment);

        if (PaymentStatus.COMPLETED.equals(status)) {
            handleSuccessfulPayment(savedPayment, previousStatus);
        }

        return savedPayment;
    }

    /**
     * Get all payments for a booking
     */
    public List<Payment> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBooking_BookingId(bookingId);
    }

    /**
     * Get payment by reference
     */
    public Payment getPaymentByReference(String paymentReference) {
        return paymentRepository.findByTransactionId(paymentReference)
                .orElseThrow(() -> new RuntimeException("Payment not found with reference: " + paymentReference));
    }

    private void handleSuccessfulPayment(Payment payment, PaymentStatus previousStatus) {
        Booking booking = payment.getBooking();
        if (booking == null) {
            logger.warn("Completed payment {} does not have an associated booking", payment.getPaymentId());
            return;
        }

        booking.setPaymentStatus(PaymentStatus.COMPLETED);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        if (PaymentStatus.COMPLETED.equals(previousStatus)) {
            logger.debug("Payment {} was already completed. Skipping confirmation email.", payment.getPaymentId());
            return;
        }

        String customerEmail = extractCustomerEmail(booking);
        if (customerEmail == null || customerEmail.isBlank()) {
            logger.warn("No customer email found for booking {}. Skipping payment confirmation email.",
                    booking.getBookingReference());
            return;
        }

        String qrCodeBase64 = generateBookingQrCode(booking);
        emailService.sendBookingConfirmationWithQR(booking, customerEmail, qrCodeBase64);
    }

    private String extractCustomerEmail(Booking booking) {
        if (booking.getCustomer() == null || booking.getCustomer().getUser() == null) {
            return null;
        }
        return booking.getCustomer().getUser().getEmail();
    }

    private String generateBookingQrCode(Booking booking) {
        try {
            String qrData = buildQrCodeData(booking);
            byte[] qrBytes = QRCodeGenerator.generateQRCodeImage(qrData, 300, 300);
            return Base64.getEncoder().encodeToString(qrBytes);
        } catch (Exception e) {
            logger.error("Failed to generate QR code for booking {}", booking.getBookingReference(), e);
            return null;
        }
    }

    private String buildQrCodeData(Booking booking) {
        StringBuilder qrData = new StringBuilder();
        qrData.append("HOTEL BOOKING CONFIRMATION\n");
        qrData.append("Booking ID: ").append(booking.getBookingReference()).append("\n");
        if (booking.getCustomer() != null && booking.getCustomer().getUser() != null) {
            qrData.append("Customer: ")
                    .append(booking.getCustomer().getUser().getFirstName())
                    .append(" ")
                    .append(booking.getCustomer().getUser().getLastName())
                    .append("\n");
        }
        if (booking.getRoom() != null) {
            qrData.append("Room: ").append(booking.getRoom().getRoomNumber()).append("\n");
        }
        qrData.append("Check-in: ").append(booking.getCheckInDate()).append("\n");
        qrData.append("Check-out: ").append(booking.getCheckOutDate()).append("\n");
        qrData.append("Amount: LKR ").append(booking.getTotalAmount()).append("\n");
        qrData.append("Status: CONFIRMED");

        return qrData.toString();
    }
}

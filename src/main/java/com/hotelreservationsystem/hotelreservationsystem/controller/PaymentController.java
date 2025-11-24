package com.hotelreservationsystem.hotelreservationsystem.controller;

import com.hotelreservationsystem.hotelreservationsystem.model.Booking;
import com.hotelreservationsystem.hotelreservationsystem.model.BookingStatus;
import com.hotelreservationsystem.hotelreservationsystem.model.Payment;
import com.hotelreservationsystem.hotelreservationsystem.model.PaymentStatus;
import com.hotelreservationsystem.hotelreservationsystem.model.PaymentProof;
import com.hotelreservationsystem.hotelreservationsystem.service.BookingService;
import com.hotelreservationsystem.hotelreservationsystem.service.PaymentService;
import com.hotelreservationsystem.hotelreservationsystem.service.PaymentProofService;
import com.hotelreservationsystem.hotelreservationsystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentProofService paymentProofService;

    @Autowired
    private EmailService emailService;

    /**
     * Process payment for a booking
     */
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(
            @RequestBody Map<String, String> paymentRequest,
            Authentication authentication) {

        try {
            System.out.println("Payment: Processing payment request");
            
            Long bookingId = Long.parseLong(paymentRequest.get("bookingId"));
            String paymentMethod = paymentRequest.get("paymentMethod");

            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("Payment: User not authenticated");
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }

            String userEmail = authentication.getName();
            System.out.println("Payment: Processing payment for booking " + bookingId + " by user " + userEmail);

            // Fetch booking
            Booking booking = bookingService.getBookingEntity(bookingId);
            
            if (!booking.getCustomerEmail().equals(userEmail)) {
                System.out.println("Payment: Access denied - booking does not belong to user");
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            // Create payment record
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setAmount(booking.getTotalAmount());
            payment.setCurrency("LKR");

            // Handle different payment methods
            if ("CREDIT_CARD".equals(paymentMethod)) {
                payment.setPaymentMethod(com.hotelreservationsystem.hotelreservationsystem.model.PaymentMethod.CREDIT_CARD);
                
                // In a real scenario, integrate with payment gateway (Stripe, PayPal, etc.)
                // For now, we'll simulate successful payment
                payment.setPaymentStatus(PaymentStatus.COMPLETED);
                payment.setPaymentDate(LocalDateTime.now());
                payment.setCompletedAt(LocalDateTime.now());
                payment.setTransactionId("TXN-" + System.currentTimeMillis());
                payment.setPaymentProvider("Simulated");
                
                System.out.println("Payment: Credit card payment simulated");
                
            } else if ("BANK_TRANSFER".equals(paymentMethod)) {
                payment.setPaymentMethod(com.hotelreservationsystem.hotelreservationsystem.model.PaymentMethod.BANK_TRANSFER);
                payment.setPaymentStatus(PaymentStatus.PENDING);
                payment.setTransactionId("TRANSFER-" + System.currentTimeMillis());
                payment.setPaymentProvider("Bank");
                
                System.out.println("Payment: Bank transfer initiated");
                
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid payment method"));
            }

            // Save payment
            Payment savedPayment = paymentService.savePayment(payment);
            System.out.println("Payment: Payment record created with ID " + savedPayment.getPaymentId());

            // Update booking status based on payment status
            if (PaymentStatus.COMPLETED.equals(payment.getPaymentStatus())) {
                booking.setPaymentStatus(PaymentStatus.COMPLETED);
                booking.setBookingStatus(BookingStatus.CONFIRMED);
                System.out.println("Payment: Booking confirmed - payment received");
                
                // Send confirmation email
                try {
                    emailService.sendBookingConfirmation(booking, booking.getCustomerEmail());
                    System.out.println("Payment: Confirmation email sent");
                } catch (Exception e) {
                    System.out.println("Payment: Error sending confirmation email - " + e.getMessage());
                }
            } else if (PaymentStatus.PENDING.equals(payment.getPaymentStatus())) {
                booking.setPaymentStatus(PaymentStatus.PENDING);
                booking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
                System.out.println("Payment: Booking status set to pending payment");
            }

            bookingService.saveBooking(booking);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("paymentId", savedPayment.getPaymentId());
            response.put("bookingId", bookingId);
            response.put("paymentStatus", savedPayment.getPaymentStatus().toString());
            response.put("message", "Payment processed successfully");

            System.out.println("Payment: Processing complete");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Payment: Error processing payment - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Payment processing failed: " + e.getMessage()));
        }
    }

    /**
     * Get payment status for a booking
     */
    @GetMapping("/status/{bookingId}")
    public ResponseEntity<?> getPaymentStatus(
            @PathVariable Long bookingId,
            Authentication authentication) {

        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }

            String userEmail = authentication.getName();
            
            Booking booking = bookingService.getBookingEntity(bookingId);
            if (!booking.getCustomerEmail().equals(userEmail)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            // Get latest payment for this booking
            Payment payment = paymentService.getLatestPaymentByBooking(booking);
            
            if (payment == null) {
                return ResponseEntity.ok(Map.of(
                    "bookingId", bookingId,
                    "paymentStatus", "NOT_STARTED",
                    "message", "No payment found for this booking"
                ));
            }

            return ResponseEntity.ok(Map.of(
                "paymentId", payment.getPaymentId(),
                "bookingId", bookingId,
                "amount", payment.getAmount(),
                "paymentStatus", payment.getPaymentStatus().toString(),
                "paymentMethod", payment.getPaymentMethod().toString(),
                "transactionId", payment.getTransactionId(),
                "paymentDate", payment.getPaymentDate()
            ));

        } catch (Exception e) {
            System.out.println("Payment Status: Error - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error retrieving payment status"));
        }
    }

    /**
     * Validate bank transfer receipt
     */
    @PostMapping("/verify-bank-transfer")
    public ResponseEntity<?> verifyBankTransfer(
            @RequestBody Map<String, String> transferRequest,
            Authentication authentication) {

        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }

            String transactionId = transferRequest.get("transactionId");
            Long bookingId = Long.parseLong(transferRequest.get("bookingId"));

            System.out.println("Payment: Verifying bank transfer for transaction " + transactionId);

            Booking booking = bookingService.getBookingEntity(bookingId);
            Payment payment = paymentService.getPaymentByTransactionId(transactionId);

            if (payment == null || !payment.getBooking().getBookingId().equals(bookingId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Transfer record not found"));
            }

            // In a real scenario, verify with bank
            // For now, we'll update the payment status
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setCompletedAt(LocalDateTime.now());
            paymentService.savePayment(payment);

            booking.setPaymentStatus(PaymentStatus.COMPLETED);
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingService.saveBooking(booking);

            // Send confirmation email
            try {
                emailService.sendBookingConfirmation(booking, booking.getCustomerEmail());
            } catch (Exception e) {
                System.out.println("Payment: Error sending confirmation email - " + e.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Bank transfer verified successfully"
            ));

        } catch (Exception e) {
            System.out.println("Payment: Error verifying bank transfer - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Verification failed"));
        }
    }

    /**
     * Process bank transfer payment with receipt/proof upload
     */
    @PostMapping("/process-bank-transfer")
    public ResponseEntity<?> processBankTransfer(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam("receiptFile") MultipartFile receiptFile,
            Authentication authentication) {

        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("Payment: User not authenticated for bank transfer");
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }

            String userEmail = authentication.getName();
            System.out.println("Payment: Processing bank transfer for booking " + bookingId + " by user " + userEmail);

            // Validate file
            if (receiptFile == null || receiptFile.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Receipt file is required"));
            }

            // Fetch booking
            Booking booking = bookingService.getBookingEntity(bookingId);
            
            if (!booking.getCustomerEmail().equals(userEmail)) {
                System.out.println("Payment: Access denied - booking does not belong to user");
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            // Create payment record
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setAmount(booking.getTotalAmount());
            payment.setCurrency("LKR");
            payment.setPaymentMethod(com.hotelreservationsystem.hotelreservationsystem.model.PaymentMethod.BANK_TRANSFER);
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setTransactionId("TRANSFER-" + System.currentTimeMillis());
            payment.setPaymentProvider("Bank");
            payment.setPaymentDate(LocalDateTime.now());

            // Save payment
            Payment savedPayment = paymentService.savePayment(payment);
            System.out.println("Payment: Payment record created with ID " + savedPayment.getPaymentId());

            // Save payment proof (receipt/screenshot)
            try {
                PaymentProof proof = paymentProofService.savePaymentProof(savedPayment, booking, receiptFile);
                System.out.println("Payment: Payment proof saved with ID " + proof.getProofId());
            } catch (IllegalArgumentException e) {
                System.out.println("Payment: Invalid receipt file - " + e.getMessage());
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid receipt file: " + e.getMessage()));
            }

            // Update booking status to pending payment with proof
            booking.setPaymentStatus(PaymentStatus.PENDING);
            booking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
            bookingService.saveBooking(booking);

            System.out.println("Payment: Bank transfer initiated with proof upload");

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("paymentId", savedPayment.getPaymentId());
            response.put("bookingId", bookingId);
            response.put("paymentStatus", savedPayment.getPaymentStatus().toString());
            response.put("message", "Payment receipt uploaded successfully. Awaiting staff verification.");
            response.put("redirectUrl", "/payment-pending");

            System.out.println("Payment: Bank transfer processing complete");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Payment: Error processing bank transfer - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Payment processing failed: " + e.getMessage()));
        }
    }
}

package com.hotelreservationsystem.hotelreservationsystem.controller;

import com.hotelreservationsystem.hotelreservationsystem.model.*;
import com.hotelreservationsystem.hotelreservationsystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for admin payment proof verification
 */
@Controller
@RequestMapping("/admin/payments")
public class AdminPaymentProofController {
    
    @Autowired
    private PaymentProofService paymentProofService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Display pending payment proofs for verification
     */
    @GetMapping("/verify-proofs")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public String viewPendingProofs(Model model, Authentication authentication) {
        try {
            System.out.println("AdminPayment: Loading pending payment proofs");
            
            List<PaymentProof> pendingProofs = paymentProofService.getPendingProofs();
            model.addAttribute("pendingProofs", pendingProofs);
            model.addAttribute("totalPending", pendingProofs.size());
            
            System.out.println("AdminPayment: Found " + pendingProofs.size() + " pending proofs");
            return "admin/verify-payments";
            
        } catch (Exception e) {
            System.err.println("AdminPayment: Error loading pending proofs - " + e.getMessage());
            model.addAttribute("error", "Error loading payment proofs");
            return "admin/verify-payments";
        }
    }
    
    /**
     * View details of a specific payment proof
     */
    @GetMapping("/proof/{proofId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public String viewProofDetail(@PathVariable Long proofId, Model model) {
        try {
            System.out.println("AdminPayment: Loading proof details for ID " + proofId);
            
            Optional<PaymentProof> proofOptional = paymentProofService.getProofById(proofId);
            if (proofOptional.isEmpty()) {
                model.addAttribute("error", "Payment proof not found");
                return "admin/verify-payments";
            }
            
            PaymentProof proof = proofOptional.get();
            model.addAttribute("proof", proof);
            model.addAttribute("payment", proof.getPayment());
            model.addAttribute("booking", proof.getBooking());
            
            return "admin/proof-detail";
            
        } catch (Exception e) {
            System.err.println("AdminPayment: Error loading proof details - " + e.getMessage());
            model.addAttribute("error", "Error loading proof details");
            return "admin/verify-payments";
        }
    }
    
    /**
     * API endpoint to verify a payment proof
     */
    @PostMapping("/api/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @ResponseBody
    public ResponseEntity<?> verifyProof(
            @RequestParam Long proofId,
            @RequestParam String notes,
            Authentication authentication) {
        
        try {
            System.out.println("AdminPayment: Verifying payment proof ID " + proofId);
            
            // Get current user
            User currentUser = (User) userService.loadUserByUsername(authentication.getName());
            
            // Verify the proof
            PaymentProof verifiedProof = paymentProofService.verifyProof(proofId, currentUser, notes);
            
            // Update related payment status
            Payment payment = verifiedProof.getPayment();
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setCompletedAt(java.time.LocalDateTime.now());
            paymentService.savePayment(payment);
            
            // Update booking status
            Booking booking = verifiedProof.getBooking();
            booking.setPaymentStatus(PaymentStatus.COMPLETED);
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingService.saveBooking(booking);
            
            System.out.println("AdminPayment: Proof verified and booking confirmed");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment proof verified successfully and booking confirmed");
            response.put("proofStatus", verifiedProof.getProofStatus());
            response.put("bookingId", booking.getBookingId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("AdminPayment: Error verifying proof - " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Error verifying proof: " + e.getMessage()));
        }
    }
    
    /**
     * API endpoint to reject a payment proof
     */
    @PostMapping("/api/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @ResponseBody
    public ResponseEntity<?> rejectProof(
            @RequestParam Long proofId,
            @RequestParam String rejectionReason,
            @RequestParam(required = false) String notes,
            Authentication authentication) {
        
        try {
            System.out.println("AdminPayment: Rejecting payment proof ID " + proofId);
            
            // Get current user
            User currentUser = (User) userService.loadUserByUsername(authentication.getName());
            
            // Reject the proof
            PaymentProof rejectedProof = paymentProofService.rejectProof(
                proofId, 
                currentUser, 
                rejectionReason, 
                notes != null ? notes : ""
            );
            
            // Update payment and booking status to failed
            Payment payment = rejectedProof.getPayment();
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason(rejectionReason);
            paymentService.savePayment(payment);
            
            Booking booking = rejectedProof.getBooking();
            booking.setPaymentStatus(PaymentStatus.FAILED);
            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingService.saveBooking(booking);
            
            System.out.println("AdminPayment: Proof rejected and booking marked as failed");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment proof rejected successfully");
            response.put("proofStatus", rejectedProof.getProofStatus());
            response.put("bookingId", booking.getBookingId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("AdminPayment: Error rejecting proof - " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Error rejecting proof: " + e.getMessage()));
        }
    }
    
    /**
     * API endpoint to get payment proofs for a booking
     */
    @GetMapping("/api/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @ResponseBody
    public ResponseEntity<?> getProofsByBooking(@PathVariable Long bookingId) {
        
        try {
            System.out.println("AdminPayment: Fetching proofs for booking " + bookingId);
            
            Booking booking = bookingService.getBookingEntity(bookingId);
            List<PaymentProof> proofs = paymentProofService.getProofsByBooking(booking);
            
            Map<String, Object> response = new HashMap<>();
            response.put("bookingId", bookingId);
            response.put("proofs", proofs);
            response.put("count", proofs.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("AdminPayment: Error fetching proofs - " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Error fetching proofs: " + e.getMessage()));
        }
    }
}

package com.hotelreservationsystem.hotelreservationsystem.repository;

import com.hotelreservationsystem.hotelreservationsystem.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PaymentProof entity
 */
@Repository
public interface PaymentProofRepository extends JpaRepository<PaymentProof, Long> {
    
    /**
     * Find all proof records for a specific payment
     */
    List<PaymentProof> findByPayment(Payment payment);
    
    /**
     * Find all proof records for a specific booking
     */
    List<PaymentProof> findByBooking(Booking booking);
    
    /**
     * Find all payment proofs by status
     */
    List<PaymentProof> findByProofStatus(ProofStatus proofStatus);
    
    /**
     * Find all pending payment proofs ordered by creation date
     */
    List<PaymentProof> findByProofStatusOrderByCreatedAtAsc(ProofStatus proofStatus);
    
    /**
     * Find the latest proof for a payment
     */
    Optional<PaymentProof> findFirstByPaymentOrderByCreatedAtDesc(Payment payment);
    
    /**
     * Find the latest proof for a booking
     */
    Optional<PaymentProof> findFirstByBookingOrderByCreatedAtDesc(Booking booking);
    
    /**
     * Check if a booking has any verified payment proof
     */
    boolean existsByBookingAndProofStatus(Booking booking, ProofStatus proofStatus);
}

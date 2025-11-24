package com.hotelreservationsystem.hotelreservationsystem.service;

import com.hotelreservationsystem.hotelreservationsystem.model.*;
import com.hotelreservationsystem.hotelreservationsystem.repository.PaymentProofRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service to handle payment proof operations
 */
@Service
@Transactional
public class PaymentProofService {
    
    @Autowired
    private PaymentProofRepository paymentProofRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    /**
     * Save payment proof from uploaded file
     */
    public PaymentProof savePaymentProof(Payment payment, Booking booking, MultipartFile file) 
            throws IOException, IllegalArgumentException {
        
        // Store the file
        String filePath = fileStorageService.saveFile(file);
        
        // Create payment proof record
        PaymentProof proof = new PaymentProof(
            payment,
            booking,
            file.getOriginalFilename(),
            filePath,
            file.getContentType(),
            file.getSize()
        );
        
        // Save to database
        PaymentProof savedProof = paymentProofRepository.save(proof);
        System.out.println("PaymentProofService: Payment proof saved with ID " + savedProof.getProofId());
        
        return savedProof;
    }
    
    /**
     * Get payment proof by ID
     */
    public Optional<PaymentProof> getProofById(Long proofId) {
        return paymentProofRepository.findById(proofId);
    }
    
    /**
     * Get all proofs for a payment
     */
    public List<PaymentProof> getProofsByPayment(Payment payment) {
        return paymentProofRepository.findByPayment(payment);
    }
    
    /**
     * Get all proofs for a booking
     */
    public List<PaymentProof> getProofsByBooking(Booking booking) {
        return paymentProofRepository.findByBooking(booking);
    }
    
    /**
     * Get latest proof for a booking
     */
    public Optional<PaymentProof> getLatestProofByBooking(Booking booking) {
        return paymentProofRepository.findFirstByBookingOrderByCreatedAtDesc(booking);
    }
    
    /**
     * Get latest proof for a payment
     */
    public Optional<PaymentProof> getLatestProofByPayment(Payment payment) {
        return paymentProofRepository.findFirstByPaymentOrderByCreatedAtDesc(payment);
    }
    
    /**
     * Get all pending verification proofs
     */
    public List<PaymentProof> getPendingProofs() {
        return paymentProofRepository.findByProofStatusOrderByCreatedAtAsc(ProofStatus.PENDING_VERIFICATION);
    }
    
    /**
     * Get all proofs by status
     */
    public List<PaymentProof> getProofsByStatus(ProofStatus status) {
        return paymentProofRepository.findByProofStatus(status);
    }
    
    /**
     * Verify a payment proof
     */
    public PaymentProof verifyProof(Long proofId, User verifiedBy, String verificationNotes) 
            throws IllegalArgumentException {
        
        Optional<PaymentProof> proofOptional = paymentProofRepository.findById(proofId);
        if (proofOptional.isEmpty()) {
            throw new IllegalArgumentException("Payment proof not found with ID: " + proofId);
        }
        
        PaymentProof proof = proofOptional.get();
        proof.setProofStatus(ProofStatus.VERIFIED);
        proof.setVerifiedByUser(verifiedBy);
        proof.setVerifiedAt(LocalDateTime.now());
        proof.setVerificationNotes(verificationNotes);
        
        PaymentProof savedProof = paymentProofRepository.save(proof);
        System.out.println("PaymentProofService: Proof verified - ID " + proofId);
        
        return savedProof;
    }
    
    /**
     * Reject a payment proof
     */
    public PaymentProof rejectProof(Long proofId, User rejectedBy, String rejectionReason, String notes) 
            throws IllegalArgumentException {
        
        Optional<PaymentProof> proofOptional = paymentProofRepository.findById(proofId);
        if (proofOptional.isEmpty()) {
            throw new IllegalArgumentException("Payment proof not found with ID: " + proofId);
        }
        
        PaymentProof proof = proofOptional.get();
        proof.setProofStatus(ProofStatus.REJECTED);
        proof.setVerifiedByUser(rejectedBy);
        proof.setVerifiedAt(LocalDateTime.now());
        proof.setRejectionReason(rejectionReason);
        proof.setVerificationNotes(notes);
        
        // Try to delete the stored file
        try {
            fileStorageService.deleteFile(proof.getFilePath());
        } catch (IOException e) {
            System.err.println("Error deleting file: " + e.getMessage());
        }
        
        PaymentProof savedProof = paymentProofRepository.save(proof);
        System.out.println("PaymentProofService: Proof rejected - ID " + proofId);
        
        return savedProof;
    }
    
    /**
     * Check if booking has verified payment proof
     */
    public boolean hasVerifiedProof(Booking booking) {
        return paymentProofRepository.existsByBookingAndProofStatus(booking, ProofStatus.VERIFIED);
    }
    
    /**
     * Delete a payment proof
     */
    public void deleteProof(Long proofId) throws IOException {
        Optional<PaymentProof> proofOptional = paymentProofRepository.findById(proofId);
        if (proofOptional.isPresent()) {
            PaymentProof proof = proofOptional.get();
            
            // Delete the file from storage
            try {
                fileStorageService.deleteFile(proof.getFilePath());
            } catch (IOException e) {
                System.err.println("Error deleting file: " + e.getMessage());
            }
            
            // Delete from database
            paymentProofRepository.deleteById(proofId);
            System.out.println("PaymentProofService: Proof deleted - ID " + proofId);
        }
    }
}

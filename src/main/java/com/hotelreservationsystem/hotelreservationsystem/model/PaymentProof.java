package com.hotelreservationsystem.hotelreservationsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * PaymentProof entity to store and track uploaded payment proof documents
 * for bank transfer and other manual payment methods
 */
@Entity
@Table(name = "payment_proofs")
public class PaymentProof {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proof_id")
    private Long proofId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    @NotNull(message = "Payment is required")
    private Payment payment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @NotNull(message = "Booking is required")
    private Booking booking;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "file_type", nullable = false)
    private String fileType; // e.g., image/jpeg, application/pdf
    
    @Column(name = "file_size")
    private Long fileSize; // in bytes
    
    @Enumerated(EnumType.STRING)
    @Column(name = "proof_status", nullable = false)
    private ProofStatus proofStatus = ProofStatus.PENDING_VERIFICATION;
    
    @Column(name = "verification_notes", length = 1000)
    private String verificationNotes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_user_id")
    private User verifiedByUser;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public PaymentProof() {
    }
    
    public PaymentProof(Payment payment, Booking booking, String fileName, String filePath, 
                        String fileType, Long fileSize) {
        this.payment = payment;
        this.booking = booking;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.proofStatus = ProofStatus.PENDING_VERIFICATION;
    }
    
    // Getters and Setters
    public Long getProofId() {
        return proofId;
    }
    
    public void setProofId(Long proofId) {
        this.proofId = proofId;
    }
    
    public Payment getPayment() {
        return payment;
    }
    
    public void setPayment(Payment payment) {
        this.payment = payment;
    }
    
    public Booking getBooking() {
        return booking;
    }
    
    public void setBooking(Booking booking) {
        this.booking = booking;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public ProofStatus getProofStatus() {
        return proofStatus;
    }
    
    public void setProofStatus(ProofStatus proofStatus) {
        this.proofStatus = proofStatus;
    }
    
    public String getVerificationNotes() {
        return verificationNotes;
    }
    
    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }
    
    public User getVerifiedByUser() {
        return verifiedByUser;
    }
    
    public void setVerifiedByUser(User verifiedByUser) {
        this.verifiedByUser = verifiedByUser;
    }
    
    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }
    
    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Utility methods
    public boolean isVerified() {
        return proofStatus == ProofStatus.VERIFIED;
    }
    
    public boolean isPendingVerification() {
        return proofStatus == ProofStatus.PENDING_VERIFICATION;
    }
    
    public boolean isRejected() {
        return proofStatus == ProofStatus.REJECTED;
    }
    
    @Override
    public String toString() {
        return "PaymentProof{" +
                "proofId=" + proofId +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", proofStatus=" + proofStatus +
                ", createdAt=" + createdAt +
                '}';
    }
}

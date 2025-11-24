-- Payment Proof Table Migration
-- This table stores uploaded payment proof documents for manual payment methods

CREATE TABLE IF NOT EXISTS payment_proofs (
    proof_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    booking_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT,
    proof_status VARCHAR(50) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    verification_notes TEXT,
    verified_by_user_id BIGINT,
    verified_at DATETIME,
    rejection_reason VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    CONSTRAINT fk_payment_proofs_payment 
        FOREIGN KEY (payment_id) REFERENCES payments(payment_id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_proofs_booking 
        FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_proofs_user 
        FOREIGN KEY (verified_by_user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    
    -- Indexes for common queries
    INDEX idx_payment_proofs_payment (payment_id),
    INDEX idx_payment_proofs_booking (booking_id),
    INDEX idx_payment_proofs_status (proof_status),
    INDEX idx_payment_proofs_verified_by (verified_by_user_id),
    INDEX idx_payment_proofs_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

package com.hotelreservationsystem.hotelreservationsystem.model;

/**
 * Enum for payment proof verification status
 */
public enum ProofStatus {
    PENDING_VERIFICATION("Awaiting Verification"),
    VERIFIED("Payment Verified"),
    REJECTED("Proof Rejected");
    
    private final String description;
    
    ProofStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

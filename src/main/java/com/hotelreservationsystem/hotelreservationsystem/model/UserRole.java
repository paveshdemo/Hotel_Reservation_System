package com.hotelreservationsystem.hotelreservationsystem.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum UserRole {
    ADMIN,
    STAFF,
    RECEPTIONIST,
    ACCOUNTANT,
    MARKETING,
    HOUSEKEEPING,
    CUSTOMER;

    private static final Set<UserRole> STAFF_ROLES = Collections.unmodifiableSet(EnumSet.of(
            RECEPTIONIST,
            ACCOUNTANT,
            MARKETING,
            HOUSEKEEPING
    ));

    public boolean isStaffRole() {
        return STAFF_ROLES.contains(this);
    }

    public static Set<UserRole> getAssignableStaffRoles() {
        return STAFF_ROLES;
    }

    public String getDisplayName() {
        return switch (this) {
            case ADMIN -> "Admin";
            case STAFF -> "Staff";
            case RECEPTIONIST -> "Receptionist";
            case ACCOUNTANT -> "Accountant";
            case MARKETING -> "Marketing";
            case HOUSEKEEPING -> "Housekeeping";
            case CUSTOMER -> "Customer";
        };
    }
}

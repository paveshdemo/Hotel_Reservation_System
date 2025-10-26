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

    private static final Set<UserRole> ASSIGNABLE_ROLES = Collections.unmodifiableSet(EnumSet.of(
            ADMIN,
            RECEPTIONIST,
            ACCOUNTANT,
            MARKETING,
            HOUSEKEEPING
    ));

    public boolean isStaffRole() {
        return STAFF_ROLES.contains(this);
    }

    public static Set<UserRole> getAssignableStaffRoles() {
        return ASSIGNABLE_ROLES;
    }

    public String getDisplayName() {
        switch (this) {
            case ADMIN:
                return "Admin";
            case STAFF:
                return "Staff";
            case RECEPTIONIST:
                return "Receptionist";
            case ACCOUNTANT:
                return "Accountant";
            case MARKETING:
                return "Marketing";
            case HOUSEKEEPING:
                return "Housekeeping";
            case CUSTOMER:
                return "Customer";
            default:
                throw new IllegalStateException("Unexpected role: " + this);
        }
    }
}

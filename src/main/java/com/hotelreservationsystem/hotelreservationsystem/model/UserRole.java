package com.hotelreservationsystem.hotelreservationsystem.model;

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

    private static final EnumSet<UserRole> STAFF_ROLES = EnumSet.of(
            ADMIN,
            STAFF,
            RECEPTIONIST,
            ACCOUNTANT,
            MARKETING,
            HOUSEKEEPING
    );

    public boolean isStaffRole() {
        return STAFF_ROLES.contains(this);
    }

    public static Set<UserRole> getStaffRoles() {
        return EnumSet.copyOf(STAFF_ROLES);
    }

    public static Set<UserRole> getAssignableStaffRoles() {
        EnumSet<UserRole> assignableRoles = EnumSet.copyOf(STAFF_ROLES);
        assignableRoles.remove(ADMIN);
        return assignableRoles;
    }
}

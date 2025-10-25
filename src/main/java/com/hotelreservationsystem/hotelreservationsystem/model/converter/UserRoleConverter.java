package com.hotelreservationsystem.hotelreservationsystem.model.converter;

import com.hotelreservationsystem.hotelreservationsystem.model.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleConverter.class);

    @Override
    public String convertToDatabaseColumn(UserRole role) {
        return role != null ? role.name() : null;
    }

    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return UserRole.CUSTOMER;
        }

        String normalized = dbData.trim().toUpperCase();
        if (normalized.isEmpty()) {
            return UserRole.CUSTOMER;
        }

        try {
            return UserRole.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            LOGGER.warn("Unknown user role '{}' found in database, defaulting to CUSTOMER", dbData);
            return UserRole.CUSTOMER;
        }
    }
}

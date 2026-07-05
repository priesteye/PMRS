// src/main/java/com/pmrs/service/ValidationService.java
package com.pmrs.service;

import com.pmrs.exception.ValidationException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Centralizes cross-field or complex business logic validation.
 * Controllers use this to validate inputs before attempting to construct Domain models.
 */
public class ValidationService {

    /**
     * Validates that a string is neither null nor purely whitespace.
     * * @param input The string to check.
     * @param fieldName The name of the field for the error message.
     * @throws ValidationException if the string is invalid.
     */
    public void validateRequiredString(String input, String fieldName) throws ValidationException {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is a required field.");
        }
    }

    /**
     * Validates a date range, ensuring the start date is before or equal to the end date.
     * * @param start The start date.
     * @param end The end date.
     * @throws ValidationException if the range is invalid.
     */
    public void validateDateRange(LocalDate start, LocalDate end) throws ValidationException {
        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidationException("Start date cannot be after end date.");
        }
    }

    /**
     * Ensures an appointment time falls within typical clinic operating hours (e.g., 8 AM - 6 PM).
     * * @param dateTime The scheduled date and time.
     * @throws ValidationException if the time is outside operating hours.
     */
    public void validateOperatingHours(LocalDateTime dateTime) throws ValidationException {
        if (dateTime == null) throw new ValidationException("Date and time must be provided.");
        int hour = dateTime.getHour();
        if (hour < 8 || hour >= 18) {
            throw new ValidationException("Appointments must be scheduled between 08:00 and 18:00.");
        }
    }
}
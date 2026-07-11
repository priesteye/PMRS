// src/test/java/com/pmrs/service/ValidationServiceTest.java
package com.pmrs.service;

import com.pmrs.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ValidationService}.
 *
 * Closes a documented gap from the PMRS technical review: validateOperatingHours()
 * was implemented and wired into AppointmentSchedulerController but had no direct
 * automated test exercising it. This class also covers validateRequiredString()
 * and validateDateRange(), which previously had no dedicated test class either.
 */
class ValidationServiceTest {

    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
    }

    // --- validateRequiredString ---

    @Test
    void testValidateRequiredString_null_throws() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validationService.validateRequiredString(null, "First name"));
        assertTrue(ex.getMessage().contains("First name"));
    }

    @Test
    void testValidateRequiredString_blank_throws() {
        assertThrows(ValidationException.class,
                () -> validationService.validateRequiredString("   ", "Last name"));
    }

    @Test
    void testValidateRequiredString_valid_doesNotThrow() {
        assertDoesNotThrow(() -> validationService.validateRequiredString("Kubi", "First name"));
    }

    // --- validateDateRange ---

    @Test
    void testValidateDateRange_startAfterEnd_throws() {
        LocalDate start = LocalDate.of(2026, 6, 1);
        LocalDate end = LocalDate.of(2026, 1, 1);
        assertThrows(ValidationException.class,
                () -> validationService.validateDateRange(start, end));
    }

    @Test
    void testValidateDateRange_startBeforeEnd_doesNotThrow() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 6, 1);
        assertDoesNotThrow(() -> validationService.validateDateRange(start, end));
    }

    @Test
    void testValidateDateRange_nullEndpoints_doesNotThrow() {
        // Method is documented as only validating when both dates are present.
        assertDoesNotThrow(() -> validationService.validateDateRange(null, null));
        assertDoesNotThrow(() -> validationService.validateDateRange(LocalDate.now(), null));
        assertDoesNotThrow(() -> validationService.validateDateRange(null, LocalDate.now()));
    }

    // --- validateOperatingHours (the reported gap) ---

    @Test
    void testValidateOperatingHours_null_throws() {
        assertThrows(ValidationException.class,
                () -> validationService.validateOperatingHours(null));
    }

    @Test
    void testValidateOperatingHours_beforeOpening_throws() {
        LocalDateTime sevenAm = LocalDateTime.of(2026, 7, 13, 7, 59);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validationService.validateOperatingHours(sevenAm));
        assertTrue(ex.getMessage().contains("08:00"));
    }

    @Test
    void testValidateOperatingHours_atOpening_doesNotThrow() {
        LocalDateTime eightAmSharp = LocalDateTime.of(2026, 7, 13, 8, 0);
        assertDoesNotThrow(() -> validationService.validateOperatingHours(eightAmSharp));
    }

    @Test
    void testValidateOperatingHours_lastMinuteBeforeClose_doesNotThrow() {
        LocalDateTime fiveFiftyNinePm = LocalDateTime.of(2026, 7, 13, 17, 59);
        assertDoesNotThrow(() -> validationService.validateOperatingHours(fiveFiftyNinePm));
    }

    @Test
    void testValidateOperatingHours_atClosing_throws() {
        // Boundary is exclusive: hour >= 18 is rejected, so 18:00 itself is out of hours.
        LocalDateTime sixPmSharp = LocalDateTime.of(2026, 7, 13, 18, 0);
        assertThrows(ValidationException.class,
                () -> validationService.validateOperatingHours(sixPmSharp));
    }

    @Test
    void testValidateOperatingHours_lateNight_throws() {
        LocalDateTime elevenPm = LocalDateTime.of(2026, 7, 13, 23, 0);
        assertThrows(ValidationException.class,
                () -> validationService.validateOperatingHours(elevenPm));
    }
}
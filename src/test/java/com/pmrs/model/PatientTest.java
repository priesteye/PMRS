// src/test/java/com/pmrs/model/PatientTest.java
package com.pmrs.model;

import com.pmrs.exception.ValidationException;
import com.pmrs.model.enums.BloodType;
import com.pmrs.model.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link Patient}, exercising validation inherited from {@link Person}
 * as well as Patient-specific state and defensive-copy behaviour.
 *
 * Closes a documented gap from the PMRS technical review: no model-layer test class
 * previously existed, despite this being part of the original test plan.
 */
class PatientTest {

    private Address validAddress;

    @BeforeEach
    void setUp() throws ValidationException {
        validAddress = new Address("12 Clinic Road", "Kumasi", "Ashanti", "00233");
    }

    private Patient buildValidPatient() throws ValidationException {
        return new Patient("PT-TEST01", "Ama", "Boateng", LocalDate.of(1995, 4, 12),
                Gender.FEMALE, "555-0100", validAddress, BloodType.O_POS, "Kojo Boateng");
    }

    // --- Construction / happy path ---

    @Test
    void testValidPatient_constructsSuccessfully() {
        assertDoesNotThrow(this::buildValidPatient);
    }

    @Test
    void testValidPatient_fieldsAreStoredCorrectly() throws ValidationException {
        Patient patient = buildValidPatient();
        assertEquals("PT-TEST01", patient.getId());
        assertEquals("Ama", patient.getFirstName());
        assertEquals("Boateng", patient.getLastName());
        assertEquals(BloodType.O_POS, patient.getBloodType());
        assertEquals("Kojo Boateng", patient.getEmergencyContact());
        assertEquals("Clinic Patient", patient.getRoleDescription());
    }

    // --- Person-level validation, exercised through Patient ---

    @Test
    void testDateOfBirth_inFuture_throws() {
        LocalDate future = LocalDate.now().plusDays(1);
        assertThrows(ValidationException.class, () ->
                new Patient("PT-TEST02", "Ama", "Boateng", future,
                        Gender.FEMALE, "555-0100", validAddress, BloodType.O_POS, "Kojo Boateng"));
    }

    @Test
    void testDateOfBirth_null_throws() {
        assertThrows(ValidationException.class, () ->
                new Patient("PT-TEST03", "Ama", "Boateng", null,
                        Gender.FEMALE, "555-0100", validAddress, BloodType.O_POS, "Kojo Boateng"));
    }

    @Test
    void testFirstName_blank_throws() {
        assertThrows(ValidationException.class, () ->
                new Patient("PT-TEST04", "  ", "Boateng", LocalDate.of(1995, 4, 12),
                        Gender.FEMALE, "555-0100", validAddress, BloodType.O_POS, "Kojo Boateng"));
    }

    @Test
    void testContactNumber_malformed_throws() {
        assertThrows(ValidationException.class, () ->
                new Patient("PT-TEST05", "Ama", "Boateng", LocalDate.of(1995, 4, 12),
                        Gender.FEMALE, "not-a-phone-number", validAddress, BloodType.O_POS, "Kojo Boateng"));
    }

    // --- Patient-specific validation ---

    @Test
    void testBloodType_null_throws() {
        assertThrows(ValidationException.class, () ->
                new Patient("PT-TEST06", "Ama", "Boateng", LocalDate.of(1995, 4, 12),
                        Gender.FEMALE, "555-0100", validAddress, null, "Kojo Boateng"));
    }

    @Test
    void testEmergencyContact_blank_throws() {
        assertThrows(ValidationException.class, () ->
                new Patient("PT-TEST07", "Ama", "Boateng", LocalDate.of(1995, 4, 12),
                        Gender.FEMALE, "555-0100", validAddress, BloodType.O_POS, "   "));
    }

    // --- Defensive copying (encapsulation) ---

    @Test
    void testGetAddress_returnsDefensiveCopy_notLiveReference() throws ValidationException {
        Patient patient = buildValidPatient();
        Address firstRead = patient.getAddress();
        Address secondRead = patient.getAddress();

        assertNotSame(firstRead, secondRead, "getAddress() must return a fresh copy each call.");
        assertEquals(firstRead.getCity(), secondRead.getCity());
    }

    @Test
    void testGetAllergies_mutatingReturnedList_doesNotAffectInternalState() throws ValidationException {
        Patient patient = buildValidPatient();
        List<String> allergies = new ArrayList<>();
        allergies.add("Penicillin");
        patient.setAllergies(allergies);

        List<String> returned = patient.getAllergies();
        returned.add("Latex"); // mutate the copy the caller received

        assertEquals(1, patient.getAllergies().size(),
                "Mutating the list returned by getAllergies() must not affect the Patient's internal state.");
    }

    @Test
    void testGetRecords_mutatingReturnedList_doesNotAffectInternalState() throws ValidationException {
        Patient patient = buildValidPatient();
        List<MedicalRecord> returned = patient.getRecords();
        int originalSize = returned.size();

        // Attempt to mutate the returned list directly; internal state must be unaffected
        // since getRecords() is documented to return a defensive copy.
        assertDoesNotThrow(() -> returned.clear());
        assertEquals(originalSize, patient.getRecords().size());
    }

    @Test
    void testSetAllergies_null_throws() throws ValidationException {
        Patient patient = buildValidPatient();
        assertThrows(ValidationException.class, () -> patient.setAllergies(null));
    }
}
// src/test/java/com/pmrs/service/PatientServiceTest.java
package com.pmrs.service;

import com.pmrs.exception.PMRSException;
import com.pmrs.model.Address;
import com.pmrs.model.Patient;
import com.pmrs.model.enums.BloodType;
import com.pmrs.model.enums.Gender;
import com.pmrs.repository.InMemoryPatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatientServiceTest {

    private PatientService patientService;

    @BeforeEach
    void setUp() throws PMRSException {
        InMemoryPatientRepository repo = new InMemoryPatientRepository();
        patientService = new PatientService(repo);

        Address dummyAddress = new Address("123 Main", "City", "State", "12345");

        Patient p1 = new Patient("PT-001", "John", "Doe", LocalDate.of(1980, 1, 1),
                Gender.MALE, "555-0100", dummyAddress, BloodType.O_POS, "Jane Doe");
        Patient p2 = new Patient("PT-002", "Alice", "Smith", LocalDate.of(1990, 5, 15),
                Gender.FEMALE, "555-0200", dummyAddress, BloodType.A_NEG, "Bob Smith");
        Patient p3 = new Patient("PT-003", "John", "Connor", LocalDate.of(1985, 8, 20),
                Gender.MALE, "555-0300", dummyAddress, BloodType.O_POS, "Sarah Connor");

        patientService.registerPatient(p1);
        patientService.registerPatient(p2);
        patientService.registerPatient(p3);
    }

    @Test
    void testSearchByTextQuery() {
        // "John" should match PT-001 (John Doe) and PT-003 (John Connor)
        List<Patient> results = patientService.search("john");
        assertEquals(2, results.size());
    }

    @Test
    void testSearchByTextAndBloodType() {
        // "John" + O_POS should return 2.
        List<Patient> results1 = patientService.search("john", BloodType.O_POS);
        assertEquals(2, results1.size());

        // "John" + A_NEG should return 0.
        List<Patient> results2 = patientService.search("john", BloodType.A_NEG);
        assertEquals(0, results2.size());
    }

    @Test
    void testSearchByDateRange() {
        // Between 1982 and 1995 should only catch Alice (1990) and John Connor (1985)
        LocalDate from = LocalDate.of(1982, 1, 1);
        LocalDate to = LocalDate.of(1995, 12, 31);

        List<Patient> results = patientService.search(from, to);
        assertEquals(2, results.size());
    }
}
// src/test/java/com/pmrs/service/AppointmentServiceTest.java
package com.pmrs.service;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.ValidationException;
import com.pmrs.model.Address;
import com.pmrs.model.Appointment;
import com.pmrs.model.Patient;
import com.pmrs.model.enums.AppointmentStatus;
import com.pmrs.model.enums.BloodType;
import com.pmrs.model.enums.Gender;
import com.pmrs.repository.InMemoryAppointmentRepository;
import com.pmrs.repository.InMemoryPatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppointmentServiceTest {

    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() throws PMRSException {
        InMemoryAppointmentRepository apptRepo = new InMemoryAppointmentRepository();
        InMemoryPatientRepository patientRepo = new InMemoryPatientRepository();

        appointmentService = new AppointmentService(apptRepo, patientRepo);

        // Seed a patient so the service can append appointments to their history
        Address dummyAddress = new Address("123 Main", "City", "State", "12345");
        Patient patient = new Patient("PT-100", "Mary", "Jane", LocalDate.of(1995, 2, 2),
                Gender.FEMALE, "555-9999", dummyAddress, BloodType.AB_POS, "Peter Parker");
        patientRepo.add(patient);
    }

    @Test
    void testDoubleBookingIsRejected() throws PMRSException {
        // Arrange
        LocalDateTime sharedTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);

        Appointment a1 = new Appointment("AP-01", "PT-100", "DOC-01", sharedTime,
                AppointmentStatus.SCHEDULED, "Routine Checkup");

        Appointment a2 = new Appointment("AP-02", "PT-100", "DOC-01", sharedTime,
                AppointmentStatus.SCHEDULED, "Follow up");

        // Act - Schedule first appointment (should succeed)
        appointmentService.scheduleAppointment(a1);

        // Assert - Scheduling the second appointment with the same time/physician throws ValidationException
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            appointmentService.scheduleAppointment(a2);
        });

        // Verify the exception message is clear per requirements
        assertTrue(exception.getMessage().contains("Double-booking rejected"));
    }
}
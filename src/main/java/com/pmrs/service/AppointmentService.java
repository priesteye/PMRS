// src/main/java/com/pmrs/service/AppointmentService.java
package com.pmrs.service;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.ValidationException;
import com.pmrs.model.Appointment;
import com.pmrs.model.Patient;
import com.pmrs.model.enums.AppointmentStatus;
import com.pmrs.repository.Repository;

import java.util.List;

/**
 * Handles business logic for scheduling and managing appointments.
 */
public class AppointmentService {

    private final Repository<Appointment> appointmentRepository;
    private final Repository<Patient> patientRepository;

    public AppointmentService(Repository<Appointment> appointmentRepository, Repository<Patient> patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Schedules a new appointment, strictly enforcing a no double-booking rule.
     * Also links the appointment to the patient's record.
     * * @param appointment The newly requested appointment.
     * @throws PMRSException if double-booked or a repository error occurs.
     */
    public void scheduleAppointment(Appointment appointment) throws PMRSException {
        // 1. Strict Double-Booking Check
        for (Appointment existing : appointmentRepository.findAll()) {
            if (existing.getPhysicianId().equals(appointment.getPhysicianId()) &&
                    existing.getDateTime().equals(appointment.getDateTime()) &&
                    existing.getStatus() != AppointmentStatus.CANCELLED) {

                throw new ValidationException(
                        "Double-booking rejected: Physician " + appointment.getPhysicianId() +
                                " is already scheduled for an appointment at " + appointment.getDateTime()
                );
            }
        }

        // 2. Persist Appointment
        appointmentRepository.add(appointment);

        // 3. Append to Patient's list (System integrity)
        Patient patient = patientRepository.findById(appointment.getPatientId());
        List<Appointment> pAppts = patient.getAppointments();
        pAppts.add(appointment);
        patient.setAppointments(pAppts);
        patientRepository.update(patient);
    }

    /**
     * Updates an appointment status (e.g. to CANCELLED).
     */
    public void updateStatus(String appointmentId, AppointmentStatus newStatus) throws PMRSException {
        Appointment appt = appointmentRepository.findById(appointmentId);
        appt.setStatus(newStatus);
        appointmentRepository.update(appt);
    }
}
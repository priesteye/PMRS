// src/main/java/com/pmrs/model/Appointment.java
package com.pmrs.model;

import com.pmrs.exception.ValidationException;
import com.pmrs.model.enums.AppointmentStatus;
import java.time.LocalDateTime;

/**
 * Represents a scheduled block of time between a physician and a patient.
 */
public class Appointment {
    private String id;
    private String patientId;
    private String physicianId;
    private LocalDateTime dateTime;
    private AppointmentStatus status;
    private String reasonForVisit;

    public Appointment(String id, String patientId, String physicianId, LocalDateTime dateTime,
                       AppointmentStatus status, String reasonForVisit) throws ValidationException {
        setId(id);
        setPatientId(patientId);
        setPhysicianId(physicianId);
        setDateTime(dateTime);
        setStatus(status);
        setReasonForVisit(reasonForVisit);
    }

    public String getId() { return id; }

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) throw new ValidationException("Appointment ID cannot be empty.");
        this.id = id;
    }

    public String getPatientId() { return patientId; }

    public void setPatientId(String patientId) throws ValidationException {
        if (patientId == null || patientId.trim().isEmpty()) throw new ValidationException("Patient ID cannot be empty.");
        this.patientId = patientId;
    }

    public String getPhysicianId() { return physicianId; }

    public void setPhysicianId(String physicianId) throws ValidationException {
        if (physicianId == null || physicianId.trim().isEmpty()) throw new ValidationException("Physician ID cannot be empty.");
        this.physicianId = physicianId;
    }

    public LocalDateTime getDateTime() { return dateTime; }

    public void setDateTime(LocalDateTime dateTime) throws ValidationException {
        if (dateTime == null) throw new ValidationException("Date and time cannot be null.");
        if (dateTime.isBefore(LocalDateTime.now())) {
            // Note: In a real system, you might allow historical entry, but for a strict scheduler:
            throw new ValidationException("New appointments cannot be scheduled in the past.");
        }
        this.dateTime = dateTime;
    }

    public AppointmentStatus getStatus() { return status; }

    public void setStatus(AppointmentStatus status) throws ValidationException {
        if (status == null) throw new ValidationException("Appointment status cannot be null.");
        this.status = status;
    }

    public String getReasonForVisit() { return reasonForVisit; }

    public void setReasonForVisit(String reasonForVisit) throws ValidationException {
        if (reasonForVisit == null || reasonForVisit.trim().isEmpty()) {
            throw new ValidationException("Reason for visit cannot be empty.");
        }
        this.reasonForVisit = reasonForVisit;
    }

}
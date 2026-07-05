// src/main/java/com/pmrs/model/Patient.java
package com.pmrs.model;

import com.pmrs.exception.ValidationException;
import com.pmrs.model.enums.BloodType;
import com.pmrs.model.enums.Gender;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a patient receiving care in the clinic.
 */
public class Patient extends Person {
    private BloodType bloodType;
    private List<String> allergies;
    private String emergencyContact;
    private List<MedicalRecord> records;
    private List<Appointment> appointments;

    public Patient(String id, String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                   String contactNumber, Address address, BloodType bloodType, String emergencyContact) throws ValidationException {
        super(id, firstName, lastName, dateOfBirth, gender, contactNumber, address);
        setBloodType(bloodType);
        setEmergencyContact(emergencyContact);
        this.allergies = new ArrayList<>();
        this.records = new ArrayList<>();
        this.appointments = new ArrayList<>();
    }

    @Override
    public String getRoleDescription() {
        return "Clinic Patient";
    }

    @Override
    public String getDashboardView() {
        return "/com/pmrs/view/patient-detail.fxml"; // Fallback, though patients don't log in per v1 spec
    }

    public BloodType getBloodType() { return bloodType; }

    public void setBloodType(BloodType bloodType) throws ValidationException {
        if (bloodType == null) {
            throw new ValidationException("Blood type cannot be null.");
        }
        this.bloodType = bloodType;
    }

    public List<String> getAllergies() {
        return new ArrayList<>(allergies);
    }

    public void setAllergies(List<String> allergies) throws ValidationException {
        if (allergies == null) throw new ValidationException("Allergies list cannot be null.");
        this.allergies = new ArrayList<>(allergies);
    }

    public String getEmergencyContact() { return emergencyContact; }

    public void setEmergencyContact(String emergencyContact) throws ValidationException {
        if (emergencyContact == null || emergencyContact.trim().isEmpty()) {
            throw new ValidationException("Emergency contact cannot be null or empty.");
        }
        this.emergencyContact = emergencyContact;
    }

    public List<MedicalRecord> getRecords() {
        return new ArrayList<>(records);
    }

    public void setRecords(List<MedicalRecord> records) throws ValidationException {
        if (records == null) throw new ValidationException("Records list cannot be null.");
        this.records = new ArrayList<>(records);
    }

    public List<Appointment> getAppointments() {
        return new ArrayList<>(appointments);
    }

    public void setAppointments(List<Appointment> appointments) throws ValidationException {
        if (appointments == null) throw new ValidationException("Appointments list cannot be null.");
        this.appointments = new ArrayList<>(appointments);
    }
}
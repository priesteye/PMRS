// src/main/java/com/pmrs/model/Physician.java
package com.pmrs.model;

import com.pmrs.exception.ValidationException;
import com.pmrs.model.enums.Gender;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a medical doctor in the clinic.
 */
public class Physician extends Person {
    private String specialization;
    private String licenseNumber;
    private List<Appointment> schedule;

    public Physician(String id, String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                     String contactNumber, Address address, String specialization, String licenseNumber) throws ValidationException {
        super(id, firstName, lastName, dateOfBirth, gender, contactNumber, address);
        setSpecialization(specialization);
        setLicenseNumber(licenseNumber);
        this.schedule = new ArrayList<>();
    }

    @Override
    public String getRoleDescription() {
        return "Attending Physician - " + specialization;
    }

    @Override
    public String getDashboardView() {
        return "/com/pmrs/view/dashboard.fxml"; // Physician dashboard
    }

    public String getSpecialization() { return specialization; }

    public void setSpecialization(String specialization) throws ValidationException {
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new ValidationException("Specialization cannot be null or empty.");
        }
        this.specialization = specialization;
    }

    public String getLicenseNumber() { return licenseNumber; }

    public void setLicenseNumber(String licenseNumber) throws ValidationException {
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            throw new ValidationException("License number cannot be null or empty.");
        }
        this.licenseNumber = licenseNumber;
    }

    public List<Appointment> getSchedule() {
        return new ArrayList<>(schedule);
    }

    public void setSchedule(List<Appointment> schedule) throws ValidationException {
        if (schedule == null) throw new ValidationException("Schedule list cannot be null.");
        this.schedule = new ArrayList<>(schedule);
    }
}
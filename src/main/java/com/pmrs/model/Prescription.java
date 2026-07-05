// src/main/java/com/pmrs/model/Prescription.java
package com.pmrs.model;

import com.pmrs.exception.ValidationException;

/**
 * Represents a medication prescribed to a patient.
 */
public class Prescription {
    private String medicationName;
    private String dosage;
    private String frequency;
    private int durationDays;

    public Prescription(String medicationName, String dosage, String frequency, int durationDays) throws ValidationException {
        setMedicationName(medicationName);
        setDosage(dosage);
        setFrequency(frequency);
        setDurationDays(durationDays);
    }

    public String getMedicationName() { return medicationName; }

    public void setMedicationName(String medicationName) throws ValidationException {
        if (medicationName == null || medicationName.trim().isEmpty()) {
            throw new ValidationException("Medication name cannot be empty.");
        }
        this.medicationName = medicationName;
    }

    public String getDosage() { return dosage; }

    public void setDosage(String dosage) throws ValidationException {
        if (dosage == null || dosage.trim().isEmpty()) {
            throw new ValidationException("Dosage cannot be empty.");
        }
        this.dosage = dosage;
    }

    public String getFrequency() { return frequency; }

    public void setFrequency(String frequency) throws ValidationException {
        if (frequency == null || frequency.trim().isEmpty()) {
            throw new ValidationException("Frequency cannot be empty.");
        }
        this.frequency = frequency;
    }

    public int getDurationDays() { return durationDays; }

    public void setDurationDays(int durationDays) throws ValidationException {
        if (durationDays <= 0) {
            throw new ValidationException("Duration days must be greater than zero.");
        }
        this.durationDays = durationDays;
    }
}
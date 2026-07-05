// src/main/java/com/pmrs/model/MedicalRecord.java
package com.pmrs.model;

import com.pmrs.exception.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single documented visit or evaluation for a patient.
 */
public class MedicalRecord {
    private String id;
    private String patientId;
    private String physicianId;
    private LocalDate visitDate;
    private String diagnosis;
    private String notes;
    private List<Prescription> prescriptions;

    public MedicalRecord(String id, String patientId, String physicianId, LocalDate visitDate,
                         String diagnosis, String notes) throws ValidationException {
        setId(id);
        setPatientId(patientId);
        setPhysicianId(physicianId);
        setVisitDate(visitDate);
        setDiagnosis(diagnosis);
        setNotes(notes);
        this.prescriptions = new ArrayList<>();
    }

    public String getId() { return id; }

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) throw new ValidationException("Record ID cannot be empty.");
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

    public LocalDate getVisitDate() { return visitDate; }

    public void setVisitDate(LocalDate visitDate) throws ValidationException {
        if (visitDate == null) throw new ValidationException("Visit date cannot be null.");
        this.visitDate = visitDate;
    }

    public String getDiagnosis() { return diagnosis; }

    public void setDiagnosis(String diagnosis) throws ValidationException {
        if (diagnosis == null || diagnosis.trim().isEmpty()) throw new ValidationException("Diagnosis cannot be empty.");
        this.diagnosis = diagnosis;
    }

    public String getNotes() { return notes; }

    public void setNotes(String notes) throws ValidationException {
        if (notes == null) throw new ValidationException("Notes cannot be null. Pass an empty string instead.");
        this.notes = notes;
    }

    public List<Prescription> getPrescriptions() {
        return new ArrayList<>(prescriptions);
    }

    public void setPrescriptions(List<Prescription> prescriptions) throws ValidationException {
        if (prescriptions == null) throw new ValidationException("Prescriptions list cannot be null.");
        this.prescriptions = new ArrayList<>(prescriptions);
    }
}
// src/main/java/com/pmrs/service/PatientService.java
package com.pmrs.service;

import com.pmrs.exception.DuplicatePatientException;
import com.pmrs.exception.PMRSException;
import com.pmrs.model.Patient;
import com.pmrs.model.enums.BloodType;
import com.pmrs.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles business logic and data access orchestration for Patients.
 */
public class PatientService {

    private final Repository<Patient> patientRepository;

    public PatientService(Repository<Patient> patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Registers a new patient in the system.
     * * @param patient The validated patient model.
     *  @throws DuplicatePatientException if a patient with the same first name,
     *          last name, and date of birth is already registered.
     * @throws PMRSException if a system or duplicate error occurs.
     */
    public void registerPatient(Patient patient) throws PMRSException {
        boolean alreadyRegistered = patientRepository.findAll().stream()
                .anyMatch(existing ->
                        existing.getFirstName().equalsIgnoreCase(patient.getFirstName())
                                && existing.getLastName().equalsIgnoreCase(patient.getLastName())
                                && existing.getDateOfBirth().equals(patient.getDateOfBirth()));

        if (alreadyRegistered) {
            throw new DuplicatePatientException(
                    "A patient named " + patient.getFirstName() + " " + patient.getLastName()
                            + " with date of birth " + patient.getDateOfBirth() + " is already registered.");
        }

        patientRepository.add(patient);
    }

    /**
     * Updates an existing patient's record in the system.
     * * @param patient The updated patient model.
     * @throws PMRSException if the patient cannot be found or a repository error occurs.
     */
    public void updatePatient(Patient patient) throws PMRSException {
        if (patient == null || patient.getId() == null) {
            throw new PMRSException("Cannot update a null patient or a patient without an ID.");
        }
        patientRepository.update(patient);
    }

    public Patient getPatient(String id) throws PMRSException {
        return patientRepository.findById(id);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Searches patients by ID, first name, or last name (case-insensitive).
     * * @param query The search string.
     * @return A list of matching patients.
     */
    public List<Patient> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllPatients();
        }
        String lowerQuery = query.toLowerCase();
        return patientRepository.findAll().stream()
                .filter(p -> p.getId().toLowerCase().contains(lowerQuery) ||
                        p.getFirstName().toLowerCase().contains(lowerQuery) ||
                        p.getLastName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    /**
     * Searches patients by text query AND filters by BloodType.
     * * @param query The search string.
     * @param filter The exact blood type to filter by.
     * @return A list of matching patients.
     */
    public List<Patient> search(String query, BloodType filter) {
        return search(query).stream()
                .filter(p -> p.getBloodType() == filter)
                .collect(Collectors.toList());
    }

    /**
     * Searches patients strictly by Date of Birth range.
     * * @param dobFrom The start date (inclusive).
     * @param dobTo The end date (inclusive).
     * @return A list of matching patients.
     */
    public List<Patient> search(LocalDate dobFrom, LocalDate dobTo) {
        return patientRepository.findAll().stream()
                .filter(p -> !p.getDateOfBirth().isBefore(dobFrom) && !p.getDateOfBirth().isAfter(dobTo))
                .collect(Collectors.toList());
    }
}
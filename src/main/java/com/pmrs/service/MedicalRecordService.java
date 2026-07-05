// src/main/java/com/pmrs/service/MedicalRecordService.java
package com.pmrs.service;

import com.pmrs.exception.PMRSException;
import com.pmrs.model.MedicalRecord;
import com.pmrs.model.Patient;
import com.pmrs.repository.Repository;

import java.util.List;

/**
 * Manages the creation and retrieval of medical records.
 */
public class MedicalRecordService {

    private final Repository<MedicalRecord> recordRepository;
    private final Repository<Patient> patientRepository;

    public MedicalRecordService(Repository<MedicalRecord> recordRepository, Repository<Patient> patientRepository) {
        this.recordRepository = recordRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Appends a new medical record/visit to the system and links it to the patient.
     * * @param record The new medical record.
     * @throws PMRSException on persistence failure or missing patient.
     */
    public void addMedicalRecord(MedicalRecord record) throws PMRSException {
        // 1. Persist record
        recordRepository.add(record);

        // 2. Append to patient history
        Patient patient = patientRepository.findById(record.getPatientId());
        List<MedicalRecord> pRecords = patient.getRecords();
        pRecords.add(record);
        patient.setRecords(pRecords);
        patientRepository.update(patient);
    }
}
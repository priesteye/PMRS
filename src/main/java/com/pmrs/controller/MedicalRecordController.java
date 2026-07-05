// src/main/java/com/pmrs/controller/MedicalRecordController.java
package com.pmrs.controller;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.ValidationException;
import com.pmrs.model.MedicalRecord;
import com.pmrs.model.Patient;
import com.pmrs.service.MedicalRecordService;
import com.pmrs.service.ValidationService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MedicalRecordController {
    private static final Logger LOGGER = Logger.getLogger(MedicalRecordController.class.getName());

    private MedicalRecordService medicalRecordService;
    private ValidationService validationService;
    private Patient contextPatient; // The patient this record belongs to

    // @FXML private TextArea diagnosisArea;
    // @FXML private TextArea notesArea;

    public void setServices(MedicalRecordService medicalRecordService, ValidationService validationService) {
        this.medicalRecordService = medicalRecordService;
        this.validationService = validationService;
    }

    public void initData(Patient patient) {
        this.contextPatient = patient;
    }

    public void handleSaveRecord() {
        try {
            if (contextPatient == null) throw new ValidationException("No patient selected context.");

            // 1. Validation
            // validationService.validateRequiredString(diagnosisArea.getText(), "Diagnosis");

            // 2. Model Creation
            // MedicalRecord record = new MedicalRecord(...);

            // 3. Persist
            // medicalRecordService.addMedicalRecord(record);

            // 4. Cleanup
            // closeForm();

        } catch (ValidationException e) {
            LOGGER.log(Level.WARNING, "Medical record validation failed.", e);
            // Stub: show inline error message
        } catch (PMRSException e) {
            LOGGER.log(Level.SEVERE, "Failed to save medical record.", e);
            // Stub: Show non-blocking styled error dialog
        }
    }
}
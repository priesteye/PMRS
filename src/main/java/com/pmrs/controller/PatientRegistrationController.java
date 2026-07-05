// src/main/java/com/pmrs/controller/PatientRegistrationController.java
package com.pmrs.controller;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.DuplicatePatientException;
import com.pmrs.exception.ValidationException;
import com.pmrs.model.Patient;
import com.pmrs.service.PatientService;
import com.pmrs.service.ValidationService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PatientRegistrationController {
    private static final Logger LOGGER = Logger.getLogger(PatientRegistrationController.class.getName());

    private PatientService patientService;
    private ValidationService validationService;

    // @FXML private TextField firstNameField;
    // ... other stubbed fields ...

    public void setServices(PatientService patientService, ValidationService validationService) {
        this.patientService = patientService;
        this.validationService = validationService;
    }

    public void handleSubmit() {
        try {
            // 1. Cross-field validation (UI -> Service)
            // validationService.validateRequiredString(firstNameField.getText(), "First Name");
            // ...

            // 2. Map fields to Domain Model
            // Patient newPatient = new Patient(...);
            // Note: The Patient constructor itself throws ValidationException for domain constraints (e.g. future DOB)

            // 3. Persist
            // patientService.registerPatient(newPatient);

            // 4. Success UI cleanup
            // clearForm();
            // showSuccessConfirmation();

        } catch (ValidationException e) {
            LOGGER.log(Level.WARNING, "Patient registration validation failed.", e);
            // Stub: Show inline error text next to the offending field
        } catch (DuplicatePatientException e) {
            LOGGER.log(Level.WARNING, "Attempted to register duplicate patient.", e);
            // Stub: Show non-blocking duplicate warning dialog
        } catch (PMRSException e) {
            LOGGER.log(Level.SEVERE, "Domain error during registration.", e);
            // Stub: Show non-blocking styled error dialog
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected system error.", e);
            // Stub: Show non-blocking styled error dialog
        }
    }
}
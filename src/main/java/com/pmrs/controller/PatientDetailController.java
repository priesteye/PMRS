// src/main/java/com/pmrs/controller/PatientDetailController.java
package com.pmrs.controller;

import com.pmrs.model.Patient;
import com.pmrs.service.PatientService;
import java.util.logging.Logger;

public class PatientDetailController {
    private static final Logger LOGGER = Logger.getLogger(PatientDetailController.class.getName());

    private PatientService patientService;
    private Patient currentPatient;

    // @FXML private Label nameLabel;
    // @FXML private TabPane detailTabs; // For Medical History & Appointments

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    public void initData(Patient patient) {
        this.currentPatient = patient;
        // Stub: Populate UI fields with patient data
        // nameLabel.setText(patient.getFirstName() + " " + patient.getLastName());
    }
}
// src/main/java/com/pmrs/controller/PatientListController.java
package com.pmrs.controller;

import com.pmrs.model.Patient;
import com.pmrs.service.PatientService;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatientListController {
    private static final Logger LOGGER = Logger.getLogger(PatientListController.class.getName());

    private PatientService patientService;

    // @FXML private TextField searchBar;
    // @FXML private TableView<Patient> patientTable;

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    public void handleSearch() {
        try {
            // String query = searchBar.getText();
            // List<Patient> results = patientService.search(query);
            // Stub: patientTable.getItems().setAll(results);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Search operation failed.", e);
            // Stub: Show non-blocking styled dialog
        }
    }

    public void onPatientDoubleClicked(Patient selectedPatient) {
        // Stub: SceneNavigator.loadCenterNode("/com/pmrs/view/patient-detail.fxml", selectedPatient);
    }
}
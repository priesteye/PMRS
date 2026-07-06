// src/main/java/com/pmrs/controller/PatientListController.java
package com.pmrs.controller;

import com.pmrs.model.enums.BloodType;
import com.pmrs.model.Patient;
import com.pmrs.service.PatientService;
import com.pmrs.util.SceneNavigator; // Assume this exposes context-passing for the next phase

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatientListController {
    private static final Logger LOGGER = Logger.getLogger(PatientListController.class.getName());

    private PatientService patientService;

    // --- FXML Bindings: Search & Filter ---
    // @FXML private TextField searchBar;
    @FXML private TextField searchQueryField;
    @FXML private ComboBox<BloodType> bloodTypeFilter;
    @FXML private DatePicker dobFromPicker;
    @FXML private DatePicker dobToPicker;
    @FXML private Label errorMessageLabel;

    // --- FXML Bindings: Table ---
    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, String> colId;
    @FXML private TableColumn<Patient, String> colFirstName;
    @FXML private TableColumn<Patient, String> colLastName;
    @FXML private TableColumn<Patient, String> colDob;
    @FXML private TableColumn<Patient, String> colGender;
    @FXML private TableColumn<Patient, String> colContact;

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Called automatically by JavaFX after the FXML is loaded.
     */
    @FXML
    public void initialize() {
        // 1. Populate Dropdown
        bloodTypeFilter.getItems().setAll(BloodType.values());

        // 2. Setup Table Columns using lambda bindings (avoids reflection string typoes)
        colId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getId()));
        colFirstName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getFirstName()));
        colLastName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getLastName()));
        colDob.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDateOfBirth().toString()));
        colGender.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getGender().name()));
        colContact.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getContactNumber()));

        // 3. Setup Row Double-Click Listener
        patientTable.setRowFactory(tv -> {
            TableRow<Patient> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Patient selectedPatient = row.getItem();
                    openPatientDetail(selectedPatient);
                }
            });
            return row;
        });
    }

    /**
     * Should be called immediately after setPatientService() by the navigation router
     * to populate the initial table view.
     */
    public void loadInitialData() {
        if (patientService != null) {
            handleClearFilters(null);
        } else {
            showError("System Error: Patient Service not initialized.");
        }
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        errorMessageLabel.setText(""); // Reset errors

        if (patientService == null) return;

        try {
            // String query = searchBar.getText();
            String query = searchQueryField.getText() != null ? searchQueryField.getText().trim() : "";
            BloodType selectedBloodType = bloodTypeFilter.getValue();
            LocalDate from = dobFromPicker.getValue();
            LocalDate to = dobToPicker.getValue();

            List<Patient> results;

            // Polymorphic dispatch based on provided filters (Section 4 constraint)
            if (from != null && to != null) {
                // DOB range takes priority if both are filled
                results = patientService.search(from, to);
            } else if (!query.isEmpty() && selectedBloodType != null) {
                // Text search AND blood type filter
                results = patientService.search(query, selectedBloodType);
            } else {
                // Basic text search (or fetch all if query is empty)
                results = patientService.search(query);
            }

            patientTable.setItems(FXCollections.observableArrayList(results));

            if (results.isEmpty()) {
                errorMessageLabel.setStyle("-fx-text-fill: #f39c12;"); // Warning orange
                errorMessageLabel.setText("No patients found matching those criteria.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Search operation failed.", e);
            // Stub: Show non-blocking styled dialog
            showError("An error occurred while searching. Please check your inputs.");
        }
    }

    @FXML
    public void handleClearFilters(ActionEvent event) {
        searchQueryField.clear();
        bloodTypeFilter.getSelectionModel().clearSelection();
        dobFromPicker.setValue(null);
        dobToPicker.setValue(null);
        errorMessageLabel.setText("");

        // Reset table to show all
        if (patientService != null) {
            patientTable.setItems(FXCollections.observableArrayList(patientService.getAllPatients()));
        }
    }

    private void openPatientDetail(Patient patient) {
        try {
            // Note: This relies on SceneNavigator being built next to accept a controller callback
            // e.g., SceneNavigator.loadCenterNode("/com/pmrs/view/patient-detail.fxml", controller -> ((PatientDetailController) controller).initData(patient));
            LOGGER.info("Opening detail view for patient: " + patient.getId());

            // Stub implementation for context passing until SceneNavigator is fully written:
            // SceneNavigator.loadPatientDetail(patient);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load Patient Detail view.", e);
            showError("Could not open patient details.");
        }
    }

    private void showError(String message) {
        errorMessageLabel.setStyle("-fx-text-fill: #d32f2f;"); // Error red
        errorMessageLabel.setText(message);
    }

    public void onPatientDoubleClicked(Patient selectedPatient) {
        // Stub: SceneNavigator.loadCenterNode("/com/pmrs/view/patient-detail.fxml", selectedPatient);
    }
}
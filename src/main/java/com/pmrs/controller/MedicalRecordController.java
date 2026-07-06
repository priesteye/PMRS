// src/main/java/com/pmrs/controller/MedicalRecordController.java
package com.pmrs.controller;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.ValidationException;
import com.pmrs.model.MedicalRecord;
import com.pmrs.model.Patient;
import com.pmrs.model.Prescription;
import com.pmrs.service.MedicalRecordService;
import com.pmrs.service.ValidationService;
import com.pmrs.util.IdGenerator;
import com.pmrs.util.SceneNavigator;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MedicalRecordController {
    private static final Logger LOGGER = Logger.getLogger(MedicalRecordController.class.getName());

    private MedicalRecordService medicalRecordService;
    private ValidationService validationService;
    private Patient contextPatient; // The patient this record belongs to

    // Tracks prescriptions added to the form before saving the entire record
    private final ObservableList<Prescription> currentPrescriptions = FXCollections.observableArrayList();

    // --- Header & Global ---
    @FXML private Label patientHeaderLabel;
    @FXML private Label globalMessageLabel;

    // --- Visit Details ---
    @FXML private DatePicker visitDatePicker;
    @FXML private TextField physicianIdField;
    @FXML private TextField diagnosisField;
    @FXML private TextArea notesArea;
    @FXML private Label dateError;
    @FXML private Label physicianError;
    @FXML private Label diagnosisError;

    // --- Prescription Sub-Form ---
    @FXML private TextField medNameField;
    @FXML private TextField dosageField;
    @FXML private TextField frequencyField;
    @FXML private TextField durationField;
    @FXML private Label rxErrorLabel;

    // --- Prescription Table ---
    @FXML private TableView<Prescription> rxTable;
    @FXML private TableColumn<Prescription, String> colMed;
    @FXML private TableColumn<Prescription, String> colDosage;
    @FXML private TableColumn<Prescription, String> colFreq;
    @FXML private TableColumn<Prescription, String> colDuration;

    public void setServices(MedicalRecordService medicalRecordService, ValidationService validationService) {
        this.medicalRecordService = medicalRecordService;
        this.validationService = validationService;
    }

    @FXML
    public void initialize() {
        // Wire up the Prescription TableView
        colMed.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMedicationName()));
        colDosage.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDosage()));
        colFreq.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFrequency()));
        colDuration.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getDurationDays())));

        rxTable.setItems(currentPrescriptions);
        clearErrors();

        // Default to today's date for convenience
        visitDatePicker.setValue(LocalDate.now());
    }

    /**
     * Initializes the controller with the patient who is receiving this medical record.
     */
    public void initData(Patient patient) {
        this.contextPatient = patient;
        if (patient != null) {
            patientHeaderLabel.setText("Patient: " + patient.getFirstName() + " " + patient.getLastName() + " (" + patient.getId() + ")");
        } else {
            showGlobalError("System Error: No patient context provided.");
        }
    }

    @FXML
    public void handleAddPrescription(ActionEvent event) {
        rxErrorLabel.setText(""); // clear previous rx errors

        try {
            String medName = medNameField.getText();
            String dosage = dosageField.getText();
            String frequency = frequencyField.getText();

            // Validate integer parsing for duration
            int duration;
            try {
                duration = Integer.parseInt(durationField.getText().trim());
            } catch (NumberFormatException e) {
                throw new ValidationException("Duration must be a valid whole number of days.");
            }

            // Create Domain Object (will throw ValidationException if domain rules are broken)
            Prescription newRx = new Prescription(medName, dosage, frequency, duration);

            currentPrescriptions.add(newRx);

            // Clear sub-form inputs on success
            medNameField.clear();
            dosageField.clear();
            frequencyField.clear();
            durationField.clear();

        } catch (ValidationException e) {
            rxErrorLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void handleSaveRecord() {
        clearErrors();

        if (contextPatient == null || medicalRecordService == null) {
            showGlobalError("System Error: Context or Services are missing.");
            return;
        }

        try {
            if (contextPatient == null) throw new ValidationException("No patient selected context.");

            // 1. Validation
            // validationService.validateRequiredString(diagnosisArea.getText(), "Diagnosis");
            // UI-Level Inline Validation
            boolean valid = true;
            if (visitDatePicker.getValue() == null) {
                dateError.setText("Visit Date is required.");
                valid = false;
            }
            if (physicianIdField.getText() == null || physicianIdField.getText().trim().isEmpty()) {
                physicianError.setText("Physician ID is required.");
                valid = false;
            }
            if (diagnosisField.getText() == null || diagnosisField.getText().trim().isEmpty()) {
                diagnosisError.setText("Diagnosis is required.");
                valid = false;
            }

            if (!valid) {
                showGlobalError("Please correct the errors highlighted above.");
                return;
            }

            // 2. Map fields to Domain Model
            String recordId = IdGenerator.generateId("MR");
            String notesText = notesArea.getText() != null ? notesArea.getText() : "";

            // Model Creation
             MedicalRecord record = new MedicalRecord(
                     recordId,
                     contextPatient.getId(),
                     physicianIdField.getText().trim(),
                     visitDatePicker.getValue(),
                     diagnosisField.getText().trim(),
                     notesText
             );

            // 3. Attach any prescriptions built up in the temporary table
            record.setPrescriptions(new ArrayList<>(currentPrescriptions));

            // 4. Persist
             medicalRecordService.addMedicalRecord(record);

            // 5. Cleanup
            showGlobalSuccess("Medical record saved successfully! ID: " + recordId);
            handleClear(null);

        } catch (ValidationException e) {
            LOGGER.log(Level.WARNING, "Medical record validation failed.", e);
            // Stub: show inline error message
            showGlobalError("Validation Error: " + e.getMessage());
        } catch (PMRSException e) {
            LOGGER.log(Level.SEVERE, "Failed to save medical record.", e);
            // Stub: Show non-blocking styled error dialog
            showGlobalError("System Error: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected UI error during medical record save.", e);
            showGlobalError("An unexpected error occurred. Check logs for details.");
        }
    }

    @FXML
    public void handleClear(ActionEvent event) {
        visitDatePicker.setValue(LocalDate.now());
        physicianIdField.clear();
        diagnosisField.clear();
        notesArea.clear();

        medNameField.clear();
        dosageField.clear();
        frequencyField.clear();
        durationField.clear();

        currentPrescriptions.clear();
        clearErrors();
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            // Returns to Patient Detail View and passes the context patient back
            SceneNavigator.loadCenterNode("/com/pmrs/view/patient-detail.fxml",
                    controller -> ((PatientDetailController) controller).initData(contextPatient));
            LOGGER.info("Returning to patient detail view.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to navigate back.", e);
        }
    }

    private void clearErrors() {
        dateError.setText("");
        physicianError.setText("");
        diagnosisError.setText("");
        rxErrorLabel.setText("");
        globalMessageLabel.setText("");
    }

    private void showGlobalError(String message) {
        globalMessageLabel.setStyle("-fx-text-fill: #d32f2f;");
        globalMessageLabel.setText(message);
    }

    private void showGlobalSuccess(String message) {
        globalMessageLabel.setStyle("-fx-text-fill: #2e7d32;");
        globalMessageLabel.setText(message);
    }

}
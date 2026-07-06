// src/main/java/com/pmrs/controller/PatientDetailController.java
package com.pmrs.controller;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.ValidationException;
import com.pmrs.model.Address;
import com.pmrs.model.Appointment;
import com.pmrs.model.MedicalRecord;
import com.pmrs.model.Patient;
import com.pmrs.model.enums.BloodType;
import com.pmrs.model.enums.Gender;
import com.pmrs.service.PatientService;
import com.pmrs.util.SceneNavigator;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TabPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PatientDetailController {
    private static final Logger LOGGER = Logger.getLogger(PatientDetailController.class.getName());

    private PatientService patientService;
    private Patient currentPatient;

    // --- Header Bindings ---
    @FXML private Label headerNameLabel;
    @FXML private Label headerIdLabel;
    @FXML private Label globalMessageLabel;
    @FXML private TabPane detailTabs; // For Medical History & Appointments

    // --- Demographics Edit Bindings ---
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<Gender> genderComboBox;
    @FXML private TextField contactNumberField;
    @FXML private TextField streetField;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField zipField;
    @FXML private ComboBox<BloodType> bloodTypeComboBox;
    @FXML private TextField emergencyContactField;
    @FXML private TextField allergiesField;

    // --- Inline Error Bindings ---
    @FXML private Label firstNameError;
    @FXML private Label lastNameError;
    @FXML private Label dobError;
    @FXML private Label genderError;
    @FXML private Label contactNumberError;
    @FXML private Label streetError;
    @FXML private Label cityError;
    @FXML private Label stateZipError;
    @FXML private Label bloodTypeError;
    @FXML private Label emergencyContactError;

    // --- Medical History Table Bindings ---
    @FXML private TableView<MedicalRecord> recordsTable;
    @FXML private TableColumn<MedicalRecord, String> colRecordDate;
    @FXML private TableColumn<MedicalRecord, String> colRecordPhysician;
    @FXML private TableColumn<MedicalRecord, String> colRecordDiagnosis;
    @FXML private TableColumn<MedicalRecord, String> colRecordPrescriptions;

    // --- Appointments Table Bindings ---
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, String> colApptDateTime;
    @FXML private TableColumn<Appointment, String> colApptPhysician;
    @FXML private TableColumn<Appointment, String> colApptReason;
    @FXML private TableColumn<Appointment, String> colApptStatus;

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Called automatically by JavaFX after the FXML is loaded.
     */
    @FXML
    public void initialize() {
        genderComboBox.getItems().setAll(Gender.values());
        bloodTypeComboBox.getItems().setAll(BloodType.values());
        clearErrorLabels();
        setupTables();
    }

    /**
     * Bootstraps the controller with the patient context.
     * Called by the SceneNavigator or previous controller during scene transition.
     */
    public void initData(Patient patient) {
        this.currentPatient = patient;

        if (patient == null) {
            showGlobalError("System Error: No patient data provided to detail view.");
            return;
        }

        // Stub: Populate UI fields with patient data
        // Header
        headerNameLabel.setText(patient.getFirstName() + " " + patient.getLastName());
        headerIdLabel.setText(patient.getId());

        // Populate Demographics Form
        firstNameField.setText(patient.getFirstName());
        lastNameField.setText(patient.getLastName());
        dobPicker.setValue(patient.getDateOfBirth());
        genderComboBox.setValue(patient.getGender());
        contactNumberField.setText(patient.getContactNumber());
        bloodTypeComboBox.setValue(patient.getBloodType());
        emergencyContactField.setText(patient.getEmergencyContact());

        if (patient.getAllergies() != null) {
            allergiesField.setText(String.join(", ", patient.getAllergies()));
        }

        Address addr = patient.getAddress();
        if (addr != null) {
            streetField.setText(addr.getStreet());
            cityField.setText(addr.getCity());
            stateField.setText(addr.getState());
            zipField.setText(addr.getZipCode());
        }

        // Populate Tables
        recordsTable.setItems(FXCollections.observableArrayList(patient.getRecords()));
        appointmentsTable.setItems(FXCollections.observableArrayList(patient.getAppointments()));
    }

    private void setupTables() {
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Medical Records Column Wiring
        colRecordDate.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getVisitDate().toString()));
        colRecordPhysician.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPhysicianId()));
        colRecordDiagnosis.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDiagnosis()));
        colRecordPrescriptions.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                String.valueOf(data.getValue().getPrescriptions().size())
        ));

        // Appointments Column Wiring
        colApptDateTime.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDateTime().format(dtFormatter)));
        colApptPhysician.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPhysicianId()));
        colApptReason.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getReasonForVisit()));
        colApptStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus().name()));
    }

    @FXML
    public void handleSaveChanges(ActionEvent event) {
        clearErrorLabels();

        if (currentPatient == null || patientService == null) return;

        try {
            // 1. UI Validation (Shared logic pattern with Registration)
            if (!validateFormUI()) {
                showGlobalError("Please correct the highlighted errors.");
                return;
            }

            // 2. Build updated components
            Address newAddress = new Address(
                    streetField.getText().trim(),
                    cityField.getText().trim(),
                    stateField.getText().trim(),
                    zipField.getText().trim()
            );

            // 3. Mutate Domain Model (Setters throw ValidationException if invalid)
            currentPatient.setFirstName(firstNameField.getText().trim());
            currentPatient.setLastName(lastNameField.getText().trim());
            currentPatient.setDateOfBirth(dobPicker.getValue());
            currentPatient.setGender(genderComboBox.getValue());
            currentPatient.setContactNumber(contactNumberField.getText().trim());
            currentPatient.setAddress(newAddress);
            currentPatient.setBloodType(bloodTypeComboBox.getValue());
            currentPatient.setEmergencyContact(emergencyContactField.getText().trim());

            String allergiesText = allergiesField.getText();
            List<String> allergiesList = (allergiesText == null || allergiesText.trim().isEmpty())
                    ? List.of()
                    : Arrays.stream(allergiesText.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            currentPatient.setAllergies(allergiesList);

            // 4. Persist
            // NOTE: Ensure PatientService.updatePatient(Patient p) exists and wraps repository.update()
            // patientService.updatePatient(currentPatient);

            // Re-sync header
            headerNameLabel.setText(currentPatient.getFirstName() + " " + currentPatient.getLastName());
            showGlobalSuccess("Patient details updated successfully.");

        } catch (ValidationException e) {
            LOGGER.log(Level.WARNING, "Update validation failed.", e);
            showGlobalError("Validation Error: " + e.getMessage());
        } catch (Exception e) { // Catch PMRSException from updatePatient
            LOGGER.log(Level.SEVERE, "Failed to update patient.", e);
            showGlobalError("System error: Could not save changes.");
        }
    }

    @FXML
    public void handleBackToList(ActionEvent event) {
        try {
            SceneNavigator.loadCenterNode("/com/pmrs/view/patient-list.fxml");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to return to list.", e);
        }
    }

    @FXML
    public void handleAddMedicalRecord(ActionEvent event) {
        try {
            // SceneNavigator.loadMedicalRecordForm(currentPatient);
            LOGGER.info("Routing to Medical Record form for: " + currentPatient.getId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load medical record form.", e);
        }
    }

    @FXML
    public void handleScheduleAppointment(ActionEvent event) {
        try {
            // SceneNavigator.loadAppointmentScheduler(currentPatient);
            LOGGER.info("Routing to Appointment Scheduler for: " + currentPatient.getId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load appointment scheduler.", e);
        }
    }

    private boolean validateFormUI() {
        boolean valid = true;
        if (isEmpty(firstNameField)) { firstNameError.setText("Required"); valid = false; }
        if (isEmpty(lastNameField)) { lastNameError.setText("Required"); valid = false; }
        if (dobPicker.getValue() == null) { dobError.setText("Required"); valid = false; }
        if (genderComboBox.getValue() == null) { genderError.setText("Required"); valid = false; }
        if (isEmpty(contactNumberField)) { contactNumberError.setText("Required"); valid = false; }
        if (isEmpty(streetField)) { streetError.setText("Required"); valid = false; }
        if (isEmpty(cityField)) { cityError.setText("Required"); valid = false; }
        if (isEmpty(stateField) || isEmpty(zipField)) { stateZipError.setText("Required"); valid = false; }
        if (bloodTypeComboBox.getValue() == null) { bloodTypeError.setText("Required"); valid = false; }
        if (isEmpty(emergencyContactField)) { emergencyContactError.setText("Required"); valid = false; }
        return valid;
    }

    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private void clearErrorLabels() {
        firstNameError.setText(""); lastNameError.setText(""); dobError.setText("");
        genderError.setText(""); contactNumberError.setText(""); streetError.setText("");
        cityError.setText(""); stateZipError.setText(""); bloodTypeError.setText("");
        emergencyContactError.setText(""); globalMessageLabel.setText("");
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
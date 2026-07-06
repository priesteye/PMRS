// src/main/java/com/pmrs/controller/PatientRegistrationController.java
package com.pmrs.controller;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.DuplicatePatientException;
import com.pmrs.exception.ValidationException;
import com.pmrs.model.Patient;
import com.pmrs.model.Address;
import com.pmrs.model.enums.BloodType;
import com.pmrs.model.enums.Gender;
import com.pmrs.service.PatientService;
import com.pmrs.service.ValidationService;
import com.pmrs.util.IdGenerator; // Note: Assuming we build this utility class next

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PatientRegistrationController {
    private static final Logger LOGGER = Logger.getLogger(PatientRegistrationController.class.getName());

    private PatientService patientService;
    private ValidationService validationService;

    // --- FXML Input Bindings ---
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

    // --- FXML Error Label Bindings ---
    @FXML private Label globalMessageLabel;
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

    public void setServices(PatientService patientService, ValidationService validationService) {
        this.patientService = patientService;
        this.validationService = validationService;
    }

    /**
     * Called automatically by JavaFX after the FXML is loaded.
     */
    @FXML
    public void initialize() {
        // Populate dropdowns with enum values
        genderComboBox.getItems().setAll(Gender.values());
        bloodTypeComboBox.getItems().setAll(BloodType.values());
        clearErrorLabels();
    }

    public void handleSubmit() {
        clearErrorLabels();

        if (patientService == null) {
            showGlobalError("System Error: Services not initialized.");
            return;
        }

        try {
            // 1. Cross-field validation (UI -> Service)
            // validationService.validateRequiredString(firstNameField.getText(), "First Name");

            // 1.1 UI-Level Required Field Pre-Validation (to target inline labels precisely)
            boolean isValid = validateFormUI();
            if (!isValid) {
                showGlobalError("Please correct the errors highlighted below.");
                return;
            }

            // 2. Map fields to Domain Model
            // Patient newPatient = new Patient(...);
            // Note: The Patient constructor itself throws ValidationException for domain constraints (e.g. future DOB)
            // 2.1 Build Address Model (throws ValidationException internally if invalid)
            Address address = new Address(
                    streetField.getText().trim(),
                    cityField.getText().trim(),
                    stateField.getText().trim(),
                    zipField.getText().trim()
            );

            // 2.2 Build Patient Model (Domain validation happens here)
            String newId = IdGenerator.generateId("PT"); // Requires IdGenerator util implementation

            Patient newPatient = new Patient(
                    newId,
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    dobPicker.getValue(),
                    genderComboBox.getValue(),
                    contactNumberField.getText().trim(),
                    address,
                    bloodTypeComboBox.getValue(),
                    emergencyContactField.getText().trim()
            );

            // Append optional allergies
            String allergiesText = allergiesField.getText();
            if (allergiesText != null && !allergiesText.trim().isEmpty()) {
                List<String> allergiesList = Arrays.stream(allergiesText.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                newPatient.setAllergies(allergiesList);
            }

            // 3. Persist
             patientService.registerPatient(newPatient);

            // 4. Success UI cleanup
             clearForm(null);
             showSuccessConfirmation("Patient " + newPatient.getFirstName() + " " + newPatient.getLastName() +
                     " successfully registered! ID: " + newPatient.getId());

        } catch (ValidationException e) {
            LOGGER.log(Level.WARNING, "Patient registration validation failed.", e);
            // Stub: Show inline error text next to the offending field
            showGlobalError("Validation Error: " + e.getMessage());
        } catch (DuplicatePatientException e) {
            LOGGER.log(Level.WARNING, "Attempted to register duplicate patient.", e);
            // Stub: Show non-blocking duplicate warning dialog
            showGlobalError("Error: This patient is already registered.");
        } catch (PMRSException e) {
            LOGGER.log(Level.SEVERE, "Domain error during registration.", e);
            // Stub: Show non-blocking styled error dialog
            showGlobalError("System Error: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected system error.", e);
            // Stub: Show non-blocking styled error dialog
            showGlobalError("An unexpected error occurred. Please check system logs.");
        }
    }

    @FXML
    public void clearForm(ActionEvent event) {
        // Reset Inputs
        firstNameField.clear();
        lastNameField.clear();
        dobPicker.setValue(null);
        genderComboBox.getSelectionModel().clearSelection();
        contactNumberField.clear();
        streetField.clear();
        cityField.clear();
        stateField.clear();
        zipField.clear();
        bloodTypeComboBox.getSelectionModel().clearSelection();
        emergencyContactField.clear();
        allergiesField.clear();

        // Reset Errors
        clearErrorLabels();
        globalMessageLabel.setText("");
    }

    /**
     * Checks for empty fields and flags specific inline labels.
     * Returns true if form passes UI-level empty checks.
     */
    private boolean validateFormUI() {
        boolean valid = true;

        if (isEmpty(firstNameField)) { firstNameError.setText("First Name required"); valid = false; }
        if (isEmpty(lastNameField)) { lastNameError.setText("Last Name required"); valid = false; }
        if (dobPicker.getValue() == null) { dobError.setText("DOB required"); valid = false; }
        else if (dobPicker.getValue().isAfter(LocalDate.now())) { dobError.setText("DOB cannot be future"); valid = false; }

        if (genderComboBox.getValue() == null) { genderError.setText("Gender required"); valid = false; }

        if (isEmpty(contactNumberField)) { contactNumberError.setText("Contact required"); valid = false; }
        else if (!contactNumberField.getText().matches("^\\+?[0-9\\-\\s]{7,15}$")) {
            contactNumberError.setText("Invalid format"); valid = false;
        }

        if (isEmpty(streetField)) { streetError.setText("Street required"); valid = false; }
        if (isEmpty(cityField)) { cityError.setText("City required"); valid = false; }
        if (isEmpty(stateField) || isEmpty(zipField)) { stateZipError.setText("State & Zip required"); valid = false; }

        if (bloodTypeComboBox.getValue() == null) { bloodTypeError.setText("Blood Type required"); valid = false; }
        if (isEmpty(emergencyContactField)) { emergencyContactError.setText("Emergency Contact required"); valid = false; }

        return valid;
    }

    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private void clearErrorLabels() {
        firstNameError.setText("");
        lastNameError.setText("");
        dobError.setText("");
        genderError.setText("");
        contactNumberError.setText("");
        streetError.setText("");
        cityError.setText("");
        stateZipError.setText("");
        bloodTypeError.setText("");
        emergencyContactError.setText("");
        globalMessageLabel.setText("");
    }

    private void showGlobalError(String message) {
        globalMessageLabel.setStyle("-fx-text-fill: #d32f2f;");
        globalMessageLabel.setText(message);
    }

    private void showSuccessConfirmation(String message) {
        globalMessageLabel.setStyle("-fx-text-fill: #2e7d32;");
        globalMessageLabel.setText(message);
    }

}
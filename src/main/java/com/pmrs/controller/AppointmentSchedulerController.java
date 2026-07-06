// src/main/java/com/pmrs/controller/AppointmentSchedulerController.java
package com.pmrs.controller;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.ValidationException;
import com.pmrs.model.Appointment;
import com.pmrs.model.Patient;
import com.pmrs.model.Physician;
import com.pmrs.model.enums.AppointmentStatus;
import com.pmrs.service.AppointmentService;
import com.pmrs.service.PatientService;
import com.pmrs.service.ValidationService;
import com.pmrs.repository.Repository; // Used to fetch Physicians directly until a PhysicianService is built
import com.pmrs.util.IdGenerator;

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
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentSchedulerController {
    private static final Logger LOGGER = Logger.getLogger(AppointmentSchedulerController.class.getName());

    private AppointmentService appointmentService;
    private ValidationService validationService;
    private PatientService patientService;
    private Repository<Physician> physicianRepository;

    // --- Header Bindings ---
    @FXML private Label globalMessageLabel;

    // --- Form Bindings ---
     @FXML private ComboBox<Patient> patientComboBox;
     @FXML private ComboBox<Physician> physicianComboBox;
     @FXML private DatePicker appointmentDate;
     @FXML private ComboBox<String> timeComboBox;
     @FXML private TextField reasonField;

    // --- Inline Error Bindings ---
    @FXML private Label patientError;
    @FXML private Label physicianError;
    @FXML private Label dateTimeError;
    @FXML private Label reasonError;

    // --- Table Bindings ---
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, String> colApptId;
    @FXML private TableColumn<Appointment, String> colPatient;
    @FXML private TableColumn<Appointment, String> colPhysician;
    @FXML private TableColumn<Appointment, String> colDateTime;
    @FXML private TableColumn<Appointment, String> colReason;
    @FXML private TableColumn<Appointment, String> colStatus;

    /**
     * Services must be injected before data is loaded.
     */
    public void setServices(AppointmentService appointmentService, ValidationService validationService,
                            PatientService patientService, Repository<Physician> physicianRepository) {
        this.appointmentService = appointmentService;
        this.validationService = validationService;
        this.patientService = patientService;
        this.physicianRepository = physicianRepository;
    }

    @FXML
    public void initialize() {
        // 1. Populate standard clinic operating times (08:00 to 17:30, 30m intervals)
        for (int h = 8; h <= 17; h++) {
            timeComboBox.getItems().add(String.format("%02d:00", h));
            timeComboBox.getItems().add(String.format("%02d:30", h));
        }

        // 2. Setup Converters for Comboboxes to display user-friendly text instead of memory addresses
        patientComboBox.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient p) {
                return p == null ? "" : p.getId() + " - " + p.getFirstName() + " " + p.getLastName();
            }
            @Override public Patient fromString(String string) { return null; }
        });

        physicianComboBox.setConverter(new StringConverter<Physician>() {
            @Override
            public String toString(Physician p) {
                return p == null ? "" : p.getId() + " - Dr. " + p.getLastName() + " (" + p.getSpecialization() + ")";
            }
            @Override public Physician fromString(String string) { return null; }
        });

        // 3. Setup Table Columns
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        colApptId.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getId()));
        colPatient.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPatientId()));
        colPhysician.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPhysicianId()));
        colDateTime.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDateTime().format(dtFormatter)));
        colReason.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getReasonForVisit()));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus().name()));

        clearErrors();
    }

    /**
     * Call this via SceneNavigator after injecting dependencies.
     */
    public void loadInitialData() {
        if (patientService == null || physicianRepository == null || appointmentService == null) {
            showGlobalError("System Error: Required services not initialized.");
            return;
        }

        patientComboBox.setItems(FXCollections.observableArrayList(patientService.getAllPatients()));
        physicianComboBox.setItems(FXCollections.observableArrayList(physicianRepository.findAll()));
        refreshTable();
    }

    public void handleScheduleAppointment() {
        clearErrors();

        try {
            // 1. UI-Level Form Validation
            boolean valid = validateFormUI();
            if (!valid) {
                showGlobalError("Please correct the errors highlighted above.");
                return;
            }

            // 2. Cross-field validation (e.g., checking operating hours)
            LocalDate date = appointmentDate.getValue();
            LocalTime time = LocalTime.parse(timeComboBox.getValue());
            LocalDateTime requestedDateTime = LocalDateTime.of(date, time);

            // 3. Business Logic Validation (Operating Hours)
             validationService.validateOperatingHours(requestedDateTime);

            // 2. Create Domain Object: Model creation
            String apptId = IdGenerator.generateId("AP");
             Appointment appt = new Appointment(
                     apptId,
                     patientComboBox.getValue().getId(),
                     physicianComboBox.getValue().getId(),
                     requestedDateTime,
                     AppointmentStatus.SCHEDULED,
                     reasonField.getText().trim()
             );

            // 3. Persist (This method throws ValidationException on Double-Booking!)
             appointmentService.scheduleAppointment(appt);

            // 4. Success State and Cleanup
            // showSuccessMessage();
            showSuccessMessage("Appointment " + apptId + " successfully scheduled.");
            handleClearForm(null);
            refreshTable();

        } catch (ValidationException e) {
            // IMPORTANT: This will catch the hard-rejection Double-Booking error thrown by AppointmentService
            LOGGER.log(Level.WARNING, "Scheduling blocked by validation rule.", e);
            // Stub: Show inline/dialog clearly stating the conflict to the user.
            showGlobalError("Conflict: " + e.getMessage());
            dateTimeError.setText("Slot unavailable");
        } catch (PMRSException e) {
            LOGGER.log(Level.SEVERE, "Failed to schedule appointment.", e);
            // Stub: Show non-blocking styled error dialog
            showGlobalError("System Error: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected UI error during scheduling.", e);
            // Stub: Show non-blocking styled error dialog
            showGlobalError("An unexpected error occurred. Please check system logs.");
        }
    }

    @FXML
    public void handleCancelAppointment(ActionEvent event) {
        Appointment selected = appointmentsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showGlobalError("Please select an appointment from the table to cancel.");
            return;
        }

        if (selected.getStatus() == AppointmentStatus.CANCELLED) {
            showGlobalError("This appointment is already cancelled.");
            return;
        }

        try {
            appointmentService.updateStatus(selected.getId(), AppointmentStatus.CANCELLED);
            showSuccessMessage("Appointment " + selected.getId() + " was cancelled.");
            refreshTable();
        } catch (PMRSException e) {
            LOGGER.log(Level.SEVERE, "Failed to cancel appointment.", e);
            showGlobalError("System Error: Could not cancel the appointment.");
        }
    }

    @FXML
    public void handleClearForm(ActionEvent event) {
        patientComboBox.getSelectionModel().clearSelection();
        physicianComboBox.getSelectionModel().clearSelection();
        appointmentDate.setValue(null);
        timeComboBox.getSelectionModel().clearSelection();
        reasonField.clear();
        clearErrors();
    }

    private void refreshTable() {
        if (appointmentService != null) {
            appointmentsTable.setItems(FXCollections.observableArrayList(appointmentService.getAllAppointments()));
        }
    }

    private boolean validateFormUI() {
        boolean valid = true;
        if (patientComboBox.getValue() == null) { patientError.setText("Required"); valid = false; }
        if (physicianComboBox.getValue() == null) { physicianError.setText("Required"); valid = false; }

        if (appointmentDate.getValue() == null || timeComboBox.getValue() == null) {
            dateTimeError.setText("Date & Time required");
            valid = false;
        } else if (appointmentDate.getValue().isBefore(LocalDate.now())) {
            dateTimeError.setText("Cannot book in the past");
            valid = false;
        }

        if (reasonField.getText() == null || reasonField.getText().trim().isEmpty()) {
            reasonError.setText("Required");
            valid = false;
        }
        return valid;
    }

    private void clearErrors() {
        patientError.setText("");
        physicianError.setText("");
        dateTimeError.setText("");
        reasonError.setText("");
        globalMessageLabel.setText("");
    }

    private void showGlobalError(String message) {
        globalMessageLabel.setStyle("-fx-text-fill: #d32f2f;");
        globalMessageLabel.setText(message);
    }

    private void showSuccessMessage(String message) {
        globalMessageLabel.setStyle("-fx-text-fill: #2e7d32;");
        globalMessageLabel.setText(message);
    }

}
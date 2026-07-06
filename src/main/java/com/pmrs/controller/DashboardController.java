// src/main/java/com/pmrs/controller/DashboardController.java
package com.pmrs.controller;

import com.pmrs.exception.PMRSException;
import com.pmrs.model.Appointment;
import com.pmrs.service.AppointmentService;
import com.pmrs.service.PatientService;

import com.pmrs.util.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Landing view. Shows high-level statistics and quick actions.
 */
public class DashboardController {
    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

    private PatientService patientService;
    private AppointmentService appointmentService;

    // --- FXML Bindings ---
    @FXML private Label welcomeLabel;
    @FXML private Label totalPatientsLabel;
    @FXML private Label todayAppointmentsLabel;

    /**
     * Injects the necessary business services.
     * Note: This must be called before loadDashboardData() is executed.
     */
    public void setServices(PatientService patientService, AppointmentService appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    /**
     * Called automatically by JavaFX after the FXML is loaded.
     */
    public void initialize() {
        // Safe to leave empty if dependencies are injected post-initialization via setters.
        // UI is ready, but data loading is deferred until services are injected.
        totalPatientsLabel.setText("-");
        todayAppointmentsLabel.setText("-");
    }

    /**
     * Calculates and updates the dashboard metrics.
     * Should be called immediately after setServices() by the scene routing logic.
     */
    public void loadDashboardData() {
        if (patientService == null || appointmentService == null) {
            LOGGER.warning("Attempted to load dashboard data before services were injected.");
            return;
        }

        try {
            // 1. Total Patients
            int totalPatients = patientService.getAllPatients().size();
            totalPatientsLabel.setText(String.valueOf(totalPatients));

            // 2. Today's Appointments
            // Note: Assuming a getAllAppointments() method exists in AppointmentService
            // to fetch from the underlying repository or we filter today's appointments.
            LocalDate today = LocalDate.now();
            List<Appointment> allAppointments = appointmentService.getAllAppointments();

            long todayApptsCount = allAppointments.stream()
                    .filter(appt -> appt.getDateTime().toLocalDate().equals(today))
                    .count();

            todayAppointmentsLabel.setText(String.valueOf(todayApptsCount));

        } catch (Exception e) { // Catching generic exception as service methods currently don't throw checked exceptions for findAll
            LOGGER.log(Level.SEVERE, "Failed to load dashboard statistics.", e);
            // Stub: Show non-blocking styled error dialog
            totalPatientsLabel.setText("Err");
            todayAppointmentsLabel.setText("Err");
            // Stub: ExceptionDialogUtil.showNonBlockingError("Could not load dashboard statistics.");
        }
    }

    /**
     * Quick Action: Routes directly to the patient registration form.
     */
    @FXML
    public void onRegisterPatient(ActionEvent event) {
        try {
            SceneNavigator.loadCenterNode("/com/pmrs/view/patient-registration.fxml");
        } catch (Exception e) {
            handleNavigationError("Patient Registration", e);
        }
    }

    /**
     * Quick Action: Routes directly to the appointment scheduler.
     */
    @FXML
    public void onScheduleAppointment(ActionEvent event) {
        try {
            SceneNavigator.loadCenterNode("/com/pmrs/view/appointment-scheduler.fxml");
        } catch (Exception e) {
            handleNavigationError("Appointment Scheduler", e);
        }
    }

    /**
     * Centralized navigation error handling.
     */
    private void handleNavigationError(String target, Exception e) {
        LOGGER.log(Level.SEVERE, "Failed to navigate to " + target + ".", e);
        // Stub: ExceptionDialogUtil.showNonBlockingError("Navigation failed: " + target);
    }

}
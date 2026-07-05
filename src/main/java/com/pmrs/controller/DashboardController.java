// src/main/java/com/pmrs/controller/DashboardController.java
package com.pmrs.controller;

import com.pmrs.exception.PMRSException;
import com.pmrs.service.AppointmentService;
import com.pmrs.service.PatientService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Landing view. Shows high-level statistics and quick actions.
 */
public class DashboardController {
    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

    private PatientService patientService;
    private AppointmentService appointmentService;

    // @FXML private Label totalPatientsLabel;
    // @FXML private Label todayAppointmentsLabel;

    public void setServices(PatientService patientService, AppointmentService appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    public void initialize() {
        // Safe to leave empty if dependencies are injected post-initialization via setters.
    }

    public void loadDashboardData() {
        try {
            int totalPatients = patientService.getAllPatients().size();
            // Stub: totalPatientsLabel.setText(String.valueOf(totalPatients));

            // Assume appointmentService has a method or we filter today's appointments
            // int todayAppts = ...
            // Stub: todayAppointmentsLabel.setText(String.valueOf(todayAppts));

        } catch (Exception e) { // Catching generic exception as service methods currently don't throw checked exceptions for findAll
            LOGGER.log(Level.SEVERE, "Failed to load dashboard statistics.", e);
            // Stub: Show non-blocking styled error dialog
        }
    }
}
// src/main/java/com/pmrs/controller/AppointmentSchedulerController.java
package com.pmrs.controller;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.ValidationException;
import com.pmrs.model.Appointment;
import com.pmrs.service.AppointmentService;
import com.pmrs.service.ValidationService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentSchedulerController {
    private static final Logger LOGGER = Logger.getLogger(AppointmentSchedulerController.class.getName());

    private AppointmentService appointmentService;
    private ValidationService validationService;

    // @FXML private ComboBox<Patient> patientSelector;
    // @FXML private ComboBox<Physician> physicianSelector;
    // @FXML private DatePicker appointmentDate;
    // @FXML private TextField timeField; // or specialized time control

    public void setServices(AppointmentService appointmentService, ValidationService validationService) {
        this.appointmentService = appointmentService;
        this.validationService = validationService;
    }

    public void handleScheduleAppointment() {
        try {
            // 1. Cross-field validation (e.g., checking operating hours)
            // LocalDateTime requestedTime = parseDateTime(appointmentDate.getValue(), timeField.getText());
            // validationService.validateOperatingHours(requestedTime);

            // 2. Model creation
            // Appointment appt = new Appointment(...);

            // 3. Persist
            // appointmentService.scheduleAppointment(appt);

            // 4. Cleanup
            // showSuccessMessage();

        } catch (ValidationException e) {
            // IMPORTANT: This will catch the hard-rejection Double-Booking error thrown by AppointmentService
            LOGGER.log(Level.WARNING, "Scheduling blocked by validation rule.", e);
            // Stub: Show inline/dialog clearly stating the conflict to the user.
        } catch (PMRSException e) {
            LOGGER.log(Level.SEVERE, "Failed to schedule appointment.", e);
            // Stub: Show non-blocking styled error dialog
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected UI error during scheduling.", e);
            // Stub: Show non-blocking styled error dialog
        }
    }
}
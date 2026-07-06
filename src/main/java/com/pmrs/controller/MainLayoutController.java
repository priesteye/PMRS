// src/main/java/com/pmrs/controller/MainLayoutController.java
package com.pmrs.controller;

import com.pmrs.util.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shell controller. Handles the persistent left/top navigation sidebar
 * and provides the center mounting point for dynamic view swapping.
 */
public class MainLayoutController {
    private static final Logger LOGGER = Logger.getLogger(MainLayoutController.class.getName());

    // --- FXML Bindings ---
    @FXML private BorderPane mainContainer;
    @FXML private StackPane centerContentArea;

    @FXML private Button navDashboardBtn;
    @FXML private Button navPatientsBtn;
    @FXML private Button navAppointmentsBtn;
    @FXML private Button navLogoutBtn;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Stub: Bind navigation buttons to SceneNavigator actions
        // Register the center pane with the SceneNavigator utility so
        // subsequent navigations know where to inject the loaded FXML.
        SceneNavigator.setCenterContentArea(centerContentArea);
    }

    // Example navigation handlers
    /**
     * Routes the user to the role-specific dashboard.
     */
    @FXML
    public void onNavigateToDashboard(ActionEvent event) {
        try {
            SceneNavigator.loadCenterNode("/com/pmrs/view/dashboard.fxml");
        } catch (Exception e) {
            handleError("Failed to load the Dashboard view.", e);
        }
    }

    /**
     * Routes the user to the searchable patient list.
     */
    @FXML
    public void onNavigateToPatients() {
        try {
             SceneNavigator.loadCenterNode("/com/pmrs/view/patient-list.fxml");
        } catch (Exception e) {
            handleError("Failed to load the Patient Directory.", e);
        }
    }

    /**
     * Routes the user to the appointment scheduling interface.
     */
    @FXML
    public void onNavigateToAppointments(ActionEvent event) {
        try {
            SceneNavigator.loadCenterNode("/com/pmrs/view/appointment-scheduler.fxml");
        } catch (Exception e) {
            handleError("Failed to load the Appointment Scheduler.", e);
        }
    }

    /**
     * Logs the active user out and returns to the root login scene.
     */
    @FXML
    public void onLogout(ActionEvent event) {
        try {
            // Because login is a full-screen view (no sidebar), we tell the
            // SceneNavigator to replace the entire Scene root, not just the center node.
            SceneNavigator.loadRootScene("/com/pmrs/view/login.fxml");
        } catch (Exception e) {
            handleError("Failed to log out safely.", e);
        }
    }

    /**
     * Centralized UI error handling for navigation failures.
     * * @param message The user-friendly error message.
     * @param e The exception caught.
     */
    private void handleError(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, e);
        // Stub: Show non-blocking styled dialog to user
        // Note: Per Section 5 & 8, a custom non-blocking dialog will be triggered here.
        // For example: ExceptionDialogUtil.showNonBlockingError(centerContentArea, message);
    }
}
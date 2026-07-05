// src/main/java/com/pmrs/controller/MainLayoutController.java
package com.pmrs.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shell controller. Handles the persistent left/top navigation.
 */
public class MainLayoutController {
    private static final Logger LOGGER = Logger.getLogger(MainLayoutController.class.getName());

    // @FXML private BorderPane rootPane;
    // @FXML private Button navDashboardBtn;
    // @FXML private Button navPatientsBtn;

    public void initialize() {
        // Stub: Bind navigation buttons to SceneNavigator actions
    }

    // Example navigation handler
    public void onNavigateToPatients() {
        try {
            // SceneNavigator.loadCenterNode("/com/pmrs/view/patient-list.fxml");
        } catch (Exception e) {
            handleError("Navigation failed.", e);
        }
    }

    private void handleError(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, e);
        // Stub: Show non-blocking styled dialog to user
    }
}
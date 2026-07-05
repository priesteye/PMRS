// src/main/java/com/pmrs/controller/LoginController.java
package com.pmrs.controller;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.ValidationException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles application login for Physicians and Admins.
 */
public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    // @FXML private TextField usernameField;
    // @FXML private PasswordField passwordField;
    // @FXML private Label inlineErrorLabel;

    public void handleLogin() {
        try {
            // String username = usernameField.getText();
            // String password = passwordField.getText();

            // Stub: We don't have an AuthService yet per spec, but we will call it here.
            // Person loggedInUser = authService.authenticate(username, password);

            // Polymorphic routing per Section 4
            // String dashboardFxml = loggedInUser.getDashboardView();
            // SceneNavigator.navigate(dashboardFxml);

        } catch (ValidationException e) {
            // Handled expected validation failures (e.g., empty fields, bad credentials)
            LOGGER.log(Level.WARNING, "Login validation failed: {0}", e.getMessage());
            // Stub: inlineErrorLabel.setText(e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during login.", e);
            // Stub: Show non-blocking styled error dialog
        }
    }
}
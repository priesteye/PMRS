// src/main/java/com/pmrs/controller/LoginController.java
package com.pmrs.controller;

import com.pmrs.exception.ValidationException;
import com.pmrs.model.Person;
import com.pmrs.service.AuthService;
import com.pmrs.util.SceneNavigator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles application login for Physicians and Admins.
 */
public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    private AuthService authService;

    // --- FXML Bindings ---
     @FXML private TextField usernameField;
     @FXML private PasswordField passwordField;
     @FXML private Label inlineErrorLabel;

    /**
     * Injects the authentication service.
     * Called manually by the SceneNavigator ControllerFactory during bootstrap.
     */
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Called automatically by JavaFX after the FXML is loaded.
     */
    @FXML
    public void initialize() {
        // Ensure the error label is cleared on startup
        inlineErrorLabel.setText("");
    }

    /**
     * Triggered by the "Sign In" button (onAction="#handleLogin").
     */
    @FXML
    public void handleLogin() {
        // 1. Reset error state on new attempt
        inlineErrorLabel.setText("");
        inlineErrorLabel.setStyle("-fx-text-fill: #d32f2f;"); // Reset to error red

        if (authService == null) {
            inlineErrorLabel.setText("System Error: Auth Service not initialized.");
            return;
        }

        try {
             String username = usernameField.getText();
             String password = passwordField.getText();

            // 2. Client-side input validation
            if (username == null || username.trim().isEmpty()) {
                throw new ValidationException("System ID is required.");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new ValidationException("Password is required.");
            }

            // 3. Authentication Seam (To be wired to AuthService)
            // Authenticate against the seeded in-memory store
             Person loggedInUser = authService.authenticate(username, password);

            // Polymorphic routing (To be wired to SceneNavigator)
             SceneNavigator.loadRootScene("/com/pmrs/view/main-layout.fxml"); // Load shell first
            // Then inject the role-specific dashboard into the center of that shell
            // Passing 'null' because the dashboard doesn't require a data payload initialization
             String dashboardView = loggedInUser.getDashboardView();
             SceneNavigator.loadCenterNode(dashboardView, null); // Then inject their specific dashboard

            // Temporary success state for build/compile phase
            inlineErrorLabel.setStyle("-fx-text-fill: #2e7d32;"); // Success green
            inlineErrorLabel.setText("Login accepted. Routing to dashboard...");
        } catch (ValidationException e) {
            // Handled expected validation failures (e.g., empty fields, bad credentials)
            LOGGER.log(Level.WARNING, "Login validation failed: {0}", e.getMessage());
             inlineErrorLabel.setText(e.getMessage());
        } catch (Exception e) {
            // Unhandled/Unexpected system failure
            LOGGER.log(Level.SEVERE, "Unexpected error during login.", e);
            // Show non-blocking styled error dialog
            inlineErrorLabel.setText("System error occurred. Please check logs.");
        }
    }
}
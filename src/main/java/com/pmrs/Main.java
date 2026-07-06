// src/main/java/com/pmrs/Main.java
package com.pmrs;

import com.pmrs.controller.*;
import com.pmrs.model.*;
import com.pmrs.model.enums.Gender;
import com.pmrs.repository.*;
import com.pmrs.service.*;
import com.pmrs.util.SceneNavigator;

import javafx.application.Application;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * PMRS Application Entry Point.
 * Bootstraps dependencies and initializes the primary stage.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Patient Medical Record System (PMRS)");

        // 1. Initialize Repositories (The Persistence Seam)
        Repository<Patient> patientRepo = new InMemoryPatientRepository();
        Repository<Appointment> apptRepo = new InMemoryAppointmentRepository();
        Repository<MedicalRecord> recordRepo = new InMemoryMedicalRecordRepository();
        Repository<Physician> physicianRepo = new InMemoryPhysicianRepository();
        Repository<Credentials> credRepo = new InMemoryCredentialRepository();

        // 2. Initialize Business Logic Services
        ValidationService validationService = new ValidationService();
        PatientService patientService = new PatientService(patientRepo);
        AppointmentService appointmentService = new AppointmentService(apptRepo, patientRepo);
        MedicalRecordService recordService = new MedicalRecordService(recordRepo, patientRepo);
        AuthService authService = new AuthService(credRepo);

        // Seed Data for Testing
        Physician adminDoc = new Physician("DOC-001", "Gregory", "House",
                LocalDate.of(1959, 6, 11),
                Gender.MALE, "555-1234", new Address("1 Princeton Way",
                "Princeton", "NJ", "08540"), "Diagnostics",
                "LIC-999");
        physicianRepo.add(adminDoc);
        credRepo.add(new Credentials("admin", "password123", adminDoc));

        // 3. Configure Manual Dependency Injection Factory
        // The SceneNavigator uses this to build controllers and inject the required services.
        SceneNavigator.initialize(primaryStage, type -> {
            try {
                if (type == LoginController.class) {
                    LoginController c = new LoginController();
                    c.setAuthService(authService); // Make sure you add this setter to LoginController
                    return c;
                }
                if (type == MainLayoutController.class) {
                    return new MainLayoutController();
                }
                if (type == DashboardController.class) {
                    DashboardController c = new DashboardController();
                    c.setServices(patientService, appointmentService);
                    return c;
                }
                if (type == PatientRegistrationController.class) {
                    PatientRegistrationController c = new PatientRegistrationController();
                    c.setServices(patientService, validationService);
                    return c;
                }
                if (type == PatientListController.class) {
                    PatientListController c = new PatientListController();
                    c.setPatientService(patientService);
                    return c;
                }
                if (type == PatientDetailController.class) {
                    PatientDetailController c = new PatientDetailController();
                    c.setPatientService(patientService);
                    return c;
                }
                if (type == MedicalRecordController.class) {
                    MedicalRecordController c = new MedicalRecordController();
                    c.setServices(recordService, validationService);
                    return c;
                }
                if (type == AppointmentSchedulerController.class) {
                    AppointmentSchedulerController c = new AppointmentSchedulerController();
                    c.setServices(appointmentService, validationService, patientService, physicianRepo);
                    return c;
                }

                // Fallback for controllers without dependencies
                return type.getDeclaredConstructor().newInstance();

            } catch (Exception e) {
                throw new RuntimeException("DI Failure: Could not construct controller " + type.getName(), e);
            }
        });

        // 4. Launch Application at the Login Screen
        SceneNavigator.loadRootScene("/com/pmrs/view/login.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
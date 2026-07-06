// src/main/java/com/pmrs/util/SceneNavigator.java
package com.pmrs.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

/**
 * Centralized utility for handling view transitions and manual Dependency Injection.
 */
public class SceneNavigator {

    private static Stage primaryStage;
    private static StackPane centerContentArea;
    private static Callback<Class<?>, Object> controllerFactory;

    /**
     * Bootstraps the navigator with the primary window and DI factory.
     */
    public static void initialize(Stage stage, Callback<Class<?>, Object> factory) {
        primaryStage = stage;
        controllerFactory = factory;
    }

    /**
     * Registers the center pane of the MainLayout so sub-views can be injected without opening new windows.
     */
    public static void setCenterContentArea(StackPane pane) {
        centerContentArea = pane;
    }

    /**
     * Replaces the entire Scene root (e.g., used for Login -> Main Layout).
     */
    public static void loadRootScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlPath));
        loader.setControllerFactory(controllerFactory);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * Injects a view into the center area of the persistent MainLayout shell.
     */
    public static void loadCenterNode(String fxmlPath) throws IOException {
        if (centerContentArea == null) {
            throw new IllegalStateException("Center content area is not initialized. Cannot load inner view.");
        }
        FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlPath));
        loader.setControllerFactory(controllerFactory);
        Parent node = loader.load();

        // Clear existing content and inject the new view
        centerContentArea.getChildren().clear();
        centerContentArea.getChildren().add(node);

        // Post-load initialization hooks (like triggering a search or data refresh automatically)
        Object controller = loader.getController();
        invokeLifecycleMethods(controller);
    }

    private static void invokeLifecycleMethods(Object controller) {
        if (controller instanceof com.pmrs.controller.DashboardController) {
            ((com.pmrs.controller.DashboardController) controller).loadDashboardData();
        } else if (controller instanceof com.pmrs.controller.PatientListController) {
            ((com.pmrs.controller.PatientListController) controller).loadInitialData();
        } else if (controller instanceof com.pmrs.controller.AppointmentSchedulerController) {
            ((com.pmrs.controller.AppointmentSchedulerController) controller).loadInitialData();
        }
    }
}
module com.pmrs {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.junit.jupiter.api;
    requires java.logging;

    opens com.pmrs to javafx.fxml;
    // Open controller package to FXML
    opens com.pmrs.controller to javafx.fxml;

    exports com.pmrs;
    exports com.pmrs.controller; // if other modules need it
}
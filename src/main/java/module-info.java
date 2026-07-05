module com.pmrs {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.junit.jupiter.api;
    requires java.logging;

    opens com.pmrs to javafx.fxml;
    exports com.pmrs;
}
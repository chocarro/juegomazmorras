module com.mazmorras {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.mazmorras to javafx.fxml;
    opens com.mazmorras.controllers to javafx.fxml;
    exports com.mazmorras;
}

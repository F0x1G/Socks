module com.example.socks {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.desktop;
    requires javafx.swing;

    opens com.example.socks to javafx.fxml;
    exports com.example.socks;
}
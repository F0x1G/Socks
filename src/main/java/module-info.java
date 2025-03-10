module com.example.socks {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.desktop;
    requires javafx.swing;
    requires java.net.http;
    requires org.json;

    opens com.example.socks to javafx.fxml;
    exports com.example.socks;
}
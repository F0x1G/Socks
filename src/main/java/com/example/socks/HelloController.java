package com.example.socks;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private Button Load;
    @FXML
    private Button Start;
    @FXML
    private Button Redo;
    @FXML
    private Button Bucket;
    @FXML
    private Button Save;
    @FXML
    private Button Test;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
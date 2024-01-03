package com.example.socks;

import javafx.application.Application;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class Explorer extends Application {

    @Override
    public void start(Stage primaryStage) {
        File selectedFile = showFileChooser(primaryStage);

        if (selectedFile != null) {
            handleFileLoad(selectedFile);
        }
    }

    private File showFileChooser(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        return fileChooser.showOpenDialog(stage);
    }

    private void handleFileLoad(File selectedFile) {
        // Implement your logic to handle the loaded file
        System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        // Add your file loading logic here
    }
}

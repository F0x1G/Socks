package com.example.socks;

import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Save {
    @FXML
    public void chooseSaveDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a folder to save files");
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            String directoryPath = selectedDirectory.getAbsolutePath();
            String outputImagePath = directoryPath + "/image.bmp";
            String outputImagePath1 = directoryPath + "/out.bmp";
            String saveStanokVision = directoryPath + "/StanokOut.bmp";
            System.out.println("The directory path is: " + directoryPath);
            System.out.println("The output image path is: " + outputImagePath);
            System.out.println("The output image path1 is: " + outputImagePath1);
            System.out.println("The save stanok vision path is: " + saveStanokVision);
        }
    }
}

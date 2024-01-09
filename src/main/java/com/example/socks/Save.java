package com.example.socks;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Save {

    public void chooseSaveDirectory() {
        // Create a DirectoryChooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Save Directory");

        // Show the directory chooser
        File selectedDirectory = directoryChooser.showDialog(new Stage());

        if (selectedDirectory != null) {
            // Directory was chosen, now construct file paths using predefined names
            String outputImagePath = "image.bmp";
            String outputImagePath1 = "out.bmp";
            String saveStanokVision ="StanokOut.bmp";

            File file1 = new File(selectedDirectory, fileName1);
            File file2 = new File(selectedDirectory, fileName2);

            // You can now use file1 and file2 to perform your save operations
            System.out.println("File 1 path: " + file1.getAbsolutePath());
            System.out.println("File 2 path: " + file2.getAbsolutePath());

            // Perform your save operations using the selected directory and file names
            // For example: Save data to file1 and file2
        } else {
            // Directory selection was canceled
            System.out.println("Directory selection canceled.");
        }
    }
}

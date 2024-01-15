package com.example.socks;

import javafx.scene.image.Image;
import java.io.File;

public class PhotoLoader {

    private File selectedFile;
    private int imageWidth;
    private int imageHeight;

    public File getSelectedFile() {
        return selectedFile;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public boolean loadPhoto(File file) {
        try {
            // Load the image
            Image image = new Image(file.toURI().toString());

            // Set the selected file and image dimensions
            selectedFile = file;
            imageWidth = (int) image.getWidth();
            imageHeight = (int) image.getHeight();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

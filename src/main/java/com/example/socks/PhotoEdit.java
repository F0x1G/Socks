package com.example.socks;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PhotoEdit {

    public static BufferedImage resize(BufferedImage image, int n, int m) throws IOException {
        BufferedImage resizedImage = new BufferedImage(n, m, BufferedImage.TYPE_INT_RGB);
        resizedImage.createGraphics().drawImage(image, 0, 0, n, m, null);
        return resizedImage;
    }

    public static void saveImage(BufferedImage image, String outputPath) throws IOException {
        ImageIO.write(image, "bmp", new File(outputPath));
    }

}

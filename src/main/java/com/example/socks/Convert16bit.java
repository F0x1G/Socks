package com.example.socks;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class Convert16bit {
    public static BufferedImage Convert(BufferedImage src) {
        int[] cmap = convertBufferedImageToColorMap(src);
        IndexColorModel icm = new IndexColorModel(4, cmap.length, cmap, 0, false, -1, DataBuffer.TYPE_BYTE);
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_BINARY, icm);
        ColorConvertOp cco = new ColorConvertOp(src.getColorModel().getColorSpace(), dest.getColorModel().getColorSpace(), null);
        cco.filter(src, dest);

        return dest;
    }

    public static int[] convertBufferedImageToColorMap(BufferedImage image) {
        int[] colorMap = new int[16];
        int count = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Convert RGB to hex string representation
                String hexColor = String.format("%02X%02X%02X", red, green, blue);

                // Convert hex string to integer
                int hexValue = Integer.parseInt(hexColor, 16);

                // Check if the color is already in the colorMap
                boolean colorExists = false;
                for (int i = 0; i < count; i++) {
                    if (colorMap[i] == hexValue) {
                        colorExists = true;
                        break;
                    }
                }

                // If color doesn't exist in the colorMap, add it
                if (!colorExists) {
                    if (count < 16) {
                        colorMap[count] = hexValue;
                        count++;
                    } else {
                        break;
                    }
                }
            }
        }

        return colorMap;
    }

}

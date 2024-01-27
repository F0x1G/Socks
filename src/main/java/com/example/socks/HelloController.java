package com.example.socks;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class HelloController {
    @FXML
    private Button Load;
    @FXML
    private Label fileNameLabel;
    @FXML
    private ImageView imageView;
    @FXML
    private ImageView ImageStanok;
    @FXML
    private ImageView Zakathchick;
    private PhotoLoader photoLoader = new PhotoLoader();

    @FXML
    private ComboBox comboBox;
    @FXML
    private void handleButtonClick(ActionEvent event) {
        openFileChooser();
    }

    @FXML
    private TextField LabelM;
    @FXML
    private TextField LabelN;

    @FXML
    private VBox RightPanel;

    @FXML
    private void onStartClick(ActionEvent event) throws IOException {
        // Get the stage from the action event
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Prompt user to choose a directory
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Save Directory");
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory == null) {
            System.out.println("No directory selected. Files not saved.");
            return;
        }

        // Allow user to specify custom names for each file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(selectedDirectory);

        // Save image.bmp

        // Save out.bmp
        fileChooser.setInitialFileName("out.bmp");
        File outputImage1File = fileChooser.showSaveDialog(stage);
        if (outputImage1File == null) {
            System.out.println("Out file not saved.");
            return;
        }
        String outputImagePath1 = outputImage1File.getAbsolutePath();

        // Save StanokOut.bmp
        fileChooser.setInitialFileName("Stanok.bmp");
        File saveStanokVisionFile = fileChooser.showSaveDialog(stage);
        if (saveStanokVisionFile == null) {
            System.out.println("StanokOut file not saved.");
            return;
        }
        String saveStanokVision = saveStanokVisionFile.getAbsolutePath();

        // Save ZakajchikcOut.bmp
        fileChooser.setInitialFileName("Vizual.bmp");
        File ZakajchikcOutFile = fileChooser.showSaveDialog(stage);
        if (ZakajchikcOutFile == null) {
            System.out.println("Vizual file not saved.");
            return;
        }
        String ZakajchikcOut = ZakajchikcOutFile.getAbsolutePath();

        Image imge = imageView.getImage();
        BufferedImage inputImagePath = SwingFXUtils.fromFXImage(imge,null);

        Converter.convertTo16BitBMP(inputImagePath);

        BufferedImage img = inputImagePath;

        photo image = photo.fromBufferedImage(img);

        image = AbstraktSelection.main(image);

        int rejim = 1;
        String select = (String) comboBox.getValue();
        if(Objects.equals(select, "без C1")){
            rejim =2;
        } else if (Objects.equals(select, "без C1 і C2")) {
            rejim =3;
        }
        image = Stanok.main(image, true,rejim);//pislya Abstrakt, pislya vyboru rejima(pislya zagryzku)(3 flaga)


        photo image1 = photo.fromBufferedImage(img);//pislya stanka yaksho true, bez knopky

        image1.setPhoto(image);//pislya stanka yaksho true

        photo stanokImg = Stanok.trueStanock(image, rejim);//pislya stanka yaksho true

        setcolorSheme(stanokImg);
        RightPanel.setVisible(true);

        BufferedImage img2 = stanokImg.toBufferedImage();//
        PhotoEdit.saveImage(img2,saveStanokVision);//

        BufferedImage img1 = image1.toBufferedImage();//
        PhotoEdit.saveImage(img1,outputImagePath1);//

        Image newImage = SwingFXUtils.toFXImage(img1,null);
        imageView.setImage(newImage);
        Image newImage1 = SwingFXUtils.toFXImage(img2,null);
        ImageStanok.setImage(newImage1);

        Image imge1 = imageView.getImage();
        BufferedImage inputImagePath1 = SwingFXUtils.fromFXImage(imge1,null);

        BufferedImage img3 = Converter.PhotoDlaZak(inputImagePath1);
        PhotoEdit.saveImage(img3,ZakajchikcOut);

        int m = img3.getHeight()/2;
        int n = img3.getWidth()/2;
        img3 = PhotoEdit.resize(img3,n,m );

        Image newImage2 = SwingFXUtils.toFXImage(img3,null);
        Zakathchick.setImage(newImage2);
    }

    @FXML
    private ImageView Oc1;
    @FXML
    private ImageView Dc1;
    @FXML
    private ImageView Oc2;
    @FXML
    private ImageView Dc2;
    @FXML
    private ImageView Oc3;
    @FXML
    private ImageView Dc3;
    @FXML
    private ImageView Tc3;
    @FXML
    private ImageView Oc4;
    @FXML
    private ImageView Dc4;
    @FXML
    private ImageView Tc4;
    @FXML
    private ImageView Oc5;
    @FXML
    private ImageView Dc5;
    @FXML
    private ImageView Tc5;
    @FXML
    private ImageView BackGraund;

    private void setcolorSheme(photo Image){
        int[][] arr = Image.getStanokScheme();
        Color[] col = AbstraktSelection.getColorScheme();

        try {
        BufferedImage BufOc1 = createImage(col[arr[0][0]]);
        Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1,null);
        Oc1.setImage(ImgOc1);
        } catch (Exception e) {Oc1.setImage(null);}
        try {
            BufferedImage BufDc1 = createImage(col[arr[0][1]]);
            Image ImgDc1 = SwingFXUtils.toFXImage(BufDc1, null);
            Dc1.setImage(ImgDc1);
        } catch (Exception e) {Dc1.setImage(null);}

        try {
        BufferedImage BufOc2 = createImage(col[arr[1][0]]);
        Image ImgOc2 = SwingFXUtils.toFXImage(BufOc2,null);
        Oc2.setImage(ImgOc2);
        } catch (Exception e) {Oc2.setImage(null);}
        try {
        BufferedImage BufDc2 = createImage(col[arr[1][1]]);
        Image ImgDc2 = SwingFXUtils.toFXImage(BufDc2,null);
        Dc2.setImage(ImgDc2);
        } catch (Exception e) {Dc2.setImage(null);}

        try {
            BufferedImage BufOc3 = createImage(col[arr[2][0]]);
            Image ImgOc3 = SwingFXUtils.toFXImage(BufOc3,null);
            Oc3.setImage(ImgOc3);
        } catch (Exception e) {Oc3.setImage(null);}
        try {
            BufferedImage BufDc3 = createImage(col[arr[2][1]]);
            Image ImgDc3 = SwingFXUtils.toFXImage(BufDc3,null);
            Dc3.setImage(ImgDc3);
        } catch (Exception e) {Dc3.setImage(null);}
        try {
            BufferedImage BufTc3 = createImage(col[arr[2][2]]);
            Image ImgTc3 = SwingFXUtils.toFXImage(BufTc3,null);
            Tc3.setImage(ImgTc3);
        } catch (Exception e) {Tc3.setImage(null);}

        try {
            BufferedImage BufOc4 = createImage(col[arr[3][0]]);
            Image ImgOc4 = SwingFXUtils.toFXImage(BufOc4,null);
            Oc4.setImage(ImgOc4);
        } catch (Exception e) {Oc4.setImage(null);}
        try {
            BufferedImage BufDc4 = createImage(col[arr[3][1]]);
            Image ImgDc4 = SwingFXUtils.toFXImage(BufDc4,null);
            Dc4.setImage(ImgDc4);
        } catch (Exception e) {Dc4.setImage(null);}
        try {
            BufferedImage BufTc4 = createImage(col[arr[3][2]]);
            Image ImgTc4 = SwingFXUtils.toFXImage(BufTc4,null);
            Tc4.setImage(ImgTc4);
        } catch (Exception e) {Tc4.setImage(null);}

        try {
            BufferedImage BufOc5 = createImage(col[arr[4][0]]);
            Image ImgOc5 = SwingFXUtils.toFXImage(BufOc5,null);
            Oc5.setImage(ImgOc5);
        } catch (Exception e) {Oc5.setImage(null);}
        try {
            BufferedImage BufDc5 = createImage(col[arr[4][1]]);
            Image ImgDc5 = SwingFXUtils.toFXImage(BufDc5,null);
            Dc5.setImage(ImgDc5);
        } catch (Exception e) {Dc5.setImage(null);}
        try {
            BufferedImage BufTc5 = createImage(col[arr[4][2]]);
            Image ImgTc5 = SwingFXUtils.toFXImage(BufTc5,null);
            Tc5.setImage(ImgTc5);
        } catch (Exception e) {Tc5.setImage(null);}

        try {
            BufferedImage BufBackGraund = createImage(col[arr[5][0]]);
            Image ImgBackGraund = SwingFXUtils.toFXImage(BufBackGraund,null);
            BackGraund.setImage(ImgBackGraund);
        } catch (Exception e) {}
    }

    public static BufferedImage createImage(Color color) {
        int width = 10;
        int height = 10;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        // Заповнення зображення вказаним кольором
        g.setColor(color);
        g.fillRect(0, 0, width, height);

        // Завершення графічного контексту
        g.dispose();
        return image;
    }
    @FXML
    private  void onRes30Click(ActionEvent event){
        Image image = imageView.getImage();
        BufferedImage img = SwingFXUtils.fromFXImage(image,null);
        try {
            int m = Integer.parseInt(LabelM.getText());
            int n = Integer.parseInt(LabelN.getText());
            double con =1.3;
            m= (int) (m*con);
            img = PhotoEdit.resize(img,n,m);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        Image newImage = SwingFXUtils.toFXImage(img,null);
        imageView.setImage(newImage);

        int width = (int) imageView.getImage().getWidth();
        int height = (int) imageView.getImage().getHeight();

        LabelM.setText(String.valueOf(width));
        LabelN.setText(String.valueOf(height));
    }
    @FXML
    private  void onRes60Click(ActionEvent event){
        Image image = imageView.getImage();
        BufferedImage img = SwingFXUtils.fromFXImage(image,null);
        try {
            int m = Integer.parseInt(LabelM.getText());
            int n = Integer.parseInt(LabelN.getText());
            double con =1.6;
            m= (int) (m*con);
            img = PhotoEdit.resize(img,n,m);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        Image newImage = SwingFXUtils.toFXImage(img,null);
        imageView.setImage(newImage);

        int width = (int) imageView.getImage().getWidth();
        int height = (int) imageView.getImage().getHeight();

        LabelM.setText(String.valueOf(width));
        LabelN.setText(String.valueOf(height));
    }
    @FXML
    private void onResClick(ActionEvent event){
        Image image = imageView.getImage();
        BufferedImage img = SwingFXUtils.fromFXImage(image,null);
        try {
            int n = Integer.parseInt(LabelM.getText());
            int m = Integer.parseInt(LabelN.getText());
            img = PhotoEdit.resize(img,n,m);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        Image newImage = SwingFXUtils.toFXImage(img,null);
        imageView.setImage(newImage);

        int width = (int) imageView.getImage().getWidth();
        int height = (int) imageView.getImage().getHeight();

        LabelM.setText(String.valueOf(width));
        LabelN.setText(String.valueOf(height));
    }
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photos");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(Load.getScene().getWindow());

        if (selectedFiles != null) {
            // Process the selected files
            for (File file : selectedFiles) {

                // Load photo using PhotoLoader
                if (photoLoader.loadPhoto(file)) {
                    // Display the name of the loaded file
                    fileNameLabel.setText("Loaded File: " + file.getName());

                    // Display the image in the ImageView
                    imageView.setImage(new Image(file.toURI().toString()));

                    // Get and display image dimensions
                    int width = photoLoader.getImageWidth();
                    int height = photoLoader.getImageHeight();
                    LabelM.setText(String.valueOf(width));
                    LabelN.setText(String.valueOf(height));
                } else {
                    // Handle photo loading error
                    System.out.println("Error loading photo.");
                }
            }
        } else {
            // Handle case where user canceled file selection
            System.out.println("File selection canceled.");
        }
    }
}
package com.example.socks;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;

import javax.swing.*;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import org.json.JSONObject;


public class HelloController {
    private static Color selectedColor;
    private static BufferedImage Vizual;
    private static int Gheigh;
    private static int newGweight;
    private static int Gweight;
    @FXML
    public TextField Aipromt;
    @FXML
    public  TextField negative_prompt;
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
    private VBox Settings;
    @FXML
    private TextField Q;
    @FXML
    private TextField T;
    @FXML
    private TextField S;
    @FXML
    private TextField B;
    @FXML
    private CheckBox autoResCheck;
    @FXML
    private Slider sliderContrast;
    @FXML
    private Slider sliderBrightness;
    private Image resetImage;
    private Image fullResetImage;
    @FXML
    private CheckBox saveImageCheckBox;
    @FXML
    private CheckBox saveStanokCheckBox;
    @FXML
    private CheckBox saveVizualCheckBox;
    @FXML
    private Button generate;
    @FXML
    private Button send;
    @FXML
    private TextField steps;
    @FXML
    private TextField cfg_scale;
    @FXML
    private TextField width;
    @FXML
    private TextField height;
    @FXML
    private TextField sample_name;
    @FXML
    private TextField batch_size;
    @FXML
    private TextField batch_count;
    @FXML
    private CheckBox enable_hr;
    @FXML
    private TextField hr_scale;
    @FXML
    private TextField denoising_strength;
    @FXML
    private CheckBox restore_faces;
    @FXML
    private ToggleSwitch text2img2img;
    @FXML
    private ImageView Iimg;


    private String prompt = ""; // Ви можете додати TextField для prompt, якщо його немає в FXML
    private String negativePrompt = ""; // Ви можете додати TextField для negativePrompt, якщо його немає в FXML

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private StableDiffusionClient client;

    public void sendMessageToAI(ActionEvent event) {
        // Відключення кнопки генерації, щоб запобігти багаторазовому натисканню
        generate.setDisable(true);

        // Асинхронне виконання запиту до API
        executorService.submit(() -> {
            try {
                // Отримання значень з полів введення
                String prompt = Aipromt.getText().trim(); // Виправлено: використовуємо getText() замість toString()
                int stepsValue = Integer.parseInt(steps.getText().trim());
                double cfgScaleValue = Double.parseDouble(cfg_scale.getText().trim());
                int widthValue = Integer.parseInt(width.getText().trim());
                int heightValue = Integer.parseInt(height.getText().trim());
                String samplerName = sample_name.getText().trim();
                int batchSizeValue = Integer.parseInt(batch_size.getText().trim());
                boolean enableHrValue = enable_hr.isSelected();
                double hrScaleValue = Double.parseDouble(hr_scale.getText().trim());
                double denoisingStrengthValue = Double.parseDouble(denoising_strength.getText().trim());
                boolean restoreFacesValue = restore_faces.isSelected();
                boolean isImg2Img = text2img2img.isSelected(); // Якщо true - img2img, якщо false - text2img

                // Отримання негативного промту
                String negativePrompt = negative_prompt.getText().trim(); // Припускаю, що у вас є таке поле

                // Створення об'єкта параметрів з правильними значеннями
                StableDiffusionClient.Parameters params = new StableDiffusionClient.Parameters()
                        .setPrompt(prompt) // Встановлюємо промт
                        .setSteps(stepsValue)
                        .setCfgScale(cfgScaleValue)
                        .setWidth(widthValue)
                        .setHeight(heightValue)
                        .setSampler(samplerName)
                        .setBatchSize(batchSizeValue)
                        .setNegativePrompt(negativePrompt)
                        .setDenoisingStrength(denoisingStrengthValue);

                // Отримання зображення в залежності від вибраного режиму
                java.awt.Image resultImage;

                if (!isImg2Img) {
                    // Генерація зображення з тексту (txt2img)
                    resultImage = client.generateImage(prompt, params);
                } else {
                    // Обробка існуючого зображення зі startImage (img2img)
                    if (startImage.getImage() == null) {
                        showAlert("Помилка", "Для режиму img2img потрібно спочатку завантажити вхідне зображення в startImage.");
                        return;
                    }

                    // Конвертація з JavaFX Image в java.awt.Image
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(startImage.getImage(), null);
                    resultImage = client.processImage(bufferedImage, prompt, params);
                }

                // Конвертація результату в JavaFX Image
                BufferedImage bufferedResultImage;
                if (resultImage instanceof BufferedImage) {
                    bufferedResultImage = (BufferedImage) resultImage;
                } else {
                    bufferedResultImage = new BufferedImage(
                            resultImage.getWidth(null),
                            resultImage.getHeight(null),
                            BufferedImage.TYPE_INT_ARGB
                    );
                    java.awt.Graphics2D g2d = bufferedResultImage.createGraphics();
                    g2d.drawImage(resultImage, 0, 0, null);
                    g2d.dispose();
                }

                final Image fxImage = SwingFXUtils.toFXImage(bufferedResultImage, null);

                // Оновлення UI в основному потоці JavaFX
                Platform.runLater(() -> {
                    // Вивід результату в Iimg
                    Iimg.setImage(fxImage);
                    Iimg.setFitWidth(widthValue);
                    Iimg.setFitHeight(heightValue);

                    // Повторне увімкнення кнопки генерації
                    generate.setDisable(false);
                });

                // Збереження параметрів у JSON файл для подальшого використання
                String jsonFilename = isImg2Img ? "last_img2img_params.json" : "last_txt2img_params.json";
                client.saveParamsToJsonFile(jsonFilename, prompt, params, isImg2Img);

            } catch (NumberFormatException e) {
                showAlert("Помилка введення", "Перевірте правильність введених числових даних: " + e.getMessage());
            } catch (IOException e) {
                showAlert("Помилка з'єднання", "Неможливо з'єднатися з API Stable Diffusion: " + e.getMessage());
            } catch (Exception e) {
                showAlert("Непередбачена помилка", "Виникла помилка при обробці запиту: " + e.getMessage());
            } finally {
                // Обов'язково повертаємо кнопку в активний стан
                Platform.runLater(() -> generate.setDisable(false));
            }
        });
    }

    // Допоміжний метод для відображення повідомлень про помилки
    private void showAlert1(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    // Допоміжний метод для відображення повідомлень про помилки
    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public void sendMessageToMain(ActionEvent event) {
        Image img = Iimg.getImage();
        startImage.setImage(img);
    }

    private void adjustBrightness(double value) {
        if (resetImage != null) {
            Image image = applyBrightness(resetImage, value);
            imageView.setImage(image);
        }
    }


    private Image applyBrightness(Image imageView, double value) {
        javafx.scene.image.PixelReader pixelReader = imageView.getPixelReader();
        int width = (int) imageView.getWidth();
        int height = (int) imageView.getHeight();
        javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage(width, height);
        javafx.scene.image.PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                javafx.scene.paint.Color color = pixelReader.getColor(x, y);
                double red = color.getRed() + value;
                double green = color.getGreen() + value;
                double blue = color.getBlue() + value;
                red = Math.min(1.0, Math.max(0.0, red));
                green = Math.min(1.0, Math.max(0.0, green));
                blue = Math.min(1.0, Math.max(0.0, blue));
                pixelWriter.setColor(x, y, javafx.scene.paint.Color.color(red, green, blue));
            }
        }
        return writableImage;
    }

    private int applyContrastToChannel(int channel, double contrast) {
        double newValue = (((channel / 255.0) - 0.5) * contrast + 0.5) * 255.0;
        return Math.min(Math.max((int) newValue, 0), 255);
    }

    private void updateContrast(double contrastValue) {
        if (resetImage != null) {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(resetImage, null);
            applyContrast(bufferedImage, contrastValue);
            Image updatedImage = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(updatedImage);
        }
    }

    private BufferedImage applyContrast(BufferedImage image, double contrast) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;
                int red = (rgb >> 16) & 0xff;
                int green = (rgb >> 8) & 0xff;
                int blue = (rgb) & 0xff;

                red = applyContrastToChannel(red, contrast);
                green = applyContrastToChannel(green, contrast);
                blue = applyContrastToChannel(blue, contrast);

                int newRGB = (alpha << 24) | (red << 16) | (green << 8) | blue;
                image.setRGB(x, y, newRGB);
            }
        }
        return image;
    }

    @FXML
    private void onBaseSave(ActionEvent event){
        Gheigh = Integer.parseInt(LabelN.getText());
        Gweight = Integer.parseInt(LabelM.getText());
    }

    @FXML
    private void onRemeuveAll(ActionEvent event){
       imageView.setImage(null);
       ImageStanok.setImage(null);
       Zakathchick.setImage(null);
       startImage.setImage(null);
       LabelM.setText("");
       LabelN.setText("");
       RightPanel.setVisible(false);
        EditBufer = null;
        Vizual = null;
    }
    @FXML
    private void initialize() {

        // Ініціалізація клієнта Stable Diffusion при завантаженні контролера
        client = new StableDiffusionClient("http://127.0.0.1:7860", 180000);

        // Встановлення значень за замовчуванням
        steps.setText("20");
        cfg_scale.setText("8.0");
        width.setText("200");
        height.setText("300");
        sample_name.setText("Euler a");
        batch_size.setText("1");
        batch_count.setText("1");
        hr_scale.setText("2.0");
        denoising_strength.setText("0.7");

        sliderContrast.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateContrast(newValue.doubleValue());
        });
        sliderContrast.focusedProperty().addListener((observable, oldValue, newValue) -> {
            saveAfterchange();
        });
        sliderBrightness.valueProperty().addListener((observable, oldValue, newValue) -> adjustBrightness(newValue.doubleValue()));
        sliderBrightness.focusedProperty().addListener((observable, oldValue, newValue) -> {
            saveAfterchange();
        });

        LabelM.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(autoResCheck.isSelected()) {
                if (!newValue) { // Перевіряємо, чи втратила елемент фокус
                    String newValueText = LabelM.getText(); // Отримуємо нове значення з елементу
                    try {
                        int newNumber = Integer.parseInt(newValueText); // Перетворюємо рядок на ціле число
                        onApdateRess(newNumber); // Викликаємо ваш метод, передаючи нове значення
                    } catch (NumberFormatException e) {
                        // Обробка винятку, якщо рядок не може бути перетворений на ціле число
                        System.err.println("Неправильний формат числа: " + newValueText);
                    } catch (IOException e) {

                    }
                }
            }
        });

        Oc1.setOnMouseClicked(event -> {try {onOc1();} catch (IOException e) {throw new RuntimeException(e);}});
        Dc1.setOnMouseClicked(event -> {try {onDc1();} catch (IOException e) {throw new RuntimeException(e);}});
        Oc2.setOnMouseClicked(event -> {try {onOc2();} catch (IOException e) {throw new RuntimeException(e);}});
        Dc2.setOnMouseClicked(event -> {try {onDc2();} catch (IOException e) {throw new RuntimeException(e);}});
        Oc3.setOnMouseClicked(event -> {try {onOc3();} catch (IOException e) {throw new RuntimeException(e);}});
        Dc3.setOnMouseClicked(event -> {try {onDc3();} catch (IOException e) {throw new RuntimeException(e);}});
        Tc3.setOnMouseClicked(event -> {try {onTc3();} catch (IOException e) {throw new RuntimeException(e);}});
        Oc4.setOnMouseClicked(event -> {try {onOc4();} catch (IOException e) {throw new RuntimeException(e);}});
        Dc4.setOnMouseClicked(event -> {try {onDc4();} catch (IOException e) {throw new RuntimeException(e);}});
        Tc4.setOnMouseClicked(event -> {try {onTc4();} catch (IOException e) {throw new RuntimeException(e);}});
        Oc5.setOnMouseClicked(event -> {try {onOc5();} catch (IOException e) {throw new RuntimeException(e);}});
        Dc5.setOnMouseClicked(event -> {try {onDc5();} catch (IOException e) {throw new RuntimeException(e);}});
        Tc5.setOnMouseClicked(event -> {try {onTc5();} catch (IOException e) {throw new RuntimeException(e);}});
        BackGraund.setOnMouseClicked(event -> {try {onBackGraund();} catch (IOException e) {throw new RuntimeException(e);}});

    }

    private void saveAfterchange(){
       resetImage = imageView.getImage();
    }

    @FXML
    private void onFRemuve (ActionEvent event) throws IOException{
        sliderContrast.setValue(1);
        sliderBrightness.setValue(0);
        imageView.setImage(fullResetImage);
    }
    private void onApdateRess(int newValue) throws IOException {
        int oldValue = newGweight;
        int oldN = Integer.parseInt(LabelN.getText());
        int n = (newValue * oldN) / oldValue;

        Image image = imageView.getImage();
        BufferedImage img = SwingFXUtils.fromFXImage(image,null);

        img = PhotoEdit.resize(img,newValue,n);

        Image newImage = SwingFXUtils.toFXImage(img,null);
        imageView.setImage(newImage);

        LabelN.setText(String.valueOf(n));
        newGweight = newValue;
    }

    @FXML
    private void onSettingClick(ActionEvent event) throws IOException{
        Q.setText("27");
        T.setText("0.2");
        S.setText("5");
        B.setText("1.8");
        Settings.setVisible(!Settings.isVisible());
    }

    @FXML
    private ImageView startImage;

    @FXML
    private void onStartClick(ActionEvent event) throws IOException {
        // Get the stage from the action event
        if(startImage.getImage()==null){
            startImage.setImage(imageView.getImage());
        }
        Image imge = startImage.getImage();

        BufferedImage inputImagePath = SwingFXUtils.fromFXImage(imge,null);

        Converter.convertTo16BitBMP(inputImagePath);

        BufferedImage img = inputImagePath;

        photo image = photo.fromBufferedImage(img);

        int rejim = 1;
        String select = (String) comboBox.getValue();
        if(Objects.equals(select, "без C1")){
            rejim =2;
        } else if (Objects.equals(select, "без C1 і C2")) {
            rejim =3;
        }
        image = AbstraktSelection.main(image);

        int q = Integer.parseInt(Q.getText());
        double t = Double.parseDouble(T.getText());
        double s = Double.parseDouble((S.getText()));
        double b = Double.parseDouble(B.getText());

        image = Stanok.main(image, true,rejim,q,t,s,b);


        photo image1 = photo.fromBufferedImage(img);//pislya stanka yaksho true, bez knopky

        image1.setPhoto(image);//pislya stanka yaksho true

        photo stanokImg = Stanok.trueStanock(image, rejim);//pislya stanka yaksho true

        setcolorSheme(stanokImg);
        RightPanel.setVisible(true);

        BufferedImage img2 = stanokImg.toBufferedImage();//

        BufferedImage img1 = image1.toBufferedImage();//

        Image newImage = SwingFXUtils.toFXImage(img1,null);
        imageView.setImage(newImage);
        Image newImage1 = SwingFXUtils.toFXImage(img2,null);
        ImageStanok.setImage(newImage1);

        Image imge1 = imageView.getImage();
        BufferedImage inputImagePath1 = SwingFXUtils.fromFXImage(imge1,null);

        BufferedImage img3 = Converter.PhotoDlaZak(inputImagePath1);
        Vizual = img3;
        int m = (int) (img3.getHeight()/2.5);
        int n = (int) (img3.getWidth()/2.5);
        img3 = PhotoEdit.resize(img3,n,m );

        Image newImage2 = SwingFXUtils.toFXImage(img3,null);
        Zakathchick.setImage(newImage2);
    }

    @FXML
    private void onSynk(ActionEvent event){
        Image m = imageView.getImage();
        fullResetImage = m;
        startImage.setImage(m);
    }

    @FXML
    private void onRemeuveVizual(ActionEvent event) throws IOException {
        try {
            Image imge1 = imageView.getImage();
            BufferedImage image2 = SwingFXUtils.fromFXImage(imge1, null);
            photo phot = photo.fromBufferedImage(image2);

            int rejim = 1;
            String select = (String) comboBox.getValue();
            if (Objects.equals(select, "без C1")) {
                rejim = 2;
            } else if (Objects.equals(select, "без C1 і C2")) {
                rejim = 3;
            }

            photo phot1 = Stanok.trueStanock(phot, rejim);
            setcolorSheme(phot1);

            BufferedImage img3 = Converter.PhotoDlaZak(image2);
            int m = (int) (img3.getHeight() / 2.5);
            int n = (int) (img3.getWidth() / 2.5);
            img3 = PhotoEdit.resize(img3, n, m);
            Image newImage = SwingFXUtils.toFXImage(img3, null);
            Zakathchick.setImage(newImage);
            EditBufer = null;
        }catch (Exception e){

        }
    }

    @FXML
    private void onSaveClick(ActionEvent event) throws IOException {
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

        if (saveImageCheckBox.isSelected()) {
            fileChooser.setInitialFileName("out.bmp");
            File outputImage1File = fileChooser.showSaveDialog(stage);
            if (outputImage1File == null) {
                System.out.println("Out file not saved.");
                return;
            }
            String outputImagePath1 = outputImage1File.getAbsolutePath();

            Image imge456 = imageView.getImage();
            BufferedImage img2 = SwingFXUtils.fromFXImage(imge456, null);
            img2 = Convert16bit.Convert(img2);
            PhotoEdit.saveImage(img2, outputImagePath1);
        }

        if (saveStanokCheckBox.isSelected()) {
            fileChooser.setInitialFileName("Stanok.bmp");
            File saveStanokVisionFile = fileChooser.showSaveDialog(stage);
            if (saveStanokVisionFile == null) {
                System.out.println("StanokOut file not saved.");
                return;
            }
            String saveStanokVision = saveStanokVisionFile.getAbsolutePath();

            Image imge123 = ImageStanok.getImage();
            BufferedImage img123 = SwingFXUtils.fromFXImage(imge123, null);
            img123 = Convert16bit.Convert(img123);
            PhotoEdit.saveImage(img123, saveStanokVision);
        }

        if (saveVizualCheckBox.isSelected()) {
            fileChooser.setInitialFileName("Vizual.bmp");
            File ZakajchikcOutFile = fileChooser.showSaveDialog(stage);
            if (ZakajchikcOutFile == null) {
                System.out.println("Vizual file not saved.");
                return;
            }
            String ZakajchikcOut = ZakajchikcOutFile.getAbsolutePath();

            BufferedImage img3 = Vizual;
            PhotoEdit.saveImage(img3, ZakajchikcOut);
        }
    }

    @FXML
    private ImageView Oc1;
    private void onOc1() throws IOException {
        showColorChooserDialog(selectedColor -> {
        Color color2 = selectedColor;
        Color[][] color1 =  AbstraktSelection.StanokSheme();
        Image imge = ImageStanok.getImage();
        BufferedImage image1 = SwingFXUtils.fromFXImage(imge,null);
            BufferedImage image2;
            if(EditBufer==null){
                Image imge1 = imageView.getImage();
                image2 = SwingFXUtils.fromFXImage(imge1,null);
            }else {
                image2 = EditBufer;
            }

        BufferedImage finish = AdvancedReplaceColor(image1,image2,color1[0][0],color2);

        try {
            BufferedImage BufOc1 = createImage(color2);
            Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1,null);
            Oc1.setImage(ImgOc1);
        } catch (Exception e) {Oc1.setImage(null);}

        //Image newImage = SwingFXUtils.toFXImage(finish,null);
        //imageView.setImage(newImage)
            EditBufer = finish;
        BufferedImage img3 = Converter.PhotoDlaZak(finish);

        int m = (int) (img3.getHeight()/2.5);
        int n = (int) (img3.getWidth()/2.5);
        img3 = PhotoEdit.resize(img3,n,m );
        Vizual = img3;

        Image newImage2 = SwingFXUtils.toFXImage(img3,null);
        Zakathchick.setImage(newImage2);
    });

    }

    private BufferedImage EditBufer;

    @FXML
    private ImageView Dc1;
    private void onDc1() throws IOException {
        try {
        showColorChooserDialog(selectedColor -> {
            Color color2 = selectedColor;
            Color[][] color1 =  AbstraktSelection.StanokSheme();
            Image imge = ImageStanok.getImage();
            BufferedImage image1 = SwingFXUtils.fromFXImage(imge,null);

            BufferedImage image2;
            if(EditBufer==null){
                Image imge1 = imageView.getImage();
                image2 = SwingFXUtils.fromFXImage(imge1,null);
            }else {
                image2 = EditBufer;
            }

            BufferedImage finish = AdvancedReplaceColor(image1,image2,color1[0][1],color2);

            try {
                BufferedImage BufOc1 = createImage(color2);
                Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1,null);
                Dc1.setImage(ImgOc1);
            } catch (Exception e) {Dc1.setImage(null);}

            //Image newImage = SwingFXUtils.toFXImage(finish,null);
            //imageView.setImage(newImage);

            EditBufer = finish;
            BufferedImage img3 = Converter.PhotoDlaZak(finish);
            Vizual = img3;
            int m = (int) (img3.getHeight()/2.5);
            int n = (int) (img3.getWidth()/2.5);
            img3 = PhotoEdit.resize(img3,n,m );

            Image newImage2 = SwingFXUtils.toFXImage(img3,null);
            Zakathchick.setImage(newImage2);
        });
    }catch (Exception e){

    }
    }
    @FXML
    private ImageView Oc2;
    private void onOc2() throws IOException {
        try {
        showColorChooserDialog(selectedColor -> {
            Color color2 = selectedColor;
            Color[][] color1 =  AbstraktSelection.StanokSheme();
            Image imge = ImageStanok.getImage();
            BufferedImage image1 = SwingFXUtils.fromFXImage(imge,null);
            BufferedImage image2;
            if(EditBufer==null){
                Image imge1 = imageView.getImage();
                image2 = SwingFXUtils.fromFXImage(imge1,null);
            }else {
                image2 = EditBufer;
            }

            BufferedImage finish = AdvancedReplaceColor(image1,image2,color1[1][0],color2);

            try {
                BufferedImage BufOc1 = createImage(color2);
                Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1,null);
                Oc2.setImage(ImgOc1);
            } catch (Exception e) {Oc2.setImage(null);}

            //Image newImage = SwingFXUtils.toFXImage(finish,null);
            //imageView.setImage(newImage);

            EditBufer = finish;
            BufferedImage img3 = Converter.PhotoDlaZak(finish);
            Vizual = img3;
            int m = (int) (img3.getHeight()/2.5);
            int n = (int) (img3.getWidth()/2.5);
            img3 = PhotoEdit.resize(img3,n,m );

            Image newImage2 = SwingFXUtils.toFXImage(img3,null);
            Zakathchick.setImage(newImage2);
        });
    }catch (Exception e){

    }
    }

    @FXML
    private ImageView Dc2;
    private void onDc2() throws IOException {
        try {
        showColorChooserDialog(selectedColor -> {
            Color color2 = selectedColor;
            Color[][] color1 =  AbstraktSelection.StanokSheme();
            Image imge = ImageStanok.getImage();
            BufferedImage image1 = SwingFXUtils.fromFXImage(imge,null);
            BufferedImage image2;
            if(EditBufer==null){
                Image imge1 = imageView.getImage();
                image2 = SwingFXUtils.fromFXImage(imge1,null);
            }else {
                image2 = EditBufer;
            }

            BufferedImage finish = AdvancedReplaceColor(image1,image2,color1[1][1],color2);

            try {
                BufferedImage BufOc1 = createImage(color2);
                Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1,null);
                Dc2.setImage(ImgOc1);
            } catch (Exception e) {Dc2.setImage(null);}

            //Image newImage = SwingFXUtils.toFXImage(finish,null);
            //imageView.setImage(newImage);

            EditBufer = finish;
            BufferedImage img3 = Converter.PhotoDlaZak(finish);
            Vizual = img3;
            int m = (int) (img3.getHeight()/2.5);
            int n = (int) (img3.getWidth()/2.5);
            img3 = PhotoEdit.resize(img3,n,m );

            Image newImage2 = SwingFXUtils.toFXImage(img3,null);
            Zakathchick.setImage(newImage2);
        });
    }catch (Exception e){

    }
    }

    @FXML
    private ImageView Oc3;

    private void onOc3() throws IOException {
        try {
        showColorChooserDialog(selectedColor -> {
            Color color2 = selectedColor;
            Color[][] color1 =  AbstraktSelection.StanokSheme();
            Image imge = ImageStanok.getImage();
            BufferedImage image1 = SwingFXUtils.fromFXImage(imge,null);
            BufferedImage image2;
            if(EditBufer==null){
                Image imge1 = imageView.getImage();
                image2 = SwingFXUtils.fromFXImage(imge1,null);
            }else {
                image2 = EditBufer;
            }

            BufferedImage finish = AdvancedReplaceColor(image1,image2,color1[2][0],color2);

            try {
                BufferedImage BufOc1 = createImage(color2);
                Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1,null);
                Oc3.setImage(ImgOc1);
            } catch (Exception e) {Oc3.setImage(null);}

            //Image newImage = SwingFXUtils.toFXImage(finish,null);
            //imageView.setImage(newImage);

            EditBufer = finish;
            BufferedImage img3 = Converter.PhotoDlaZak(finish);
            Vizual = img3;
            int m = (int) (img3.getHeight()/2.5);
            int n = (int) (img3.getWidth()/2.5);
            img3 = PhotoEdit.resize(img3,n,m );

            Image newImage2 = SwingFXUtils.toFXImage(img3,null);
            Zakathchick.setImage(newImage2);
        });
    }catch (Exception e){

    }
    }

    @FXML
    private ImageView Dc3;
    private void onDc3() throws IOException {
        try {
        showColorChooserDialog(selectedColor -> {
            Color color2 = selectedColor;
            Color[][] color1 =  AbstraktSelection.StanokSheme();
            Image imge = ImageStanok.getImage();
            BufferedImage image1 = SwingFXUtils.fromFXImage(imge,null);
            BufferedImage image2;
            if(EditBufer==null){
                Image imge1 = imageView.getImage();
                image2 = SwingFXUtils.fromFXImage(imge1,null);
            }else {
                image2 = EditBufer;
            }

            BufferedImage finish = AdvancedReplaceColor(image1,image2,color1[2][1],color2);

            try {
                BufferedImage BufOc1 = createImage(color2);
                Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1,null);
                Dc3.setImage(ImgOc1);
            } catch (Exception e) {Dc3.setImage(null);}

            //Image newImage = SwingFXUtils.toFXImage(finish,null);
            //imageView.setImage(newImage);

            EditBufer = finish;
            BufferedImage img3 = Converter.PhotoDlaZak(finish);

            int m = (int) (img3.getHeight()/2.5);
            int n = (int) (img3.getWidth()/2.5);
            img3 = PhotoEdit.resize(img3,n,m );
            Vizual = img3;
            Image newImage2 = SwingFXUtils.toFXImage(img3,null);
            Zakathchick.setImage(newImage2);
        });
    }catch (Exception e){

    }
    }
    @FXML
    private ImageView Tc3;
    private void onTc3() throws IOException {
        try {
        showColorChooserDialog(selectedColor -> {
            Color color2 = selectedColor;
            Color[][] color1 =  AbstraktSelection.StanokSheme();
            Image imge = ImageStanok.getImage();
            BufferedImage image1 = SwingFXUtils.fromFXImage(imge,null);
            BufferedImage image2;
            if(EditBufer==null){
                Image imge1 = imageView.getImage();
                image2 = SwingFXUtils.fromFXImage(imge1,null);
            }else {
                image2 = EditBufer;
            }

            BufferedImage finish = AdvancedReplaceColor(image1,image2,color1[2][2],color2);

            try {
                BufferedImage BufOc1 = createImage(color2);
                Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1,null);
                Tc3.setImage(ImgOc1);
            } catch (Exception e) {Tc3.setImage(null);}

            //Image newImage = SwingFXUtils.toFXImage(finish,null);
            //imageView.setImage(newImage);
            EditBufer = finish;
            BufferedImage img3 = Converter.PhotoDlaZak(finish);

            int m = (int) (img3.getHeight()/2.5);
            int n = (int) (img3.getWidth()/2.5);
            img3 = PhotoEdit.resize(img3,n,m );
            Vizual = img3;
            Image newImage2 = SwingFXUtils.toFXImage(img3,null);
            Zakathchick.setImage(newImage2);
        });
    }catch (Exception e){

    }
    }
    @FXML
    private ImageView Oc4;
    private void onOc4() throws IOException {
        try {
        showColorChooserDialog(selectedColor -> {
            Color color2 = selectedColor;
            Color[][] color1 =  AbstraktSelection.StanokSheme();
            Image imge = ImageStanok.getImage();
            BufferedImage image1 = SwingFXUtils.fromFXImage(imge,null);
            BufferedImage image2;
            if(EditBufer==null){
                Image imge1 = imageView.getImage();
                image2 = SwingFXUtils.fromFXImage(imge1,null);
            }else {
                image2 = EditBufer;
            }

            BufferedImage finish = AdvancedReplaceColor(image1,image2,color1[3][0],color2);

            try {
                BufferedImage BufOc1 = createImage(color2);
                Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1,null);
                Oc4.setImage(ImgOc1);
            } catch (Exception e) {Oc4.setImage(null);}

            //Image newImage = SwingFXUtils.toFXImage(finish,null);
            //imageView.setImage(newImage);
            EditBufer = finish;
            BufferedImage img3 = Converter.PhotoDlaZak(finish);
            Vizual = img3;
            int m = (int) (img3.getHeight()/2.5);
            int n = (int) (img3.getWidth()/2.5);
            img3 = PhotoEdit.resize(img3,n,m );

            Image newImage2 = SwingFXUtils.toFXImage(img3,null);
            Zakathchick.setImage(newImage2);
        });
    }catch (Exception e){

    }
    }
    @FXML
    private ImageView Dc4;
    private void onDc4() throws IOException {
        try {
        showColorChooserDialog(selectedColor -> {
            Color color2 = selectedColor;
            Color[][] color1 =  AbstraktSelection.StanokSheme();
            Image imge = ImageStanok.getImage();
            BufferedImage image1 = SwingFXUtils.fromFXImage(imge,null);
            BufferedImage image2;
            if(EditBufer==null){
                Image imge1 = imageView.getImage();
                image2 = SwingFXUtils.fromFXImage(imge1,null);
            }else {
                image2 = EditBufer;
            }

            BufferedImage finish = AdvancedReplaceColor(image1,image2,color1[3][1],color2);

            try {
                BufferedImage BufOc1 = createImage(color2);
                Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1,null);
                Dc4.setImage(ImgOc1);
            } catch (Exception e) {Dc4.setImage(null);}
            EditBufer = finish;
            //Image newImage = SwingFXUtils.toFXImage(finish,null);
            //imageView.setImage(newImage);

            BufferedImage img3 = Converter.PhotoDlaZak(finish);
            Vizual = img3;
            int m = (int) (img3.getHeight()/2.5);
            int n = (int) (img3.getWidth()/2.5);
            img3 = PhotoEdit.resize(img3,n,m );

            Image newImage2 = SwingFXUtils.toFXImage(img3,null);
            Zakathchick.setImage(newImage2);
        });
    }catch (Exception e){

    }
    }
    @FXML
    private ImageView Tc4;
    private void onTc4() throws IOException {
        try {
        showColorChooserDialog(selectedColor -> {
            Color color2 = selectedColor;
            Color[][] color1 =  AbstraktSelection.StanokSheme();
            Image imge = ImageStanok.getImage();
            BufferedImage image1 = SwingFXUtils.fromFXImage(imge,null);
            BufferedImage image2;
            if(EditBufer==null){
                Image imge1 = imageView.getImage();
                image2 = SwingFXUtils.fromFXImage(imge1,null);
            }else {
                image2 = EditBufer;
            }

            BufferedImage finish = AdvancedReplaceColor(image1,image2,color1[3][2],color2);

            try {
                BufferedImage BufOc1 = createImage(color2);
                Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1,null);
                Tc4.setImage(ImgOc1);
            } catch (Exception e) {Tc4.setImage(null);}
            EditBufer = finish;
            //Image newImage = SwingFXUtils.toFXImage(finish,null);
            //imageView.setImage(newImage);

            BufferedImage img3 = Converter.PhotoDlaZak(finish);
            Vizual = img3;
            int m = (int) (img3.getHeight()/2.5);
            int n = (int) (img3.getWidth()/2.5);
            img3 = PhotoEdit.resize(img3,n,m );

            Image newImage2 = SwingFXUtils.toFXImage(img3,null);
            Zakathchick.setImage(newImage2);
        });
    }catch (Exception e){

    }
    }

    @FXML
    private ImageView Oc5;
    private void onOc5() throws IOException {
        try {
        showColorChooserDialog(selectedColor -> {
            Color color2 = selectedColor;
            Color[][] color1 =  AbstraktSelection.StanokSheme();
            Image imge = ImageStanok.getImage();
            BufferedImage image1 = SwingFXUtils.fromFXImage(imge,null);
            BufferedImage image2;
            if(EditBufer==null){
                Image imge1 = imageView.getImage();
                image2 = SwingFXUtils.fromFXImage(imge1,null);
            }else {
                image2 = EditBufer;
            }

            BufferedImage finish = AdvancedReplaceColor(image1,image2,color1[4][0],color2);

            try {
                BufferedImage BufOc1 = createImage(color2);
                Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1,null);
                Oc5.setImage(ImgOc1);
            } catch (Exception e) {Oc5.setImage(null);}

            //Image newImage = SwingFXUtils.toFXImage(finish,null);
            //imageView.setImage(newImage);
            EditBufer = finish;
            BufferedImage img3 = Converter.PhotoDlaZak(finish);
            Vizual = img3;
            int m = (int) (img3.getHeight()/2.5);
            int n = (int) (img3.getWidth()/2.5);
            img3 = PhotoEdit.resize(img3,n,m );

            Image newImage2 = SwingFXUtils.toFXImage(img3,null);
            Zakathchick.setImage(newImage2);
        });
    }catch (Exception e){

    }
    }
    @FXML
    private ImageView Dc5;
    private void onDc5() throws IOException {
        try {
        showColorChooserDialog(selectedColor -> {
            Color color2 = selectedColor;
            Color[][] color1 =  AbstraktSelection.StanokSheme();
            Image imge = ImageStanok.getImage();
            BufferedImage image1 = SwingFXUtils.fromFXImage(imge,null);
            BufferedImage image2;
            if(EditBufer==null){
                Image imge1 = imageView.getImage();
                image2 = SwingFXUtils.fromFXImage(imge1,null);
            }else {
                image2 = EditBufer;
            }

            BufferedImage finish = AdvancedReplaceColor(image1,image2,color1[4][1],color2);

            try {
                BufferedImage BufOc1 = createImage(color2);
                Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1,null);
                Dc5.setImage(ImgOc1);
            } catch (Exception e) {Dc5.setImage(null);}

            //Image newImage = SwingFXUtils.toFXImage(finish,null);
            //imageView.setImage(newImage);
            EditBufer = finish;
            BufferedImage img3 = Converter.PhotoDlaZak(finish);
            Vizual = img3;
            int m = (int) (img3.getHeight()/2.5);
            int n = (int) (img3.getWidth()/2.5);
            img3 = PhotoEdit.resize(img3,n,m );

            Image newImage2 = SwingFXUtils.toFXImage(img3,null);
            Zakathchick.setImage(newImage2);
        });
    }catch (Exception e){

    }
    }
    @FXML
    private ImageView Tc5;
    private void onTc5() throws IOException {
        try {
        showColorChooserDialog(selectedColor -> {
            Color color2 = selectedColor;
            Color[][] color1 =  AbstraktSelection.StanokSheme();
            Image imge = ImageStanok.getImage();
            BufferedImage image1 = SwingFXUtils.fromFXImage(imge,null);
            BufferedImage image2;
            if(EditBufer==null){
                Image imge1 = imageView.getImage();
                image2 = SwingFXUtils.fromFXImage(imge1,null);
            }else {
                image2 = EditBufer;
            }

            BufferedImage finish = AdvancedReplaceColor(image1,image2,color1[4][2],color2);

            try {
                BufferedImage BufOc1 = createImage(color2);
                Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1,null);
                Tc5.setImage(ImgOc1);
            } catch (Exception e) {Tc5.setImage(null);}

            //Image newImage = SwingFXUtils.toFXImage(finish,null);
            //imageView.setImage(newImage);
            EditBufer = finish;
            BufferedImage img3 = Converter.PhotoDlaZak(finish);
            Vizual = img3;
            int m = (int) (img3.getHeight()/2.5);
            int n = (int) (img3.getWidth()/2.5);
            img3 = PhotoEdit.resize(img3,n,m );

            Image newImage2 = SwingFXUtils.toFXImage(img3,null);
            Zakathchick.setImage(newImage2);
        });
    }catch (Exception e){

    }
    }
    @FXML
    private ImageView BackGraund;
    private void onBackGraund() throws IOException {
        try {
            showColorChooserDialog(selectedColor -> {
                Color color2 = selectedColor;
                Color[][] color1 = AbstraktSelection.StanokSheme();
                Image imge = ImageStanok.getImage();
                BufferedImage image1 = SwingFXUtils.fromFXImage(imge, null);
                BufferedImage image2;
                if(EditBufer==null){
                    Image imge1 = imageView.getImage();
                    image2 = SwingFXUtils.fromFXImage(imge1,null);
                }else {
                    image2 = EditBufer;
                }

                BufferedImage finish = AdvancedReplaceColor(image1, image2, color1[5][0], color2);

                try {
                    BufferedImage BufOc1 = createImage(color2);
                    Image ImgOc1 = SwingFXUtils.toFXImage(BufOc1, null);
                    BackGraund.setImage(ImgOc1);
                } catch (Exception e) {
                    BackGraund.setImage(null);
                }

                //Image newImage = SwingFXUtils.toFXImage(finish, null);
                //imageView.setImage(newImage);
                EditBufer = finish;
                BufferedImage img3 = Converter.PhotoDlaZak(finish);
                Vizual = img3;
                int m = (int) (img3.getHeight() / 2.5);
                int n = (int) (img3.getWidth() / 2.5);
                img3 = PhotoEdit.resize(img3, n, m);

                Image newImage2 = SwingFXUtils.toFXImage(img3, null);
                Zakathchick.setImage(newImage2);
            });
        }catch (Exception e){

        }
    }


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
    private void onResClick(ActionEvent event){
        Image image = imageView.getImage();
        BufferedImage img = SwingFXUtils.fromFXImage(image,null);
        try {
            int n = Integer.parseInt(LabelM.getText());
            int m = Integer.parseInt(LabelN.getText());
            img = PhotoEdit.resize(img,n,m);
        }catch (Exception e){

        }
        Image newImage = SwingFXUtils.toFXImage(img,null);
        imageView.setImage(newImage);

        int width = (int) imageView.getImage().getWidth();
        int height = (int) imageView.getImage().getHeight();

        LabelM.setText(String.valueOf(width));
        LabelN.setText(String.valueOf(height));
        newGweight = width;
    }
    @FXML
    private TextField LabelP;
    @FXML
    private void onResPClick(ActionEvent event){
        Image image = imageView.getImage();
        BufferedImage img = SwingFXUtils.fromFXImage(image, null);
        try {
            int m = Integer.parseInt(LabelM.getText());
            int n = Integer.parseInt(LabelN.getText());
            double con;
            String labelText = LabelP.getText();
            if (labelText.endsWith("%")) {
                con = Double.parseDouble(labelText.replace("%", "")) / 100 + 1.0;
            } else {
                con = Double.parseDouble(labelText) / 10;
            }
            n = (int) (n * con);
            img = PhotoEdit.resize(img, m, n);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Image newImage = SwingFXUtils.toFXImage(img, null);
        imageView.setImage(newImage);

        int width = (int) imageView.getImage().getWidth();
        int height = (int) imageView.getImage().getHeight();

        LabelM.setText(String.valueOf(width));
        LabelN.setText(String.valueOf(height));
    }
    @FXML
    private void onRemuve(ActionEvent event){
        Image image = imageView.getImage();
        BufferedImage img = SwingFXUtils.fromFXImage(image,null);
        try {
            int m = Gheigh;
            int n = Gweight;
            img = PhotoEdit.resize(img,n,m);
        }catch (Exception e){

        }
        Image newImage = SwingFXUtils.toFXImage(img,null);
        imageView.setImage(newImage);

        int width = (int) imageView.getImage().getWidth();
        int height = (int) imageView.getImage().getHeight();

        LabelM.setText(String.valueOf(width));
        LabelN.setText(String.valueOf(height));
        newGweight=width;
    }

    public interface Callback<T> {
            void call (T result) throws IOException;
    }

    public static void showColorChooserDialog(Callback<Color> callback) {
        try {
            // Створюємо об'єкт JFrame (можна використовувати існуючий вікно)
            JFrame frame = new JFrame("Color Chooser Example");

            // Створюємо JButton, який викличе вікно палітри
            JButton chooseColorButton = new JButton("Choose Color");

            // Додаємо слухача подій до кнопки
            chooseColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        // Відображаємо вікно палітри та зберігаємо вибраний колір
                        selectedColor = JColorChooser.showDialog(frame, "Choose a Color", Color.BLACK);

                        // Перевіряємо, чи колір не є null (користувач скасував вибір)
                        if (selectedColor != null) {
                            // Викликаємо додаткові дії з вибраним кольором, якщо потрібно
                            // Наприклад, можна викликати інший метод з обраним колором

                            // Закриваємо вікно палітри
                            frame.dispose();

                        }
                        callback.call(selectedColor);
                    } catch (Exception t) {

                    }
                }
            });
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // Викликаємо callback та закриваємо вікно
                    try {
                        callback.call(selectedColor);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    frame.dispose();
                }
            });

            // Додаємо кнопку до вікна
            frame.add(chooseColorButton);

            // Налаштовуємо параметри вікна
            frame.setSize(300, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());
            frame.setAlwaysOnTop(true);
            frame.setVisible(true);
        }catch (Exception e){

        }
    }

    public static BufferedImage AdvancedReplaceColor(BufferedImage image1, BufferedImage image2, Color color1, Color color2) {
        try {
            int width = image1.getWidth();
            int height = image1.getHeight();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Отримуємо колір пікселя з першого зображення
                    Color currentColor = new Color(image1.getRGB(x, y));

                    // Порівнюємо колір пікселя з першого зображення з переданим колором
                    if (currentColor.equals(color1)) {
                        // Якщо колір співпадає, то замінюємо піксель на відповідний колір з другого зображення
                        image2.setRGB(x, y, color2.getRGB());
                    }
                }
            }
        }catch (Exception e){

        }
        return image2;
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
                    fullResetImage = new Image(file.toURI().toString());
                    resetImage = new Image(file.toURI().toString());
                    // Get and display image dimensions
                    int width = photoLoader.getImageWidth();
                    int height = photoLoader.getImageHeight();
                    LabelM.setText(String.valueOf(width));
                    LabelN.setText(String.valueOf(height));
                    Gheigh = height;
                    newGweight = width;
                    Gweight = width;

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
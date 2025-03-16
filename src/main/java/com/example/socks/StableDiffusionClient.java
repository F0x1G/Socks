package com.example.socks;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import org.json.JSONObject;

/**
 * Єдиний клас для взаємодії з Stable Diffusion API через AUTOMATIC1111 WebUI
 */
public class StableDiffusionClient {
    private final String apiUrl;
    private final int timeout;

    /**
     * Конструктор класу
     * @param host хост сервера AUTOMATIC1111 WebUI (наприклад, "http://localhost:7860")
     * @param timeout таймаут з'єднання в мілісекундах
     */
    public StableDiffusionClient(String host, int timeout) {
        this.apiUrl = host + "/sdapi/v1";
        this.timeout = timeout;
    }

    /**
     * Конструктор з значеннями за замовчуванням
     */
    public StableDiffusionClient() {
        this("http://127.0.0.1:7860", 120000);
    }

    /**
     * Генерує зображення з текстового запиту
     * @param prompt текстовий опис зображення
     * @return згенероване зображення
     * @throws IOException у випадку помилки з'єднання
     */
    public Image generateImage(String prompt) throws IOException {
        return generateImage(prompt, new Parameters());
    }

    /**
     * Генерує зображення з текстового запиту та додаткових параметрів
     * @param prompt текстовий опис зображення
     * @param params додаткові параметри генерації
     * @return згенероване зображення
     * @throws IOException у випадку помилки з'єднання
     */
    public Image generateImage(String prompt, Parameters params) throws IOException {
        JSONObject requestBody = params.toTextToImageJson(prompt);
        String endpoint = this.apiUrl + "/txt2img";

        String response = sendPostRequest(endpoint, requestBody.toString());

        // Парсимо відповідь та отримуємо закодоване зображення
        JSONObject responseJson = new JSONObject(response);
        String imageBase64 = responseJson.getJSONArray("images").getString(0);

        // Декодуємо Base64 у зображення
        byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
        return ImageIO.read(new ByteArrayInputStream(imageBytes));
    }

    /**
     * Обробляє існуюче зображення за допомогою текстового запиту (img2img)
     * @param inputImage вхідне зображення
     * @param prompt текстовий опис бажаного результату
     * @return оброблене зображення
     * @throws IOException у випадку помилки з'єднання
     */
    public Image processImage(Image inputImage, String prompt) throws IOException {
        return processImage(inputImage, prompt, new Parameters());
    }

    /**
     * Обробляє існуюче зображення за допомогою текстового запиту та додаткових параметрів
     * @param inputImage вхідне зображення
     * @param prompt текстовий опис бажаного результату
     * @param params додаткові параметри обробки
     * @return оброблене зображення
     * @throws IOException у випадку помилки з'єднання
     */
    public Image processImage(Image inputImage, String prompt, Parameters params) throws IOException {
        // Конвертуємо вхідне зображення в Base64
        String imageBase64 = imageToBase64(inputImage);

        JSONObject requestBody = params.toImageToImageJson(prompt, imageBase64);
        String endpoint = this.apiUrl + "/img2img";

        String response = sendPostRequest(endpoint, requestBody.toString());

        // Парсимо відповідь та отримуємо закодоване зображення
        JSONObject responseJson = new JSONObject(response);
        String resultImageBase64 = responseJson.getJSONArray("images").getString(0);

        // Декодуємо Base64 у зображення
        byte[] imageBytes = Base64.getDecoder().decode(resultImageBase64);
        return ImageIO.read(new ByteArrayInputStream(imageBytes));
    }

    /**
     * Зберігає параметри генерації у JSON файл
     * @param filePath шлях до файлу
     * @param prompt текстовий опис
     * @param params параметри генерації
     * @param isImageToImage прапорець, чи є це параметри для img2img
     * @throws IOException у випадку помилки запису файлу
     */
    public void saveParamsToJsonFile(String filePath, String prompt, Parameters params, boolean isImageToImage) throws IOException {
        JSONObject json;

        if (isImageToImage) {
            json = params.toImageToImageJson(prompt, "");
            // Видаляємо поле з зображенням для кращої читабельності
            json.remove("init_images");
            json.put("using_init_image", true);
        } else {
            json = params.toTextToImageJson(prompt);
        }

        // Додаємо метадані
        json.put("generation_type", isImageToImage ? "img2img" : "txt2img");
        json.put("timestamp", System.currentTimeMillis());

        // Записуємо у файл
        Files.write(
                new File(filePath).toPath(),
                json.toString(2).getBytes(StandardCharsets.UTF_8)
        );
    }

    private String sendPostRequest(String endpoint, String jsonBody) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);

        // Відправляємо запит
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Зчитуємо відповідь
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        return response.toString();
    }

    private String imageToBase64(Image image) throws IOException {
        // Конвертація Image у Base64
        java.awt.image.BufferedImage bufferedImage;
        if (image instanceof java.awt.image.BufferedImage) {
            bufferedImage = (java.awt.image.BufferedImage) image;
        } else {
            bufferedImage = new java.awt.image.BufferedImage(
                    image.getWidth(null),
                    image.getHeight(null),
                    java.awt.image.BufferedImage.TYPE_INT_ARGB
            );
            java.awt.Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
        }

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Внутрішній клас для параметрів генерації зображень в Stable Diffusion
     */
    public static class Parameters {
        private String prompt = ""; // Доданий параметр prompt
        private String negativePrompt = "";
        private int steps = 20;
        private int width = 512;
        private int height = 512;
        private double cfgScale = 7.0;
        private String sampler = "Euler a";
        private long seed = -1; // -1 означає випадкове число
        private int batchSize = 1;
        private double denoisingStrength = 0.75; // Тільки для img2img

        // Гетери та сетери
        public String getPrompt() {
            return prompt;
        }

        public Parameters setPrompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public String getNegativePrompt() {
            return negativePrompt;
        }

        public Parameters setNegativePrompt(String negativePrompt) {
            this.negativePrompt = negativePrompt;
            return this;
        }

        public int getSteps() {
            return steps;
        }

        public Parameters setSteps(int steps) {
            this.steps = steps;
            return this;
        }

        public int getWidth() {
            return width;
        }

        public Parameters setWidth(int width) {
            this.width = width;
            return this;
        }

        public int getHeight() {
            return height;
        }

        public Parameters setHeight(int height) {
            this.height = height;
            return this;
        }

        public double getCfgScale() {
            return cfgScale;
        }

        public Parameters setCfgScale(double cfgScale) {
            this.cfgScale = cfgScale;
            return this;
        }

        public String getSampler() {
            return sampler;
        }

        public Parameters setSampler(String sampler) {
            this.sampler = sampler;
            return this;
        }

        public long getSeed() {
            return seed;
        }

        public Parameters setSeed(long seed) {
            this.seed = seed;
            return this;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public Parameters setBatchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public double getDenoisingStrength() {
            return denoisingStrength;
        }

        public Parameters setDenoisingStrength(double denoisingStrength) {
            this.denoisingStrength = denoisingStrength;
            return this;
        }

        /**
         * Створює JSON з параметрами для генерації зображення з тексту (txt2img)
         * @param prompt текстовий опис зображення
         * @return JSONObject з параметрами запиту
         */
        public JSONObject toTextToImageJson(String prompt) {
            // Якщо prompt переданий як параметр, використовуємо його
            // Якщо ні, використовуємо prompt з властивості класу
            String finalPrompt = prompt != null && !prompt.isEmpty() ? prompt : this.prompt;

            JSONObject json = new JSONObject();

            json.put("prompt", finalPrompt);
            json.put("negative_prompt", this.negativePrompt);
            json.put("steps", this.steps);
            json.put("width", this.width);
            json.put("height", this.height);
            json.put("cfg_scale", this.cfgScale);
            json.put("sampler_name", this.sampler);
            json.put("seed", this.seed);
            json.put("batch_size", this.batchSize);

            return json;
        }

        /**
         * Створює JSON з параметрами для обробки зображення (img2img)
         * @param prompt текстовий опис бажаного результату
         * @param imageBase64 вхідне зображення в форматі Base64
         * @return JSONObject з параметрами запиту
         */
        public JSONObject toImageToImageJson(String prompt, String imageBase64) {
            JSONObject json = toTextToImageJson(prompt);

            // Додаткові параметри для img2img
            json.put("init_images", new String[]{imageBase64});
            json.put("denoising_strength", this.denoisingStrength);

            return json;
        }
    }
}
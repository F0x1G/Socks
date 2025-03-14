package com.example.socks;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class A1111ForgeStableDiffusionClient {
    // URL локального A1111 Forge інтерфейсу
    private static final String API_URL = "http://127.0.0.1:7860";
    // Головні ендпоінти API
    private static final String TEXT_TO_IMAGE_ENDPOINT = "/sdapi/v1/txt2img";
    private static final String IMAGE_TO_IMAGE_ENDPOINT = "/sdapi/v1/img2img";
    private static final String SD_MODELS_ENDPOINT = "/sdapi/v1/sd-models";
    private static final String SAMPLERS_ENDPOINT = "/sdapi/v1/samplers";
    private static final String PROGRESS_ENDPOINT = "/sdapi/v1/progress";
    private static final String OPTIONS_ENDPOINT = "/sdapi/v1/options";

    public static void main(String[] args) {
        // Перевіряємо доступність API
        checkApiAvailability();

        // Отримуємо інформацію про API (доступні моделі, семплери)
        getApiInfo();

        // Генеруємо зображення за допомогою локального API
        generateImage("cat");
    }

    /**
     * Перевіряє доступність A1111 Forge API
     */
    private static void checkApiAvailability() {
        System.out.println("Перевірка доступності A1111 Forge...");

        try {
            URL url = new URL(API_URL + PROGRESS_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                System.out.println("A1111 Forge доступний! Код відповіді: " + responseCode);

                // Отримуємо статус прогресу
                try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8.name())) {
                    String responseBody = scanner.useDelimiter("\\A").next();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    boolean isGenerating = jsonResponse.optBoolean("is_generating", false);
                    float progress = jsonResponse.optFloat("progress", 0.0f);

                    if (isGenerating) {
                        System.out.println("Увага: A1111 Forge зараз виконує генерацію (прогрес: " +
                                Math.round(progress * 100) + "%)");
                    } else {
                        System.out.println("A1111 Forge готовий до роботи");
                    }
                }
            } else {
                System.out.println("A1111 Forge недоступний. Код відповіді: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            System.out.println("Помилка при перевірці API: " + e.getMessage());
            System.out.println("Переконайтеся, що A1111 Forge запущений з параметром --api");
        }
    }

    /**
     * Отримує інформацію про моделі та семплери з API Forge
     */
    private static void getApiInfo() {
        getAvailableModels();
        getAvailableSamplers();
        getCurrentOptions();
    }

    /**
     * Отримує список доступних моделей
     */
    private static void getAvailableModels() {
        System.out.println("\nОтримання списку доступних моделей...");

        try {
            URL url = new URL(API_URL + SD_MODELS_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8.name())) {
                    String responseBody = scanner.useDelimiter("\\A").next();
                    JSONArray jsonResponse = new JSONArray(responseBody);

                    System.out.println("Доступні моделі (" + jsonResponse.length() + "):");
                    for (int i = 0; i < Math.min(jsonResponse.length(), 5); i++) {
                        JSONObject model = jsonResponse.getJSONObject(i);
                        System.out.println(" - " + model.getString("title") +
                                " (" + model.getString("model_name") + ")");
                    }

                    if (jsonResponse.length() > 5) {
                        System.out.println(" - ... та ще " + (jsonResponse.length() - 5) + " моделей");
                    }
                }
            } else {
                System.out.println("Помилка при отриманні списку моделей. Код: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            System.out.println("Помилка при виконанні запиту: " + e.getMessage());
        }
    }

    /**
     * Отримує список доступних семплерів
     */
    private static void getAvailableSamplers() {
        System.out.println("\nОтримання списку доступних семплерів...");

        try {
            URL url = new URL(API_URL + SAMPLERS_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8.name())) {
                    String responseBody = scanner.useDelimiter("\\A").next();
                    JSONArray jsonResponse = new JSONArray(responseBody);

                    System.out.println("Доступні семплери:");
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject sampler = jsonResponse.getJSONObject(i);
                        System.out.println(" - " + sampler.getString("name"));
                    }
                }
            } else {
                System.out.println("Помилка при отриманні списку семплерів. Код: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            System.out.println("Помилка при виконанні запиту: " + e.getMessage());
        }
    }

    /**
     * Отримує поточні налаштування A1111 Forge
     */
    private static void getCurrentOptions() {
        System.out.println("\nОтримання поточних налаштувань...");

        try {
            URL url = new URL(API_URL + OPTIONS_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8.name())) {
                    String responseBody = scanner.useDelimiter("\\A").next();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    // Отримуємо основну інформацію про поточні налаштування
                    String currentModel = jsonResponse.optString("sd_model_checkpoint", "невідомо");

                    System.out.println("Поточна модель: " + currentModel);
                    System.out.println("CLIP skip: " + jsonResponse.optInt("CLIP_stop_at_last_layers", 1));
                }
            } else {
                System.out.println("Помилка при отриманні налаштувань. Код: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            System.out.println("Помилка при виконанні запиту: " + e.getMessage());
        }
    }

    /**
     * Генерує зображення за допомогою A1111 Forge API
     * @param prompt Текстовий опис зображення для генерації
     */
    private static void generateImage(String prompt) {
        System.out.println("\nГенерація зображення з промптом: " + prompt);

        try {
            URL url = new URL(API_URL + TEXT_TO_IMAGE_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Підготовка JSON запиту відповідно до API Forge
            JSONObject requestBody = new JSONObject();
            requestBody.put("prompt", prompt);
            requestBody.put("negative_prompt", "");
            requestBody.put("steps", 28);
            requestBody.put("cfg_scale", 8.0);
            requestBody.put("width", 524);
            requestBody.put("height", 524);
            requestBody.put("sampler_name", "Euler a");
            requestBody.put("batch_size", 1);
            requestBody.put("batch_count", 1);

            // Додаткові параметри Forge
            requestBody.put("enable_hr", true);                // Високе розширення
            requestBody.put("hr_scale", 1.5);                  // Масштаб високого розширення
            requestBody.put("denoising_strength", 0.6);        // Сила шумопонижування для HR
            requestBody.put("restore_faces", true);            // Покращення облич (якщо є)

            // Параметри для розширеного контролю (якщо доступно у Forge)
            JSONObject override_settings = new JSONObject();
            override_settings.put("CLIP_stop_at_last_layers", 1);  // CLIP skip
            requestBody.put("override_settings", override_settings);

            // Відправка запиту
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Отримання та обробка відповіді
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Обробка успішної відповіді
                try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8.name())) {
                    String responseBody = scanner.useDelimiter("\\A").next();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    // Зберігаємо згенероване зображення та інформацію про параметри
                    saveGeneratedImage(jsonResponse);
                    saveSeedInfo(jsonResponse);
                }
            } else {
                // Обробка помилкової відповіді
                try (Scanner scanner = new Scanner(connection.getErrorStream(), StandardCharsets.UTF_8.name())) {
                    String errorResponse = scanner.useDelimiter("\\A").next();
                    System.out.println("Помилка при генерації зображення. Код: " + responseCode);
                    System.out.println("Відповідь сервера: " + errorResponse);
                }
            }

            connection.disconnect();
        } catch (IOException e) {
            System.out.println("Помилка при виконанні запиту: " + e.getMessage());
        }
    }

    /**
     * Зберігає згенероване зображення з відповіді API
     * @param jsonResponse JSON відповідь від API
     */
    private static void saveGeneratedImage(JSONObject jsonResponse) {
        try {
            // Отримуємо base64 зображення з відповіді WebUI API
            JSONArray images = jsonResponse.getJSONArray("images");

            if (images.length() > 0) {
                String base64Image = images.getString(0);

                // Декодуємо та зберігаємо зображення
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                Files.write(Paths.get("generated_image.png"), imageBytes);

                System.out.println("Зображення успішно згенеровано та збережено у файл generated_image.png");
            } else {
                System.out.println("У відповіді відсутні зображення");
            }
        } catch (Exception e) {
            System.out.println("Помилка при збереженні зображення: " + e.getMessage());
        }
    }

    /**
     * Зберігає інформацію про seed та параметри генерації
     * @param jsonResponse JSON відповідь від API
     */
    private static void saveSeedInfo(JSONObject jsonResponse) {
        try {
            JSONObject info = new JSONObject(jsonResponse.getString("info"));

            // Отримуємо важливі параметри
            long seed = info.optLong("seed", -1);
            String sampler = info.optString("sampler_name", "невідомо");
            int steps = info.optInt("steps", 0);
            double cfgScale = info.optDouble("cfg_scale", 0.0);

            System.out.println("\nПараметри генерації:");
            System.out.println("Seed: " + seed);
            System.out.println("Семплер: " + sampler);
            System.out.println("Кроки: " + steps);
            System.out.println("CFG Scale: " + cfgScale);

            // Зберігаємо інформацію у текстовий файл для повторення результату
            String infoText = "Prompt: " + info.optString("prompt", "") + "\n" +
                    "Negative prompt: " + info.optString("negative_prompt", "") + "\n" +
                    "Seed: " + seed + "\n" +
                    "Sampler: " + sampler + "\n" +
                    "Steps: " + steps + "\n" +
                    "CFG Scale: " + cfgScale + "\n" +
                    "Size: " + info.optInt("width", 0) + "x" + info.optInt("height", 0);

            Files.write(Paths.get("generation_info.txt"), infoText.getBytes(StandardCharsets.UTF_8));
            System.out.println("Інформація про параметри генерації збережена у файл generation_info.txt");

        } catch (Exception e) {
            System.out.println("Помилка при збереженні інформації про генерацію: " + e.getMessage());
        }
    }
}
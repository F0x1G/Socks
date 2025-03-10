package com.example.socks;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

public class StableDiffusionClient {

    private static final String BASE_URL = "http://127.0.0.1:7860";

    /**
     * Генерує зображення через Gradio API за допомогою правильного індексу функції
     *
     * @param prompt Текстовий запит для генерації зображення
     * @param negativePrompt Негативний текстовий запит (що не повинно бути на зображенні)
     * @param outputPath Шлях для збереження згенерованого зображення
     * @return Шлях до згенерованого зображення
     * @throws IOException У випадку помилки вводу/виводу
     * @throws InterruptedException У випадку переривання HTTP запиту
     */
    public static String generateImageViaGradio(String prompt, String negativePrompt, String outputPath)
            throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMinutes(5))
                .build();

        // Отримуємо правильний fn_index з API
        int fnIndex = getGradioFnIndex();

        // Використовуємо ендпоінт градіо API
        String apiUrl = BASE_URL + "/api/predict";

        // Генеруємо унікальний session_hash
        String sessionHash = UUID.randomUUID().toString();

        // Створюємо JSON для запиту відповідно до очікуваної структури Gradio API
        JSONObject requestBody = new JSONObject();
        requestBody.put("session_hash", sessionHash);
        requestBody.put("fn_index", fnIndex); // Використовуємо визначений індекс

        // Створюємо правильну структуру даних для запиту
        JSONArray data = new JSONArray();

        // Базова структура даних для txt2img у Gradio
        data.put(prompt);                              // Основний промпт
        data.put(negativePrompt);                      // Негативний промпт
        data.put(new JSONObject()                      // Налаштування генерації
                .put("seed", -1)                           // -1 для випадкового значення
                .put("batch_size", 1)
                .put("steps", 30)
                .put("cfg_scale", 7)
                .put("width", 512)
                .put("height", 512)
                .put("sampler_index", "Euler a")           // Використовуємо назву семплера
                .put("restore_faces", false)
                .put("denoising_strength", 0.7)
        );

        requestBody.put("data", data);

        // Будуємо HTTP запит
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(BodyPublishers.ofString(requestBody.toString()))
                .build();

        System.out.println("Відправляємо запит до Gradio API...");
        System.out.println("Запит: " + requestBody.toString());

        // Відправляємо запит і отримуємо відповідь
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("API повернув помилку: " + response.statusCode() + " - " + response.body());
        }

        System.out.println("Отримано відповідь від API. Обробляємо зображення...");

        // Обробляємо відповідь
        JSONObject jsonResponse = new JSONObject(response.body());
        System.out.println("Структура відповіді: " + jsonResponse.toString(2));

        // Витягуємо зображення з відповіді
        String base64Image = extractBase64Image(jsonResponse);

        if (base64Image.isEmpty()) {
            System.out.println("Відповідь API: " + response.body());
            throw new IOException("Не вдалося знайти зображення у відповіді API");
        }

        // Декодуємо base64
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        // Створюємо директорію, якщо вона не існує
        Path outputDir = Paths.get(outputPath).getParent();
        if (outputDir != null && !Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        // Зберігаємо зображення
        try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
            outputStream.write(imageBytes);
        }

        System.out.println("Зображення збережено за шляхом: " + outputPath);

        return outputPath;
    }

    /**
     * Витягує base64 зображення з відповіді Gradio API
     *
     * @param jsonResponse JSON-відповідь від API
     * @return Base64-кодоване зображення або порожній рядок, якщо зображення не знайдено
     */
    private static String extractBase64Image(JSONObject jsonResponse) {
        if (!jsonResponse.has("data")) {
            return "";
        }

        // Структура відповіді Gradio може відрізнятися
        JSONArray dataArray = jsonResponse.getJSONArray("data");

        // Перевіряємо всі можливі варіанти структури
        for (int i = 0; i < dataArray.length(); i++) {
            Object item = dataArray.get(i);

            // Варіант 1: Пряме зображення у data масиві
            if (item instanceof String) {
                String str = (String) item;
                if (str.startsWith("data:image")) {
                    return str.substring(str.indexOf(",") + 1);
                }
            }

            // Варіант 2: Зображення у вкладеному об'єкті
            else if (item instanceof JSONObject) {
                JSONObject obj = (JSONObject) item;

                // Пряме поле зображення
                if (obj.has("image")) {
                    String imgStr = obj.getString("image");
                    if (imgStr.startsWith("data:image")) {
                        return imgStr.substring(imgStr.indexOf(",") + 1);
                    }
                    return imgStr;
                }

                // Поле даних, що містить зображення
                else if (obj.has("data")) {
                    String dataStr = obj.getString("data");
                    if (dataStr.startsWith("data:image")) {
                        return dataStr.substring(dataStr.indexOf(",") + 1);
                    }
                }

                // Перевіряємо, чи є вкладений масив
                else if (obj.has("value") && obj.get("value") instanceof JSONArray) {
                    JSONArray valueArray = obj.getJSONArray("value");
                    for (int j = 0; j < valueArray.length(); j++) {
                        if (valueArray.get(j) instanceof String) {
                            String str = valueArray.getString(j);
                            if (str.startsWith("data:image")) {
                                return str.substring(str.indexOf(",") + 1);
                            }
                        }
                    }
                }
            }
        }

        return "";
    }

    /**
     * Отримує правильний індекс функції txt2img з Gradio API
     *
     * @return Індекс функції, що відповідає за генерацію зображень
     * @throws IOException У випадку помилки вводу/виводу
     * @throws InterruptedException У випадку переривання HTTP запиту
     */
    private static int getGradioFnIndex() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        // Запит до API для отримання списку функцій
        String apiUrl = BASE_URL + "/api/v1/app";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            // Якщо цей API недоступний, спробуємо використати функцію 0
            System.out.println("Не вдалося отримати список функцій Gradio. Використовуємо індекс за замовчуванням.");
            return 0;
        }

        JSONObject jsonResponse = new JSONObject(response.body());

        if (jsonResponse.has("dependencies")) {
            JSONArray dependencies = jsonResponse.getJSONArray("dependencies");

            // Шукаємо функцію, що відповідає за txt2img
            for (int i = 0; i < dependencies.length(); i++) {
                JSONObject dependency = dependencies.getJSONObject(i);
                if (dependency.has("inputs") && dependency.has("outputs")) {
                    JSONArray outputs = dependency.getJSONArray("outputs");

                    // Перевіряємо, чи виходи містять компоненти, які часто використовуються для відображення зображень
                    for (int j = 0; j < outputs.length(); j++) {
                        int outputId = outputs.getInt(j);
                        // Перевіряємо чи вихід відповідає за галерею або зображення
                        if (jsonResponse.has("components")) {
                            JSONArray components = jsonResponse.getJSONArray("components");
                            if (outputId < components.length()) {
                                JSONObject component = components.getJSONObject(outputId);
                                String compType = component.optString("type", "");
                                if (compType.equals("gallery") || compType.equals("image")) {
                                    return i; // Знайдено індекс функції, що генерує зображення
                                }
                            }
                        }
                    }
                }
            }
        }

        // Якщо не знайдено конкретну функцію, використовуємо значення за замовчуванням
        return 0;
    }

    /**
     * Тестовий метод для надсилання простого запиту та друку відповіді
     */
    public static void testApiEndpoints() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMinutes(1))
                .build();

        System.out.println("Тестуємо доступні ендпоінти...");

        // Перевіряємо різні варіанти Gradio API
        String[] gradioEndpoints = {
                "/api/predict",
                "/api/v1/app",
                "/queue/join",
                "/run/predict"
        };

        for (String endpoint : gradioEndpoints) {
            String apiUrl = BASE_URL + endpoint;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            System.out.println("Тестуємо ендпоінт: " + apiUrl);
            try {
                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                System.out.println("Статус: " + response.statusCode());
                if (response.statusCode() == 200) {
                    System.out.println("Відповідь: " + response.body().substring(0, Math.min(500, response.body().length())) + "...");
                } else {
                    System.out.println("Відповідь: " + response.body());
                }
            } catch (Exception e) {
                System.out.println("Помилка при запиті до " + apiUrl + ": " + e.getMessage());
            }
            System.out.println("-----------------------------");
        }
    }

    /**
     * Приклад використання
     */
    public static void main(String[] args) {
        try {
            String prompt = "beautiful landscape with mountains and lake at sunset, highly detailed";
            String negativePrompt = "blurry, low quality, deformed";
            String outputPath = "./generated-images/landscape-gradio.png";

            // Тестуємо доступні ендпоінти
            testApiEndpoints();

            // Генеруємо зображення через Gradio API
            System.out.println("\nГенеруємо зображення через Gradio API...");
            generateImageViaGradio(prompt, negativePrompt, outputPath);

            System.out.println("Генерація зображення завершена успішно");
        } catch (Exception e) {
            System.err.println("Помилка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
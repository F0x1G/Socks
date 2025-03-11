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
     * Генерує зображення через комбінований підхід до Gradio/API інтерфейсів
     *
     * @param prompt Текстовий запит для генерації зображення
     * @param negativePrompt Негативний текстовий запит (що не повинно бути на зображенні)
     * @param outputPath Шлях для збереження згенерованого зображення
     * @return Шлях до згенерованого зображення
     * @throws IOException У випадку помилки вводу/виводу
     * @throws InterruptedException У випадку переривання HTTP запиту
     */
    public static String generateImage(String prompt, String negativePrompt, String outputPath)
            throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMinutes(5))
                .build();

        // Спочатку отримуємо структуру інтерфейсу, щоб визначити правильну точку входу
        System.out.println("Аналізуємо структуру Gradio інтерфейсу...");
        JSONObject interfaceStructure = getInterfaceStructure();

        // Перевіряємо доступні API методи та їх структуру
        String apiMethod = determineApiMethod(interfaceStructure);
        System.out.println("Визначено метод API: " + apiMethod);

        // Здійснюємо запит на генерацію зображення
        System.out.println("Генеруємо зображення...");
        HttpResponse<String> response;

        switch (apiMethod) {
            case "txt2img_gradio":
                response = requestGradioTxt2Img(client, prompt, negativePrompt);
                break;
            case "comfy":
                response = requestComfyWorkflow(client, prompt, negativePrompt);
                break;
            case "api_predict":
                response = requestApiPredict(client, prompt, negativePrompt);
                break;
            default:
                response = requestRunPredict(client, prompt, negativePrompt);
                break;
        }

        if (response.statusCode() != 200) {
            throw new IOException("API повернув помилку: " + response.statusCode() + " - " + response.body());
        }

        System.out.println("Отримано відповідь від API. Обробляємо зображення...");
        JSONObject jsonResponse = new JSONObject(response.body());
        System.out.println("Відповідь API (короткий фрагмент): " +
                response.body().substring(0, Math.min(500, response.body().length())) + "...");

        // Детектуємо тип відповіді та витягуємо зображення
        String base64Image = extractImageFromResponse(jsonResponse, apiMethod);

        if (base64Image.isEmpty()) {
            // Спробуємо повторно надіслати запит через інший метод
            System.out.println("Зображення не знайдено. Спробуємо альтернативний метод...");

            // Якщо перший метод невдалий, спробуємо надіслати POST запит на інший ендпоінт
            if (!apiMethod.equals("run_predict")) {
                response = requestRunPredict(client, prompt, negativePrompt);
                if (response.statusCode() == 200) {
                    jsonResponse = new JSONObject(response.body());
                    base64Image = extractImageFromResponse(jsonResponse, "run_predict");
                }
            }

            if (base64Image.isEmpty()) {
                System.out.println("Вміст відповіді: " + jsonResponse.toString(2));
                throw new IOException("Не вдалося знайти зображення у відповіді API");
            }
        }

        // Перевіряємо, чи потрібно видалити префікс Base64 (data:image/png;base64,)
        if (base64Image.contains(",")) {
            base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
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
     * Отримує структуру інтерфейсу від сервера
     */
    private static JSONObject getInterfaceStructure() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        // Спробуємо декілька ендпоінтів для визначення структури інтерфейсу
        String[] endpoints = {
                "/config",
                "/index.html",
                "/docs",
                "/ui",
                "/ui-config.json",
                "/app",
                "/"
        };

        for (String endpoint : endpoints) {
            String url = BASE_URL + endpoint;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    // Перевіряємо, чи це JSON відповідь
                    String body = response.body().trim();
                    if (body.startsWith("{") && body.endsWith("}")) {
                        try {
                            return new JSONObject(body);
                        } catch (Exception e) {
                            // Не JSON або некоректний JSON, продовжуємо пошук
                        }
                    }
                }
            } catch (Exception e) {
                // Ігноруємо помилку і спробуємо наступний ендпоінт
            }
        }

        // Якщо не вдалося отримати структуру, повертаємо порожній об'єкт
        return new JSONObject();
    }

    /**
     * Визначає доступний метод API на основі структури інтерфейсу
     */
    private static String determineApiMethod(JSONObject interfaceStructure) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        // Перевіряємо доступні ендпоінти API
        String[] endpoints = {
                "/api/predict",
                "/sdapi/v1/txt2img",
                "/queue/join",
                "/run/predict",
                "/api/app/predict",
                "/api/v1/generation/text-to-image"
        };

        for (String endpoint : endpoints) {
            String url = BASE_URL + endpoint;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method("OPTIONS", BodyPublishers.noBody())
                    .build();

            try {
                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                if (response.statusCode() == 200 || response.statusCode() == 204) {
                    // Ендпоінт доступний для OPTIONS запиту
                    switch (endpoint) {
                        case "/sdapi/v1/txt2img":
                            return "txt2img_api";
                        case "/api/predict":
                            return "api_predict";
                        case "/api/v1/generation/text-to-image":
                            return "comfy";
                        case "/run/predict":
                            return "run_predict";
                        default:
                            // Невідомий, але доступний ендпоінт
                            return "default";
                    }
                }
            } catch (Exception e) {
                // Ігноруємо помилку і спробуємо наступний ендпоінт
            }
        }

        // Якщо не знайдено жодного доступного ендпоінту, використовуємо стандартний
        return "api_predict";
    }

    /**
     * Надсилає запит з використанням gradio txt2img інтерфейсу
     */
    private static HttpResponse<String> requestGradioTxt2Img(HttpClient client, String prompt, String negativePrompt)
            throws IOException, InterruptedException {
        String url = BASE_URL + "/sdapi/v1/txt2img";
        JSONObject requestBody = new JSONObject();
        requestBody.put("prompt", prompt);
        requestBody.put("negative_prompt", negativePrompt);
        requestBody.put("steps", 30);
        requestBody.put("cfg_scale", 7);
        requestBody.put("width", 512);
        requestBody.put("height", 512);
        requestBody.put("sampler_name", "Euler a");
        requestBody.put("batch_size", 1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(BodyPublishers.ofString(requestBody.toString()))
                .build();

        System.out.println("Запит до txt2img API: " + requestBody.toString());
        return client.send(request, BodyHandlers.ofString());
    }

    /**
     * Надсилає запит з використанням ComfyUI / Custom API
     */
    private static HttpResponse<String> requestComfyWorkflow(HttpClient client, String prompt, String negativePrompt)
            throws IOException, InterruptedException {
        String url = BASE_URL + "/api/v1/generation/text-to-image";
        JSONObject requestBody = new JSONObject();
        requestBody.put("prompt", prompt);
        requestBody.put("negative_prompt", negativePrompt);
        requestBody.put("steps", 30);
        requestBody.put("cfg_scale", 7);
        requestBody.put("width", 512);
        requestBody.put("height", 512);
        requestBody.put("sampler", "euler_a");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(BodyPublishers.ofString(requestBody.toString()))
                .build();

        System.out.println("Запит до ComfyUI API: " + requestBody.toString());
        return client.send(request, BodyHandlers.ofString());
    }

    /**
     * Надсилає запит з використанням Gradio API predict
     */
    private static HttpResponse<String> requestApiPredict(HttpClient client, String prompt, String negativePrompt)
            throws IOException, InterruptedException {
        String url = BASE_URL + "/api/predict";

        // Спробуємо різні fn_index
        int[] possibleFnIndices = {0, 1, 2, 11, 17, 22, 42};

        for (int fnIndex : possibleFnIndices) {
            String sessionHash = UUID.randomUUID().toString();
            JSONObject requestBody = new JSONObject();
            requestBody.put("session_hash", sessionHash);
            requestBody.put("fn_index", fnIndex);

            // Перші два формати даних (найбільш поширені)
            JSONArray dataFormats = new JSONArray();

            // Формат 1 - проста пара промптів
            JSONArray format1 = new JSONArray()
                    .put(prompt)
                    .put(negativePrompt);

            // Формат 2 - розширений набір параметрів
            JSONArray format2 = new JSONArray()
                    .put(prompt)
                    .put(negativePrompt)
                    .put(new JSONObject()
                            .put("seed", -1)
                            .put("batch_size", 1)
                            .put("steps", 30)
                            .put("cfg_scale", 7)
                            .put("width", 512)
                            .put("height", 512)
                            .put("sampler_index", "Euler a")
                            .put("restore_faces", false));

            // Формат 3 - параметри як окремі елементи масиву
            JSONArray format3 = new JSONArray()
                    .put(prompt)
                    .put(negativePrompt)
                    .put(-1)  // seed
                    .put(1)   // batch_size
                    .put(30)  // steps
                    .put(7)   // cfg_scale
                    .put(512) // width
                    .put(512) // height
                    .put("Euler a"); // sampler_name

            dataFormats.put(format1);
            dataFormats.put(format2);
            dataFormats.put(format3);

            for (int i = 0; i < dataFormats.length(); i++) {
                JSONArray dataFormat = dataFormats.getJSONArray(i);
                requestBody.put("data", dataFormat);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .POST(BodyPublishers.ofString(requestBody.toString()))
                        .build();

                System.out.println("Запит до API predict (fn_index=" + fnIndex + ", format=" + i + "): " + requestBody.toString());

                try {
                    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                    if (response.statusCode() == 200) {
                        JSONObject jsonResponse = new JSONObject(response.body());
                        if (jsonResponse.has("data") && !jsonResponse.isNull("data") &&
                                !jsonResponse.getJSONArray("data").isEmpty()) {
                            return response;
                        }
                    }
                } catch (Exception e) {
                    // Ігноруємо помилку і продовжуємо спроби
                    System.out.println("Помилка при запиті: " + e.getMessage());
                }
            }
        }

        // Якщо жоден з методів не працює, повертаємо останню відповідь
        return requestRunPredict(client, prompt, negativePrompt);
    }

    /**
     * Надсилає запит з використанням run/predict API
     */
    private static HttpResponse<String> requestRunPredict(HttpClient client, String prompt, String negativePrompt)
            throws IOException, InterruptedException {
        String url = BASE_URL + "/run/predict";

        JSONObject requestBody = new JSONObject();
        requestBody.put("prompt", prompt);
        requestBody.put("negative_prompt", negativePrompt);
        requestBody.put("steps", 30);
        requestBody.put("cfg_scale", 7);
        requestBody.put("width", 512);
        requestBody.put("height", 512);
        requestBody.put("sampler", "Euler a");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(BodyPublishers.ofString(requestBody.toString()))
                .build();

        System.out.println("Запит до run/predict API: " + requestBody.toString());
        return client.send(request, BodyHandlers.ofString());
    }

    /**
     * Витягує Base64 зображення з різних форматів відповіді
     */
    private static String extractImageFromResponse(JSONObject response, String apiMethod) {
        switch (apiMethod) {
            case "txt2img_api":
                if (response.has("images") && response.getJSONArray("images").length() > 0) {
                    return response.getJSONArray("images").getString(0);
                }
                break;

            case "comfy":
                if (response.has("image")) {
                    return response.getString("image");
                } else if (response.has("output") && response.getJSONObject("output").has("image")) {
                    return response.getJSONObject("output").getString("image");
                }
                break;

            case "api_predict":
            case "run_predict":
            default:
                if (response.has("data")) {
                    JSONArray data = response.getJSONArray("data");
                    return findImageInJsonArray(data);
                }
                break;
        }

        // Загальний пошук будь-якого поля, яке може містити зображення
        return findImageRecursively(response);
    }

    /**
     * Шукає Base64 зображення в JSON масиві даних
     */
    private static String findImageInJsonArray(JSONArray dataArray) {
        for (int i = 0; i < dataArray.length(); i++) {
            Object item = dataArray.get(i);

            // Якщо елемент - рядок
            if (item instanceof String) {
                String str = (String) item;
                if (isBase64Image(str)) {
                    return str;
                }
            }

            // Якщо елемент - JSON об'єкт
            else if (item instanceof JSONObject) {
                JSONObject obj = (JSONObject) item;

                // Перевіряємо поширені ключі зображень
                String[] imageKeys = {"image", "data", "value", "img", "picture"};
                for (String key : imageKeys) {
                    if (obj.has(key)) {
                        Object value = obj.get(key);

                        // Перевіряємо, чи значення - рядок
                        if (value instanceof String) {
                            String str = (String) value;
                            if (isBase64Image(str)) {
                                return str;
                            }
                        }

                        // Перевіряємо, чи значення - масив
                        else if (value instanceof JSONArray) {
                            String result = findImageInJsonArray((JSONArray) value);
                            if (!result.isEmpty()) {
                                return result;
                            }
                        }
                    }
                }

                // Рекурсивний пошук у вкладених об'єктах
                String recursiveResult = findImageRecursively(obj);
                if (!recursiveResult.isEmpty()) {
                    return recursiveResult;
                }
            }

            // Якщо елемент - масив
            else if (item instanceof JSONArray) {
                String result = findImageInJsonArray((JSONArray) item);
                if (!result.isEmpty()) {
                    return result;
                }
            }
        }

        return "";
    }

    /**
     * Рекурсивно шукає Base64 зображення в JSON об'єкті
     */
    private static String findImageRecursively(JSONObject obj) {
        for (String key : obj.keySet()) {
            Object value = obj.get(key);

            // Якщо значення - рядок
            if (value instanceof String) {
                String str = (String) value;
                if (isBase64Image(str)) {
                    return str;
                }
            }

            // Якщо значення - JSON об'єкт
            else if (value instanceof JSONObject) {
                String result = findImageRecursively((JSONObject) value);
                if (!result.isEmpty()) {
                    return result;
                }
            }

            // Якщо значення - масив
            else if (value instanceof JSONArray) {
                String result = findImageInJsonArray((JSONArray) value);
                if (!result.isEmpty()) {
                    return result;
                }
            }
        }

        return "";
    }

    /**
     * Перевіряє, чи рядок є Base64 зображенням
     */
    private static boolean isBase64Image(String str) {
        return str.startsWith("data:image") ||
                (str.length() > 100 && str.matches("^[A-Za-z0-9+/]*={0,2}$"));
    }

    /**
     * Тестовий метод для надсилання простого запиту та друку відповіді
     */
    public static void testApiEndpoints() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMinutes(1))
                .build();

        System.out.println("Тестуємо доступні ендпоінти...");

        // Перевіряємо різні можливі ендпоінти
        String[] endpoints = {
                "/",
                "/index.html",
                "/debug",
                "/docs",
                "/internal-api/progress",
                "/api/v1/status",
                "/api/v1/app"
        };

        for (String endpoint : endpoints) {
            String apiUrl = BASE_URL + endpoint;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            System.out.println("Тестуємо ендпоінт: " + apiUrl);
            try {
                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                System.out.println("Статус: " + response.statusCode());
                System.out.println("Заголовки: " + response.headers().map());
                if (response.statusCode() == 200) {
                    // Виводимо перші 300 символів відповіді для аналізу
                    String preview = response.body().substring(0, Math.min(300, response.body().length()));
                    System.out.println("Відповідь (початок): " + preview + "...");
                }
            } catch (Exception e) {
                System.out.println("Помилка при запиті до " + apiUrl + ": " + e.getMessage());
            }
            System.out.println("-----------------------------");
        }

        // Перевіряємо POST ендпоінти з методом OPTIONS
        String[] postEndpoints = {
                "/api/predict",
                "/run/predict",
                "/sdapi/v1/txt2img",
                "/queue/join"
        };

        for (String endpoint : postEndpoints) {
            String apiUrl = BASE_URL + endpoint;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .method("OPTIONS", BodyPublishers.noBody())
                    .build();

            System.out.println("Тестуємо OPTIONS для POST ендпоінту: " + apiUrl);
            try {
                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                System.out.println("Статус: " + response.statusCode());
                System.out.println("Заголовки: " + response.headers().map());
                System.out.println("Відповідь: " + response.body());
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
            String outputPath = "./generated-images/landscape.png";

            // Тестуємо доступні ендпоінти
            testApiEndpoints();

            // Генеруємо зображення через визначений метод API
            System.out.println("\nГенеруємо зображення...");
            generateImage(prompt, negativePrompt, outputPath);

            System.out.println("Генерація зображення завершена успішно");
        } catch (Exception e) {
            System.err.println("Помилка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
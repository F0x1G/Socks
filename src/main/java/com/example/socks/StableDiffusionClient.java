package com.example.socks;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StableDiffusionClient {
    public static void main(String[] args) throws Exception {
        // Уточніть правильний ендпоінт у Swagger-документації, якщо потрібно
        String apiEndpoint = "http://127.0.0.1:7860/api/txt2img";

        // Додаємо поле "data"
        String jsonPayload = "{ \"data\": { \"prompt\": \"A futuristic sports car\", \"steps\": 20 } }";

        URL url = new URL(apiEndpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonPayload.getBytes("utf-8"));
        }

        // Читаємо відповідь сервера
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            try (InputStream responseStream = connection.getInputStream()) {
                Files.copy(responseStream, Paths.get("generated_image.png"));
                System.out.println("Зображення збережено!");
            }
        } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                System.err.println("Помилка: " + response.toString());
            }
        }
    }
}
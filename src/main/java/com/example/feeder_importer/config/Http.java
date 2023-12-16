package com.example.feeder_importer.config;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class Http {

    public JSONObject post(String act){

        String apiUrl ="http://localhost:3003/ws/live2.php?=&=";
        JSONObject res = new JSONObject();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Set request headers
            connection.setRequestProperty("Content-Type", "application/json");

            // Create the request payload
            String requestBody = act;

            // Write the request payload to the connection's output stream
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Read the response
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                String err = jsonResponse.getString("error_desc");
                res = jsonResponse;
            }

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;

    }

}

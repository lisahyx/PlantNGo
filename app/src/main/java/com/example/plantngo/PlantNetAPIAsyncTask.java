package com.example.plantngo;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class PlantNetAPIAsyncTask extends AsyncTask<Uri, Void, String> {

    private static final String API_KEY = "2b10yIsN5IqMw3S4xNoWO3Txu";
    private static final String API_URL = "https://my-api.plantnet.org/v2/identify?api-key=" + API_KEY;

    @Override
    protected String doInBackground(Uri... uris) {
        if (uris.length > 0) {
            Uri imageUri = uris[0];
            try {
                // Convert Uri to File
                File imageFile = new File(Objects.requireNonNull(imageUri.getPath()));
                Log.d("PlantNetAPIAsyncTask", "Image URI path: " + imageUri.getPath());

                // Implement your PlantNet API logic here

                return sendImageToPlantNet(imageFile);
            } catch (Exception e) {
                Log.e("PlantNetAPIAsyncTask", "Error processing PlantNet API request", e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // Handle the result from the PlantNet API, such as updating the UI or displaying a message
        if (result != null) {
            Log.d("PlantNetAPIAsyncTask", "Plant identification result: " + result);

        } else {
            Log.e("PlantNetAPIAsyncTask", "Error identifying plant");

        }
    }

    private String sendImageToPlantNet(File imageFile) {
        try {
            // Create a connection to the PlantNet API
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Set API key as a request header
            connection.setRequestProperty("api-key", API_KEY);

            // Set content type as multipart form-data
            String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // Prepare the data to send
            StringBuilder postData = new StringBuilder();
            postData.append("--").append(boundary).append("\r\n");
            postData.append("Content-Disposition: form-data; name=\"images\"; filename=\"").append(imageFile.getName()).append("\"\r\n");
            postData.append("Content-Type: application/octet-stream\r\n\r\n");

            // Write image data
            FileInputStream fileInputStream = new FileInputStream(imageFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                connection.getOutputStream().write(buffer, 0, bytesRead);
            }
            fileInputStream.close();

            // Finish the request
            connection.getOutputStream().write(("\r\n--" + boundary + "--\r\n").getBytes());

            // Get the response from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Close the connection
            connection.disconnect();

            return response.toString();
        } catch (IOException e) {
            Log.e("PlantNetAPIAsyncTask", "Error sending image to PlantNet API", e);
            return null;
        }
    }

    // Use executeOnExecutor to avoid deprecation warning
    public void executeAsync(Uri imageUri) {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageUri);
    }
}

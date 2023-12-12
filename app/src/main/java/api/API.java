package api;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class API {
    private final Context context;

    public API(Context context) {
        this.context = context;
    }
    private static final String API_URL = "https://my-api.plantnet.org/v2/identify";
    private static final String API_KEY = "2b10yIsN5IqMw3S4xNoWO3Txu";
    private static final String PROJECT = "com.example.plantngo";

    public void sendImageToPlantNet(Uri imageUri) {
        try {
            File imageFile = new File(imageUri.getPath());

            // Implement your PlantNet API logic here
            String apiUrl = API_URL + "/" + PROJECT + "?api-key=" + API_KEY;

            // Create a connection to the PlantNet API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            // Set content type as multipart form-data
            String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // Get the response code from the server
            int responseCode = connection.getResponseCode();
            Log.e("API status", "Response code: " + responseCode);

            // Check if the request was successful (status code 2xx)
            if (responseCode >= 200 && responseCode < 300) {
                // Prepare the data to send
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(("Content-Disposition: form-data; name=\"images\"; filename=\"" +
                            imageFile.getName() + "\"\r\n").getBytes());
                    outputStream.write("Content-Type: application/octet-stream\r\n\r\n".getBytes());

                    // Write image data
                    try (InputStream inputStream = Files.newInputStream(imageFile.toPath())) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
            } else {
                try (InputStream errorStream = connection.getErrorStream()) {
                    if (errorStream != null) {
                        // Process the error stream
                        String errorResponse = new BufferedReader(new InputStreamReader(errorStream))
                                .lines().collect(Collectors.joining("\n"));
                        Log.e("connection error", "Errorstream not null" + errorResponse);
                    }
                }
            }

            // Finish the request
            connection.getOutputStream().write(("\r\n--" + boundary + "--\r\n").getBytes());

            // Get the response from the server
            StringBuilder response = new StringBuilder();
            try (InputStream inputStream = connection.getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    response.append(new String(buffer, 0, bytesRead));
                }
            }

            // Handle the response, e.g., display it to the user
            showMessage(response.toString());

        } catch (IOException e) {
            Log.e("PlantNetApiCaller", "Error sending image to PlantNet API", e);
            PlantNetResponse response = new PlantNetResponse();
        }
    }

    private void showMessage(String message) {
        // Use a handler to post the message to the main thread for UI updates
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
    }
}

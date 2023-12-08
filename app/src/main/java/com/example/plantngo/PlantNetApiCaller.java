package com.example.plantngo;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantNetApiCaller {
    //private static PlantNetApiCaller instance = null;
    private PlantNetApiService plantNetApiService;
    private Context context;

    public PlantNetApiCaller(Context context) {
        this.context = context;
        // Initialize your Retrofit instance and PlantNetApiService here
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://my-api.plantnet.org/v2/")  // Adjust the base URL accordingly
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        plantNetApiService = retrofit.create(PlantNetApiService.class);
    }

//    synchronized PlantNetApiCaller getInstance() {
//        if(instance == null) {
//            instance = new PlantNetApiCaller();
//        }
//        return instance;
//    }
//
//    public PlantNetApiService getAPI() {
//        return plantNetApiService;
//    }

    Executor executor;

    public void identifyPlant(Uri imageUri, PlantNetCallback callback) {
        executor.execute(() -> {
            try {
                // Open an InputStream from the ContentResolver
                InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                if (inputStream == null) {
                    callback.onError("Failed to open InputStream from ContentResolver");
                    return;
                }

                // Create a temporary file
                File tempFile = File.createTempFile("plantnet_temp", ".jpg", context.getCacheDir());
                FileOutputStream outputStream = new FileOutputStream(tempFile);

                // Copy data from InputStream to the temporary file
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // Close the streams
                inputStream.close();
                outputStream.close();

                // Convert the temporary file to MultipartBody.Part
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), tempFile);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("images", tempFile.getName(), requestFile);

                // Create a RequestBody for the API key
                RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), "2b10yIsN5IqMw3S4xNoWO3Txu"); // Replace with your actual API key

                // Execute the API request
                Call<PlantNetResponse> call = plantNetApiService.identifyPlant(apiKey, imagePart);
                Response<PlantNetResponse> response = call.execute();

                // Check if the request was successful
                if (response.isSuccessful() && response.body() != null) {
                    PlantNetResponse plantNetResponse = response.body();
                    callback.onSuccess(plantNetResponse.getResult());
                } else {
                    callback.onError("Error identifying plant: " + response.message());
                    callback.onError("Error identifying plant. Status code: " + response.code());
                    try {
                        callback.onError(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onError("Error identifying plant. Unable to read error body.");
                    }
                }
            } catch (IOException e) {
                callback.onError("Error processing image URI");
            }
        });
    }
}

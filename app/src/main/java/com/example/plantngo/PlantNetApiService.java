package com.example.plantngo;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PlantNetApiService {
    @Multipart
    @POST("identify")
    Call<PlantNetResponse> identifyPlant(
            @Part("api-key") RequestBody apiKey,
            @Part MultipartBody.Part imageFile
    );
}


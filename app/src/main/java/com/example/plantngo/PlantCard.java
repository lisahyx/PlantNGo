package com.example.plantngo;

import android.widget.ImageView;

public class PlantCard {

    private String plantImageUrl;
    private String plantName;

    public PlantCard(String imageUrl, String name) {
        plantImageUrl = imageUrl;
        plantName = name;
    }

    public String getImageUrl() {
        return plantImageUrl;
    }

    public String getPlantName() {
        return plantName;
    }
}

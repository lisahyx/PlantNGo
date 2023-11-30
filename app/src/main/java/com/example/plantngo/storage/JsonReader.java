package com.example.plantngo.storage;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * JsonReader class provides methods to read and parse JSON files.
 */
public class JsonReader {
    private static Long time;

    /**
     * Constructor to initialize the notification time with the current system time.
     */
    public JsonReader() {
        time = System.currentTimeMillis();
    }

    /**
     * Reads the content of a JSON file from the specified resource.
     *
     * @param context    The application context.
     * @param resourceId The resource ID of the JSON file.
     * @return The content of the JSON file as a string.
     */
    public String readJsonFile(Context context, int resourceId) {
        try {
            // Open the resource using getResourceAsStream
            InputStream inputStream = context.getResources().openRawResource(resourceId);

            // Read the InputStream using a BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            // Close the InputStream and BufferedReader
            reader.close();
            inputStream.close();

            // Return the content of the file as a string
            return stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parses JSON content to extract the scientific name of a plant.
     *
     * @param jsonContent The JSON content to be parsed.
     * @return The scientific name of the plant.
     * @throws JSONException If an error occurs while parsing JSON.
     */
    public String parsePlantNameJson(String jsonContent) throws JSONException {
        String scientificName = null;

        if (jsonContent != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonContent);

                if (jsonObject.has("results")) {
                    JSONArray resultsArray = jsonObject.getJSONArray("results");

                    if (resultsArray.length() > 0) {
                        JSONObject firstResultObject = resultsArray.getJSONObject(0);

                        if (firstResultObject.has("species")) {
                            JSONObject speciesObject = firstResultObject.getJSONObject("species");

                            if (speciesObject.has("scientificNameWithoutAuthor")) {
                                scientificName = speciesObject.getString("scientificNameWithoutAuthor");
                                return scientificName;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return scientificName;
    }

    /**
     * Parses JSON content to extract sunlight information for a plant.
     *
     * @param jsonContent The JSON content to be parsed.
     * @return Sunlight information for the plant.
     * @throws JSONException If an error occurs while parsing JSON.
     */
    public String parseSunlightJson(String jsonContent) throws JSONException {
        String sunlight = null;

        if (jsonContent != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonContent);

                if (jsonObject.has("sunlight")) {
                    JSONArray sunlightArray = jsonObject.getJSONArray("sunlight");

                    if (sunlightArray.length() > 0) {
                        sunlight = sunlightArray.getString(0);
                        return sunlight;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sunlight;
    }

    /**
     * Parses JSON content to extract watering information for a plant.
     *
     * @param jsonContent The JSON content to be parsed.
     * @return Watering information for the plant.
     * @throws JSONException If an error occurs while parsing JSON.
     */
    public String parseWateringJson(String jsonContent) throws JSONException {
        String wateringInfo = null;

        if (jsonContent != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonContent);

                if (jsonObject.has("watering_general_benchmark")) {
                    JSONObject wateringObject = jsonObject.getJSONObject("watering_general_benchmark");

                    if (wateringObject.has("value") && wateringObject.has("unit")) {
                        String value = wateringObject.getString("value");
                        String unit = wateringObject.getString("unit");

                        wateringInfo = "Every " + value + " " + unit;

                        // if unit is in days, convert value to milliseconds
                        long intervalMillis = Long.parseLong(value) * 24 * 60 * 60 * 1000;
                        setNotificationTime(intervalMillis);

                        return wateringInfo;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return wateringInfo;
    }

    /**
     * Sets the notification time interval.
     *
     * @param time The notification time interval in milliseconds.
     */
    public void setNotificationTime(Long time) {
        JsonReader.time = time;
    }

    /**
     * Gets the notification time interval.
     *
     * @return The notification time interval in milliseconds.
     */
    public static Long getNotificationTime() {
        return time;
    }
}
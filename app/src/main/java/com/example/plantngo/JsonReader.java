package com.example.plantngo;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonReader {

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
}


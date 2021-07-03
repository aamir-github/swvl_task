package com.example.task.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonUtil {

    public static String getJson(String fileName, Context context) {
        //Turn json data into string
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //Get assets resource manager
            AssetManager assetManager = context.getAssets();
            //Open the file and read through the manager
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}

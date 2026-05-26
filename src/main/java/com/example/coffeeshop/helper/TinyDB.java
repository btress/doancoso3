package com.example.coffeeshop.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.coffeeshop.model.ItemsModel;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class TinyDB {

    private SharedPreferences preferences;
    private String DEFAULT_APP_IMAGEDATA_DIRECTORY;
    private String lastImagePath = "";

    public TinyDB(Context appContext) {
        preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public String getString(String key) {
        return preferences.getString(key, "");
    }

    public ArrayList<String> getListString(String key) {
        String pref = preferences.getString(key, "");
        if (pref.isEmpty()) return new ArrayList<>();
        return new ArrayList<String>(Arrays.asList(TextUtils.split(pref, "‚‗‚")));
    }

    public ArrayList<ItemsModel> getListObject(String key) {
        Gson gson = new Gson();
        ArrayList<String> objStrings = getListString(key);
        ArrayList<ItemsModel> playerList = new ArrayList<ItemsModel>();

        for (String jObjString : objStrings) {
            if (!jObjString.isEmpty()) {
                ItemsModel player = gson.fromJson(jObjString, ItemsModel.class);
                if (player != null) {
                    playerList.add(player);
                }
            }
        }
        return playerList;
    }

    public void putString(String key, String value) {
        if (key != null && value != null) {
            preferences.edit().putString(key, value).apply();
        }
    }

    public void putListString(String key, ArrayList<String> stringList) {
        if (key == null) return;
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
    }

    public void putListObject(String key, ArrayList<ItemsModel> playerList) {
        if (key == null) return;
        Gson gson = new Gson();
        ArrayList<String> objStrings = new ArrayList<String>();
        for (ItemsModel player : playerList) {
            objStrings.add(gson.toJson(player));
        }
        putListString(key, objStrings);
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}

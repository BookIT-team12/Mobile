package com.example.bookit.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.bookit.security.UserTokenState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.util.Map;

public class AppPreferences {

    private static final String KEY_TOKEN = "jwt_token";
    // Add more keys as needed

    public AppPreferences() {}

    //EVERY SINGLE CONTEXT IN THIS CLASS SHOULD BE CALLED ONLY WITH getApplicationContext();!

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("authPreferences", Context.MODE_PRIVATE);
    }

    public static Map<String, ?> debug_printAllSharedPreferences(Context context) { //just for printing and debugging. not usable!
        SharedPreferences sharedPreferences = context.getSharedPreferences("authPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getAll();
    }


    public static void saveToken(Context context, UserTokenState token) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        Gson gson = new Gson();
        String jwtJson = gson.toJson(token);
        editor.putString(KEY_TOKEN, jwtJson);
        editor.apply();
    }

    public static UserTokenState getToken(Context context) {
        String jwtJson = getSharedPreferences(context).getString(KEY_TOKEN, "");
        Gson gson = new Gson();
        UserTokenState jwtToken = gson.fromJson(jwtJson, UserTokenState.class);
        return jwtToken;
    }

    public static void deleteToken(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_TOKEN, "");
        editor.apply();
    }

    public static boolean isLoggedIn(Context context){
        return getToken(context) != null;
    }
}

package com.example.bookit.security;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.example.bookit.app.AppPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiInterceptor implements Interceptor {
    private final Context context;
    public ApiInterceptor(Context context){
        this.context = context;
    }
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        System.out.println(AppPreferences.debug_printAllSharedPreferences(context));
        UserTokenState jwtToken = AppPreferences.getToken(context);

        Request originalRequest = chain.request();
        if (AppPreferences.isLoggedIn(context)) {

            // Customize the request, for example, add headers
            Request modifiedRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer " + jwtToken.getAccessToken())
                    .build();

            // Proceed with the modified request
            Response response = chain.proceed(modifiedRequest);

            // You can also intercept and modify the response if needed

            return response;
        }
        Response response = chain.proceed(originalRequest);
        return response;
    }
}

package com.example.bookit.security;

import android.content.Context;

import com.example.bookit.model.User;
import com.example.bookit.model.enums.Role;
import com.example.bookit.retrofit.RetrofitService;
import com.example.bookit.retrofit.api.UserApi;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserTokenService {
    RetrofitService retrofitService;
    UserApi userApi;
    User currentUser;
    public UserTokenService (Context context) {
        retrofitService = new RetrofitService(context);
        userApi = retrofitService.getRetrofit().create(UserApi.class);
        currentUser = null;
    }

    public Role getRole(UserTokenState jwtToken) throws ParseException {
        assert jwtToken != null;
        JWT jwt = JWTParser.parse(jwtToken.getAccessToken());

        // Get JWT Claims
        JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();
        String role = jwtClaimsSet.getStringClaim("role");  // Assuming "role" is a claim in your JWT
        switch (role){
            case "OWNER":
                return Role.OWNER;
            case "ADMINISTRATOR":
                return Role.ADMINISTRATOR;
            case "GUEST":
                return Role.GUEST;
            default:
                return null;
        }
    }

    public String getCurrentUser(UserTokenState jwtToken) throws ParseException {     //could cause errors, caase it could return currentUser before userApi.getUser() sets it.
        JWT jwt = JWTParser.parse(jwtToken.getAccessToken());

        // Get JWT Claims
        JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();
        String username = jwtClaimsSet.getSubject();
        return username;
    }

    public User getCurrentUserObject(UserTokenState jwtToken) throws ParseException {     //could cause errors, caase it could return currentUser before userApi.getUser() sets it.
        JWT jwt = JWTParser.parse(jwtToken.getAccessToken());

        // Get JWT Claims
        JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();
        String username = jwtClaimsSet.getSubject();
        userApi.getUser(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                currentUser = response.body();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                currentUser = null;
            }
        });
        return currentUser;
    }
}

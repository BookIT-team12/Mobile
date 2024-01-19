package com.example.bookit.security;

import com.google.gson.annotations.SerializedName;

public class UserTokenState {
    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("expiresIn")
    private int expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }
}
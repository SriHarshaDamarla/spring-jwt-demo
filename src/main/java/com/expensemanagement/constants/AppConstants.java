package com.expensemanagement.constants;

public interface AppConstants {
    String LOGIN_URL = "/login";
    String SIGNUP_URL = "/signup";
    String BASE_FORGOT_PASSWORD_URL = "/fpwd";
    String FORGOT_PASSWORD_URL = "/fpwd/**";

    String[] ALLOWED_ENDPOINTS = {
            LOGIN_URL,
            SIGNUP_URL,
            FORGOT_PASSWORD_URL
    };

    String JWT_SECRET_KEY = "JWT_SECRET";
    String JWT_DEFAULT_SECRET = "a25PouXrweAQewVhQRrldfJoilkjdsf85ljld03LKjpw037i";
}
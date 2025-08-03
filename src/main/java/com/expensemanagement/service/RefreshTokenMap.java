package com.expensemanagement.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RefreshTokenMap {
    private final Map<String, String> refreshTokenMap;

    public RefreshTokenMap() {
        this.refreshTokenMap = new ConcurrentHashMap<>();
    }

    public void addRefreshToken(String username, String refreshToken) {
        refreshTokenMap.put(refreshToken, username);
    }

    public String getUserFromToken(String refreshToken) {
        return refreshTokenMap.get(refreshToken);
    }

    public void removeRefreshToken(String refreshToken) {
        refreshTokenMap.remove(refreshToken);
    }
}

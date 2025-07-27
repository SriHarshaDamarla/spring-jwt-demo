package com.expensemanagement.bean;

import lombok.Data;

@Data
public class StatusResponse {
    private int statusCode;
    private String message;
    public static StatusResponse of(int statusCode, String message) {
        StatusResponse response = new StatusResponse();
        response.setStatusCode(statusCode);
        response.setMessage(message);
        return response;
    }
}

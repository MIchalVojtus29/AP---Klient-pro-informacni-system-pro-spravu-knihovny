package com.example.client.util;

import com.example.client.dto.UserResponseDto;

public class SessionManager {
    private static UserResponseDto loggedInUser;

    public static void setLoggedInUser(UserResponseDto user) {
        loggedInUser = user;
    }

    public static UserResponseDto getLoggedInUser() {
        return loggedInUser;
    }

    public static void logout() {
        loggedInUser = null;
    }
}
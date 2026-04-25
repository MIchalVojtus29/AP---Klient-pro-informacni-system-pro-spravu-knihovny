package com.example.client.util;

import com.example.client.dto.UserResponseDto;

/**
 * Singleton class to store information about the currently logged-in user.
 */
public class UserSession {
    private static UserResponseDto loggedUser;

    public static void setLoggedUser(UserResponseDto user) {
        loggedUser = user;
    }

    public static UserResponseDto getLoggedUser() {
        return loggedUser;
    }

    public static void clear() {
        loggedUser = null;
    }
}
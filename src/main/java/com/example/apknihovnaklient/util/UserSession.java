package com.example.apknihovnaklient.util;

import com.example.apknihovnaklient.dto.UserResponseDto;

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
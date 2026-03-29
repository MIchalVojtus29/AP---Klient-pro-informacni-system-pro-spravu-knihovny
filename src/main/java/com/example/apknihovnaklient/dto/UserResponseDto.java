package com.example.apknihovnaklient.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String phoneNumber;
}
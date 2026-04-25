package com.example.client.dto;

import lombok.Data;

@Data
public class UserCreateDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;
    private String password;

}
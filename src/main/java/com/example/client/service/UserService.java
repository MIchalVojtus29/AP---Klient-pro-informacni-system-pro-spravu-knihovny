package com.example.client.service;

import com.example.client.controller.api.UserApiClient;
import com.example.client.dto.PageResponse;
import com.example.client.dto.UserCreateDto;
import com.example.client.dto.UserResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserService {

    private final UserApiClient userApiClient = new UserApiClient();
    private final ObjectMapper objectMapper;

    public UserService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public PageResponse<UserResponseDto> getUsers(int page, int size, String search) throws Exception {
        String jsonResponse = userApiClient.fetchUsersJson(page, size, search);

        return objectMapper.readValue(jsonResponse, new TypeReference<PageResponse<UserResponseDto>>() {});
    }
    public void createUser(UserCreateDto userDto) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(userDto);
        userApiClient.createUserJson(jsonBody);
    }
    public void updateUser(Long id, UserCreateDto userDto) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(userDto);
        userApiClient.updateUserJson(id, jsonBody);
    }
    public void deleteUser(Integer id) throws Exception {
        userApiClient.deleteUser(id);
    }
}
package org.example.service;

import org.example.dto.UserRegistrationRequestDto;
import org.example.dto.UserResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);

    UserResponseDto updateUserInfo(int id, UserRegistrationRequestDto requestDto);

    UserResponseDto updateUserInfoPartially(int id, Map<String, Object> fields);

    void deleteUser(int id);

    List<UserResponseDto> searchByBirthDateRange(LocalDate fromDate, LocalDate toDate);
}

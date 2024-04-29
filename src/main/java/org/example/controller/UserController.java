package org.example.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.dto.SearchByBirthDateRequestDto;
import org.example.dto.UserRegistrationRequestDto;
import org.example.dto.UserResponseDto;
import org.example.exception.RegistrationException;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateAllUserInfo(@PathVariable int id,
                                  @RequestBody @Valid UserRegistrationRequestDto requestDto) {
        return userService.updateUserInfo(id, requestDto);
    }

    @PatchMapping("/{id}")
    public UserResponseDto updatePartially(@PathVariable int id,
                                  @RequestBody Map<String, Object> fields) {
        return userService.updateUserInfoPartially(id, fields);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        userService.deleteUser(id);
    }

    @GetMapping("/searchByBirthDateRange")
    public List<UserResponseDto> searchUsers(@Valid SearchByBirthDateRequestDto requestDto) {
        return userService.searchByBirthDateRange(requestDto.getFromDate(), requestDto.getToDate());
    }
}

package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserRegistrationRequestDto;
import org.example.dto.UserResponseDto;
import org.example.exception.EntityNotFoundException;
import org.example.exception.RegistrationException;
import org.example.exception.ValidationException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.service.UserService;
import org.example.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    @Value("${minimum.age}")
    private int minimumAge;
    private int userCounter;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Can't register user");
        }
        User user = userMapper.toModel(requestDto);
        user.setId(++userCounter);
        Storage.userStorage.add(user);
        return userMapper.toDto(user);
    }

    @Override
    public void deleteUser(int id) {
        User existingUser = findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find and delete user by id: " + id)
        );
        Storage.userStorage.remove(existingUser);
    }

    @Override
    public UserResponseDto updateUserInfo(int id, UserRegistrationRequestDto requestDto) {
        User existingUser = findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find and update user by id: " + id)
        );

        existingUser.setEmail(requestDto.getEmail());
        existingUser.setFirstName(requestDto.getFirstName());
        existingUser.setLastName(requestDto.getLastName());
        existingUser.setBirthDate(requestDto.getBirthDate());
        existingUser.setAddress(requestDto.getAddress());
        existingUser.setPhoneNumber(requestDto.getPhoneNumber());
        return userMapper.toDto(existingUser);
    }

    @Override
    public UserResponseDto updateUserInfoPartially(int id, Map<String, Object> fields) {
        User existingUser = findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find and update user by id: " + id)
        );

        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(User.class, key);
            if (field != null) {
                if (!"address".equals(key) && !"phoneNumber".equals(key) &&
                        (value == null || value.toString().isEmpty())) {
                    throw new ValidationException(key + " must not be empty!");
                }
                field.setAccessible(true);
                if ("birthDate".equals(key)) {
                    String dateString = (String) value;
                    LocalDate birthDate = LocalDate.parse(dateString);
                    if (birthDate.isBefore(LocalDate.now())
                            && Period.between(birthDate, LocalDate.now()).getYears() >= minimumAge) {
                        value = birthDate;
                    } else {
                        throw new ValidationException("Invalid birth date. Check again");
                    }
                }

                ReflectionUtils.setField(field, existingUser, value);
            }
        });

        return userMapper.toDto(existingUser);
    }

    @Override
    public List<UserResponseDto> searchByBirthDateRange(LocalDate fromDate, LocalDate toDate) {
        return Storage.userStorage.stream()
                .filter(user -> (user.getBirthDate().isAfter(fromDate)
                       && user.getBirthDate().isBefore(toDate)))
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    private Optional<User> findByEmail(String email) {
        return Storage.userStorage.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    private Optional<User> findById(int id) {
        return Storage.userStorage.stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }
}

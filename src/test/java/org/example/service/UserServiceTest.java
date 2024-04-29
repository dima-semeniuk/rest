package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.dto.UserRegistrationRequestDto;
import org.example.dto.UserResponseDto;
import org.example.exception.EntityNotFoundException;
import org.example.exception.RegistrationException;
import org.example.exception.ValidationException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.service.impl.UserServiceImpl;
import org.example.storage.Storage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final int EXISTING_ID = 1;
    private static final int NOT_EXISTING_ID = 2;
    private static User user;
    private static User savedUser;
    private static User savedUser2;
    private static User updatedUser;
    private static User updatedUserPartially;
    private static Map<String, Object> fieldsToBeUpdated;
    private static Map<String, Object> emptyLastName;
    private static Map<String, Object> notValidBirthDate;
    private static UserRegistrationRequestDto registrationRequestDto;
    private static UserRegistrationRequestDto updatingRequestDto;
    private static UserRegistrationRequestDto updatingRequestDtoEmailExist;
    private static UserResponseDto registrationResponseDto;
    private static UserResponseDto registrationResponseDto2;
    private static UserResponseDto updatedResponseDto;
    private static UserResponseDto updatedPartiallyResponseDto;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeAll
    static void beforeAll() {
        registrationRequestDto = new UserRegistrationRequestDto()
                .setEmail("some.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("Brown")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("Lisova, 12");

        user = new User()
                .setEmail("some.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("Brown")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("Lisova, 12");

        updatingRequestDto = new UserRegistrationRequestDto()
                .setEmail("some.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("Brown-Smith")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("Svobody, 42");

        updatingRequestDtoEmailExist = new UserRegistrationRequestDto()
                .setEmail("some.existing.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("Brown-Smith")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("Svobody, 42");

        fieldsToBeUpdated = new HashMap<>();
        fieldsToBeUpdated.put("lastName", "AnotherLastName");
        fieldsToBeUpdated.put("address", "AnotherAddress, 111");

        emptyLastName = new HashMap<>();
        emptyLastName.put("lastName", "");

        notValidBirthDate = new HashMap<>();
        notValidBirthDate.put("birthDate", "2010-04-12");

        savedUser = new User()
                .setId(1)
                .setEmail("some.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("Brown")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("Lisova, 12");

        savedUser2 = new User()
                .setId(2)
                .setEmail("some.existing.email@ukr.net")
                .setFirstName("Mark")
                .setLastName("Lumberg")
                .setBirthDate(LocalDate.parse("1996-06-19"))
                .setAddress("NewStreet, 345");

        registrationResponseDto = new UserResponseDto()
                .setId(1)
                .setEmail("some.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("Brown")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("Lisova, 12");

        registrationResponseDto2 = new UserResponseDto()
                .setId(2)
                .setEmail("some.existing.email@ukr.net")
                .setFirstName("Mark")
                .setLastName("Lumberg")
                .setBirthDate(LocalDate.parse("1996-06-19"))
                .setAddress("NewStreet, 345");

        updatedUser = new User()
                .setId(1)
                .setEmail("some.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("Brown-Smith")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("Svobody, 42");

        updatedUserPartially = new User()
                .setId(1)
                .setEmail("some.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("AnotherLastName")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("AnotherAddress, 111");

        updatedResponseDto = new UserResponseDto()
                .setId(1)
                .setEmail("some.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("Brown-Smith")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("Svobody, 42");

        updatedPartiallyResponseDto = new UserResponseDto()
                .setId(1)
                .setEmail("some.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("AnotherLastName")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("AnotherAddress, 111");
    }

    @AfterEach
    void tearDown() {
        Storage.userStorage.clear();
    }

    @Test
    @DisplayName("Register a new user")
    public void register_ValidUserRegistrationRequestDto_ReturnUserResponseDto() {
        when(userMapper.toModel(registrationRequestDto)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(registrationResponseDto);

        UserResponseDto actual = userService.register(registrationRequestDto);

        assertEquals(registrationResponseDto, actual);
    }

    @Test
    @DisplayName("Register a new user, emil is exist")
    public void register_EmailIdExist_RegistrationExceptionExpected() {
        Storage.userStorage.add(savedUser);
        String expectedMessage = "Can't register user";

        Exception exception = assertThrows(
                RegistrationException.class,
                () -> userService.register(registrationRequestDto)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Update all user's info by id")
    public void updateUserInfo_ExistingId_ReturnUserResponseDto() {
        Storage.userStorage.add(savedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(updatedResponseDto);

        UserResponseDto actual = userService.updateUserInfo(EXISTING_ID, updatingRequestDto);

        assertEquals(updatedResponseDto, actual);
    }

    @Test
    @DisplayName("Update all user's info by not existing id")
    public void updateUserInfo_NotExistingId_EntityNotFoundExceptionExpected() {
        String expectedMessage = "Can't find and update user by id: " + NOT_EXISTING_ID;

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateUserInfo(NOT_EXISTING_ID, updatingRequestDto)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Update all user's info by id and existing email")
    public void updateUserInfo_ExistingEmail_RegistrationExceptionExpected() {
        Storage.userStorage.add(savedUser);
        Storage.userStorage.add(savedUser2);
        String expectedMessage = "Can't change email address";

        Exception exception = assertThrows(
                RegistrationException.class,
                () -> userService.updateUserInfo(EXISTING_ID, updatingRequestDtoEmailExist)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Update user's info partially by id")
    public void updateUserInfoPartially_ExistingId_ReturnUserResponseDto() {
        Storage.userStorage.add(savedUser);
        when(userMapper.toDto(updatedUserPartially)).thenReturn(updatedPartiallyResponseDto);

        UserResponseDto actual = userService.updateUserInfoPartially(EXISTING_ID,
                fieldsToBeUpdated);

        assertEquals(updatedPartiallyResponseDto, actual);
    }

    @Test
    @DisplayName("Update user's info partially not existing by id")
    public void updateUserInfoPartially_NotExistingId_EntityNotFoundExceptionExpected() {
        String expectedMessage = "Can't find and update user by id: " + NOT_EXISTING_ID;

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateUserInfoPartially(NOT_EXISTING_ID, fieldsToBeUpdated)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Update user's info partially, empty lastName")
    public void updateUserInfoPartially_EmptyLastName_ValidationExceptionExpected() {
        Storage.userStorage.add(savedUser);
        String expectedMessage = "lastName must not be empty!";

        Exception exception = assertThrows(
                ValidationException.class,
                () -> userService.updateUserInfoPartially(EXISTING_ID, emptyLastName)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    //    @Test
    //    @DisplayName("Update user's info partially, not valid birthdate")
    //    public void updateUserInfoPartially_NotValidBirthdate_ValidationExceptionExpected() {
    //        Storage.userStorage.add(savedUser);
    //        String expectedMessage = "Invalid birth date. Check again";
    //
    //        Exception exception = assertThrows(
    //                ValidationException.class,
    //                () -> userService.updateUserInfoPartially(EXISTING_ID, notValidBirthDate)
    //        );
    //
    //        assertEquals(expectedMessage, exception.getMessage());
    //    }

    @Test
    @DisplayName("Search users by birthdate range")
    public void searchUsers_BirthdateRangeOk_ReturnListOfUserResponseDto() {
        Storage.userStorage.add(savedUser);
        Storage.userStorage.add(savedUser2);
        LocalDate fromDate = LocalDate.parse("1995-01-01");
        LocalDate toDate = LocalDate.parse("2006-02-20");
        when(userMapper.toDto(savedUser)).thenReturn(registrationResponseDto);
        when(userMapper.toDto(savedUser2)).thenReturn(registrationResponseDto2);
        List<UserResponseDto> expected = List.of(registrationResponseDto, registrationResponseDto2);

        List<UserResponseDto> actual = userService.searchByBirthDateRange(fromDate, toDate);

        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());
    }
}

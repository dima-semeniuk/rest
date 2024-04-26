package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.dto.SearchByBirthDateRequestDto;
import org.example.dto.UserRegistrationRequestDto;
import org.example.dto.UserResponseDto;
import org.example.exception.EntityNotFoundException;
import org.example.exception.RegistrationException;
import org.example.exception.ValidationException;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(UserController.class)
class UserControllerTest {
    private static final int INDEX_OF_UPDATING_USER = 2;
    private static final int INDEX_OF_DELETING_USER = 2;
    private static final int INDEX_OF_NOT_EXISTING_USER = 10;
    private static UserRegistrationRequestDto registrationRequestDto;
    private static UserRegistrationRequestDto updatingRequestDto;
    private static SearchByBirthDateRequestDto birthDateRangeRequestDto;
    private static Map<String, Object> updateFields;
    private static Map<String, Object> updateFieldsEmptyName;
    private static UserRegistrationRequestDto notValidBirthDateRequestDto;
    private static UserRegistrationRequestDto notValidEmailRequestDto;
    private static UserResponseDto registrationResponseDto;
    private static UserResponseDto updatingResponseDto;
    private static UserResponseDto updatingPartiallyResponseDto;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @BeforeAll
    static void beforeAll() {
        registrationRequestDto = new UserRegistrationRequestDto()
                .setEmail("some.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("Brown")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("Lisova, 12");

        updatingRequestDto = new UserRegistrationRequestDto()
                .setEmail("some.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("Brown")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("Svobody, 42");

        birthDateRangeRequestDto = new SearchByBirthDateRequestDto();
        birthDateRangeRequestDto.setFromDate(LocalDate.parse("2000-01-01"));
        birthDateRangeRequestDto.setToDate(LocalDate.parse("2005-12-30"));

        updateFields = new HashMap<>();
        updateFields.put("lastName", "Smith");
        updateFields.put("address", "UnionStreet, 111");

        updateFieldsEmptyName = new HashMap<>();
        updateFieldsEmptyName.put("firstName", "");
        updateFieldsEmptyName.put("address", "UnionStreet, 111");

        registrationResponseDto = new UserResponseDto()
                .setId(1)
                .setEmail("some.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("Brown")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("Lisova, 12");

        updatingResponseDto = new UserResponseDto()
                .setId(2)
                .setEmail("some.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("Brown")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("Svobody, 42");

        updatingPartiallyResponseDto = new UserResponseDto()
                .setId(2)
                .setEmail("some.email@ukr.net")
                .setFirstName("Eric")
                .setLastName("Smith")
                .setBirthDate(LocalDate.parse("2002-03-14"))
                .setAddress("UnionStreet, 111");

        notValidBirthDateRequestDto = new UserRegistrationRequestDto()
                .setEmail("sam.email@ukr.net")
                .setFirstName("Sam")
                .setLastName("Haris")
                .setBirthDate(LocalDate.parse("2010-07-24"))
                .setAddress("Shevchenka, 232");

        notValidEmailRequestDto = new UserRegistrationRequestDto()
                .setEmail("sam.ukr.net")
                .setFirstName("Sam")
                .setLastName("Haris")
                .setBirthDate(LocalDate.parse("2002-07-24"))
                .setAddress("Shevchenka, 232");
    }

    @Test
    @DisplayName("Register a new user")
    void registerUser_ValidRequestDto_Success() throws Exception {
        when(userService.register(registrationRequestDto)).thenReturn(registrationResponseDto);

        String jsonRequest = objectMapper.writeValueAsString(registrationRequestDto);
        MvcResult result = mockMvc.perform(
                        post("/users/registration")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserResponseDto.class);
        assertNotNull(actual);
        assertEquals(registrationResponseDto, actual);
    }

    @Test
    @DisplayName("Register a new user, email is exist")
    void registerUser_EmailIsExist_BadRequest() throws Exception {
        when(userService.register(registrationRequestDto))
                .thenThrow(new RegistrationException("Can't register user"));

        String jsonRequest = objectMapper.writeValueAsString(registrationRequestDto);
        MvcResult result = mockMvc.perform(
                        post("/users/registration")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("Register a new user, not valid birthdate")
    void registerUser_NotValidAgeRequestDto_BadRequest() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(notValidBirthDateRequestDto);
        MvcResult result = mockMvc.perform(
                        post("/users/registration")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("Register a new user, not valid email")
    void registerUser_NotValidEmailRequestDto_BadRequest() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(notValidEmailRequestDto);
        MvcResult result = mockMvc.perform(
                        post("/users/registration")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("Update all user's info by id")
    void updateUserInfo_ValidRequestDto_Success() throws Exception {
        when(userService.updateUserInfo(INDEX_OF_UPDATING_USER, updatingRequestDto))
                .thenReturn(updatingResponseDto);

        String jsonRequest = objectMapper.writeValueAsString(updatingRequestDto);
        MvcResult result = mockMvc.perform(
                        put("/users/{id}", INDEX_OF_UPDATING_USER)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserResponseDto.class);
        assertNotNull(actual);
        assertEquals(updatingResponseDto, actual);
    }

    @Test
    @DisplayName("Update all user's info by not existing id")
    void updateUserInfo_NotExistingId_NotFound() throws Exception {
        when(userService.updateUserInfo(INDEX_OF_NOT_EXISTING_USER, updatingRequestDto))
                .thenThrow(new EntityNotFoundException("User not found"));

        String jsonRequest = objectMapper.writeValueAsString(updatingRequestDto);
        MvcResult result = mockMvc.perform(
                        put("/users/{id}", INDEX_OF_NOT_EXISTING_USER)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @DisplayName("Update user's info partially by id")
    void updateUserInfoPartially_ValidRequestDto_Success() throws Exception {
        when(userService.updateUserInfoPartially(INDEX_OF_UPDATING_USER, updateFields))
                .thenReturn(updatingPartiallyResponseDto);

        String jsonRequest = objectMapper.writeValueAsString(updateFields);
        MvcResult result = mockMvc.perform(
                        patch("/users/{id}", INDEX_OF_UPDATING_USER)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserResponseDto.class);
        assertNotNull(actual);
        assertEquals(updatingPartiallyResponseDto, actual);
    }

    @Test
    @DisplayName("Update user's info partially by not existing id")
    void updateUserInfoPartially_NotExistingId_NotFound() throws Exception {
        when(userService.updateUserInfoPartially(INDEX_OF_NOT_EXISTING_USER, updateFields))
                .thenThrow(new EntityNotFoundException("User not found"));

        String jsonRequest = objectMapper.writeValueAsString(updateFields);
        MvcResult result = mockMvc.perform(
                        patch("/users/{id}", INDEX_OF_NOT_EXISTING_USER)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @DisplayName("Update user's info partially by id, empty first name")
    void updateUserInfoPartially_EmptyName_BadRequest() throws Exception {
        when(userService.updateUserInfoPartially(INDEX_OF_UPDATING_USER, updateFieldsEmptyName))
                .thenThrow(new ValidationException("Empty first name"));

        String jsonRequest = objectMapper.writeValueAsString(updateFieldsEmptyName);
        MvcResult result = mockMvc.perform(
                        patch("/users/{id}", INDEX_OF_UPDATING_USER)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("Delete user by id")
    void deleteUserById_ExistingId_NoContent() throws Exception {
        mockMvc.perform(
                        delete("/users/{id}", INDEX_OF_DELETING_USER)
                )
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @DisplayName("Search users by birthdate range")
    void searchUsers_birthDateRangeOk_Success() throws Exception {
        when(userService.searchByBirthDateRange(birthDateRangeRequestDto.getFromDate(),
                birthDateRangeRequestDto.getToDate()))
                .thenReturn(List.of(new UserResponseDto(), new UserResponseDto(),
                        new UserResponseDto()));
        int expectedSize = 3;

        MvcResult result = mockMvc.perform(
                        get("/users/searchByBirthDateRange")
                                .param("fromDate", String.valueOf(birthDateRangeRequestDto
                                        .getFromDate()))
                                .param("toDate", String.valueOf(birthDateRangeRequestDto
                                        .getToDate()))
                )
                .andExpect(status().isOk())
                .andReturn();

        List<UserResponseDto> actual = objectMapper.readValue(result.getResponse()
                    .getContentAsString(), new TypeReference<List<UserResponseDto>>() {
                    });

        assertEquals(expectedSize, actual.size());
    }

    @Test
    @DisplayName("Search users by not valid birthdate range")
    void searchUsers_notValidBirthDateRange_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/users/searchByBirthDateRange")
                                .param("fromDate", String.valueOf(birthDateRangeRequestDto
                                        .getToDate()))
                                .param("toDate", String.valueOf(birthDateRangeRequestDto
                                        .getFromDate()))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}

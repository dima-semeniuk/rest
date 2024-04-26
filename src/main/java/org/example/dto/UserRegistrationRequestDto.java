package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.validation.CorrectFormat;
import org.example.validation.OlderThan;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UserRegistrationRequestDto {
    private static final String NOT_BLANK = "can't be blank.";
    private static final String NOT_WELL_EMAIL = "was formed not well.";

    @Email(message = NOT_WELL_EMAIL)
    private String email;

    @NotBlank(message = NOT_BLANK)
    private String firstName;

    @NotBlank(message = NOT_BLANK)
    private String lastName;

    @Past(message = "must be earlier than current date")
    @OlderThan
    @CorrectFormat
    private LocalDate birthDate;

    private String address;
    private String phoneNumber;
}

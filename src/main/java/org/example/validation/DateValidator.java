package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateValidator implements ConstraintValidator<CorrectFormat, LocalDate> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if(birthDate == null) {
            return false;
        }
        try {
            String dateString = birthDate.format(DATE_FORMATTER);
            return dateString.equals(birthDate.toString());
        } catch (Exception e) {
            return false;
        }
    }
}

package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDate;
import java.time.Period;

public class AgeValidator implements ConstraintValidator<OlderThan, LocalDate> {
    @Value("${minimum.age}")
    private int minimumAge;

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if(birthDate == null) {
            return false;
        }
        return Period.between(birthDate, LocalDate.now()).getYears() >= minimumAge;
    }
}

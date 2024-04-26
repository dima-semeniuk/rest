package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.dto.SearchByBirthDateRequestDto;

public class FromDateBeforeToDateValidator implements ConstraintValidator<FromDateBeforeToDate,
        SearchByBirthDateRequestDto> {
    @Override
    public boolean isValid(SearchByBirthDateRequestDto requestDto, ConstraintValidatorContext context) {
        if (requestDto.getFromDate() == null || requestDto.getToDate() == null) {
            return false;
        }
        return requestDto.getFromDate().isBefore(requestDto.getToDate());
    }
}

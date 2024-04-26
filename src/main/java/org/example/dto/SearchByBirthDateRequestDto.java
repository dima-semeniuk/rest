package org.example.dto;

import lombok.Data;
import org.example.validation.FromDateBeforeToDate;

import java.time.LocalDate;

@Data
@FromDateBeforeToDate
public class SearchByBirthDateRequestDto {

    private LocalDate fromDate;

    private LocalDate toDate;
}

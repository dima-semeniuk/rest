package org.example.dto;

import java.time.LocalDate;
import lombok.Data;
import org.example.validation.FromDateBeforeToDate;

@Data
@FromDateBeforeToDate
public class SearchByBirthDateRequestDto {

    private LocalDate fromDate;

    private LocalDate toDate;
}

package org.example.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String address;
    private String phoneNumber;
}

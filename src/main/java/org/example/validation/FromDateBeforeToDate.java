package org.example.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FromDateBeforeToDateValidator.class)
public @interface FromDateBeforeToDate {
    String message() default "Invalid date range: 'From' date must be earlier than 'To' date.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

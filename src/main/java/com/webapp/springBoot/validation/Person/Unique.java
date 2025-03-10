package com.webapp.springBoot.validation.Person;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint( validatedBy = UniqueValidation.class)
@Documented
public @interface Unique {
    String message() default "Значения уже присутствует";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
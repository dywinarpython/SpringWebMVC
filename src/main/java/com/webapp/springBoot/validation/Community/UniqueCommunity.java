package com.webapp.springBoot.validation.Community;

import com.webapp.springBoot.validation.Person.UniqueValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint( validatedBy = UniqueValidationCommunity.class)
@Documented
public @interface UniqueCommunity {
    String message() default "Значения уже присутствует";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
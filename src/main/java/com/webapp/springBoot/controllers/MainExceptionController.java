package com.webapp.springBoot.controllers;


import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.util.NoSuchElementException;
@Hidden
@ControllerAdvice
public class MainExceptionController {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handlerNoSuchElementException(NoSuchElementException ex) {
        return new ResponseEntity<>(
                "Ошибка нахождения элемента: " + ex.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }
    @ExceptionHandler(ValidationErrorWithMethod.class)
    public ResponseEntity<String> handlerValidationException(ValidationErrorWithMethod ex){
        return new ResponseEntity<>(
                ex.getAllErrors().toString(), HttpStatus.BAD_REQUEST
        );
    }
}

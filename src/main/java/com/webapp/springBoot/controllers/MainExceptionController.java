package com.webapp.springBoot.controllers;


import com.webapp.springBoot.exception.FileIsNull;
import com.webapp.springBoot.exception.ValidationErrorWithMethod;
import io.swagger.v3.oas.annotations.Hidden;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



import java.util.NoSuchElementException;

@Hidden
@ControllerAdvice
public class MainExceptionController {

    private final Logger logger = LoggerFactory.getLogger(MainExceptionController.class);
    private String messageError;

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handlerNoSuchElementException(NoSuchElementException ex) {
        messageError = "Ошибка нахождения элемента: " + ex.getMessage();
        logger.error(messageError);
        return new ResponseEntity<>(
                messageError,
                HttpStatus.NOT_FOUND
        );
    }
    @ExceptionHandler(ValidationErrorWithMethod.class)
    public ResponseEntity<String> handlerValidationException(ValidationErrorWithMethod ex){
        messageError = ex.getMessage();
        logger.warn(messageError);
        return new ResponseEntity<>(
                messageError, HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(FileIsNull.class)
    public ResponseEntity<String> handlerValidationException(FileIsNull ex){
        messageError = ex.getMessage();
        logger.error(messageError);
        return new ResponseEntity<>(
                messageError, HttpStatus.BAD_REQUEST
        );
    }

}

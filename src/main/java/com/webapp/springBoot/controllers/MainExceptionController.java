package com.webapp.springBoot.controllers;


import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import io.swagger.v3.oas.annotations.Hidden;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.util.NoSuchElementException;

@Slf4j
@Hidden
@ControllerAdvice
public class MainExceptionController {

    private String messageError;

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handlerNoSuchElementException(NoSuchElementException ex) {
        messageError = "Ошибка нахождения элемента: " + ex.getMessage();
        log.error(messageError);
        return new ResponseEntity<>(
                messageError,
                HttpStatus.NOT_FOUND
        );
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handlerUsernameNotFoundException(UsernameNotFoundException ex) {
        messageError = "Ошибка нахождения nickname пользователя: " + ex.getMessage();
        log.error(messageError);
        return new ResponseEntity<>(
                messageError,
                HttpStatus.NOT_FOUND
        );
    }
    @ExceptionHandler(ValidationErrorWithMethod.class)
    public ResponseEntity<String> handlerValidationException(ValidationErrorWithMethod ex){
        messageError = ex.getMessage();
        log.warn(messageError);
        return new ResponseEntity<>(
                messageError, HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<String> handlerLockedException(LockedException ex){
        messageError = ex.getMessage();
        log.warn(messageError);
        return new ResponseEntity<>(
                messageError, HttpStatus.FORBIDDEN
        );
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handlerAccessDeniedException(AccessDeniedException ex){
        messageError = ex.getMessage();
        log.warn(messageError);
        return new ResponseEntity<>(
                messageError, HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handlerException(Exception ex){
        messageError = ex.getMessage();
        log.error(messageError, ex);
        return new ResponseEntity<>(
                messageError, HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}

package com.webapp.springBoot.exception.validation;

import org.springframework.validation.ObjectError;

import java.util.List;

public class ValidationErrorWithMethod extends Exception {

    public ValidationErrorWithMethod(List<ObjectError> objectErrorList){
        super(greateMessageError(objectErrorList));
    }
    public ValidationErrorWithMethod(String message){
        super(message);
    }
    private static String greateMessageError(List<ObjectError> objectErrorList){
        StringBuilder stringBuilder = new StringBuilder("Ошибка валидации: ");
        objectErrorList.forEach(
                x -> stringBuilder.append("\n     ").append(x.getDefaultMessage()));
        return stringBuilder.toString();
    }

}

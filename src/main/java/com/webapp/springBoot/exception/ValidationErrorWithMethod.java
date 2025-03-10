package com.webapp.springBoot.exception;

import org.springframework.boot.context.properties.bind.validation.ValidationErrors;
import org.springframework.validation.ObjectError;

import java.util.List;

public class ValidationErrorWithMethod extends Exception {
    private List<ObjectError> objectErrorList;
    public ValidationErrorWithMethod(List<ObjectError> objectErrorList){
        this.objectErrorList = objectErrorList;
    }
    public StringBuilder getAllErrors(){
        StringBuilder stringBuilder = new StringBuilder("Ошибки валидации: ");
        objectErrorList.forEach(
                x -> stringBuilder.append("\n     ").append(x.getDefaultMessage())
        );
        return stringBuilder;
    }
}

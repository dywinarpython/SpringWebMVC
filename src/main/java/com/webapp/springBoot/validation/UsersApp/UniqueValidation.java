package com.webapp.springBoot.validation.UsersApp;

import com.webapp.springBoot.repository.UsersAppRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UniqueValidation implements ConstraintValidator<Unique, String> {
    @Autowired
    private UsersAppRepository userRepository;


    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext constraintValidatorContext) {
            return userRepository.checkNicknameUser(nickname);
    }

}

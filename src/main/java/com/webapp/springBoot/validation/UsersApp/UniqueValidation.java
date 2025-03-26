package com.webapp.springBoot.validation.UsersApp;

import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.repository.UsersAppRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;


@Component
public class UniqueValidation implements ConstraintValidator<Unique, String> {
    @Autowired
    private UsersAppRepository userRepository;


    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext constraintValidatorContext) {

        Optional<UsersApp> optionalUsers = userRepository.findByNickname(nickname);
        return optionalUsers.isEmpty() || userRepository.count() == 0;
    }

    }

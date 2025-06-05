package com.webapp.springBoot.validation.Community;

import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.repository.CommunityRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class UniqueValidationCommunity implements ConstraintValidator<UniqueCommunity, String> {
    @Autowired
    private CommunityRepository communityRepository;


    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext constraintValidatorContext) {
        return communityRepository.checkNicknameUser(nickname);
    }

    }

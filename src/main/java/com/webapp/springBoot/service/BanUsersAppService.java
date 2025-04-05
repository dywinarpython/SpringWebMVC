package com.webapp.springBoot.service;


import com.webapp.springBoot.DTO.Meneger.BanUsersDTO;
import com.webapp.springBoot.entity.BanUsersApp;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.BanUsersAppRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.Instant;
import java.util.Objects;

@Service
public class BanUsersAppService {

    @Autowired
    private BanUsersAppRepository banUsersAppRepository;

    @Autowired
    private UsersService usersService;

    @Autowired
    private UsersAppRepository usersAppRepository;


    @Transactional
    public void setBanUsers(BanUsersDTO banUsersDTO, BindingResult result) throws ValidationErrorWithMethod {
        if (result.hasErrors()){
            throw new ValidationErrorWithMethod(result.getAllErrors());
        }
        UsersApp usersApp = usersService.findUsersByNickname(banUsersDTO.getNickname());
        if(usersApp.getRoles().stream().anyMatch(x -> x.getName().contains("ADMIN"))){
            throw new LockedException("У вас нет прав блокировать админа!");
        }

        BanUsersApp banUsersAppNull = usersApp.getBanUsersApp();
        BanUsersApp banUsersApp = Objects.requireNonNullElseGet(banUsersAppNull, BanUsersApp::new);
        if(banUsersDTO.getBanForEver() != null){
            banUsersApp.setBanForEver(banUsersDTO.getBanForEver());
        } else if (banUsersDTO.getTime() != null) {
            System.out.println(banUsersDTO.getTime());
            banUsersApp.setTimeBan(Instant.now().plusMillis(banUsersDTO.getTime()).toEpochMilli());
        } else{
            throw new ValidationErrorWithMethod("Не переданы необходимые значения для бана!");
        }
        usersApp.setBanUsersApp(banUsersApp);
        usersAppRepository.save(usersApp);
    }
}

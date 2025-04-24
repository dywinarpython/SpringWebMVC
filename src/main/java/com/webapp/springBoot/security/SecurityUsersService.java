package com.webapp.springBoot.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUsersService {


    @Autowired
    private PasswordEncoder passwordEncoder;

    public String passwordEncode(String password){
        return passwordEncoder.encode(password);
    }

}

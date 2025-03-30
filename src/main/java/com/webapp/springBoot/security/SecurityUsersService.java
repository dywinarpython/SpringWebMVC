package com.webapp.springBoot.security;


import com.webapp.springBoot.repository.UsersAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUsersService {

    @Autowired
    private UsersAppRepository usersAppRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String passwordEncode(String password){
        return passwordEncoder.encode(password);
    }

}

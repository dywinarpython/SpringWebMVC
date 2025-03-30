package com.webapp.springBoot.security.service;

import com.webapp.springBoot.entity.Roles;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.repository.UsersAppRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class CustomUsersDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUsersDetailsService.class);

    @Autowired
    private UsersAppRepository usersAppRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UsersApp> optionalUsersApp = usersAppRepository.findByNickname(username);

        if(optionalUsersApp.isEmpty()){
            throw new UsernameNotFoundException("Nickname пользвоателя не найден");
        }
        UsersApp usersApp = optionalUsersApp.get();
        String[] roles = usersApp.getRoles().stream().map(Roles::getName).toArray(String[]::new);
        return User.builder()
                .username(usersApp.getNickname())
                .password(usersApp.getPassword())
                .roles(roles)
                .build();
    }
}

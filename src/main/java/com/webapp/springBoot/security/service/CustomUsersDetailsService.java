package com.webapp.springBoot.security.service;

import com.webapp.springBoot.entity.BanUsersApp;
import com.webapp.springBoot.entity.Roles;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.repository.BanUsersAppRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.Optional;


@Service
public class CustomUsersDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUsersDetailsService.class);

    @Autowired
    private UsersAppRepository usersAppRepository;

    @Autowired
    private BanUsersAppRepository banUsersAppRepository;

    private UsersApp getUserApp(Optional<UsersApp> optionalUsersApp){
        if(optionalUsersApp.isEmpty()){
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        return optionalUsersApp.get();
    }
    private boolean checkBan(UsersApp usersApp){
        boolean ban;
        BanUsersApp banUsersApp = usersApp.getBanUsersApp();
        if(banUsersApp == null){
            ban = false;
        } else if (banUsersApp.isBanForEver()) {
            ban = true;
        } else{
            ban = !(Instant.now().isAfter(Instant.ofEpochMilli(banUsersApp.getTimeBan())));
            if(!ban){
                usersApp.setBanUsersApp(null);
                banUsersAppRepository.delete(banUsersApp);
            }
        }
        return ban;
    }
    private String[] getRoles(UsersApp usersApp){
        return usersApp.getRoles().stream().map(Roles::getName).toArray(String[]::new);
    }
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UsersApp> optionalUsersApp = usersAppRepository.findByNickname(username);
        UsersApp usersApp = getUserApp(optionalUsersApp);
        String[] roles = getRoles(usersApp);
        boolean ban = checkBan(usersApp);
        return User.builder()
                .username(usersApp.getNickname())
                .password(usersApp.getPassword())
                .roles(roles)
                .accountLocked(ban)
                .build();
    }

    @Transactional
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException{
        Optional<UsersApp> optionalUsersApp = usersAppRepository.findByEmail(email);
        UsersApp usersApp = getUserApp(optionalUsersApp);
        String[] roles = getRoles(usersApp);
        boolean ban = checkBan(usersApp);
        return User.builder()
                .username(usersApp.getNickname())
                .password("N/A")
                .roles(roles)
                .accountLocked(ban)
                .build();
    }

}

package com.webapp.springBoot.security.service;

import com.webapp.springBoot.entity.BanUsersApp;
import com.webapp.springBoot.entity.Roles;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.repository.BanUsersAppRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.Optional;


@Slf4j
@Service
public class CustomUsersDetailsService implements UserDetailsService {


    @Autowired
    private UsersAppRepository usersAppRepository;

    @Autowired
    private BanUsersAppRepository banUsersAppRepository;

    private UsersApp getUserApp(Optional<UsersApp> optionalUsersApp){
        if(optionalUsersApp.isEmpty()){
            log.warn("Пользователь не найден", UsernameNotFoundException.class);
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
        boolean ban = checkBan(usersApp);
        return User.builder()
                .username(usersApp.getNickname())
                .password(usersApp.getPassword())
                .roles(getRoles(usersApp))
                .accountLocked(ban)
                .build();
    }

    @Transactional
    public UserDetails loadUserByEmail(UsersApp usersApp) throws UsernameNotFoundException{
        String[] roles = getRoles(usersApp);
        boolean ban = checkBan(usersApp);
        return User.builder()
                .username(usersApp.getNickname())
                .password("N/A")
                .roles(roles)
                .accountLocked(ban)
                .build();
    }

    public Optional<UsersApp> checkEmailUser(String email){
        return  usersAppRepository.findByEmail(email);

    }

}

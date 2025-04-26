package com.webapp.springBoot.security.service;

import com.webapp.springBoot.entity.BanUsersApp;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.repository.BanUsersAppRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.List;
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
    private List<SimpleGrantedAuthority> getAuthorities(UsersApp usersApp){
        return usersApp.getRoles().stream().map(roles -> new SimpleGrantedAuthority(STR."ROLE_\{roles.getName()}")).toList();
    }

    @Cacheable(value = "SECURITY", key="#nickname")
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        Optional<UsersApp> optionalUsersApp = usersAppRepository.findByNickname(nickname);
        UsersApp usersApp = getUserApp(optionalUsersApp);
        boolean ban = checkBan(usersApp);
        return User.builder()
                .username(usersApp.getNickname())
                .password(usersApp.getPassword())
                .authorities(getAuthorities(usersApp))
                .accountLocked(ban)
                .build();
    }

    @Cacheable(value = "SECURITY", key="usersApp.getNickname()")
    @Transactional
    public UserDetails loadUserByEmail(UsersApp usersApp) throws UsernameNotFoundException{
        boolean ban = checkBan(usersApp);
        return User.builder()
                .username(usersApp.getNickname())
                .password("N/A")
                .authorities(getAuthorities(usersApp))
                .accountLocked(ban)
                .build();
    }

    public Optional<UsersApp> checkEmailUser(String email){
        return  usersAppRepository.findByEmail(email);

    }

}

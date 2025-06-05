package com.webapp.springBoot.util;



import com.twilio.Twilio;
import com.webapp.springBoot.entity.Community;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.repository.CommunityRepository;
import com.webapp.springBoot.repository.RolesRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import com.webapp.springBoot.security.SecurityUsersService;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PostService {

    @Value("${upload.pathCommunity}")
    private String pathCommunity;

    @Value("${upload.pathUser}")
    private String pathUser;

    @Value("${upload.pathUser.video}")
    private String pathUserVideo;

    @Value("${upload.pathCommunity.video}")
    private String pathCommunityVideo;

    @Value("${upload.pathCommunity.image}")
    private String pathCommunityImage;

    @Value("${upload.pathUser.image}")
    private String pathUserImage;

    @Value("${twillio.sid}")
    private String accountSid;

    @Value("${twillio.authToken}")
    private String authToken;

    @Autowired
    private UsersAppRepository usersAppRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private SecurityUsersService securityUsersService;

    @Autowired
    private RolesRepository rolesRepository;

    @PostConstruct
    public void init() throws IOException {
        Path communityPath = Path.of(pathCommunity);
        Path userPath = Path.of(pathUser);
        Path userVideoPath = Path.of(pathUserVideo);
        Path communityVideoPath = Path.of(pathCommunityVideo);
        Path communityImagePath = Path.of(pathCommunityImage);
        Path userImagePath = Path.of(pathUserImage);
        Files.createDirectories(communityPath);
        Files.createDirectories(userPath);
        Files.createDirectories(userVideoPath);
        Files.createDirectories(communityVideoPath);
        Files.createDirectories(communityImagePath);
        Files.createDirectories(userImagePath);
    }

    @PostConstruct
    public void initTwilio(){
        Twilio.init(accountSid, authToken);
    }

    @PostConstruct
    public  void addSuperUser(){
        if(usersAppRepository.findByNickname("admin").isEmpty()) {
            UsersApp usersApp = new UsersApp();
            usersApp.setName("Кирилл");
            usersApp.setNickname("admin");
            usersApp.setSurname("Коцюбинский");
            usersApp.setAge(21);
            usersApp.setPassword(securityUsersService.passwordEncode("Dywinar1@"));
            usersApp.rolesAdd(rolesRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException("Ошибка добавления суперпользователя")));
            Community community = new Community();
            community.setUserOwner(usersApp);
            community.setDescription("Сообщество приложения");
            community.setNickname("community");
            community.setName("Офицальное сообщество");
            communityRepository.save(community);
            usersAppRepository.save(usersApp);
        }

    }
}

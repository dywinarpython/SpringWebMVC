package com.webapp.springBoot.util;



import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;

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
}

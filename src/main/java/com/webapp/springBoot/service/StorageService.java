package com.webapp.springBoot.service;



import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class StorageService {

    @Value("${upload.pathCommunuty}")
    private String pathCommunity;

    @Value("${upload.pathUser}")
    private String pathUser;

    @PostConstruct
    public void init() throws IOException {
        Path communityPath = Path.of(pathCommunity);
        Path userPath = Path.of(pathUser);
        if(!Files.exists(userPath) && !Files.exists(communityPath)){
            Files.createDirectories(communityPath);
            Files.createDirectories(userPath);
        }
    }
}
